package com.raishxn.ufo.menu;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import net.pedroksl.ae2addonlib.api.IFluidTankHandler;
import net.pedroksl.ae2addonlib.gui.OutputDirectionMenu;
import net.pedroksl.ae2addonlib.core.network.clientPacket.FluidTankStackUpdatePacket;

import com.raishxn.ufo.block.entity.DimensionalMatterAssemblerBlockEntity;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import appeng.api.util.IConfigManager;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.guisync.ClientActionKey;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;

public class DimensionalMatterAssemblerMenu extends UpgradeableMenu<DimensionalMatterAssemblerBlockEntity>
        implements IProgressProvider, IFluidTankHandler {

    @GuiSync(2)
    public int maxProcessingTime = -1;

    @GuiSync(3)
    public int processingTime = -1;

    @GuiSync(7)
    public YesNo autoExport = YesNo.NO;

    @GuiSync(8)
    public boolean showWarning = false;

    @GuiSync(9)
    public int currentPower = 0;

    @GuiSync(10)
    public int temperature = 0;

    @GuiSync(11)
    public int maxTemperature = 10000;

    @GuiSync(12)
    public int overloadTimer = -1;

    public final int INPUT_FLUID_SIZE = 16;
    public final int OUTPUT_FLUID_SIZE = 16;

    private static final ClientActionKey<Void> CONFIGURE_OUTPUT = new ClientActionKey<>("configureOutput");
    private static final ClientActionKey<Integer> CLEAR_TANK = new ClientActionKey<>("clearTank");
    private static final ClientActionKey<Integer> FILL_DRAIN_TANK = new ClientActionKey<>("fillDrainTank");

    private final List<Slot> inputs = new ArrayList<>(9);

    public DimensionalMatterAssemblerMenu(final int id, final Inventory ip, final DimensionalMatterAssemblerBlockEntity host) {
        super(UFOMenus.DIMENSIONAL_MATTER_ASSEMBLER.get(), id, ip, host);

        final var inputsInv = host.getInput();

        for (var x = 0; x < inputsInv.size(); x++) {
            this.inputs.add(x, this.addSlot(new AppEngSlot(inputsInv, x), SlotSemantics.MACHINE_INPUT));
        }

        final var outputsInv = host.getOutput();
        this.addSlot(new OutputSlot(outputsInv, 0, null), SlotSemantics.MACHINE_OUTPUT);
        if (outputsInv.size() > 1) {
            this.addSlot(new OutputSlot(outputsInv, 1, null), UFOSlotSemantics.MACHINE_OUTPUT_2);
        }

        registerClientAction(CONFIGURE_OUTPUT, this::configureOutput);
        registerClientAction(CLEAR_TANK, ByteBufCodecs.INT, this::clearTank);
        registerClientAction(FILL_DRAIN_TANK, ByteBufCodecs.INT, this::fillOrDrainTank);
    }

    protected void loadSettingsFromHost(final IConfigManager cm) {
        this.autoExport = this.getHost().getConfigManager().getSetting(Settings.AUTO_EXPORT);
    }

    @Override
    protected void standardDetectAndSendChanges() {
        if (isServerSide()) {
            this.maxProcessingTime = getHost().getMaxProcessingTime();
            this.processingTime = getHost().getProcessingTime();
            this.showWarning = getHost().showWarning();
            this.currentPower = (int) getHost().getAECurrentPower();
            this.temperature = getHost().getTemperature();
            this.maxTemperature = getHost().getMaxTemperature();
            this.overloadTimer = getHost().getOverloadTimer();

            for (int i = 0; i < 4; i++) {
                final var genFluid = this.getHost().getTank().getStack(i);
                FluidStack fluidStack = FluidStack.EMPTY;
                if (genFluid != null && genFluid.what() != null) {
                    fluidStack = ((AEFluidKey) genFluid.what()).toStack(((int) genFluid.amount()));
                }
                sendPacketToClient(new FluidTankStackUpdatePacket(i, fluidStack));
            }
        }
        super.standardDetectAndSendChanges();
    }

    @Override
    public boolean isValidForSlot(final Slot s, final ItemStack is) {
        if (this.inputs.contains(s)) {
            return true;
        }
        return true;
    }

    @Override
    public int getCurrentProgress() {
        return this.processingTime;
    }

    @Override
    public int getMaxProgress() {
        return this.maxProcessingTime;
    }

    public YesNo getAutoExport() {
        return autoExport;
    }

    public boolean getShowWarning() {
        return this.showWarning;
    }

    public void configureOutput() {
        if (isClientSide()) {
            sendClientAction(CONFIGURE_OUTPUT);
            return;
        }

        final var locator = getLocator();
        if (locator != null && isServerSide()) {
            OutputDirectionMenu.open(
                    ((ServerPlayer) this.getPlayer()),
                    getLocator(),
                    this.getHost().getAllowedOutputs());
        }
    }

    /**
     * Clears a specific tank slot. Triggered by GUI clear buttons.
     */
    public void clearTank(final int slot) {
        if (isClientSide()) {
            sendClientAction(CLEAR_TANK, slot);
            return;
        }
        getHost().clearTank(slot);
    }

    /**
     * Fills or drains a tank slot using the player's held bucket/container.
     * Called when player right-clicks a tank in the GUI.
     */
    public void fillOrDrainTank(final int tankIndex) {
        if (isClientSide()) {
            sendClientAction(FILL_DRAIN_TANK, tankIndex);
            return;
        }

        final var player = getPlayer();
        if (player == null) return;

        final ItemStack held = getCarried();
        if (held.isEmpty()) return;

        final var tank = getHost().getTank();

        if (tankIndex == 2 || tankIndex == 3) {
            final var itemAccess = ItemAccess.forPlayerCursor(player, this);
            final var fluidCap = itemAccess.getCapability(Capabilities.Fluid.ITEM);
            if (fluidCap != null) {
                FluidResource available = FluidResource.EMPTY;
                long availableAmount = 0;
                for (int i = 0; i < fluidCap.size(); i++) {
                    if (!fluidCap.getResource(i).isEmpty() && fluidCap.getAmountAsLong(i) > 0) {
                        available = fluidCap.getResource(i);
                        availableAmount = fluidCap.getAmountAsLong(i);
                        break;
                    }
                }
                if (!available.isEmpty()) {
                    final AEFluidKey fluidKey = AEFluidKey.of(available.getFluid());
                    final var currentStack = tank.getStack(tankIndex);

                    if (currentStack == null || currentStack.what().equals(fluidKey)) {
                        final long currentAmount = currentStack != null ? currentStack.amount() : 0;
                        final long maxCapacity = tank.getMaxAmount(fluidKey);
                        final long space = maxCapacity - currentAmount;

                        if (space > 0) {
                            final int toDrain = (int) Math.min(availableAmount, space);
                            final int drained;
                            try (final Transaction transaction = Transaction.openRoot()) {
                                drained = fluidCap.extract(available, toDrain, transaction);
                                if (drained > 0) transaction.commit();
                            }
                            if (drained > 0) {
                                final long newAmount = currentAmount + drained;
                                tank.setStack(tankIndex, new GenericStack(fluidKey, newAmount));
                                getHost().onChangeTank();
                                getHost().saveChanges();
                            }
                        }
                    }
                    return;
                }
            }
        }

        if (tankIndex == 0 || tankIndex == 1 || tankIndex == 2 || tankIndex == 3) {
            final var currentStack = tank.getStack(tankIndex);
            if (currentStack != null && currentStack.what() instanceof final AEFluidKey fluidKey) {
                final var itemAccess = ItemAccess.forPlayerCursor(player, this);
                final var fluidCap = itemAccess.getCapability(Capabilities.Fluid.ITEM);
                if (fluidCap != null) {
                    final FluidResource toFill = FluidResource.of(fluidKey.getFluid());
                    final int requested = (int) Math.min(currentStack.amount(), Integer.MAX_VALUE);
                    final int filled;
                    try (final Transaction transaction = Transaction.openRoot()) {
                        filled = fluidCap.insert(toFill, requested, transaction);
                        if (filled > 0) transaction.commit();
                    }
                    if (filled > 0) {
                        final long remaining = currentStack.amount() - filled;
                        if (remaining > 0) {
                            tank.setStack(tankIndex, new GenericStack(fluidKey, remaining));
                        } else {
                            tank.setStack(tankIndex, null);
                        }
                        getHost().onChangeTank();
                        getHost().saveChanges();
                    }
                }
            }
        }
    }

    @Override
    public ServerPlayer getServerPlayer() {
        if (isClientSide()) {
            return null;
        }
        return ((ServerPlayer) getPlayer());
    }

    @Override
    public ItemStack getCarriedItem() {
        return getCarried();
    }

    public void setCarriedItem(final ItemStack stack) {
        setCarried(stack);
    }

    @Override
    public GenericStackInv getTank() {
        return this.getHost().getTank();
    }

    @Override
    public boolean canExtractFromTank(final int index) {
        return index == 0 || index == 1;
    }

    @Override
    public boolean canInsertInto(final int index) {
        return index == 2 || index == 3;
    }
}
