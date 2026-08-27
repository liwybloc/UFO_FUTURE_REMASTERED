package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

import java.util.List;

public class UfoEnergyShovelItem extends ShovelItem implements IEnergyTool, IHasModeHUD, IHasCycleableModes {

    private static final int ENERGY_COST_NORMAL = 100;
    private static final int ENERGY_COST_FAST = 1000;

    public UfoEnergyShovelItem(final ToolMaterial material, final Properties properties) {
        super(material, 1.5F, -3.0F, properties.stacksTo(1));
    }

    @Override
    public Component getName(final ItemStack stack) {
        return IEnergyTool.super.getName(stack);
    }

    @Override
    public int getEnergyPerUse() {
        return ENERGY_COST_NORMAL;
    }

    @Override
    public void cycleMode(final ItemStack stack, final Player player) {
        final boolean newMode = !stack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        stack.set(ModDataComponents.FAST_MODE.get(), newMode);
    }

    @Override
    public Component getModeHudComponent(final ItemStack stack) {
        final boolean isFast = stack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        final Component modeText = isFast
                ? Component.translatable("tooltip.ufo.mode.fast").withStyle(ChatFormatting.RED)
                : Component.translatable("tooltip.ufo.mode.normal").withStyle(ChatFormatting.GREEN);
        return Component.translatable("tooltip.ufo.current_mode", modeText);
    }

    @Override
    public boolean mineBlock(final ItemStack pStack, final Level pLevel, final BlockState pState, final BlockPos pPos, final LivingEntity pEntityLiving) {
        if (!pLevel.isClientSide() && pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            final int cost = pStack.getOrDefault(ModDataComponents.FAST_MODE.get(), false) ? ENERGY_COST_FAST : ENERGY_COST_NORMAL;
            consumeEnergy(pStack, cost);
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    @Override
    public float getDestroySpeed(final ItemStack pStack, final BlockState pState) {
        final boolean isFast = pStack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        final EnergyHandler energy = com.raishxn.ufo.util.EnergyToolHelper.getEnergyHandler(pStack);
        if (energy != null) {
            if (isFast && energy.getAmountAsInt() >= ENERGY_COST_FAST) {
                return Float.MAX_VALUE;
            } else if (!isFast && energy.getAmountAsInt() >= ENERGY_COST_NORMAL) {
                return super.getDestroySpeed(pStack, pState);
            }
        }
        return 1.0F;
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
        pTooltipComponents.add(getModeHudComponent(pStack));
    }

    private boolean consumeEnergy(final ItemStack stack, final int amount) {
        final EnergyHandler energy = com.raishxn.ufo.util.EnergyToolHelper.getEnergyHandler(stack);
        if (energy != null && energy.getAmountAsInt() >= amount) {
            com.raishxn.ufo.util.EnergyToolHelper.extractEnergy(energy, amount, false);
            return true;
        }
        return false;
    }
}
