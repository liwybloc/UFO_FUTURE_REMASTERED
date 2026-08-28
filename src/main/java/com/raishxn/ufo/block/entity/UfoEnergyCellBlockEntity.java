package com.raishxn.ufo.block.entity;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.block.networking.EnergyCellBlock;
import appeng.blockentity.networking.EnergyCellBlockEntity;
import com.raishxn.ufo.util.AdjacentEnergyExporter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class UfoEnergyCellBlockEntity extends EnergyCellBlockEntity {
    private final SnapshotJournal<Double> energyJournal = new SnapshotJournal<>() {
        @Override
        protected Double createSnapshot() {
            return UfoEnergyCellBlockEntity.this.getAECurrentPower();
        }

        @Override
        protected void revertToSnapshot(final Double snapshot) {
            final double current = UfoEnergyCellBlockEntity.this.getAECurrentPower();
            if (current < snapshot) {
                UfoEnergyCellBlockEntity.this.injectAEPower(snapshot - current, Actionable.MODULATE);
            } else if (current > snapshot) {
                UfoEnergyCellBlockEntity.this.extractAEPower(
                        current - snapshot, Actionable.MODULATE, PowerMultiplier.ONE);
            }
        }
    };

    private final EnergyHandler exposedEnergy = new EnergyHandler() {
        @Override
        public long getAmountAsLong() {
            return Math.min(Integer.MAX_VALUE,
                    (long) Math.floor(PowerMultiplier.CONFIG.divide(UfoEnergyCellBlockEntity.this.getAECurrentPower())));
        }

        @Override
        public long getCapacityAsLong() {
            return Math.min(Integer.MAX_VALUE,
                    (long) Math.floor(PowerMultiplier.CONFIG.divide(UfoEnergyCellBlockEntity.this.getAEMaxPower())));
        }

        @Override
        public int insert(final int amount, final TransactionContext transaction) {
            TransferPreconditions.checkNonNegative(amount);
            return 0;
        }

        @Override
        public int extract(final int amount, final TransactionContext transaction) {
            TransferPreconditions.checkNonNegative(amount);
            if (amount == 0) {
                return 0;
            }

            energyJournal.updateSnapshots(transaction);
            return (int) Math.min(amount,
                    UfoEnergyCellBlockEntity.this.extractAEPower(
                            amount, Actionable.MODULATE, PowerMultiplier.CONFIG));
        }
    };

    public UfoEnergyCellBlockEntity(final BlockEntityType<?> blockEntityType, final BlockPos pos, final BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    public void serverTick() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        final int totalBudget = getForgeExportRate();
        AdjacentEnergyExporter.pushEnergy(this.level, this.worldPosition, this.exposedEnergy, totalBudget, totalBudget);
    }

    public EnergyHandler getExposedEnergy() {
        return this.exposedEnergy;
    }

    private int getForgeExportRate() {
        if (!(this.getBlockState().getBlock() instanceof final EnergyCellBlock energyCellBlock)) {
            return 0;
        }

        return (int) Math.max(1,
                Math.min(Integer.MAX_VALUE, Math.floor(PowerMultiplier.CONFIG.divide(energyCellBlock.getChargeRate()))));
    }
}
