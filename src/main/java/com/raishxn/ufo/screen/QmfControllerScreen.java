package com.raishxn.ufo.screen;

import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class QmfControllerScreen extends AbstractUniversalMultiblockControllerScreen<QmfControllerMenu> {

    public QmfControllerScreen(final QmfControllerMenu menu, final Inventory playerInventory, final Component title, final ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
