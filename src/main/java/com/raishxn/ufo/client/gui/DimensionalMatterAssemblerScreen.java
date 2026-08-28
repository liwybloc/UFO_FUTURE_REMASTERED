package com.raishxn.ufo.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;

import com.raishxn.ufo.menu.DimensionalMatterAssemblerMenu;

import net.pedroksl.ae2addonlib.api.IFluidTankScreen;
import net.pedroksl.ae2addonlib.client.widgets.AddonActionItems;
import net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot;
import net.pedroksl.ae2addonlib.client.widgets.ToolbarActionButton;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.*;

public final class DimensionalMatterAssemblerScreen extends UpgradeableScreen<DimensionalMatterAssemblerMenu> implements IFluidTankScreen {

    private final ProgressBar pb;
    private final SettingToggleButton<YesNo> autoExportBtn;
    private final ToolbarActionButton outputConfigure;
    private final AlertWidget powerAlert;

    private FluidTankSlot inputCoolantSlot;
    private FluidTankSlot inputFluidSlot;
    private FluidTankSlot outputFluid1Slot;
    private FluidTankSlot outputFluid2Slot;

    private static final int ENERGY_X = 155;
    private static final int ENERGY_Y = 34;
    private static final int ENERGY_W = 6;
    private static final int ENERGY_H = 18;

    private static final int HEAT_BAR_X = 9;
    private static final int HEAT_BAR_Y = 5;
    private static final int HEAT_BAR_W = 91; // 100 - 9
    private static final int HEAT_BAR_H = 10; // 15 - 5

    public DimensionalMatterAssemblerScreen(
            final DimensionalMatterAssemblerMenu menu, final Inventory playerInventory, final Component title, final ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.pb = new ProgressBar(this.menu, style.getImage("progressBar"), ProgressBar.Direction.HORIZONTAL);
        widgets.add("progressBar", this.pb);

        this.autoExportBtn = new ServerSettingToggleButton<>(Settings.AUTO_EXPORT, YesNo.NO);
        this.addToLeftToolbar(autoExportBtn);

        this.outputConfigure =
                new ToolbarActionButton(AddonActionItems.DIRECTIONAL_OUTPUT, btn -> menu.configureOutput());
        this.outputConfigure.setVisibility(getMenu().getAutoExport() == YesNo.YES);
        this.addToLeftToolbar(this.outputConfigure);

        this.powerAlert = new AlertWidget(style.getImage("powerAlert"));
        this.powerAlert.setTooltip(Tooltip.create(Component.literal("Insufficient Power")));
        this.widgets.add("powerAlert", this.powerAlert);
    }

    @Override
    protected void init() {
        this.outputFluid1Slot = this.addRenderableWidget(new FluidTankSlot(
                this, 0, this.leftPos + 119, this.topPos + 75, 14, 17, this.menu.OUTPUT_FLUID_SIZE));
        
        this.outputFluid2Slot = this.addRenderableWidget(new FluidTankSlot(
                this, 1, this.leftPos + 148, this.topPos + 76, 14, 17, this.menu.OUTPUT_FLUID_SIZE));

        this.inputCoolantSlot = this.addRenderableWidget(new FluidTankSlot(
                this, 2, this.leftPos + 9, this.topPos + 21, 12, 54, this.menu.INPUT_FLUID_SIZE));

        this.inputFluidSlot = this.addRenderableWidget(new FluidTankSlot(
                this, 3, this.leftPos + 28, this.topPos + 21, 12, 54, this.menu.INPUT_FLUID_SIZE));

        super.init();

    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        int progress = 0;
        if (this.menu.getMaxProgress() > 0) {
            progress = this.menu.getCurrentProgress() * 100 / this.menu.getMaxProgress();
        }
        this.pb.setFullMsg(Component.literal(progress + "%"));

        this.autoExportBtn.set(getMenu().getAutoExport());
        this.outputConfigure.setVisibility(getMenu().getAutoExport() == YesNo.YES);

        this.outputFluid1Slot.setPosition(this.leftPos + 119, this.topPos + 75);
        this.outputFluid2Slot.setPosition(this.leftPos + 148, this.topPos + 76);
        this.inputCoolantSlot.setPosition(this.leftPos + 9, this.topPos + 21);
        this.inputFluidSlot.setPosition(this.leftPos + 28, this.topPos + 21);

        this.powerAlert.visible = this.getMenu().getShowWarning();
    }

