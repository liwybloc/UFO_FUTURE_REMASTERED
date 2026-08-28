package com.raishxn.ufo.block.entity;

import com.raishxn.ufo.api.ae.IMassiveInjector;
import com.raishxn.ufo.api.multiblock.IMultiblockPart;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.stacks.AEKey;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

/**
 * Block Entity for the ME Massive Output Hatch.
 * <p>
 * This is a hybrid entity: it extends AE2's {@link AENetworkedBlockEntity}
 * to gain a real grid connection (cables, channels, power), while also
 * implementing our internal API interfaces:
 * <ul>
 *   <li>{@link IMassiveInjector} — bulk item/fluid injection into the ME network</li>
 *   <li>{@link IMultiblockPart} — multiblock structure participation</li>
 * </ul>
 * <p>
 * <b>How injection works:</b>
 * <ol>
 *   <li>The Stellar Nexus Controller finishes a simulation cycle</li>
 *   <li>It locates all Output Hatches among its parts</li>
 *   <li>Calls {@link #injectIntoNetwork(AEKey, long, Level)} on each hatch</li>
 *   <li>The hatch uses {@code grid.getStorageService().getInventory().insert(...)} to push items directly into ME storage</li>
 * </ol>
 */
public final class MassiveOutputHatchBE extends AENetworkedBlockEntity
        implements IMassiveInjector, IMultiblockPart, IGridTickable {

    /** Controller position for multiblock link (null if standalone). */
    @Nullable
    private BlockPos controllerPos = null;

    /** Statistics: total items injected since last reset. */
    private long totalInjected = 0;

    /** Statistics: last injection amount (for GUI/tooltip display). */
    private long lastInjectionAmount = 0;

    public MassiveOutputHatchBE(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
        this.getMainNode()
                .setExposedOnSides(java.util.EnumSet.allOf(Direction.class))
                .setFlags()                          // No special flags
                .setIdlePowerUsage(0)                // No passive drain
                .addService(IGridTickable.class, this); // Register for tick callbacks
    }


    @Override
    public long injectIntoNetwork(final AEKey what, final long amount, final Level level) {
        if (!isNetworkReady() || what == null || amount <= 0) {
            return 0;
        }

        final var gridNode = this.getMainNode().getNode();
        if (gridNode == null || gridNode.getGrid() == null) {
            return 0;
        }

        final var grid = gridNode.getGrid();
        final var storageService = grid.getStorageService();
        final var inventory = storageService.getInventory();

        final var source = IActionSource.ofMachine(this);

        final long inserted = inventory.insert(what, amount, Actionable.MODULATE, source);

        if (inserted > 0) {
            this.totalInjected += inserted;
            this.lastInjectionAmount = inserted;
            this.setChanged();
        }

        return inserted;
    }

    @Override
    public boolean isNetworkReady() {
        final var node = this.getMainNode().getNode();
        return node != null && node.isActive() && node.isPowered();
    }


    @Override
    public void linkToController(final BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        refreshGridConnection();
        this.setChanged();
    }

    @Override
    public void unlinkFromController() {
        this.controllerPos = null;
        refreshGridConnection();
        this.setChanged();
    }

    @Nullable
    @Override
    public BlockPos getControllerPos() {
        return this.controllerPos;
    }

    /**
     * @return {@code true} if this hatch is currently linked to a multiblock controller.
     */
    public boolean isLinked() {
        return this.controllerPos != null;
    }


    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return new TickingRequest(20, 20, true);
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        return TickRateModulation.SLEEP;
    }


    @Override
    public AECableType getCableConnectionType(final Direction dir) {
        return AECableType.DENSE_SMART;
    }

    @Override
    public Set<Direction> getGridConnectableSides(final BlockOrientation orientation) {
        return EnumSet.allOf(Direction.class);
    }

    public void refreshGridConnection() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        this.getMainNode().setExposedOnSides(EnumSet.allOf(Direction.class));
        this.onGridConnectableSidesChanged();
    }


    public long getTotalInjected() {
        return this.totalInjected;
    }

    public long getLastInjectionAmount() {
        return this.lastInjectionAmount;
    }

    public void resetStatistics() {
        this.totalInjected = 0;
        this.lastInjectionAmount = 0;
        this.setChanged();
    }


    @Override
    public void saveAdditional(@NotNull final ValueOutput output) {
        super.saveAdditional(output);
        if (this.controllerPos != null) {
            output.store("controllerPos", BlockPos.CODEC, this.controllerPos);
        }
        output.putLong("totalInjected", this.totalInjected);
        output.putLong("lastInjection", this.lastInjectionAmount);
    }

    @Override
    protected void loadAdditional(@NotNull final ValueInput input) {
        super.loadAdditional(input);
        this.controllerPos = input.read("controllerPos", BlockPos.CODEC).map(BlockPos::immutable).orElse(null);
        this.totalInjected = input.getLongOr("totalInjected", 0L);
        this.lastInjectionAmount = input.getLongOr("lastInjection", 0L);
    }
}
