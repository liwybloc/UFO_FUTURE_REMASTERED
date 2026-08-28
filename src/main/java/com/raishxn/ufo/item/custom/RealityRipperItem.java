package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.util.EntityDamageHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.entity.PartEntity;

import java.util.List;

public final class RealityRipperItem extends Item {

    public RealityRipperItem(final ToolMaterial material, final Item.Properties properties) {
        super(properties.sword(material, 20, -2.4F).stacksTo(1));
    }

    @Override
    public void hurtEnemy(final ItemStack stack, final LivingEntity target, final LivingEntity attacker) {
        execute(target, attacker);
        super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public boolean onLeftClickEntity(final ItemStack stack, final Player player, final Entity entity) {
        execute(entity, player);
        return super.onLeftClickEntity(stack, player, entity);
    }
    public void appendHoverText(final ItemStack stack, final Item.TooltipContext context, final List<Component> tooltip, final TooltipFlag flag) {
        tooltip.add(Component.literal("Infinite Damage").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Can kill creative players").withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.literal("Reality itself is optional").withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    private static void execute(final Entity entity, final Entity attacker) {
        if (entity == null || entity.level().isClientSide()) {
            return;
        }

        if (entity instanceof final LivingEntity livingEntity) {
            if (livingEntity.getHealth() <= 0.0F) {
                livingEntity.remove(RemovalReason.KILLED);
                return;
            }

            boolean restoreInvulnerable = false;
            if (livingEntity instanceof final Player targetPlayer && targetPlayer.getAbilities().invulnerable) {
                restoreInvulnerable = true;
                targetPlayer.getAbilities().invulnerable = false;
                targetPlayer.onUpdateAbilities();
            }

            livingEntity.invulnerableTime = 0;
            EntityDamageHelper.hurt(livingEntity, attacker.damageSources().playerAttack(attacker instanceof final Player player ? player : null), Float.MAX_VALUE);
            final var maxHealth = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null) {
                maxHealth.setBaseValue(0.0D);
            }
            livingEntity.setHealth(0.0F);
            livingEntity.die(attacker.damageSources().genericKill());
            if (restoreInvulnerable && !livingEntity.isRemoved() && livingEntity.isAlive() && livingEntity instanceof final Player targetPlayer) {
                targetPlayer.getAbilities().invulnerable = true;
                targetPlayer.onUpdateAbilities();
            }
            return;
        }

        if (entity instanceof final PartEntity<?> partEntity) {
            execute(partEntity.getParent(), attacker);
            return;
        }

        EntityDamageHelper.hurt(entity, attacker.damageSources().genericKill(), Float.MAX_VALUE);
        entity.remove(RemovalReason.KILLED);
    }
}
