package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.util.ColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AnimatedNameItem extends Item {

    private final ChatFormatting[] colors;

    public AnimatedNameItem(final Properties props, final ChatFormatting... colors) {
        super(props);
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
