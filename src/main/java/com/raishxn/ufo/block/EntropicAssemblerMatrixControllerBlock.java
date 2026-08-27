package com.raishxn.ufo.block;

import com.raishxn.ufo.block.entity.AbstractEntropicMachineBE;
import com.raishxn.ufo.block.entity.EntropicAssemblerMatrixBE;
import com.raishxn.ufo.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EntropicAssemblerMatrixControllerBlock extends EntropicMachineShellBlock implements net.minecraft.world.level.block.EntityBlock {
    public EntropicAssemblerMatrixControllerBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public EntropicAssemblerMatrixBE newBlockEntity(final BlockPos pos, final BlockState state) {
        return new EntropicAssemblerMatrixBE(pos, state);
    }

    @Nullable
    @Override
    public <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityTicker<T> getTicker(final Level level, final BlockState state, final BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return type == ModBlockEntities.ENTROPIC_ASSEMBLER_MATRIX_BE.get()
                ? (lvl, pos, blockState, be) -> ((AbstractEntropicMachineBE) be).serverTick()
                : null;
    }
}
