package com.raishxn.ufo.screen;

import appeng.api.client.AEKeyRendering;
import appeng.api.config.LockCraftingMode;
import appeng.api.stacks.AmountFormat;
import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.Icon;
import appeng.client.gui.Tooltip;
import appeng.core.localization.GuiText;
import appeng.core.localization.InGameTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public final class QuantumPatternHatchLockReason implements ICompositeWidget {
    private final QuantumPatternHatchScreen screen;
    private boolean visible;
    private int x;
    private int y;

    public QuantumPatternHatchLockReason(final QuantumPatternHatchScreen screen) {
        this.screen = screen;
    }

    @Override
    public void setPosition(final Point position) {
        x = position.getX();
        y = position.getY();
    }

    @Override
    public void setSize(final int width, final int height) {
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(x, y, 126, 16);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    @Override
    public void drawForegroundLayer(final GuiGraphicsExtractor guiGraphics, final Rect2i bounds, final Point mouse) {
        final var menu = screen.getMenu();

        final Icon icon;
        final Component lockStatusText;
        if (menu.getCraftingLockedReason() == LockCraftingMode.NONE) {
            icon = Icon.UNLOCKED;
            lockStatusText = GuiText.CraftingLockIsUnlocked.text().withStyle(ChatFormatting.DARK_GREEN);
        } else {
            icon = Icon.LOCKED;
            lockStatusText = GuiText.CraftingLockIsLocked.text().withStyle(ChatFormatting.DARK_RED);
        }

        icon.getBlitter().dest(x, y).blit(guiGraphics);
        guiGraphics.drawString(Minecraft.getInstance().font, lockStatusText, x + 15, y + 5, -1, false);
    }

    @Nullable
    @Override
    public Tooltip getTooltip(final int mouseX, final int mouseY) {
        final var menu = screen.getMenu();
        final var tooltip = switch (menu.getCraftingLockedReason()) {
            case NONE -> null;
            case LOCK_UNTIL_PULSE -> InGameTooltip.CraftingLockedUntilPulse.text();
            case LOCK_WHILE_HIGH -> InGameTooltip.CraftingLockedByRedstoneSignal.text();
            case LOCK_WHILE_LOW -> InGameTooltip.CraftingLockedByLackOfRedstoneSignal.text();
            case LOCK_UNTIL_RESULT -> {
                final var stack = menu.getUnlockStack();
                final Component stackName;
                final Component stackAmount;
                if (stack != null) {
                    stackName = AEKeyRendering.getDisplayName(stack.what());
                    stackAmount = Component.literal(stack.what().formatAmount(stack.amount(), AmountFormat.FULL));
                } else {
                    stackName = Component.literal("ERROR");
                    stackAmount = Component.literal("ERROR");
                }
                yield InGameTooltip.CraftingLockedUntilResult.text(stackName, stackAmount);
            }
        };

        return tooltip != null ? new Tooltip(tooltip) : null;
    }
}
