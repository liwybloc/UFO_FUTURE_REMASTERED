package com.raishxn.ufo.mixin;

import appeng.api.behaviors.ContainerItemStrategies;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.helpers.InventoryAction;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.MEStorageMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MEStorageScreen.class)
public abstract class MixinMEStorageScreen {
    @Inject(method = "handleGridInventoryEntryMouseClick", at = @At("HEAD"), cancellable = true)
    private void ufo$allowEmptyingChemicalContainersFromHands(@Nullable final GridInventoryEntry entry, final int mouseButton, final ClickType clickType, final CallbackInfo ci) {
        final AbstractContainerMenu rawMenu = ((AccessorAbstractContainerScreen) this).ufo$getMenu();
        if (!(rawMenu instanceof final MEStorageMenu menu)) {
            return;
        }
        if (mouseButton != 1 || !menu.getCarried().isEmpty()) {
            return;
        }

        final var player = menu.getPlayer();
        final var mainHand = player.getMainHandItem();
        var emptyingAction = ContainerItemStrategies.getEmptyingAction(mainHand);
        if (emptyingAction == null) {
            final var offHand = player.getOffhandItem();
            emptyingAction = ContainerItemStrategies.getEmptyingAction(offHand);
        }

        if (emptyingAction == null || !menu.isKeyVisible(emptyingAction.what())) {
            return;
        }

        menu.handleInteraction(-1, clickType == ClickType.QUICK_MOVE
                ? InventoryAction.EMPTY_ENTIRE_ITEM
                : InventoryAction.EMPTY_ITEM);
        ci.cancel();
    }
}
