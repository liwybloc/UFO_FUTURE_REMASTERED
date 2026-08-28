package com.raishxn.ufo.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.item.ModArmorMaterials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import java.util.List;


public final class ThermalResistorExosuitItem extends ArmorItem implements IThermalArmor {

    private final Multimap<Holder<Attribute>, AttributeModifier> customAttributeModifiers;

    public ThermalResistorExosuitItem(final ArmorItem.Type type, final Properties properties) {
        super(ModArmorMaterials.UFO_ARMOR, type, properties.fireResistant());

        final ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(
                Attributes.MINING_EFFICIENCY,
                new AttributeModifier(
                        UfoMod.id("thermal_mining_speed"),
                        0.15,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                )
        );

        this.customAttributeModifiers = builder.build();
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(final ItemStack stack) {
        final ItemAttributeModifiers defaultModifiers = super.getDefaultAttributeModifiers(stack);

        final ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        for (final ItemAttributeModifiers.Entry entry : defaultModifiers.modifiers()) {
            builder.add(entry.attribute(), entry.modifier(), entry.slot());
        }

        this.customAttributeModifiers.forEach((attributeHolder, modifier) -> {
            builder.add(attributeHolder, modifier, EquipmentSlotGroup.bySlot(this.type.getSlot()));
        });

        return builder.build();
    }

    @Override
    public void inventoryTick(final ItemStack stack, final Level level, final Entity entity, final int slot, final boolean isSelected) {
        super.inventoryTick(stack, level, entity, slot, isSelected);

        if (level.isClientSide() || !(entity instanceof final Player player)) {
            return;
        }
        if (player.getItemBySlot(this.type.getSlot()) != stack) {
            return;
        }

        if (player.isOnFire()) {
            player.clearFire();
        }

        if (hasFullSet(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 220, 1, false, false, true));
        }
    }
    public void appendHoverText(final ItemStack stack, final TooltipContext context, final List<Component> tooltip, final TooltipFlag flag) {
        tooltip.add(Component.literal("§6Thermal Resistor Exosuit"));
        tooltip.add(Component.literal("§7Projetada para resistir ao impossível."));
        tooltip.add(Component.literal("§8Camadas de matéria densa dissipam calor extremo."));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§b+100% Resistência Térmica"));
        tooltip.add(Component.literal("§cImune ao Calor do DMA"));
        tooltip.add(Component.literal("§9Refrigeração Integrada"));
        tooltip.add(Component.empty());

        if (net.minecraft.client.Minecraft.getInstance().hasShiftDown()) {
            final Player player = Minecraft.getInstance().player;
            if (player != null && hasFullSet(player)) {
                tooltip.add(Component.literal("§5[Conjunto Completo Ativo]"));
                tooltip.add(Component.literal("§bImune a Fogo e Lava"));
                tooltip.add(Component.literal("§bImune a Calor Industrial"));
                tooltip.add(Component.literal("§bRemoção Instantânea de Queimaduras"));
                tooltip.add(Component.literal("§bEstabilidade Térmica Total"));
            } else {
                tooltip.add(Component.literal("§8Equipe o conjunto completo para bônus."));
            }
        } else {
            tooltip.add(Component.literal("§8Pressione <SHIFT> para detalhes."));
        }

    }

    private boolean hasFullSet(final Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof IThermalArmor &&
                player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof IThermalArmor &&
                player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof IThermalArmor &&
                player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof IThermalArmor;
    }
}
