package com.raishxn.ufo.block.custom;

import appeng.block.AEBaseBlockItem;
import com.raishxn.ufo.core.MegaCoProcessorTier;
import com.raishxn.ufo.util.ColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class MegaCoProcessorBlockItem extends AEBaseBlockItem {

    private final MegaCoProcessorTier tier;

    public MegaCoProcessorBlockItem(final Block block, final Properties props, final MegaCoProcessorTier tier) {
        super(block, props);
        this.tier = tier;
    }

    public MegaCoProcessorTier getTier() {
        return tier;
    }

    @Override
    public Component getName(final ItemStack stack) {
        final String text = Component.translatable(stack.getItem().getDescriptionId()).getString();
        final ChatFormatting[] colors;

        switch (this.tier) {
            case COPROCESSOR_50M:
                colors = new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.RED, ChatFormatting.GOLD};
                break;
            case COPROCESSOR_150M:
                colors = new ChatFormatting[]{ChatFormatting.DARK_BLUE, ChatFormatting.BLUE};
                break;
            case COPROCESSOR_300M:
                colors = new ChatFormatting[]{ChatFormatting.DARK_AQUA, ChatFormatting.AQUA, ChatFormatting.BLUE};
                break;
            case COPROCESSOR_750M:
                colors = new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE};
                break;
            case COPROCESSOR_2B:
                colors = new ChatFormatting[]{ChatFormatting.DARK_GREEN, ChatFormatting.GREEN};
                break;
            default:
                return Component.translatable(stack.getItem().getDescriptionId());
        }

        return ColorHelper.getSolidColoredText(text, colors);
    }
}
