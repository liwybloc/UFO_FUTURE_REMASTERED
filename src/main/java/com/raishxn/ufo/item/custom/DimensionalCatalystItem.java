package com.raishxn.ufo.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import appeng.items.materials.UpgradeCardItem;

import java.util.List;

/**
 * Catalisador Criativo.
 * Extends UpgradeCardItem for AE2 upgrade slot compatibility.
 */
public class DimensionalCatalystItem extends UpgradeCardItem {

    public DimensionalCatalystItem(final Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        return CatalystUpgradeUseHelper.tryInstallHeldCatalyst(context);
    }

    public void appendHoverText(final ItemStack stack, final TooltipContext context, final List<Component> components, final TooltipFlag flag) {

        if (net.minecraft.client.Minecraft.getInstance().hasShiftDown()) {
            components.add(Component.literal("CREATIVE MODE CATALYST").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            components.add(Component.literal("")); // Espaçador
            components.add(Component.literal("Effects:").withStyle(ChatFormatting.AQUA));
            components.add(Component.literal(" - ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Instant Crafting").withStyle(ChatFormatting.GREEN)));
            components.add(Component.literal(" - ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("No Energy Cost").withStyle(ChatFormatting.GREEN)));
            components.add(Component.literal(" - ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("No Heat Generation").withStyle(ChatFormatting.GREEN)));
            components.add(Component.literal(" - ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Still Consumes Recipe Inputs").withStyle(ChatFormatting.AQUA)));

        } else {
            components.add(Component.literal("Hold <").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("SHIFT").withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC))
                    .append(Component.literal("> for details.").withStyle(ChatFormatting.DARK_GRAY)));
        }

    }
}
