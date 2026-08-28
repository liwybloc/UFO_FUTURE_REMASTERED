package com.raishxn.ufo.screen;

import appeng.api.config.LockCraftingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.client.gui.widgets.ToolboxPanel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class QuantumPatternHatchScreen extends AEBaseScreen<QuantumPatternHatchMenu> {
    private final SettingToggleButton<YesNo> blockingModeButton;
    private final SettingToggleButton<LockCraftingMode> lockCraftingModeButton;
    private final SettingToggleButton<YesNo> showInPatternAccessTerminalButton;

    public QuantumPatternHatchScreen(final QuantumPatternHatchMenu menu, final Inventory playerInventory, final Component title, final ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.blockingModeButton = new ServerSettingToggleButton<>(Settings.BLOCKING_MODE, YesNo.NO);
        this.addToLeftToolbar(this.blockingModeButton);

        this.lockCraftingModeButton = new ServerSettingToggleButton<>(Settings.LOCK_CRAFTING_MODE, LockCraftingMode.NONE);
        this.addToLeftToolbar(this.lockCraftingModeButton);

        widgets.addOpenPriorityButton();

        this.showInPatternAccessTerminalButton = new ServerSettingToggleButton<>(Settings.PATTERN_ACCESS_TERMINAL, YesNo.NO);
        this.addToLeftToolbar(this.showInPatternAccessTerminalButton);

        if (menu.getToolbox().isPresent()) {
            this.widgets.add("toolbox", new ToolboxPanel(style, menu.getToolbox().getName()));
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.blockingModeButton.set(this.menu.getBlockingMode());
        this.lockCraftingModeButton.set(this.menu.getLockCraftingMode());
        this.showInPatternAccessTerminalButton.set(this.menu.getShowInAccessTerminal());
    }
}
