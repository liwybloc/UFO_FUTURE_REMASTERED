package com.raishxn.ufo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import com.raishxn.ufo.block.entity.StellarNexusControllerBE;
import com.raishxn.ufo.init.ModBlockEntities;

/**
 * The central controller block for the Stellar Nexus multiblock.
 * <p>
 * Handles directional placement and the ASSEMBLED state property which
 * controls the visual appearance (active overlay vs inactive).
 */
public class StellarNexusControllerBlock extends DirectionalBlock implements net.minecraft.world.level.block.EntityBlock {

    public static final MapCodec<StellarNexusControllerBlock> CODEC = simpleCodec(StellarNexusControllerBlock::new);
    public static final BooleanProperty ASSEMBLED = BooleanProperty.create("assembled");

    public StellarNexusControllerBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ASSEMBLED, false));
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            if (level.isClientSide()) {
                com.raishxn.ufo.client.GhostHologramRenderer.toggleHologram(pos, state.getValue(FACING));
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        if (!level.isClientSide()) {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof final StellarNexusControllerBE controller) {
                player.openMenu(controller, pos);
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ASSEMBLED);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new StellarNexusControllerBE(ModBlockEntities.STELLAR_NEXUS_CONTROLLER_BE.get(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level, final BlockState state, final BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.STELLAR_NEXUS_CONTROLLER_BE.get()
                ? (lvl, pos, st, be) -> ((StellarNexusControllerBE) be).serverTick()
                : null;
    }




    @Override
    protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean moved) {
        if (level.getBlockEntity(pos) instanceof final StellarNexusControllerBE be) {
            be.onControllerBroken();
        }
        super.affectNeighborsAfterRemoval(state, level, pos, moved);
    }

    @Override
    protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block changedBlock, final Orientation orientation, final boolean isMoving) {
        super.neighborChanged(state, level, pos, changedBlock, orientation, isMoving);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof final StellarNexusControllerBE be) {
            be.markStructureDirty();
        }
    }
}
