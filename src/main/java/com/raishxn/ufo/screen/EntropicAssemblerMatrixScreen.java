package com.raishxn.ufo.screen;

import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class EntropicAssemblerMatrixScreen extends AbstractUniversalMultiblockControllerScreen<EntropicAssemblerMatrixMenu> {
    public EntropicAssemblerMatrixScreen(final EntropicAssemblerMatrixMenu menu, final Inventory playerInventory, final Component title, final ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
