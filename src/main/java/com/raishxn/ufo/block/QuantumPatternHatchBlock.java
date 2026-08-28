package com.raishxn.ufo.block;

import appeng.block.crafting.PatternProviderBlock;
import com.raishxn.ufo.api.multiblock.IMultiblockController;
import com.raishxn.ufo.block.entity.AbstractSimpleMultiblockControllerBE;
import com.raishxn.ufo.block.entity.QuantumPatternHatchBE;
import com.raishxn.ufo.block.entity.StellarNexusControllerBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class QuantumPatternHatchBlock extends PatternProviderBlock {

    public QuantumPatternHatchBlock(final BlockBehaviour.Properties properties) {
        super(properties.strength(5.0F).requiresCorrectToolForDrops());
    }

    @Override
    protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean moved) {
        if (level.getBlockEntity(pos) instanceof final QuantumPatternHatchBE hatch) {
            final var controllerPos = hatch.getControllerPos();
            if (controllerPos != null && level.getBlockEntity(controllerPos) instanceof final IMultiblockController controller) {
                controller.removePart(pos);
                controller.scanStructure(level);
            }
            hatch.unlinkFromController();
        }
        super.affectNeighborsAfterRemoval(state, level, pos, moved);
    }

    @Override
    protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final Orientation orientation, final boolean isMoving) {
        super.neighborChanged(state, level, pos, block, orientation, isMoving);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof final QuantumPatternHatchBE hatch) {
            final var controllerPos = hatch.getControllerPos();
            if (controllerPos != null) {
                markControllerDirty(level, controllerPos);
            }
        }
    }

    private static void markControllerDirty(final Level level, final BlockPos controllerPos) {
        final BlockEntity entity = level.getBlockEntity(controllerPos);
        if (entity instanceof final AbstractSimpleMultiblockControllerBE controller) {
            controller.markStructureDirty();
        } else if (entity instanceof final StellarNexusControllerBE controller) {
            controller.markStructureDirty();
        } else if (entity instanceof final IMultiblockController controller) {
            controller.scanStructure(level);
        }
    }
}
