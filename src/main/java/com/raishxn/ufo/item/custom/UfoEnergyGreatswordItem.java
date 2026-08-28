package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public final class UfoEnergyGreatswordItem extends Item implements IEnergyTool {

    public UfoEnergyGreatswordItem(final ToolMaterial material, final Properties properties) {
        super(properties.sword(material, 10, -3.5F).stacksTo(1));
    }

    @Override
    public Component getName(final ItemStack stack) {
        return IEnergyTool.super.getName(stack);
    }

    @Override
    public int getEnergyPerUse() {
        return 150;
    }

    @Override
    public void hurtEnemy(final ItemStack pStack, final LivingEntity pTarget, final LivingEntity pAttacker) {
        if (consumeEnergy(pStack)) {
            super.hurtEnemy(pStack, pTarget, pAttacker);
        }
    }


    @Override
    public boolean isBarVisible(final ItemStack pStack) {
        return EnergyToolHelper.isBarVisible(pStack);
    }

    @Override
    public int getBarWidth(final ItemStack pStack) {
        return EnergyToolHelper.getBarWidth(pStack);
    }

    @Override
    public int getBarColor(final ItemStack pStack) {
        return EnergyToolHelper.getBarColor(pStack);
    }
    public void appendHoverText(final ItemStack pStack, final Item.TooltipContext pContext, final List<Component> pTooltipComponents, final TooltipFlag pTooltipFlag) {
        final int kills = pStack.getOrDefault(ModDataComponents.KILL_COUNT.get(), 0);
        final int bonusDmg = kills * 2;
        pTooltipComponents.add(Component.literal("Soul Harvest: " + kills + " Kills").withStyle(ChatFormatting.DARK_RED));
        pTooltipComponents.add(Component.literal("Bonus Dmg: +" + bonusDmg).withStyle(ChatFormatting.RED));
    }
}
