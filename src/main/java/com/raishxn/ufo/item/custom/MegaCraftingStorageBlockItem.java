package com.raishxn.ufo.item.custom;

import appeng.block.AEBaseBlockItem;
import com.raishxn.ufo.core.MegaCraftingStorageTier;
import com.raishxn.ufo.util.ColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class MegaCraftingStorageBlockItem extends AEBaseBlockItem {

    private final MegaCraftingStorageTier tier;

    public MegaCraftingStorageBlockItem(final Block block, final Properties props, final MegaCraftingStorageTier tier) {
        super(block, props);
        this.tier = tier;
    }

    public MegaCraftingStorageTier getTier() {
        return this.tier;
    }

    @Override
    public Component getName(final ItemStack stack) {
        final String text = Component.translatable(stack.getItem().getDescriptionId()).getString();
        final ChatFormatting[] colors;

        switch (this.tier) {
            case STORAGE_1B:
                colors = new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.RED, ChatFormatting.GOLD};
                break;
            case STORAGE_50B:
                colors = new ChatFormatting[]{ChatFormatting.DARK_BLUE, ChatFormatting.BLUE};
                break;
            case STORAGE_1T:
                colors = new ChatFormatting[]{ChatFormatting.DARK_AQUA, ChatFormatting.AQUA, ChatFormatting.BLUE};
                break;
            case STORAGE_250T:
                colors = new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE};
                break;
            case STORAGE_1QD:
                colors = new ChatFormatting[]{ChatFormatting.DARK_GREEN, ChatFormatting.GREEN};
                break;
            default:
                return Component.translatable(stack.getItem().getDescriptionId());
        }

        return ColorHelper.getSolidColoredText(text, colors);
    }
}
