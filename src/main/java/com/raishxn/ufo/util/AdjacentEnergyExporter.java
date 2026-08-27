package com.raishxn.ufo.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class AdjacentEnergyExporter {
    private AdjacentEnergyExporter() {
    }

    public static void pushEnergy(final Level level, final BlockPos pos, final EnergyHandler source, final int totalBudget, final int perSideBudget) {
        if (level == null || level.isClientSide() || source == null || totalBudget <= 0 || perSideBudget <= 0) {
            return;
        }

        int remaining = totalBudget;
        for (final Direction direction : Direction.values()) {
            if (remaining <= 0) {
                break;
            }

            final BlockPos neighborPos = pos.relative(direction);
            final EnergyHandler target = level.getCapability(Capabilities.Energy.BLOCK, neighborPos, direction.getOpposite());
            if (target == null) {
                continue;
            }

            final int offer = Math.min(perSideBudget, remaining);
            if (offer <= 0) {
                continue;
            }

            final int transferred;
            try (final Transaction transaction = Transaction.openRoot()) {
                transferred = EnergyHandlerUtil.move(source, target, offer, transaction);
                if (transferred > 0) {
                    transaction.commit();
                }
            }
            remaining -= transferred;
        }
    }
}
