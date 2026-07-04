package com.raishxn.ufo.event;

import com.raishxn.ufo.UfoMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@EventBusSubscriber(modid = UfoMod.MOD_ID)
public class HazardHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final TagKey<Item> HAZARDOUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(UfoMod.MOD_ID, "hazardous"));

    public static boolean hasThermalProtection(Player player) {
        if (player.isCreative() || player.isSpectator()) return true;

        boolean hasHelm = false, hasChest = false, hasLegs = false, hasBoots = false;
        for (ItemStack stack : player.getArmorSlots()) {
            Item item = stack.getItem();
            if (item == com.raishxn.ufo.item.ModArmor.THERMAL_RESISTOR_MASK.get() || item == com.raishxn.ufo.item.ModArmor.UFO_HELMET.get()) hasHelm = true;
            if (item == com.raishxn.ufo.item.ModArmor.THERMAL_RESISTOR_CHEST.get() || item == com.raishxn.ufo.item.ModArmor.UFO_CHESTPLATE.get()) hasChest = true;
            if (item == com.raishxn.ufo.item.ModArmor.THERMAL_RESISTOR_PANTS.get() || item == com.raishxn.ufo.item.ModArmor.UFO_LEGGINGS.get()) hasLegs = true;
            if (item == com.raishxn.ufo.item.ModArmor.THERMAL_RESISTOR_BOOTS.get() || item == com.raishxn.ufo.item.ModArmor.UFO_BOOTS.get()) hasBoots = true;
        }
        return hasHelm && hasChest && hasLegs && hasBoots;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide || player.tickCount % 20 != 0) {
            return;
        }

        // DEBUG: Verificar se o evento está a correr
        // LOGGER.info("HazardHandler ticking for {}", player.getName().getString());

        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        if (hasThermalProtection(player)) {
            removeHazardEffects(player);
            return;
        }

        boolean hasHazard = false;

        // Verifica inventário principal
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty() && stack.is(HAZARDOUS)) {
                LOGGER.info("HAZARD DETECTED in inventory: {}", stack.getItem().toString());
                hasHazard = true;
                break;
            }
        }

        // Verifica offhand
        if (!hasHazard && player.getOffhandItem().is(HAZARDOUS)) {
            LOGGER.info("Hazard detected in the offhand : {}", player.getOffhandItem().getItem().toString());
            hasHazard = true;
        }

        if (hasHazard) {
            applyHazardEffects(player);
        }
    }

    private static void applyHazardEffects(Player player) {
        LOGGER.info("Aplicando efeitos de hazard em {}", player.getName().getString());
        player.hurt(player.damageSources().magic(), 2.0f);
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 5, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 5, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 5, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 5, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 5, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 5, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.INFESTED, 100, 5, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 5, false, false));

        if (player instanceof ServerPlayer && player.tickCount % 100 == 0) {
            player.displayClientMessage(Component.literal("WARNING: Dimensional Containment Failed!").withStyle(ChatFormatting.RED), true);
        }
    }

    private static void removeHazardEffects(Player player) {
        player.removeEffect(MobEffects.POISON);
        player.removeEffect(MobEffects.CONFUSION);
        player.removeEffect(MobEffects.BLINDNESS);
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        player.removeEffect(MobEffects.DIG_SLOWDOWN);
        player.removeEffect(MobEffects.HUNGER);
        player.removeEffect(MobEffects.INFESTED);
        player.removeEffect(MobEffects.WEAKNESS);
    }
}
