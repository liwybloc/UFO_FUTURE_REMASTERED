package com.raishxn.ufo.block;

import com.mojang.serialization.MapCodec;
import com.raishxn.ufo.block.entity.QuantumCryoforgeControllerBE;
import com.raishxn.ufo.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class QuantumCryoforgeControllerBlock extends AbstractSimpleMultiblockControllerBlock<QuantumCryoforgeControllerBE> {

    public static final MapCodec<QuantumCryoforgeControllerBlock> CODEC = simpleCodec(QuantumCryoforgeControllerBlock::new);

    public QuantumCryoforgeControllerBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends net.minecraft.world.level.block.DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected BlockEntityType<QuantumCryoforgeControllerBE> getBlockEntityType() {
        return ModBlockEntities.QUANTUM_CRYOFORGE_CONTROLLER_BE.get();
    }

    @Override
    protected QuantumCryoforgeControllerBE createBlockEntity(final BlockPos pos, final BlockState state) {
        return new QuantumCryoforgeControllerBE(pos, state);
    }
}