    @Override
    public void updateFluidTankContents(final int index, final FluidStack stack) {
        if (index == 0) this.outputFluid1Slot.setFluidStack(stack);
        else if (index == 1) this.outputFluid2Slot.setFluidStack(stack);
        else if (index == 2) this.inputCoolantSlot.setFluidStack(stack);
        else if (index == 3) this.inputFluidSlot.setFluidStack(stack);
    }

    @Override
    public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
        final int mx = (int) event.x();
        final int my = (int) event.y();
        
        final Runnable playClickSound = () ->
            net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F)
            );

        if (event.button() == 0) {
            if (isInTankRegion(mx, my, this.leftPos + 7, this.topPos + 78, 7, 7)) {
                playClickSound.run();
                this.menu.clearTank(2);
                return true;
            }
            if (isInTankRegion(mx, my, this.leftPos + 34, this.topPos + 78, 7, 7)) {
                playClickSound.run();
                this.menu.clearTank(3);
                return true;
            }
            if (isInTankRegion(mx, my, this.leftPos + 108, this.topPos + 87, 7, 7)) {
                playClickSound.run();
                this.menu.clearTank(0);
                return true;
            }
            if (isInTankRegion(mx, my, this.leftPos + 166, this.topPos + 87, 7, 7)) {
                playClickSound.run();
                this.menu.clearTank(1);
                return true;
            }
        }

        if (event.button() == 1) {
            if (isInTankRegion(mx, my, this.leftPos + 119, this.topPos + 75, 14, 17)) {
                playClickSound.run();
                this.menu.fillOrDrainTank(0);
                return true;
            }
            if (isInTankRegion(mx, my, this.leftPos + 148, this.topPos + 76, 14, 17)) {
                playClickSound.run();
                this.menu.fillOrDrainTank(1);
                return true;
            }
            if (isInTankRegion(mx, my, this.leftPos + 9, this.topPos + 21, 12, 54)) {
                playClickSound.run();
                this.menu.fillOrDrainTank(2);
                return true;
            }
            if (isInTankRegion(mx, my, this.leftPos + 28, this.topPos + 21, 12, 54)) {
                playClickSound.run();
                this.menu.fillOrDrainTank(3);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    private boolean isInTankRegion(final int mx, final int my, final int x, final int y, final int w, final int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override
    public void drawBG(final GuiGraphicsExtractor guiGraphics, final int offsetX, final int offsetY, final int mouseX, final int mouseY, final float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);

        final double stored = this.menu.currentPower;
        final double maxStore = this.menu.getHost().getAEMaxPower();
        int filled = 0;
        if (maxStore > 0) {
            filled = (int) ((stored * ENERGY_H) / maxStore);
        }
        final int topEmpty = ENERGY_H - filled;

        Blitter.texture("guis/dimensional_matter_assembler.png")
            .src(225, topEmpty, ENERGY_W, filled)
            .dest(this.leftPos + ENERGY_X, this.topPos + ENERGY_Y + topEmpty)
            .blit(guiGraphics);

        final int temp = this.menu.temperature;
        final int maxTemp = this.menu.maxTemperature;
        final int overload = this.menu.overloadTimer;

        final int barX = this.leftPos + HEAT_BAR_X;
        final int barY = this.topPos + HEAT_BAR_Y;

        guiGraphics.fill(barX, barY, barX + HEAT_BAR_W, barY + HEAT_BAR_H, 0xFF1A1A1A);
        guiGraphics.fill(barX - 1, barY - 1, barX + HEAT_BAR_W + 1, barY, 0xFF333333); // top
        guiGraphics.fill(barX - 1, barY + HEAT_BAR_H, barX + HEAT_BAR_W + 1, barY + HEAT_BAR_H + 1, 0xFF333333); // bottom
        guiGraphics.fill(barX - 1, barY - 1, barX, barY + HEAT_BAR_H + 1, 0xFF333333); // left
        guiGraphics.fill(barX + HEAT_BAR_W, barY - 1, barX + HEAT_BAR_W + 1, barY + HEAT_BAR_H + 1, 0xFF333333); // right

        double ratio = maxTemp > 0 ? (double) temp / maxTemp : 0.0;
        ratio = Math.min(ratio, 1.0);

        final int filledWidth = (int) (ratio * HEAT_BAR_W);

        if (filledWidth > 0) {
            if (overload > 0) {
                final int pulse = (int) (127 + 128 * Math.sin(System.currentTimeMillis() / 100.0));
                final int color = 0xFF000000 | (pulse << 16);
                guiGraphics.fill(barX, barY, barX + filledWidth, barY + HEAT_BAR_H, color);
            } else {
                for (int col = 0; col < filledWidth; col++) {
                    final double colRatio = (double) col / HEAT_BAR_W;
                    final int color = getHeatColor(colRatio);
                    guiGraphics.fill(barX + col, barY, barX + col + 1, barY + HEAT_BAR_H, color);
                }
            }
        }

        if (overload > 0) {
            final int seconds = overload / 20;
            final String text = "§l⚠ OVERLOAD " + seconds + "s";
            final int textWidth = this.font.width(text);
            final int textX = barX + (HEAT_BAR_W - textWidth) / 2;
            final int textY = barY + 1;
            guiGraphics.text(this.font, text, textX, textY, 0xFFFF0000, true);
        }
    }

    /**
     * Interpolates the heat color from green through yellow to red.
     */
    private static int getHeatColor(final double ratio) {
        final int r;
        int g;
        final int b;
        if (ratio < 0.5) {
            final double t = ratio / 0.5;
            r = (int) (t * 255);
            g = 255;
            b = 0;
        } else {
            final double t = (ratio - 0.5) / 0.5;
            r = 255;
            g = (int) ((1 - t) * 255);
            b = 0;
        }
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    @Override
    public void extractRenderState(
            final GuiGraphicsExtractor guiGraphics,
            final int mouseX,
            final int mouseY,
            final float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        final int barLeft = this.leftPos + ENERGY_X;
        final int barTop = this.topPos + ENERGY_Y;
        final int barRight = barLeft + ENERGY_W;
        final int barBottom = barTop + ENERGY_H;

        if (mouseX >= barLeft && mouseX <= barRight && mouseY >= barTop && mouseY <= barBottom) {
            final double stored = this.menu.currentPower;
            final double maxStore = this.menu.getHost().getAEMaxPower();

            final List<Component> tooltip = List.of(
                    Component.literal("Energy: " + formatEnergy(stored) + " / " + formatEnergy(maxStore))
            );
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }

        final int heatLeft = this.leftPos + HEAT_BAR_X;
        final int heatTop = this.topPos + HEAT_BAR_Y;
        final int heatRight = heatLeft + HEAT_BAR_W;
        final int heatBottom = heatTop + HEAT_BAR_H;

        if (mouseX >= heatLeft && mouseX <= heatRight && mouseY >= heatTop && mouseY <= heatBottom) {
            final int temp = this.menu.temperature;
            final int maxTemp = this.menu.maxTemperature;
            final int overload = this.menu.overloadTimer;

            final List<Component> tooltip = new java.util.ArrayList<>();
            tooltip.add(Component.literal("§6Heat: §f" + temp + " / " + maxTemp + " HU"));

            final double pct = maxTemp > 0 ? ((double) temp / maxTemp * 100.0) : 0;
            tooltip.add(Component.literal("§7" + String.format("%.1f%%", pct) + " capacity"));

            if (overload > 0) {
                tooltip.add(Component.literal("§c§l⚠ CRITICAL OVERLOAD IN " + (overload / 20) + "s!"));
            } else if (pct >= 80) {
                tooltip.add(Component.literal("§c⚠ DANGER: Hazard zone active!"));
            } else if (pct >= 50) {
                tooltip.add(Component.literal("§e⚠ Warning: High temperature"));
            }

            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
    }

    /**
     * Formats an energy value using a compact, human-readable unit.
     */
    private static String formatEnergy(final double energy) {
        if (energy >= 1_000_000_000) {
            return String.format("%.1fG AE", energy / 1_000_000_000.0);
        } else if (energy >= 1_000_000) {
            return String.format("%.1fM AE", energy / 1_000_000.0);
        } else if (energy >= 1_000) {
            return String.format("%.1fK AE", energy / 1_000.0);
        }
        return String.format("%.0f AE", energy);
    }


    private static final class AlertWidget extends AbstractWidget {

        private final Blitter powerAlert;

        public AlertWidget(final Blitter powerAlert) {
            super(0, 0, 18, 18, Component.empty());
            this.powerAlert = powerAlert;
        }

        @Override
        protected void extractWidgetRenderState(
                final GuiGraphicsExtractor guiGraphics,
                final int mouseX,
                final int mouseY,
                final float partialTick) {
            if (this.powerAlert != null) {
                this.powerAlert.dest(this.getX(), this.getY()).blit(guiGraphics);
            }
        }

        @Override
        protected void updateWidgetNarration(final NarrationElementOutput narrationElementOutput) {}
    }
}
