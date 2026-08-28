package com.raishxn.ufo.util;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class UfoPersistentEnergyStorage implements EnergyHandler {
    private final ItemStack parent;
    private final DataComponentType<Integer> componentType;
    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;
    private int energy;
    private final SnapshotJournal<Integer> journal = new SnapshotJournal<>() {
        @Override
        protected Integer createSnapshot() {
            return energy;
        }

        @Override
        protected void revertToSnapshot(final Integer snapshot) {
            energy = snapshot;
        }

        @Override
        protected void onRootCommit(final Integer originalState) {
            save();
        }
    };

    public UfoPersistentEnergyStorage(final ItemStack parent, final DataComponentType<Integer> componentType,
                                      final int capacity, final int maxReceive, final int maxExtract) {
        this.parent = parent;
        this.componentType = componentType;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = this.parent.getOrDefault(this.componentType, 0);
    }

    private void save() {
        this.parent.set(this.componentType, this.energy);
    }

    @Override
    public long getAmountAsLong() {
        return this.energy;
    }

    @Override
    public long getCapacityAsLong() {
        return this.capacity;
    }

    @Override
    public int insert(final int amount, final TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);
        final int inserted = Math.min(Math.min(amount, this.maxReceive), this.capacity - this.energy);
        if (inserted > 0) {
            this.journal.updateSnapshots(transaction);
            this.energy += inserted;
        }
        return inserted;
    }

    @Override
    public int extract(final int amount, final TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);
        final int extracted = Math.min(Math.min(amount, this.maxExtract), this.energy);
        if (extracted > 0) {
            this.journal.updateSnapshots(transaction);
            this.energy -= extracted;
        }
        return extracted;
    }

    public void setEnergy(final int energy) {
        this.energy = Math.clamp(energy, 0, this.capacity);
        save();
    }
}
