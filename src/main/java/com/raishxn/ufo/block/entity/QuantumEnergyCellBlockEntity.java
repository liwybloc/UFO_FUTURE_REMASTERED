package com.raishxn.ufo.block.entity;

import appeng.blockentity.networking.CreativeEnergyCellBlockEntity;
import com.raishxn.ufo.util.AdjacentEnergyExporter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.InfiniteEnergyHandler;

public final class QuantumEnergyCellBlockEntity extends CreativeEnergyCellBlockEntity {
    private static final int CREATIVE_EXPORT_RATE = Integer.MAX_VALUE;

    private final EnergyHandler exposedEnergy = InfiniteEnergyHandler.INSTANCE;

    public QuantumEnergyCellBlockEntity(final BlockEntityType<?> blockEntityType, final BlockPos pos, final BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    public void serverTick() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        AdjacentEnergyExporter.pushEnergy(this.level, this.worldPosition, this.exposedEnergy, CREATIVE_EXPORT_RATE, CREATIVE_EXPORT_RATE);
    }

    public EnergyHandler getExposedEnergy() {
        return this.exposedEnergy;
    }
}
