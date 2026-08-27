package com.raishxn.ufo.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Consumer;

public class AstralNexusArmorItem extends Item {

    public AstralNexusArmorItem(final ArmorMaterial material, final ArmorType type, final Item.Properties properties) {
        super(properties.humanoidArmor(material, type).stacksTo(1));
    }
    public void appendHoverText(final ItemStack stack, final Item.TooltipContext context,
                                final TooltipDisplay display, final Consumer<Component> tooltip,
                                final TooltipFlag flag) {
        tooltip.accept(Component.literal("Infinite Armor").withStyle(ChatFormatting.GOLD));
        tooltip.accept(Component.literal("Absolute immunity to damage and death").withStyle(ChatFormatting.RED));
        tooltip.accept(Component.literal("Reflects damage at 1,000,000x").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.accept(Component.literal("Night Vision, Water Breathing, Step Assist").withStyle(ChatFormatting.AQUA));
        tooltip.accept(Component.literal("Creative Flight").withStyle(ChatFormatting.BLUE));
    }
}
