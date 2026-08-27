package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.item.ModTools;
import com.raishxn.ufo.util.ColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

import java.util.List;
import java.util.function.Supplier;

public interface IEnergyTool {

    List<Supplier<? extends Item>> TOOL_CYCLE = List.of(
            ModTools.UFO_STAFF, ModTools.UFO_SWORD, ModTools.UFO_PICKAXE, ModTools.UFO_AXE,
            ModTools.UFO_SHOVEL, ModTools.UFO_HOE, ModTools.UFO_HAMMER, ModTools.UFO_GREATSWORD,
            ModTools.UFO_FISHING_ROD, ModTools.UFO_BOW
    );

    int getEnergyPerUse();

    default boolean consumeEnergy(final ItemStack pStack) {
        final EnergyHandler energyStorage = com.raishxn.ufo.util.EnergyToolHelper.getEnergyHandler(pStack);
        if (energyStorage != null && energyStorage.getAmountAsInt() >= getEnergyPerUse()) {
            com.raishxn.ufo.util.EnergyToolHelper.extractEnergy(energyStorage, getEnergyPerUse(), false);
            return true;
        }
        return false;
    }

    default void appendHoverText(final ItemStack pStack, final Item.TooltipContext pContext, final List<Component> pTooltipComponents, final TooltipFlag pTooltipFlag) {
        if (net.minecraft.client.Minecraft.getInstance().hasShiftDown()) {
            final EnergyHandler energyStorage = com.raishxn.ufo.util.EnergyToolHelper.getEnergyHandler(pStack);
            if (energyStorage != null) {
                final String energyText = String.format("%,d / %,d RF", energyStorage.getAmountAsInt(), energyStorage.getCapacityAsInt());
                pTooltipComponents.add(Component.literal(energyText).withStyle(ChatFormatting.GRAY));
            }
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.ufo.press_shift").withStyle(ChatFormatting.AQUA));
        }
    }

    default void transformTool(final Level level, final Player player, final InteractionHand hand, final boolean forward) {
        if (!level.isClientSide()) {
            final ItemStack currentStack = player.getItemInHand(hand);
            final int currentIndex = currentStack.getOrDefault(ModDataComponents.TOOL_MODE_INDEX.get(), 0);

            final int nextIndex;
            if (forward) {
                nextIndex = (currentIndex + 1) % TOOL_CYCLE.size();
            } else {
                nextIndex = (currentIndex - 1 + TOOL_CYCLE.size()) % TOOL_CYCLE.size();
            }

            final Item nextItem = TOOL_CYCLE.get(nextIndex).get();
            final ItemStack nextStack = currentStack.transmuteCopy(nextItem, 1);
            nextStack.set(ModDataComponents.TOOL_MODE_INDEX.get(), nextIndex);
            player.setItemInHand(hand, nextStack);
        }
    }

    default Component getName(final ItemStack stack) {
        final String text = Component.translatable(stack.getItem().getDescriptionId()).getString();

        final ChatFormatting[] rainbowColors = new ChatFormatting[]{
                ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.YELLOW,
                ChatFormatting.GREEN, ChatFormatting.AQUA, ChatFormatting.BLUE,
                ChatFormatting.LIGHT_PURPLE
        };

        return ColorHelper.getSolidColoredText(text, rainbowColors);
    }
}
