package com.raishxn.ufo.screen;

import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class QuantumSlicerControllerScreen extends AbstractUniversalMultiblockControllerScreen<QuantumSlicerControllerMenu> {

    public QuantumSlicerControllerScreen(final QuantumSlicerControllerMenu menu, final Inventory playerInventory, final Component title, final ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
