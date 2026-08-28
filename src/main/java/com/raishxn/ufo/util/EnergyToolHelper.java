package com.raishxn.ufo.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Optional;

public final class EnergyToolHelper {
    public static EnergyHandler getEnergyHandler(final ItemStack stack) {
        return ItemAccess.forStack(stack).getCapability(Capabilities.Energy.ITEM);
    }

    public static int extractEnergy(final EnergyHandler handler, final int amount, final boolean simulate) {
        if (handler == null || amount <= 0) {
            return 0;
        }
        try (final Transaction transaction = Transaction.openRoot()) {
            final int extracted = handler.extract(amount, transaction);
            if (!simulate) {
                transaction.commit();
            }
            return extracted;
        }
    }

    public static int extractEnergy(final ItemStack stack, final int amount, final boolean simulate) {
        return stack.isEmpty() ? 0 : extractEnergy(getEnergyHandler(stack), amount, simulate);
    }

    public static boolean hasEnoughEnergy(final ItemStack stack, final int amount, final boolean simulate) {
        return extractEnergy(stack, amount, simulate) >= amount;
    }

    public static boolean isBarVisible(final ItemStack stack) {
        return Optional.ofNullable(getEnergyHandler(stack)).map(storage -> storage.getCapacityAsLong() > 0).orElse(false);
    }

    public static int getBarWidth(final ItemStack stack) {
        return Optional.ofNullable(getEnergyHandler(stack)).map(storage -> {
            if (storage.getCapacityAsLong() == 0) return 0;
            return (int) Math.round(13.0 * storage.getAmountAsLong() / storage.getCapacityAsLong());
        }).orElse(0);
    }

    public static int getBarColor(final ItemStack stack) {
        return 0x0066FF;
    }
}
