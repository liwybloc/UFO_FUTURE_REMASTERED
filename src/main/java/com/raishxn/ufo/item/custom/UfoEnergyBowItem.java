package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public final class UfoEnergyBowItem extends BowItem implements IEnergyTool, IHasModeHUD, IHasCycleableModes {

    private static final int ENERGY_COST_NORMAL = 500;
    private static final int ENERGY_COST_FAST = 25000;

    public UfoEnergyBowItem(final Properties pProperties) {
        super(pProperties.stacksTo(1));
    }
    @Override
    public Component getName(final ItemStack stack) {
        return IEnergyTool.super.getName(stack);
    }
    @Override
    public boolean releaseUsing(final ItemStack pStack, final Level pLevel, final LivingEntity pEntityLiving, final int pTimeLeft) {
        if (!(pEntityLiving instanceof final Player player)) {
            return false;
        }

        final boolean isFastMode = pStack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        final EnergyHandler energy = com.raishxn.ufo.util.EnergyToolHelper.getEnergyHandler(pStack);
        if (energy == null) return false;

        if (isFastMode) {
            if (energy.getAmountAsInt() >= ENERGY_COST_FAST) {
                if (!pLevel.isClientSide()) {
                    com.raishxn.ufo.util.EnergyToolHelper.extractEnergy(energy, ENERGY_COST_FAST, false);
                    for (int i = 0; i < 5; i++) {
                        final Arrow arrow = new Arrow(EntityType.ARROW, pLevel);
                        arrow.setOwner(player);
                        arrow.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());

                        arrow.setCritArrow(true);
                        arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 10.0F);
                        pLevel.addFreshEntity(arrow);
                    }
                }
                pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            }
            return true;
        }

        ItemStack ammo = player.getProjectile(pStack);
        final boolean isCreative = player.getAbilities().instabuild;

        if (!ammo.isEmpty() || isCreative) {
            if (ammo.isEmpty()) {
                ammo = new ItemStack(Items.ARROW);
            }

            final int useDuration = this.getUseDuration(pStack, player) - pTimeLeft;
            final float power = getPowerForTime(useDuration);

            if (power >= 0.1F && energy.getAmountAsInt() >= ENERGY_COST_NORMAL) {
                if (!pLevel.isClientSide()) {
                    com.raishxn.ufo.util.EnergyToolHelper.extractEnergy(energy, ENERGY_COST_NORMAL, false);

                    final ArrowItem arrowitem = (ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);

                    final AbstractArrow abstractarrow = arrowitem.createArrow(pLevel, ammo, player, pStack);

                    abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);

                    if (power == 1.0F) {
                        abstractarrow.setCritArrow(true);
                    }

                    pStack.hurtAndBreak(1, player, player.getUsedItemHand() == InteractionHand.MAIN_HAND
                            ? net.minecraft.world.entity.EquipmentSlot.MAINHAND
                            : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                    pLevel.addFreshEntity(abstractarrow);
                }

                pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

                if (!isCreative) {
                    ammo.shrink(1);
                    if (ammo.isEmpty()) {
                        player.getInventory().removeItem(ammo);
                    }
                }
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
        return true;
    }


    @Override
    public InteractionResult use(final Level pLevel, final Player pPlayer, final InteractionHand pUsedHand) {
        final ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        final boolean isFastMode = itemstack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        final boolean canFire = !pPlayer.getProjectile(itemstack).isEmpty() || isFastMode;
        if (!pPlayer.getAbilities().instabuild && !canFire) {
            return InteractionResult.FAIL;
        } else {
            pPlayer.startUsingItem(pUsedHand);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void cycleMode(final ItemStack stack, final Player player) {
        final boolean isFast = stack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        final boolean newMode = !isFast;
        stack.set(ModDataComponents.FAST_MODE.get(), newMode);
        final Component modeText = newMode ?
                Component.translatable("tooltip.ufo.mode.fast").withStyle(ChatFormatting.RED) :
                Component.translatable("tooltip.ufo.mode.normal").withStyle(ChatFormatting.GREEN);
        player.sendSystemMessage(Component.translatable("tooltip.ufo.mode_changed_to", modeText));
    }

    @Override
    public Component getModeHudComponent(final ItemStack stack) {
        final boolean isFast = stack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        final Component modeText = isFast ?
                Component.translatable("tooltip.ufo.mode.fast").withStyle(ChatFormatting.RED) :
                Component.translatable("tooltip.ufo.mode.normal").withStyle(ChatFormatting.GREEN);

        return Component.translatable("tooltip.ufo.current_mode", modeText);
    }
    public void appendHoverText(final ItemStack pStack, final Item.TooltipContext pContext, final List<Component> pTooltipComponents, final TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(getModeHudComponent(pStack));
    }

    @Override public int getUseDuration(final ItemStack pStack, final LivingEntity p_344558_) { return 72000; }
    @Override public int getEnergyPerUse() { return ENERGY_COST_NORMAL; }
    @Override public boolean isBarVisible(final ItemStack pStack) { return EnergyToolHelper.isBarVisible(pStack); }
    @Override public int getBarWidth(final ItemStack pStack) { return EnergyToolHelper.getBarWidth(pStack); }
    @Override public int getBarColor(final ItemStack pStack) { return EnergyToolHelper.getBarColor(pStack); }
}
