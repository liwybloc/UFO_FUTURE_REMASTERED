package com.raishxn.ufo.block;

import com.mojang.serialization.MapCodec;
import com.raishxn.ufo.block.entity.QuantumProcessorAssemblerControllerBE;
import com.raishxn.ufo.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class QuantumProcessorAssemblerControllerBlock extends AbstractSimpleMultiblockControllerBlock<QuantumProcessorAssemblerControllerBE> {

    public static final MapCodec<QuantumProcessorAssemblerControllerBlock> CODEC = simpleCodec(QuantumProcessorAssemblerControllerBlock::new);

    public QuantumProcessorAssemblerControllerBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends net.minecraft.world.level.block.DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected BlockEntityType<QuantumProcessorAssemblerControllerBE> getBlockEntityType() {
        return ModBlockEntities.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER_BE.get();
    }

    @Override
    protected QuantumProcessorAssemblerControllerBE createBlockEntity(final BlockPos pos, final BlockState state) {
        return new QuantumProcessorAssemblerControllerBE(pos, state);
    }
}
