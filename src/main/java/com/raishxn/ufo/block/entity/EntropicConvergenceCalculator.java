package com.raishxn.ufo.block.entity;

import appeng.api.networking.IGrid;
import appeng.api.networking.events.GridCraftingCpuChange;
import appeng.me.cluster.MBCalculator;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import com.raishxn.ufo.api.multiblock.FieldTieredCubeValidator;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.mixin.InvokerCraftingCPUCluster;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Iterator;

public final class EntropicConvergenceCalculator {
    private static final int SEARCH_RADIUS = FieldTieredCubeValidator.OUTER_SIZE - 1;
    private final EntropicConvergenceEngineBE target;

    public EntropicConvergenceCalculator(final EntropicConvergenceEngineBE target) {
        this.target = target;
    }

    public void calculateMultiblock(final ServerLevel level, final BlockPos loc) {
        if (MBCalculator.isModificationInProgress()) {
            return;
        }

        final var currentCluster = this.target.getCluster();
        if (currentCluster != null && currentCluster.isDestroyed()) {
            return;
        }

        final var result = FieldTieredCubeValidator.findMatchingCube(level, loc,
                (state, testLevel, pos) -> state.is(MultiblockBlocks.ENTROPIC_CONVERGENCE_CASING.get()));

        if (result.isEmpty() || !result.get().valid() || !result.get().shellPositions().contains(loc)) {
            this.target.clearCalculatedStructure();
            this.target.disconnect(true);
            return;
        }

        final var validation = result.get();
        boolean updateGrid = false;
        CraftingCPUCluster cluster = this.target.getCluster();

        try {
            if (cluster == null
                    || !cluster.getBoundsMin().equals(validation.minCorner())
                    || !cluster.getBoundsMax().equals(validation.maxCorner())) {
                cluster = new CraftingCPUCluster(validation.minCorner(), validation.maxCorner());
                MBCalculator.setModificationInProgress(cluster);
                this.updateBlockEntities(cluster, level, validation);
                updateGrid = true;
            } else {
                MBCalculator.setModificationInProgress(cluster);
                this.refreshBlockEntities(cluster, level, validation);
            }

            cluster.updateStatus(updateGrid);
        } finally {
            MBCalculator.setModificationInProgress(null);
        }
    }

    public void updateMultiblockAfterNeighborUpdate(final ServerLevel level, final BlockPos loc, final BlockPos changedPos) {
        final boolean recheck;
        final CraftingCPUCluster cluster = this.target.getCluster();

        if (cluster != null) {
            recheck = isWithinBounds(changedPos, cluster.getBoundsMin(), cluster.getBoundsMax())
                    || this.isRelevantBlock(level.getBlockEntity(changedPos));
        } else {
            recheck = true;
        }

        if (recheck) {
            calculateMultiblock(level, loc);
        }
    }

    private void updateBlockEntities(final CraftingCPUCluster cluster, final ServerLevel level,
                                     final FieldTieredCubeValidator.ValidationResult validation) {
        for (final BlockPos shellPos : validation.shellPositions()) {
            final BlockEntity blockEntity = level.getBlockEntity(shellPos);
            if (blockEntity instanceof final EntropicConvergenceEngineBE convergence) {
                convergence.applyCalculatedStructure(cluster, validation);
                ((InvokerCraftingCPUCluster) (Object) cluster).ufo$addBlockEntity(convergence);
            }
        }

        ((InvokerCraftingCPUCluster) (Object) cluster).ufo$done();
        postCpuChange(cluster);
    }

    private void refreshBlockEntities(final CraftingCPUCluster cluster, final ServerLevel level,
                                      final FieldTieredCubeValidator.ValidationResult validation) {
        for (final BlockPos shellPos : validation.shellPositions()) {
            final BlockEntity blockEntity = level.getBlockEntity(shellPos);
            if (blockEntity instanceof final EntropicConvergenceEngineBE convergence) {
                convergence.applyCalculatedStructure(cluster, validation);
            }
        }
    }

    private void postCpuChange(final CraftingCPUCluster cluster) {
        final Iterator<appeng.blockentity.crafting.CraftingBlockEntity> iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            final var blockEntity = iterator.next();
            final var node = blockEntity.getGridNode();
            if (node != null) {
                final IGrid grid = node.getGrid();
                if (grid != null) {
                    grid.postEvent(new GridCraftingCpuChange(node));
                }
                return;
            }
        }
    }

    private boolean isRelevantBlock(final BlockEntity blockEntity) {
        return blockEntity instanceof EntropicConvergenceEngineBE;
    }

    public static void markNearbyDirty(final ServerLevel level, final BlockPos origin) {
        for (final BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS),
                origin.offset(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS))) {
            final BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof final EntropicConvergenceEngineBE convergence) {
                convergence.scanStructure(level);
            }
        }
    }

    private static boolean isWithinBounds(final BlockPos pos, final BlockPos min, final BlockPos max) {
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        return x >= min.getX() && y >= min.getY() && z >= min.getZ()
                && x <= max.getX() && y <= max.getY() && z <= max.getZ();
    }
}
