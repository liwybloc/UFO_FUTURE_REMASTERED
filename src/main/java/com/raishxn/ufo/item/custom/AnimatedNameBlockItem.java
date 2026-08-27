package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.util.ColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class AnimatedNameBlockItem extends BlockItem {

    private final ChatFormatting[] colors;

    public AnimatedNameBlockItem(final Block block, final Properties props, final ChatFormatting... colors) {
        super(block, props);
        this.colors = colors;
    }

    @Override
    public Component getName(final ItemStack stack) {
        return ColorHelper.getSolidColoredText(
                Component.translatable(stack.getItem().getDescriptionId()).getString(),
                this.colors
        );
    }
}
