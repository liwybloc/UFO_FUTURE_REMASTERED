package com.raishxn.ufo.block;

import appeng.block.AEBaseEntityBlock;
import com.raishxn.ufo.api.multiblock.EntropicMachineLocator;
import com.raishxn.ufo.api.multiblock.IEntropicMachineController;
import com.raishxn.ufo.block.entity.EntropicMachinePartBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class EntropicMachineShellBlock extends AEBaseEntityBlock<EntropicMachinePartBE> {
    public EntropicMachineShellBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        final IEntropicMachineController controller = EntropicMachineLocator.findController(level, pos);
        if (controller == null || !controller.isAssembled() || !controller.isNetworkConnected()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide() && controller instanceof final net.minecraft.world.MenuProvider menuProvider) {
            player.openMenu(menuProvider, controller.getControllerPos());
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void neighborChanged(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull Block changedBlock, final Orientation orientation, final boolean isMoving) {
        super.neighborChanged(state, level, pos, changedBlock, orientation, isMoving);
        if (!level.isClientSide()) {
            EntropicMachineLocator.markNearbyDirty(level, pos);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(final @NotNull BlockState state, final @NotNull ServerLevel level, final @NotNull BlockPos pos, final boolean moved) {
        EntropicMachineLocator.markNearbyDirty(level, pos);
        super.affectNeighborsAfterRemoval(state, level, pos, moved);
    }
}
