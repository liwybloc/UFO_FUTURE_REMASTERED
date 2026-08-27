package com.raishxn.ufo.api.multiblock;

public final class MultiblockTierScaling {
    private MultiblockTierScaling() {
    }

    public static boolean canRunRecipe(final int machineTier, final int recipeTier) {
        return machineTier >= recipeTier;
    }

    public static int adjustedTime(final int baseTime, final int machineTier, final int recipeTier) {
        final int delta = Math.max(0, machineTier - recipeTier);
        return Math.max(1, (int) Math.ceil(baseTime / Math.pow(2, delta)));
    }

    public static long adjustedEnergy(final long baseEnergy, final int machineTier, final int recipeTier) {
        final int delta = Math.max(0, machineTier - recipeTier);
        return Math.max(1L, (long) Math.ceil(baseEnergy * Math.pow(0.75D, delta)));
    }
}
