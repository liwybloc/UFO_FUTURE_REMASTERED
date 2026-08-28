package com.raishxn.ufo.block;

import com.mojang.serialization.MapCodec;
import com.raishxn.ufo.block.entity.EntropicAssemblerMatrixBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public final class EntropicAssemblerCasingBlock extends AbstractEntropicMachineBlock<EntropicAssemblerMatrixBE> {
    public static final MapCodec<EntropicAssemblerCasingBlock> CODEC = simpleCodec(EntropicAssemblerCasingBlock::new);

    public EntropicAssemblerCasingBlock() {
        this(BlockBehaviour.Properties.of()
                .strength(30.0f, 1200.0f)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> state.getValue(FORMED) ? 12 : 0));
    }

    public EntropicAssemblerCasingBlock(final BlockBehaviour.Properties properties) {
        super(properties.strength(30.0f, 1200.0f)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> state.getValue(FORMED) ? 12 : 0));
    }

    @Override
    protected @NonNull MapCodec<? extends AbstractEntropicMachineBlock<EntropicAssemblerMatrixBE>> codec() {
        return CODEC;
    }

    @Override
    public EntropicAssemblerMatrixBE newBlockEntity(final BlockPos pos, final BlockState state) {
        return new EntropicAssemblerMatrixBE(pos, state);
    }
}
