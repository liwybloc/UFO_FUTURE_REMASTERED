package com.raishxn.ufo.block;

import appeng.api.orientation.IOrientableBlock;
import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import com.raishxn.ufo.block.entity.AbstractSimpleMultiblockControllerBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSimpleMultiblockControllerBlock<T extends AbstractSimpleMultiblockControllerBE> extends DirectionalBlock implements net.minecraft.world.level.block.EntityBlock, IOrientableBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    protected AbstractSimpleMultiblockControllerBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
    }

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.facing();
    }

    @Override
    protected InteractionResult useItemOn(final ItemStack stack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
        if (stack.is(Tags.Items.TOOLS_WRENCH) && player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                level.destroyBlock(pos, true, player);
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
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
            if (entity instanceof final AbstractSimpleMultiblockControllerBE controller) {
                player.openMenu(controller, pos);
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    protected abstract BlockEntityType<T> getBlockEntityType();

    protected abstract T createBlockEntity(BlockPos pos, BlockState state);

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return createBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <E extends BlockEntity> BlockEntityTicker<E> getTicker(final Level level, final BlockState state, final BlockEntityType<E> type) {
        if (level.isClientSide()) {
            return null;
        }
        return type == getBlockEntityType()
                ? (lvl, pos, st, be) -> ((AbstractSimpleMultiblockControllerBE) be).serverTick()
                : null;
    }

    @Override
    protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block changedBlock, final Orientation orientation, final boolean isMoving) {
        super.neighborChanged(state, level, pos, changedBlock, orientation, isMoving);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof final AbstractSimpleMultiblockControllerBE controller) {
            controller.markStructureDirty();
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean moved) {
        if (level.getBlockEntity(pos) instanceof final AbstractSimpleMultiblockControllerBE controller) {
            controller.onControllerBroken();
        }
        super.affectNeighborsAfterRemoval(state, level, pos, moved);
    }
}
