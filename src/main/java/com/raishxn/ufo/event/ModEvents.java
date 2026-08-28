package com.raishxn.ufo.event;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.item.custom.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents; // Novo Import
import net.minecraft.core.registries.Registries; // Novo Import
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantments; // Novo Import
import net.minecraft.world.item.enchantment.ItemEnchantments; // Novo Import
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = UfoMod.MOD_ID)
public final class ModEvents {

    @SubscribeEvent
    public static void onBlockBreak(final BreakBlockEvent event) {
        final Player player = event.getPlayer();
        if (player == null || player.level().isClientSide()) return;
        final ItemStack stack = player.getMainHandItem();

        if (stack.getItem() instanceof UfoEnergyPickaxeItem || stack.getItem() instanceof HammerItem) {

            if (stack.getItem() instanceof UfoEnergyPickaxeItem) {
                final int currentFortune = stack.getOrDefault(ModDataComponents.PROGRESSIVE_FORTUNE.get(), 0);

                if (currentFortune < 300) {
                    final int newFortune = currentFortune + 1;

                    stack.set(ModDataComponents.PROGRESSIVE_FORTUNE.get(), newFortune);

                    final var registry = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                    final var fortuneEnchant = registry.getOrThrow(Enchantments.FORTUNE);

                    final ItemEnchantments.Mutable enchantmentsMap = new ItemEnchantments.Mutable(stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
                    enchantmentsMap.set(fortuneEnchant, newFortune);

                    stack.set(DataComponents.ENCHANTMENTS, enchantmentsMap.toImmutable());
                }
            }

            if (stack.getOrDefault(ModDataComponents.AUTO_SMELT.get(), false)) {
                final ServerLevel level = (ServerLevel) player.level();
                final BlockPos pos = event.getPos();
                final BlockState state = event.getState();

                final List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos), player, stack);
                boolean smelledAny = false;

                for (final ItemStack drop : drops) {
                    final Optional<net.minecraft.world.item.crafting.RecipeHolder<SmeltingRecipe>> recipe = level.recipeAccess()
                            .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(drop), level);

                    if (recipe.isPresent()) {
                        final ItemStack result = recipe.get().value().assemble(new SingleRecipeInput(drop)).copy();
                        result.setCount(drop.getCount());

                        spawnItem(level, pos, result);
                        smelledAny = true;
                    } else {
                        spawnItem(level, pos, drop);
                    }
                }

                if (smelledAny) {
                    consumeEnergyDirect(stack, 50);

                    event.setCanceled(true);

                    level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private static void spawnItem(final Level level, final BlockPos pos, final ItemStack stack) {
        final ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        level.addFreshEntity(entity);
    }


    @SubscribeEvent
    public static void onPlayerAttack(final LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof final Player player) {
            final ItemStack stack = player.getMainHandItem();

            if (stack.getItem() instanceof UfoEnergySwordItem || stack.getItem() instanceof UfoEnergyGreatswordItem) {
                final int kills = stack.getOrDefault(ModDataComponents.KILL_COUNT.get(), 0);
                float extraDmg = kills * 2.0f;

                final long lastHit = stack.getOrDefault(ModDataComponents.LAST_HIT_TIME.get(), 0L);
                int combo = stack.getOrDefault(ModDataComponents.COMBO_COUNT.get(), 0);
                final long time = player.level().getGameTime();

                if (time - lastHit < 60) {
                    combo++;
                    if (combo >= 5) {
                        extraDmg *= 1.5f;
                        player.sendOverlayMessage(Component.literal("COMBO!").withStyle(ChatFormatting.GOLD));
                        combo = 0;
                    }
                } else {
                    combo = 1;
                }
                stack.set(ModDataComponents.COMBO_COUNT.get(), combo);
                stack.set(ModDataComponents.LAST_HIT_TIME.get(), time);

                event.setAmount(event.getAmount() + extraDmg);
                if (stack.getItem() instanceof UfoEnergySwordItem) {
                    event.getEntity().addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2));
                }
            }

            if (stack.getItem() instanceof UfoEnergyGreatswordItem) {
                final float maxHp = player.getMaxHealth();
                final float currentHp = player.getHealth();
                final float percent = currentHp / maxHp;

                float multiplier = 1.0f;
                if (percent <= 0.10f) multiplier = 1.5f;
                else if (percent <= 0.30f) multiplier = 1.2f;

                event.setAmount(event.getAmount() * multiplier);
            }

            if (stack.getItem() instanceof UfoEnergyAxeItem) {
                final float targetArmor = event.getEntity().getArmorValue();
                if (targetArmor > 0) {
                    event.setAmount(event.getAmount() + (targetArmor * 0.2f));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDefend(final LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof final ServerPlayer player) {
            if (!isFullUfoArmor(player)) return;

            if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD) && event.getAmount() < Float.MAX_VALUE) {
                if (consumeArmorEnergyDirect(player, 50000)) {
                    event.setCanceled(true);
                    teleportToSafety(player);
                    return;
                }
            }

            if (isExtremeDamage(event.getSource(), event.getAmount())) {

                if (consumeArmorEnergyDirect(player, 100000)) {
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth());
                    player.sendSystemMessage(Component.literal("Anti-Death Protocol Activated!").withStyle(ChatFormatting.GOLD));
                    return;
                }
            }

            if (player.getHealth() - event.getAmount() <= 4.0f) {
                if (consumeArmorEnergyDirect(player, 10000)) {
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth() / 2);
                    teleportToSafety(player);
                    player.sendSystemMessage(Component.literal("Emergency Evacuation!").withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(final LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof final Player attacker) {
            final ItemStack stack = attacker.getMainHandItem();
            if (stack.getItem() instanceof UfoEnergySwordItem || stack.getItem() instanceof UfoEnergyGreatswordItem) {
                if (event.getEntity() instanceof Enemy) {
                    final int kills = stack.getOrDefault(ModDataComponents.KILL_COUNT.get(), 0);
                    stack.set(ModDataComponents.KILL_COUNT.get(), kills + 1);
                } else {
                    final int kills = stack.getOrDefault(ModDataComponents.KILL_COUNT.get(), 0);
                    final int newKills = Math.max(0, kills - 1);
                    stack.set(ModDataComponents.KILL_COUNT.get(), newKills);
                    if (kills > newKills) {
                        attacker.sendSystemMessage(Component.literal("The sword's power weakens after a non-hostile kill.").withStyle(ChatFormatting.RED));
                    }
                }
            }
        }

        if (event.getEntity() instanceof final ServerPlayer player) {
            if (isFullUfoArmor(player)) {
                if (isExtremeDamage(event.getSource(), Float.MAX_VALUE) || consumeArmorEnergyDirect(player, 200000)) {
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth());
                    player.removeAllEffects();
                    player.sendSystemMessage(Component.literal("Lazarus Protocol Activated: Death Cancelled.").withStyle(ChatFormatting.GOLD));
                }
            }
        }
    }


    private static boolean isFullUfoArmor(final Player player) {
        for (final ItemStack stack : armorItems(player)) {
            if (!(stack.getItem() instanceof UfoArmorItem)) return false;
        }
        return true;
    }

    private static boolean isExtremeDamage(final DamageSource source, final float amount) {
        final String messageId = source.getMsgId();
        return source.is(DamageTypes.GENERIC_KILL)
                || amount >= Float.MAX_VALUE
                || (source.is(DamageTypes.FELL_OUT_OF_WORLD) && amount > 10000f)
                || messageId.contains("chaos")
                || messageId.contains("guardian");
    }

    private static boolean consumeArmorEnergyDirect(final Player player, final int amountNeeded) {
        int amountLeft = amountNeeded;
        int totalAvailable = 0;

        for (final ItemStack stack : armorItems(player)) {
            if (stack.getItem() instanceof UfoArmorItem) {
                totalAvailable += stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
            }
        }

        if (totalAvailable < amountNeeded) return false;

        for (final ItemStack stack : armorItems(player)) {
            if (amountLeft <= 0) break;
            if (stack.getItem() instanceof UfoArmorItem) {
                final int current = stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
                final int toExtract = Math.min(current, amountLeft);

                stack.set(ModDataComponents.ENERGY.get(), current - toExtract);
                amountLeft -= toExtract;
            }
        }
        return true;
    }

    private static void consumeEnergyDirect(final ItemStack stack, final int amount) {
        final int current = stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
        if (current >= amount) {
            stack.set(ModDataComponents.ENERGY.get(), current - amount);
        }
    }

    private static void teleportToSafety(final ServerPlayer player) {
        final var respawnData = player.getRespawnConfig().respawnData();
        final BlockPos respawnPos = respawnData.pos();
        final ServerLevel respawnLevel = player.level().getServer().getLevel(respawnData.dimension());

        if (respawnPos != null && respawnLevel != null) {
            player.teleportTo(respawnLevel, respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(),
                    java.util.Set.of(), respawnData.yaw(), respawnData.pitch(), false);
        } else {
            player.teleportTo(player.getX(), 300, player.getZ());
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 1200));
        }
    }

    private static java.util.List<ItemStack> armorItems(final Player player) {
        return java.util.List.of(
                player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD),
                player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST),
                player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS),
                player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET));
    }
}
