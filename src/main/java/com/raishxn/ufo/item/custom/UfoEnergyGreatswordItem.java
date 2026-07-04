package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class UfoEnergyGreatswordItem extends SwordItem implements IEnergyTool {

    public UfoEnergyGreatswordItem(Tier pTier, Properties pProperties) {
        // Dano base alto (10), Velocidade muito baixa (-3.5F, quase 1.5s cooldown)
        super(pTier, pProperties.attributes(SwordItem.createAttributes(pTier, 10, -3.5F)).stacksTo(1));
    }

    // --- CORREÇÃO ADICIONADA AQUI ---
    @Override
    public Component getName(ItemStack stack) {
        return IEnergyTool.super.getName(stack);
    }

    @Override
    public int getEnergyPerUse() {
        return 150;
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if (consumeEnergy(pStack)) {
            return super.hurtEnemy(pStack, pTarget, pAttacker);
        }
        return false;
    }


    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return EnergyToolHelper.isBarVisible(pStack);
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        return EnergyToolHelper.getBarWidth(pStack);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return EnergyToolHelper.getBarColor(pStack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        int kills = pStack.getOrDefault(ModDataComponents.KILL_COUNT.get(), 0);
        int bonusDmg = kills * 2;
        pTooltipComponents.add(Component.literal("Soul Harvest: " + kills + " Kills").withStyle(ChatFormatting.DARK_RED));
        pTooltipComponents.add(Component.literal("Bonus Dmg: +" + bonusDmg).withStyle(ChatFormatting.RED));
        IEnergyTool.super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }
}
