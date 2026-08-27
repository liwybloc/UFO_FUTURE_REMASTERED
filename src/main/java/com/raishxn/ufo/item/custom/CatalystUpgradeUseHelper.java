package com.raishxn.ufo.item.custom;

import appeng.api.upgrades.IUpgradeableObject;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

final class CatalystUpgradeUseHelper {

    private CatalystUpgradeUseHelper() {
    }

    static InteractionResult tryInstallHeldCatalyst(final UseOnContext context) {
        final Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        final Level level = context.getLevel();
        final BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof final IUpgradeableObject upgradeable)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        final var upgrades = upgradeable.getUpgrades();
        if (upgrades == null) {
            return InteractionResult.PASS;
        }

        final ItemStack heldStack = context.getItemInHand();
        final ItemStack toInsert = heldStack.copyWithCount(1);
        final ItemStack remainder = upgrades.addItems(toInsert);
        if (!remainder.isEmpty()) {
            player.sendOverlayMessage(Component.literal("This controller cannot accept that catalyst, or its catalyst slots are full.")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.SUCCESS_SERVER;
        }

        if (!player.getAbilities().instabuild) {
            heldStack.shrink(1);
        }
        blockEntity.setChanged();
        player.sendOverlayMessage(Component.literal("Catalyst installed.")
                .withStyle(ChatFormatting.GREEN));
        return InteractionResult.SUCCESS_SERVER;
    }
}
