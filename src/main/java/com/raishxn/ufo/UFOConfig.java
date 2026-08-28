package com.raishxn.ufo;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class UFOConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.DoubleValue INFINITY_CELL_ENERGY = BUILDER
            .comment("ME Infinity Cell idle energy cost (unit: AE/t)")
            .defineInRange("item.infinity_cell_energy_cost", 8.0, 0.1, 64.0);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static double infCellCost;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            infCellCost = INFINITY_CELL_ENERGY.get();
        }
    }
}
