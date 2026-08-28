package com.raishxn.ufo.item.custom.cell;

import appeng.api.stacks.AEKeyType;
import appeng.items.storage.StorageTier;
import com.raishxn.ufo.util.ColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class AnimatedModCellItem extends ModCellItem {

    private final String baseNameKey;
    private final String tierNameKey;
    private final ChatFormatting[] baseNameColors; // <-- ADICIONADO
    private final ChatFormatting[] tierColors;

    public AnimatedModCellItem(final StorageTier tier, final int maxTypes, final AEKeyType keyType, final String baseNameKey, final String tierNameKey, final ChatFormatting[] baseNameColors, final ChatFormatting... tierColors) {
        super(tier, maxTypes, keyType);
        this.baseNameKey = baseNameKey;
        this.tierNameKey = tierNameKey;
        this.baseNameColors = baseNameColors; // <-- ADICIONADO
        this.tierColors = tierColors;
    }

    @Override
    public Component getName(final ItemStack stack) {
        final Component baseName = ColorHelper.getAnimatedColoredText(Component.translatable(this.baseNameKey).getString(), this.baseNameColors); // <-- CORRIGIDO
        final Component tierName = ColorHelper.getAnimatedColoredText(Component.translatable(this.tierNameKey).getString(), this.tierColors);

        return baseName.copy().append(" - ").append(tierName);
    }
}