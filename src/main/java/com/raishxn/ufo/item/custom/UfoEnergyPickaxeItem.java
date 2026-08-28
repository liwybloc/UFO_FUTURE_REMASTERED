package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

import java.util.List;

public final class UfoEnergyPickaxeItem extends Item implements IEnergyTool, IHasModeHUD, IHasCycleableModes {

    private static final int ENERGY_COST_NORMAL = 200;
    private static final int ENERGY_COST_FAST = 2000;

    public UfoEnergyPickaxeItem(final ToolMaterial material, final Properties properties) {
        super(properties.pickaxe(material, 1.0F, -2.8F).stacksTo(1));
    }

    @Override
    public Component getName(final ItemStack stack) {
        return IEnergyTool.super.getName(stack);
    }

    @Override
    public void cycleMode(final ItemStack stack, final Player player) {
        final boolean isFast = stack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);
        final boolean newMode = !isFast;
        stack.set(ModDataComponents.FAST_MODE.get(), newMode);

        final Component modeText = newMode ?
                Component.translatable("tooltip.ufo.mode.fast").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)) :
                Component.translatable("tooltip.ufo.mode.normal").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

    }



    @Override
    public Component getModeHudComponent(final ItemStack stack) {
        final boolean isFast = stack.getOrDefault(ModDataComponents.FAST_MODE.get(), false);

        final Component modeText = isFast ?
                Component.translatable("tooltip.ufo.mode.fast").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)) :
                Component.translatable("tooltip.ufo.mode.normal").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));

        return Component.translatable("tooltip.ufo.current_mode", modeText);
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
    public boolean mineBlock(final ItemStack pStack, final Level pLevel, final BlockState pState, final BlockPos pPos, final LivingEntity pEntityLiving) {
        if (!pLevel.isClientSide() && pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            final int cost = pStack.getOrDefault(ModDataComponents.FAST_MODE.get(), false) ? ENERGY_COST_FAST : ENERGY_COST_NORMAL;
            consumeEnergy(pStack, cost);
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }

    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand usedHand) {
        final ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide() && player.isShiftKeyDown()) {
            final boolean currentSmelt = stack.getOrDefault(ModDataComponents.AUTO_SMELT.get(), false);
            stack.set(ModDataComponents.AUTO_SMELT.get(), !currentSmelt);
            player.sendSystemMessage(Component.literal("Auto-Smelt: " + (!currentSmelt ? "ON" : "OFF"))
                    .withStyle(!currentSmelt ? ChatFormatting.GREEN : ChatFormatting.RED));
            return InteractionResult.SUCCESS;
        }
        return super.use(level, player, usedHand);
    }
    public void appendHoverText(final ItemStack pStack, final Item.TooltipContext pContext, final List<Component> pTooltipComponents, final TooltipFlag pTooltipFlag) {

        final boolean smite = pStack.getOrDefault(ModDataComponents.AUTO_SMELT.get(), false);
        final int fortune = pStack.getOrDefault(ModDataComponents.PROGRESSIVE_FORTUNE.get(), 0);

        pTooltipComponents.add(Component.literal("Auto-Smelt: " + (smite ? "ON" : "OFF")).withStyle(smite ? ChatFormatting.GOLD : ChatFormatting.GRAY));
        pTooltipComponents.add(Component.literal("Prog. Fortune: " + fortune + "/300").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public boolean isBarVisible(final ItemStack pStack) { return EnergyToolHelper.isBarVisible(pStack); }
    @Override
    public int getBarWidth(final ItemStack pStack) { return EnergyToolHelper.getBarWidth(pStack); }
    @Override
    public int getBarColor(final ItemStack pStack) { return EnergyToolHelper.getBarColor(pStack); }
    @Override
    public int getEnergyPerUse() { return ENERGY_COST_NORMAL; }

    private boolean consumeEnergy(final ItemStack stack, final int amount) {
        final EnergyHandler energy = com.raishxn.ufo.util.EnergyToolHelper.getEnergyHandler(stack);
        if (energy != null && energy.getAmountAsInt() >= amount) {
            com.raishxn.ufo.util.EnergyToolHelper.extractEnergy(energy, amount, false);
            return true;
        }
        return false;
    }
}
