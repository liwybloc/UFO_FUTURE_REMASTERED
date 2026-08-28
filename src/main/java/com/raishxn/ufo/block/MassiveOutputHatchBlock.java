package com.raishxn.ufo.block;

import com.raishxn.ufo.api.multiblock.IMultiblockController;
import com.raishxn.ufo.block.entity.AbstractSimpleMultiblockControllerBE;
import com.raishxn.ufo.block.entity.MassiveOutputHatchBE;
import com.raishxn.ufo.block.entity.StellarNexusControllerBE;
import com.raishxn.ufo.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * ME Massive Output Hatch — an AE2-integrated output port for the Stellar Nexus.
 * <p>
 * This block is directional (the face indicates which side connects to AE2 cables)
 * and uses a specialized BlockEntity ({@link MassiveOutputHatchBE}) that extends
 * AE2's {@code AENetworkedBlockEntity} to establish a real grid connection.
 * <p>
 * Unlike the generic {@link StellarNexusPartBlock}, this block has a dedicated
 * BE type because it needs to hold an AE2 grid node and implement
 * {@link com.raishxn.ufo.api.ae.IMassiveInjector}.
 */
public final class MassiveOutputHatchBlock extends DirectionalBlock implements net.minecraft.world.level.block.EntityBlock {

    public MassiveOutputHatchBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends DirectionalBlock> codec() {
        return com.mojang.serialization.MapCodec.unit(
                () -> new MassiveOutputHatchBlock(BlockBehaviour.Properties.of()));
    }

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new MassiveOutputHatchBE(ModBlockEntities.ME_MASSIVE_OUTPUT_HATCH_BE.get(), pos, state);
    }


    @Override
    protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof final MassiveOutputHatchBE be) {
            final String status = be.isNetworkReady()
                    ? "§a" + (be.isLinked() ? "Online — Linked to Controller" : "Online — Standalone")
                    : "§cOffline — No ME Network";
            player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("ME Massive Output Hatch: " + status));
            return InteractionResult.SUCCESS;
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }


    @Override
    protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean moved) {
        if (level.getBlockEntity(pos) instanceof final MassiveOutputHatchBE hatch) {
            final BlockPos controllerPos = hatch.getControllerPos();
            if (controllerPos != null && level.getBlockEntity(controllerPos) instanceof final IMultiblockController controller) {
                controller.removePart(pos);
                controller.scanStructure(level);
            }
            hatch.unlinkFromController();
        }
        super.affectNeighborsAfterRemoval(state, level, pos, moved);
    }

    @Override
    protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block changedBlock, final Orientation orientation, final boolean isMoving) {
        super.neighborChanged(state, level, pos, changedBlock, orientation, isMoving);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof final MassiveOutputHatchBE hatch) {
            hatch.refreshGridConnection();
            final BlockPos controllerPos = hatch.getControllerPos();
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
