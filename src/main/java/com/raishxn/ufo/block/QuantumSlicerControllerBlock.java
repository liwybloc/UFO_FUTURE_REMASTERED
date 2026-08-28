package com.raishxn.ufo.block;

import com.mojang.serialization.MapCodec;
import com.raishxn.ufo.block.entity.QuantumSlicerControllerBE;
import com.raishxn.ufo.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class QuantumSlicerControllerBlock extends AbstractSimpleMultiblockControllerBlock<QuantumSlicerControllerBE> {

    public static final MapCodec<QuantumSlicerControllerBlock> CODEC = simpleCodec(QuantumSlicerControllerBlock::new);

    public QuantumSlicerControllerBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends net.minecraft.world.level.block.DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected BlockEntityType<QuantumSlicerControllerBE> getBlockEntityType() {
        return ModBlockEntities.QUANTUM_SLICER_CONTROLLER_BE.get();
    }

    @Override
    protected QuantumSlicerControllerBE createBlockEntity(final BlockPos pos, final BlockState state) {
        return new QuantumSlicerControllerBE(pos, state);
    }
}
