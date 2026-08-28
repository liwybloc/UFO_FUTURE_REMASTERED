package com.raishxn.ufo.compat.appflux;

import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;


import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.upgrades.Upgrades;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class AppliedFluxPlugin {










    public static double rechargeAeStorageItem(
            final IGrid grid, double neededPower, final Player player, final ItemStack stack, final IAEItemPowerStorage aePowerStorage) {
        try {
            final var storage = grid.getStorageService();

            final var mult = PowerMultiplier.CONFIG;
            final var neededFePower = mult.divide(neededPower);

            final var extracted = mult.multiply(storage.getInventory()
                    .extract(
                            FluxKey.of(EnergyType.FE),
                            (long) neededFePower,
                            Actionable.MODULATE,
                            IActionSource.ofPlayer(player)));

            final var remainder = aePowerStorage.injectAEPower(stack, extracted, Actionable.MODULATE);
            storage.getInventory()
                    .insert(
                            FluxKey.of(EnergyType.FE),
                            (long) mult.divide(remainder),
                            Actionable.MODULATE,
                            IActionSource.ofPlayer(player));

            neededPower -= extracted - remainder;
        } catch (final Throwable ignored) {
        }
        return neededPower;
    }

    public static void rechargeEnergyStorage(final IGrid grid, final int afRate, final IActionSource source, final EnergyHandler cap) {
        try {
            final var storage = grid.getStorageService();
            final var extracted =
                    storage.getInventory().extract(FluxKey.of(EnergyType.FE), afRate, Actionable.MODULATE, source);
            final int inserted;
            try (final Transaction transaction = Transaction.openRoot()) {
                inserted = cap.insert((int) Math.min(Integer.MAX_VALUE, extracted), transaction);
                transaction.commit();
            }
            storage.getInventory().insert(FluxKey.of(EnergyType.FE), extracted - inserted, Actionable.MODULATE, source);
        } catch (final Throwable ignored) {
        }
    }
}
