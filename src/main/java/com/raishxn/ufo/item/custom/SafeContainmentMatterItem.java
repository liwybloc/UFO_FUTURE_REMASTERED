package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ComponentItemHandler;

import java.util.List;

public class SafeContainmentMatterItem extends Item {
    public SafeContainmentMatterItem(final Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack ccmStack = player.getItemInHand(hand); // Podia renomear para scmStack, mas ccmStack funciona
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        final ComponentItemHandler handler = new ComponentItemHandler(ccmStack, ModDataComponents.SAVED_INVENTORY.get(), 1);
        final ItemStack inside = handler.getStackInSlot(0);

        final ItemStack otherHandStack = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

        if (inside.isEmpty()) {
            if (!otherHandStack.isEmpty() && otherHandStack.is(ModTags.Items.HAZARDOUS)) {
                final ItemStack inserted = handler.insertItem(0, otherHandStack.copy(), false);
                if (inserted.getCount() < otherHandStack.getCount()) {
                    player.setItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, inserted);
                    player.sendOverlayMessage(Component.literal("Item contained securely.").withStyle(ChatFormatting.GREEN));
                    return InteractionResult.CONSUME;
                }
            } else if (!otherHandStack.isEmpty()) {
                player.sendOverlayMessage(Component.literal("This item does not require containment.").withStyle(ChatFormatting.RED));
            }
        } else {
            if (otherHandStack.isEmpty()) {
                player.setItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, inside.copy());
                handler.setStackInSlot(0, ItemStack.EMPTY);
                player.sendOverlayMessage(Component.literal("WARNING: Hazardous item removed from containment!").withStyle(ChatFormatting.GOLD));
                return InteractionResult.CONSUME;
            } else {
                player.sendOverlayMessage(Component.literal("Empty your other hand to remove the item.").withStyle(ChatFormatting.RED));
            }
        }

        return InteractionResult.PASS;
    }

    public void appendHoverText(final ItemStack stack, final TooltipContext context, final List<Component> tooltipComponents, final TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.ufo.scm").withStyle(ChatFormatting.GRAY));

        final ComponentItemHandler handler = new ComponentItemHandler(stack, ModDataComponents.SAVED_INVENTORY.get(), 1);
        final ItemStack inside = handler.getStackInSlot(0);
        if (!inside.isEmpty()) {
            tooltipComponents.add(Component.literal("Contains: ").append(inside.getHoverName()).withStyle(ChatFormatting.RED));
        }
    }
}
