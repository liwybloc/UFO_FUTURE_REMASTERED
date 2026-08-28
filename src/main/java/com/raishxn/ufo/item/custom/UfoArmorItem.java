package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

import java.util.List;
import java.util.function.Consumer;

public final class UfoArmorItem extends Item implements IEnergyTool {

    private static final int ENERGY_COST_PER_SECOND = 400; // 20 RF/tick * 20 ticks
    private static final int DRAIN_INTERVAL = 20; // Drain every 20 ticks (1 second) to avoid triggering equip sound
    private static final Identifier ARMOR_HEALTH_MODIFIER_ID = Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, "armor_health_boost");
    private final ArmorType type;

    public UfoArmorItem(final ArmorMaterial material, final ArmorType type, final Properties properties) {
        super(properties.humanoidArmor(material, type));
        this.type = type;
    }

    @Override
    public Component getName(final ItemStack stack) {
        return IEnergyTool.super.getName(stack);
    }

    @Override
    public void inventoryTick(final ItemStack stack, final ServerLevel level, final Entity entity, final EquipmentSlot slot) {
        if (entity instanceof final Player player) {
            if (this.type == ArmorType.CHESTPLATE) {
                final ItemStack equippedChestplate = player.getItemBySlot(EquipmentSlot.CHEST);

                if (equippedChestplate.getItem() instanceof UfoArmorItem) {
                    if (hasFullSuitOfArmorOn(player) && hasEnoughEnergy(player)) {
                        applyAllEffects(player);
                        if (level.getGameTime() % DRAIN_INTERVAL == 0) {
                            drainEnergy(player);
                        }
                    } else {
                        removeAllEffects(player);
                    }
                } else {
                    removeAllEffects(player);
                }
            }
        }
        super.inventoryTick(stack, level, entity, slot);
    }

    private void drainEnergy(final Player player) {
        for (final ItemStack armorStack : armorItems(player)) {
            if (armorStack.getItem() instanceof UfoArmorItem) {
                final int currentEnergy = armorStack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
                final int newEnergy = Math.max(0, currentEnergy - ENERGY_COST_PER_SECOND);
                if (newEnergy != currentEnergy) {
                    armorStack.set(ModDataComponents.ENERGY.get(), newEnergy);
                }
            }
        }
    }

    private boolean hasEnoughEnergy(final Player player) {
        for (final ItemStack armorStack : armorItems(player)) {
            if (armorStack.getItem() instanceof UfoArmorItem) {
                final int currentEnergy = armorStack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
                if (currentEnergy < ENERGY_COST_PER_SECOND) {
                    return false;
                }
            }
        }
        return true;
    }

    private void applyAllEffects(final Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 9, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0, false, false, true));

        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        final net.minecraft.world.entity.ai.attributes.AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null && healthAttribute.getModifier(ARMOR_HEALTH_MODIFIER_ID) == null) {
            final AttributeModifier modifier = new AttributeModifier(
                    ARMOR_HEALTH_MODIFIER_ID,
                    40.0,
                    AttributeModifier.Operation.ADD_VALUE
            );
            healthAttribute.addPermanentModifier(modifier);
        }
    }

    private void removeAllEffects(final Player player) {
        final net.minecraft.world.entity.ai.attributes.AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null && healthAttribute.getModifier(ARMOR_HEALTH_MODIFIER_ID) != null) {
            healthAttribute.removeModifier(ARMOR_HEALTH_MODIFIER_ID);
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }

        if (!player.getAbilities().instabuild && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    private boolean hasFullSuitOfArmorOn(final Player player) {
        for (final ItemStack armorStack : armorItems(player)) {
            if (!(armorStack.getItem() instanceof UfoArmorItem)) {
                return false;
            }
        }
        return true;
    }

    private static java.util.List<ItemStack> armorItems(final Player player) {
        return java.util.List.of(
                player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD),
                player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST),
                player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS),
                player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET));
    }

    @Override
    public boolean isBarVisible(final ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarWidth(final ItemStack pStack) {
        return EnergyToolHelper.getBarWidth(pStack);
    }

    @Override
    public int getBarColor(final ItemStack pStack) {
        return EnergyToolHelper.getBarColor(pStack);
    }

    @Override
    public int getEnergyPerUse() {
        return 0; // O consumo de energia da armadura é por tick, não por uso.
    }
    public void appendHoverText(final ItemStack pStack, final Item.TooltipContext pContext,
                                final TooltipDisplay display, final Consumer<Component> tooltip,
                                final TooltipFlag pTooltipFlag) {
        if (net.minecraft.client.Minecraft.getInstance().hasShiftDown()) {
            final EnergyHandler energyStorage = com.raishxn.ufo.util.EnergyToolHelper.getEnergyHandler(pStack);
            if (energyStorage != null) {
                final String energyText = String.format("%,d / %,d RF", energyStorage.getAmountAsInt(), energyStorage.getCapacityAsInt());
                tooltip.accept(Component.literal(energyText).withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltip.accept(Component.translatable("tooltip.ufo.press_shift").withStyle(ChatFormatting.AQUA));
        }
    }
}
