package com.raishxn.ufo.block.entity;

import appeng.api.stacks.AEItemKey;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.block.crafting.PatternProviderBlock;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import com.raishxn.ufo.api.multiblock.IMultiblockPart;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.init.ModBlockEntities;
import com.raishxn.ufo.init.ModMenus;
import com.raishxn.ufo.screen.QuantumPatternHatchMenu;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuantumPatternHatchBE extends PatternProviderBlockEntity implements IMultiblockPart, MenuProvider {
    public static final int PATTERN_CAPACITY = 72;

    @Nullable
    private BlockPos controllerPos;

    public QuantumPatternHatchBE(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntities.QUANTUM_PATTERN_HATCH_BE.get(), pos, blockState);
    }

    @Override
    protected PatternProviderLogic createLogic() {
        return new QuantumPatternProviderLogic(this, PATTERN_CAPACITY);
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get());
    }

    @Override
    public void linkToController(final BlockPos controllerPos) {
        if (controllerPos.equals(this.controllerPos)) {
            return;
        }
        this.controllerPos = controllerPos;
        setChanged();
    }

    @Override
    public void unlinkFromController() {
        if (this.controllerPos == null) {
            return;
        }
        this.controllerPos = null;
        setChanged();
    }

    @Override
    public @Nullable BlockPos getControllerPos() {
        return this.controllerPos;
    }

    public Direction getPushDirectionForController() {
        final var pushDirection = getBlockState().getValue(PatternProviderBlock.PUSH_DIRECTION).getDirection();
        return pushDirection != null ? pushDirection : Direction.NORTH;
    }

    @Override
    public @NotNull net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("block.ufo.quantum_pattern_hatch");
    }

    @Override
    public void openMenu(final Player player, final MenuHostLocator locator) {
        MenuOpener.open(ModMenus.QUANTUM_PATTERN_HATCH_MENU.get(), player, locator);
    }

    @Override
    public void returnToMainMenu(final Player player, final ISubMenu subMenu) {
        MenuOpener.returnTo(ModMenus.QUANTUM_PATTERN_HATCH_MENU.get(), player, subMenu.getLocator());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int containerId, final Inventory playerInventory, final Player player) {
        return new QuantumPatternHatchMenu(containerId, playerInventory, this);
    }

    @Override
    public void saveAdditional(@NotNull final ValueOutput output) {
        super.saveAdditional(output);
        if (this.controllerPos != null) {
            output.store("controllerPos", BlockPos.CODEC, this.controllerPos);
        }
    }

    @Override
    protected void loadAdditional(@NotNull final ValueInput input) {
        super.loadAdditional(input);
        this.controllerPos = input.read("controllerPos", BlockPos.CODEC).map(BlockPos::immutable).orElse(null);
    }
}
