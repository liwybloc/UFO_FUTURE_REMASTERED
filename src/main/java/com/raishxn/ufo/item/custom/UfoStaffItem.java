package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.util.EnergyToolHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public final class UfoStaffItem extends Item implements IEnergyTool {

    public UfoStaffItem(final Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Component getName(final ItemStack stack) {
        return IEnergyTool.super.getName(stack);
    }

    @Override
    public int getEnergyPerUse() {
        return 0;
    }
    public void appendHoverText(final ItemStack pStack, final Item.TooltipContext pContext, final List<Component> pTooltipComponents, final TooltipFlag pTooltipFlag) {
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
}
