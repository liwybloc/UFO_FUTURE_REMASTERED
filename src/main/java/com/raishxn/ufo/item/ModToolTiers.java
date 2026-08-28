package com.raishxn.ufo.item;

import com.raishxn.ufo.util.ModTags;
import net.minecraft.world.item.ToolMaterial;

public final class ModToolTiers {
    public static final int UFO_MINING_LEVEL = 10;

    public static final ToolMaterial UFO = new ToolMaterial(
            ModTags.Blocks.INCORRECT_FOR_UFO_TOOL,
            10000,
            10f,
            10f,
            50,
            ModTags.Items.INGREDIENTS_UFO
    );
}
