package com.raishxn.ufo.block;

import com.raishxn.ufo.api.multiblock.EntropicMachineLocator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import com.raishxn.ufo.api.multiblock.IMultiblockController;
import com.raishxn.ufo.block.entity.EntropicConvergenceCalculator;
import com.raishxn.ufo.block.entity.StellarNexusControllerBE;
import com.raishxn.ufo.block.entity.StellarNexusPartBE;
import com.raishxn.ufo.init.ModBlockEntities;

/**
 * Generic structural block for the Stellar Nexus multiblock.
 * <p>
 * This class is reused for various "parts" of the structure:
 * casings, hatches, field generators, coolant matrices, etc.
 * Each instance gets its own unique registry ID and texture, but
 * they all share the same block entity type and linking logic.
 */
public class StellarNexusPartBlock extends Block implements net.minecraft.world.level.block.EntityBlock {

    public StellarNexusPartBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new StellarNexusPartBE(ModBlockEntities.STELLAR_NEXUS_PART_BE.get(), pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        final var controller = EntropicMachineLocator.findController(level, pos);
        if (controller != null) {
            if (!level.isClientSide() && controller.isAssembled() && controller.isNetworkConnected()
                    && controller instanceof final net.minecraft.world.MenuProvider menuProvider) {
                player.openMenu(menuProvider, controller.getControllerPos());
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean moved) {
        if (level.getBlockEntity(pos) instanceof final StellarNexusPartBE part) {
                final BlockPos controllerPos = part.getControllerPos();
                if (controllerPos != null && level.getBlockEntity(controllerPos) instanceof final IMultiblockController controller) {
                    controller.removePart(pos);
                    controller.scanStructure(level);
                }
                part.unlinkFromController();
        }
        EntropicMachineLocator.markNearbyDirty(level, pos);
        EntropicConvergenceCalculator.markNearbyDirty(level, pos);
        super.affectNeighborsAfterRemoval(state, level, pos, moved);
    }

    @Override
    protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block changedBlock, final Orientation orientation, final boolean isMoving) {
        super.neighborChanged(state, level, pos, changedBlock, orientation, isMoving);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof final StellarNexusPartBE part) {
            final BlockPos controllerPos = part.getControllerPos();
            if (controllerPos != null) {
                markControllerDirty(level, controllerPos);
            }
            EntropicMachineLocator.markNearbyDirty(level, pos);
            if (level instanceof final net.minecraft.server.level.ServerLevel serverLevel) {
                EntropicConvergenceCalculator.markNearbyDirty(serverLevel, pos);
            }
        }
    }

    private static void markControllerDirty(final Level level, final BlockPos controllerPos) {
        final BlockEntity entity = level.getBlockEntity(controllerPos);
        if (entity instanceof final StellarNexusControllerBE controller) {
            controller.markStructureDirty();
        } else if (entity instanceof final IMultiblockController controller) {
            controller.scanStructure(level);
        }
    }
}
