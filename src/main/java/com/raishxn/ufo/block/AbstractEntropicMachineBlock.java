package com.raishxn.ufo.block;

import appeng.block.AEBaseEntityBlock;
import com.mojang.serialization.MapCodec;
import com.raishxn.ufo.api.multiblock.EntropicMachineLocator;
import com.raishxn.ufo.api.multiblock.IEntropicMachineController;
import com.raishxn.ufo.block.entity.AbstractEntropicMachineBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public abstract class AbstractEntropicMachineBlock<T extends AbstractEntropicMachineBE> extends AEBaseEntityBlock<T> {
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    protected AbstractEntropicMachineBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FORMED, false).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FORMED, POWERED);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        final IEntropicMachineController controller = EntropicMachineLocator.findController(level, pos);
        if (!(controller instanceof final AbstractEntropicMachineBE be)) {
            return InteractionResult.PASS;
        }

        if (!controller.isAssembled()) {
            return InteractionResult.PASS;
        }

        if (!controller.isNetworkConnected()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            player.openMenu(be, pos);
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void neighborChanged(final @NonNull BlockState state, final @NonNull Level level, final @NonNull BlockPos pos, final @NonNull Block changedBlock, final Orientation orientation, final boolean isMoving) {
        super.neighborChanged(state, level, pos, changedBlock, orientation, isMoving);
        if (!level.isClientSide()) {
            EntropicMachineLocator.markNearbyDirty(level, pos);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(final @NonNull BlockState state, final @NonNull ServerLevel level, final @NonNull BlockPos pos, final boolean isMoving) {
        EntropicMachineLocator.markNearbyDirty(level, pos);
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
    }

    @Nullable
    @Override
    public <B extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityTicker<B> getTicker(final Level level, final BlockState state, final BlockEntityType<B> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, be) -> {
            if (be instanceof final AbstractEntropicMachineBE machine) {
                machine.serverTick();
            }
        };
    }

    @Override
    protected abstract @NonNull MapCodec<? extends AEBaseEntityBlock<T>> codec();
}
