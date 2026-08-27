package com.raishxn.ufo.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.init.ModRecipes;
import com.raishxn.ufo.network.ModPackets;
import com.raishxn.ufo.network.packet.PacketChangeStellarRecipe;
import com.raishxn.ufo.network.packet.PacketScanStellarStructure;
import com.raishxn.ufo.network.packet.PacketStartStellarOperation;
import com.raishxn.ufo.network.packet.PacketToggleStellarSafeMode;
import com.raishxn.ufo.network.packet.PacketToggleStellarAutoStart;
import com.raishxn.ufo.network.packet.PacketToggleStellarLock;
import com.raishxn.ufo.network.packet.PacketToggleStellarOverclock;
import com.raishxn.ufo.client.tutorial.UfoTutorialScreens;
import com.raishxn.ufo.recipe.StellarSimulationRecipe;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;

public class StellarNexusControllerScreen extends AbstractContainerScreen<StellarNexusControllerMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, "textures/gui/stellar_nexus_controller.png");

    private List<RecipeHolder<StellarSimulationRecipe>> availableRecipes = new ArrayList<>();
    private int currentRecipeIndex = 0;
    
    private Button prevButton;
    private Button nextButton;
    private Button startButton;
    private Button safeModeButton;
    private Button scanButton;
    private Button autoStartButton;
    private Button lockButton;
    private Button overclockButton;
    private Button tutorialButton;

    public StellarNexusControllerScreen(final StellarNexusControllerMenu pMenu, final Inventory pPlayerInventory, final Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 191;
        this.imageHeight = 160;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000; // hide player inventory label
        this.titleLabelY = 8;
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;

        if (this.minecraft != null && this.minecraft.level != null) {
            this.availableRecipes = new ArrayList<>();
            
            final Identifier activeId = this.menu.getBlockEntity().getActiveRecipeId();
            if (activeId != null) {
                for (int i = 0; i < this.availableRecipes.size(); i++) {
                    if (this.availableRecipes.get(i).id().equals(activeId)) {
                        this.currentRecipeIndex = i;
                        break;
                    }
                }
            }
        }

        final int recipeY = this.topPos + 64;
        this.prevButton = this.addRenderableWidget(Button.builder(Component.literal("<"), btn -> cycleRecipe(-1))
                .bounds(this.leftPos + 19, recipeY, 22, 22).build());

        this.nextButton = this.addRenderableWidget(Button.builder(Component.literal(">"), btn -> cycleRecipe(1))
                .bounds(this.leftPos + 150, recipeY, 22, 22).build());
        
        this.startButton = this.addRenderableWidget(Button.builder(
                Component.literal("\u25B6 START"), btn -> {
                    ModPackets.sendToServer(new PacketStartStellarOperation(this.menu.getBlockEntity().getBlockPos()));
                })
                .bounds(this.leftPos + 70, this.topPos + 126, 52, 16).build());

        this.safeModeButton = this.addRenderableWidget(Button.builder(
                Component.literal("\u2697 Safe"), btn -> {
                    ModPackets.sendToServer(new PacketToggleStellarSafeMode(this.menu.getBlockEntity().getBlockPos()));
                })
                .bounds(this.leftPos - 52, this.topPos + this.imageHeight - 54, 50, 16).build());

        this.autoStartButton = this.addRenderableWidget(Button.builder(
                Component.literal("\u21BB Auto"), btn -> {
                    ModPackets.sendToServer(new PacketToggleStellarAutoStart(this.menu.getBlockEntity().getBlockPos()));
                })
                .bounds(this.leftPos - 52, this.topPos + this.imageHeight - 72, 50, 16).build());

        this.lockButton = this.addRenderableWidget(Button.builder(
                Component.literal("\uD83D\uDD12 Lock"), btn -> {
                    ModPackets.sendToServer(new PacketToggleStellarLock(this.menu.getBlockEntity().getBlockPos()));
                })
                .bounds(this.leftPos - 52, this.topPos + this.imageHeight - 36, 50, 16).build());

        this.overclockButton = this.addRenderableWidget(Button.builder(
                Component.literal("\u26A1 O.C."), btn -> {
                    ModPackets.sendToServer(new PacketToggleStellarOverclock(this.menu.getBlockEntity().getBlockPos()));
                })
                .bounds(this.leftPos - 52, this.topPos + this.imageHeight - 90, 50, 16).build());

        this.scanButton = this.addRenderableWidget(Button.builder(
                Component.literal("\uD83D\uDD0D Scan"), btn -> {
                    ModPackets.sendToServer(new PacketScanStellarStructure(this.menu.getBlockEntity().getBlockPos()));
                    
                    if (this.minecraft != null && this.minecraft.level != null && this.minecraft.player != null) {
                        final BlockPos pos = this.menu.getBlockEntity().getBlockPos();
                        final net.minecraft.world.level.block.state.BlockState state = this.minecraft.level.getBlockState(pos);
                        net.minecraft.core.Direction facing = net.minecraft.core.Direction.NORTH;
                        if (state.hasProperty(net.minecraft.world.level.block.DirectionalBlock.FACING)) {
                            facing = state.getValue(net.minecraft.world.level.block.DirectionalBlock.FACING);
                        }
                        final com.raishxn.ufo.api.multiblock.MultiblockPattern.MatchResult result = com.raishxn.ufo.block.entity.pattern.StellarNexusPatternFactory.getPattern().match(this.minecraft.level, pos, facing);
                        
                        if (!result.isValid()) {
                            final java.util.List<com.raishxn.ufo.api.multiblock.MultiblockPattern.PatternError> errors = result.allErrors();
                            if (errors != null && !errors.isEmpty()) {
                                final int shown = Math.min(errors.size(), 10);
                                this.minecraft.player.sendSystemMessage(Component.literal("§e[Stellar Nexus] §c" + errors.size() + " block(s) missing or misplaced:"));
                                for (int i = 0; i < shown; i++) {
                                    final var error = errors.get(i);
                                    final BlockPos errorPos = error.pos();
                                    final Component message = Component.literal("  §7• [" + errorPos.getX() + ", " + errorPos.getY() + ", " + errorPos.getZ() + "] §fExpected: ")
                                            .append(error.expected().copy().withStyle(net.minecraft.ChatFormatting.YELLOW));
                                    this.minecraft.player.sendSystemMessage(message);
                                }
                                if (errors.size() > shown) {
                                    this.minecraft.player.sendSystemMessage(Component.literal("  §7... and " + (errors.size() - shown) + " more."));
                                }
                                final int maxHighlight = Math.min(errors.size(), 50);
                                for (int i = 0; i < maxHighlight; i++) {
                                    com.raishxn.ufo.client.render.StructureHighlightRenderer.highlight(errors.get(i).pos(), 5000);
                                }
                            }
                        } else if (!this.menu.getBlockEntity().isAssembled()) {
                             this.minecraft.player.sendSystemMessage(Component.literal("§e[Stellar Nexus] §cStructure shape is valid, but hatch requirements are not met!"));
                             this.minecraft.player.sendSystemMessage(Component.literal("§7Required: exactly 1 of each: ME Output Hatch, ME Fluid Hatch, ME Input Hatch, AE Energy Hatch."));
                        } else {
                            this.minecraft.player.sendOverlayMessage(Component.translatable("message.ufo.structure_formed").withStyle(net.minecraft.ChatFormatting.GREEN));
                        }
                    }
                })
                .bounds(this.leftPos - 52, this.topPos + this.imageHeight - 18, 50, 16).build());
        this.scanButton.setTooltip(Tooltip.create(Component.literal("§e\uD83D\uDD0D Scan Structure\n§7Force re-scan the multiblock.\n§7Missing blocks will be reported.")));

        this.tutorialButton = this.addRenderableWidget(Button.builder(
                Component.literal("?"), btn -> UfoTutorialScreens.openFor(this.menu.getBlockEntity()))
                .bounds(this.leftPos - 24, this.topPos + this.imageHeight - 108, 22, 16)
                .tooltip(Tooltip.create(Component.translatable("ufo.tutorial.open")))
                .build());

        updateButtonTooltips();
    }

    private void updateButtonTooltips() {
        if (this.availableRecipes.isEmpty() || this.prevButton == null || this.nextButton == null) return;
        
        final int prevIndex = (this.currentRecipeIndex - 1 + this.availableRecipes.size()) % this.availableRecipes.size();
        final int nextIndex = (this.currentRecipeIndex + 1) % this.availableRecipes.size();
        
        final Component prevComp = Component.translatable("gui.ufo.previous").append(": ").append(Component.literal(getRecipeDisplayName(this.availableRecipes.get(prevIndex))));
        final Component nextComp = Component.translatable("gui.ufo.next").append(": ").append(Component.literal(getRecipeDisplayName(this.availableRecipes.get(nextIndex))));
        
        this.prevButton.setTooltip(Tooltip.create(prevComp));
        this.nextButton.setTooltip(Tooltip.create(nextComp));
        
        if (this.startButton != null) {
            boolean canStart = this.menu.isAssembled() && !this.menu.isRunning() && this.menu.getCooldownTimer() == 0;
            
            final StellarSimulationRecipe recipe = this.availableRecipes.isEmpty() ? null : this.availableRecipes.get(this.currentRecipeIndex).value();

            final List<Component> tipLines = new ArrayList<>();
            tipLines.add(Component.literal("§e▶ Start Stellar Simulation"));
            
            if (recipe != null) {
                if (this.menu.isAssembled()) {
                    tipLines.add(Component.literal("§a✓ Structure assembled"));
                } else {
                    tipLines.add(Component.literal("§c✗ Structure not assembled"));
                    canStart = false;
                }
                
                if (this.menu.isRunning()) {
                    tipLines.add(Component.literal("§c✗ Already in operation"));
                    canStart = false;
                }
                
                if (this.menu.getCooldownTimer() > 0) {
                    final int secLeft = this.menu.getCooldownTimer() / 20;
                    tipLines.add(Component.literal("§c✗ Cooling down: " + secLeft + "s remaining"));
                    canStart = false;
                }
                
                final int fl = this.menu.getFieldLevel();
                if (fl >= recipe.getFieldTier()) {
                    tipLines.add(Component.literal("§a✓ Field Generator: Mk." + toRoman(fl) + " (Req: Mk." + toRoman(recipe.getFieldTier()) + ")"));
                } else {
                    tipLines.add(Component.literal("§c✗ Field Generator: Mk." + toRoman(fl) + " (Req: Mk." + toRoman(recipe.getFieldTier()) + ")"));
                    canStart = false;
                }
                
                final long eBuffer = this.menu.getEnergyBuffer();
                double multiplier = this.menu.isSafeMode() ? 2.5 : 1.0;
                if (this.menu.isOverclocked()) multiplier *= 10.0;
                final long eCost = (long)(recipe.getEnergyCost() * multiplier);
                String safeNote = this.menu.isSafeMode() ? " (2.5x Safe Mode)" : "";
                if (this.menu.isOverclocked()) safeNote += " (10x O.C.)";
                if (eBuffer >= eCost) {
                    tipLines.add(Component.literal("§a✓ Energy: " + formatAmount(eBuffer) + " / " + formatAmount(eCost) + " AE" + safeNote));
                } else {
                    tipLines.add(Component.literal("§c✗ Energy: " + formatAmount(eBuffer) + " / " + formatAmount(eCost) + " AE" + safeNote));
                    canStart = false;
                }
                
                if (recipe.getFuelAmount() > 0 && !recipe.getFuelFluid().isEmpty()) {
                    final Identifier fRL = Identifier.parse(recipe.getFuelFluid());
                    double fMult = this.menu.isSafeMode() ? 2.5 : 1.0;
                    if (this.menu.isOverclocked()) fMult *= 5.0;
                    final long fAmount = (long)(recipe.getFuelAmount() * fMult);
                    tipLines.add(Component.literal("§7◇ Requires " + formatAmount(fAmount) + "mB " + fRL.getPath()));
                }
                if (recipe.getCoolantAmount() > 0) {
                    tipLines.add(Component.literal("§7◇ Requires " + formatAmount(recipe.getCoolantAmount()) + "mB Coolant (Tier " + recipe.getCoolingLevel() + ")"));
                }
                if (!recipe.getItemInputs().isEmpty() || !recipe.getFluidInputs().isEmpty()) {
                    tipLines.add(Component.literal("§7◇ Items/Fluids must be in ME Network"));
                }
            } else {
                tipLines.add(Component.literal("§c✗ No program selected"));
                canStart = false;
            }
            
            this.startButton.active = canStart;
            
            Component combined = tipLines.get(0);
            for (int i = 1; i < tipLines.size(); i++) {
                combined = combined.copy().append(Component.literal("\n")).append(tipLines.get(i));
            }
            this.startButton.setTooltip(Tooltip.create(combined));
        }
        
        if (this.safeModeButton != null) {
            final boolean safe = this.menu.isSafeMode();
            this.safeModeButton.setTooltip(Tooltip.create(
                    Component.literal(safe ? "§aSafe Mode: ON\n§7Auto-shutdown on overheat" : "§cSafe Mode: OFF\n§4WARNING: Explosion on overheat!")
            ));
        }

        if (this.autoStartButton != null) {
            final boolean auto = this.menu.isAutoStart();
            this.autoStartButton.setTooltip(Tooltip.create(
                    Component.literal(auto ? "§aAuto-Start: ON\n§7Automatically loops simulation." : "§cAuto-Start: OFF\n§7Manual start required.")
            ));
        }

        if (this.lockButton != null) {
            final boolean locked = this.menu.isSimulationLocked();
            this.lockButton.setTooltip(Tooltip.create(
                    Component.literal(locked ? "§aSimulation Locked\n§7Prevents changing recipe." : "§cSimulation Unlocked\n§7Recipe can be changed.")
            ));
        }

        if (this.overclockButton != null) {
            final boolean isOC = this.menu.isOverclocked();
            this.overclockButton.setTooltip(Tooltip.create(
                    Component.literal(isOC ? "§aOverclock: ON\n§710x Energy, 5x Heat/Fuel/Speed.\n§cMassive cooldown penalty." : "§cOverclock: OFF\n§7Normal simulation values.")
            ));
        }
    }

    private void cycleRecipe(final int delta) {
        if (this.availableRecipes.isEmpty() || this.menu.isSimulationLocked()) return;
        
        this.currentRecipeIndex = (this.currentRecipeIndex + delta) % this.availableRecipes.size();
        if (this.currentRecipeIndex < 0) {
            this.currentRecipeIndex += this.availableRecipes.size();
        }

        final Identifier newId = this.availableRecipes.get(this.currentRecipeIndex).id();
        ModPackets.sendToServer(new PacketChangeStellarRecipe(this.menu.getBlockEntity().getBlockPos(), newId));
        updateButtonTooltips();
    }

    @Override
    protected void renderBg(final GuiGraphicsExtractor guiGraphics, final float pPartialTick, final int pMouseX, final int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, 191, 160);

        final int p = this.menu.getProgress();
        final int max = this.menu.getTotalTime();
        if (max > 0 && p > 0) {
            final int progressWidth = (int) ((float) p / max * 157.0f);
            guiGraphics.blit(TEXTURE, this.leftPos + 17, this.topPos + 106, 17, 180, progressWidth, 12);
        }
    }

    @Override
    public void render(final GuiGraphicsExtractor guiGraphics, final int mouseX, final int mouseY, final float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        renderCustomTooltips(guiGraphics, mouseX, mouseY);
        updateButtonTooltips();
    }

    private void renderCustomTooltips(final GuiGraphicsExtractor guiGraphics, final int mouseX, final int mouseY) {
        final int localX = mouseX - this.leftPos;
        final int localY = mouseY - this.topPos;

        if (localX >= 13 && localX <= 66 && localY >= 129 && localY <= 139) {
            final int fl = this.menu.getFieldLevel();
            final List<Component> tips = new ArrayList<>();
            tips.add(Component.literal("§d⚛ Stellar Field Generator"));
            if (fl > 0) {
                tips.add(Component.literal("§7Current Tier: §e Mk." + toRoman(fl)));
                tips.add(Component.literal("§7Higher tiers enable more advanced simulations."));
            } else {
                tips.add(Component.literal("§cNo Field Generator detected."));
                tips.add(Component.literal("§7Place a Stellar Field Generator in the structure."));
            }
            guiGraphics.renderTooltip(this.font, tips, java.util.Optional.empty(), mouseX, mouseY);
        }

        if (localX >= 125 && localX <= 178 && localY >= 129 && localY <= 139) {
            final int energyPct = this.menu.getEnergyPercent();
            final long energyBuffer = this.menu.getEnergyBuffer();
            final long energyCapacity = this.menu.getEnergyCapacity();
            final List<Component> tips = new ArrayList<>();
            tips.add(Component.literal("§b⚡ Global AE Energy Buffer"));
            tips.add(Component.literal("§7Stored: §e" + String.format("%,d", energyBuffer) + " / " + String.format("%,d", energyCapacity) + " AE"));
            tips.add(Component.literal("§7Charged: §e" + energyPct + "%"));
            tips.add(Component.literal("§7This is a universal buffer shared by all"));
            tips.add(Component.literal("§7simulations. High-tier safe mode runs"));
            tips.add(Component.literal("§7may require > 1 Billion AE."));
            if (energyPct >= 100) {
                tips.add(Component.literal("§a✓ Energy fully charged."));
            }
            guiGraphics.renderTooltip(this.font, tips, java.util.Optional.empty(), mouseX, mouseY);
        }

        if (localX >= 125 && localX <= 175 && localY >= 40 && localY <= 55) {
            final int heat = this.menu.getHeatLevel();
            final float heatPct = heat / 10.0f;
            final List<Component> tips = new ArrayList<>();
            tips.add(Component.literal("§c🌡 Thermal Status"));
            tips.add(Component.literal("§7Heat Level: §e" + String.format("%.1f%%", heatPct)));
            tips.add(Component.literal("§7Coolant reduces heat during operation."));
            if (this.menu.isSafeMode()) {
                tips.add(Component.literal("§a⚗ Safe Mode: Auto-shutdown on overheat"));
            } else {
                tips.add(Component.literal("§4⚗ WARNING: Explosion on overheat!"));
            }
            guiGraphics.renderTooltip(this.font, tips, java.util.Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(final GuiGraphicsExtractor guiGraphics, final int pMouseX, final int pMouseY) {
        final Component statusText;
        if (this.menu.getCooldownTimer() > 0) {
            final int secLeft = this.menu.getCooldownTimer() / 20;
            final int minLeft = secLeft / 60;
            final int secRemain = secLeft % 60;
            statusText = Component.literal(String.format("Cooldown %dm%02ds", minLeft, secRemain)).withStyle(ChatFormatting.GOLD);
        } else if (this.menu.isRunning()) {
            statusText = Component.literal("Running").withStyle(ChatFormatting.AQUA);
        } else if (this.menu.isAssembled()) {
            statusText = Component.literal("Assembled").withStyle(ChatFormatting.GREEN);
        } else {
            statusText = Component.literal("Offline").withStyle(ChatFormatting.RED);
        }
        guiGraphics.drawString(this.font, statusText, 15, 44, 0xFFFFFF);

        final int heat = this.menu.getHeatLevel();
        final float heatPct = heat / 10.0f;
        final int heatColor;
        final String heatStr;
        if (heat == 0) {
            heatStr = "Stable";
            heatColor = 0x00FFFF;
        } else if (heatPct < 50) {
            heatStr = String.format("%.0f%%", heatPct);
            heatColor = 0xFFFF00;
        } else if (heatPct < 80) {
            heatStr = String.format("%.0f%%", heatPct);
            heatColor = 0xFF8800;
        } else {
            heatStr = String.format("%.0f%% \u26A0", heatPct);
            heatColor = 0xFF0000;
        }
        guiGraphics.drawString(this.font, heatStr, 127, 44, heatColor);

        Component recipeName = Component.literal("No Program");
        if (!this.availableRecipes.isEmpty()) {
            recipeName = Component.literal(getRecipeDisplayName(this.availableRecipes.get(this.currentRecipeIndex)));
        }
        
        final int textWidth = this.font.width(recipeName);
        final int textX = 44 + ((103 - textWidth) / 2);
        guiGraphics.drawString(this.font, recipeName, textX, 72, 0xFFFFFF);
        
        final int p = this.menu.getProgress();
        final int max = this.menu.getTotalTime();
        if (max > 0) {
            final String prog = String.format("%.1f %%", (p / (float)max) * 100f);
            guiGraphics.drawString(this.font, prog, 22, 95, 0x00FF00);
        }
        
        final int fl = this.menu.getFieldLevel();
        final String fieldLevel = fl > 0 ? "Mk." + toRoman(fl) : "Lv. 0";
        final int fieldColor = fl >= 3 ? 0xFF00FF : (fl >= 2 ? 0xAA00AA : (fl >= 1 ? 0x8800AA : 0x666666));
        guiGraphics.drawString(this.font, fieldLevel, 15, 131, fieldColor);
        
        final int energyPct = this.menu.getEnergyPercent();
        final String energyAmount = energyPct + "%";
        final int energyColor = energyPct >= 100 ? 0x00FF00 : (energyPct > 50 ? 0xFFFF00 : 0xFF4444);
        guiGraphics.drawString(this.font, energyAmount, 127, 131, energyColor);

        final boolean safeMode = this.menu.isSafeMode();
        guiGraphics.drawString(this.font, safeMode ? "§a●" : "§c●", -3, 110, 0xFFFFFF);

        final boolean autoStart = this.menu.isAutoStart();
        guiGraphics.drawString(this.font, autoStart ? "§a●" : "§c●", -3, 92, 0xFFFFFF);

        final boolean isLocked = this.menu.isSimulationLocked();
        guiGraphics.drawString(this.font, isLocked ? "§a●" : "§c●", -3, 128, 0xFFFFFF);

        final boolean overclocked = this.menu.isOverclocked();
        guiGraphics.drawString(this.font, overclocked ? "§a●" : "§c●", -3, 74, 0xFFFFFF);
    }

    /**
     * Gets the display name for a recipe.
     * Uses the simulationName field if available, otherwise formats the recipe ID.
     */
    private String getRecipeDisplayName(final RecipeHolder<StellarSimulationRecipe> holder) {
        final String simName = holder.value().getSimulationName();
        if (simName != null && !simName.isEmpty()) {
            return simName;
        }
        return formatRecipeId(holder.id());
    }

    private String formatRecipeId(final Identifier id) {
        String path = id.getPath();
        final int slashIndex = path.lastIndexOf('/');
        if (slashIndex != -1) {
            path = path.substring(slashIndex + 1);
        }
        if (path.startsWith("stellar_")) {
            path = path.substring("stellar_".length());
        }
        final String[] words = path.split("_");
        final StringBuilder sb = new StringBuilder();
        for (final String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
    
    private static String formatAmount(final long amount) {
        if (amount >= 1_000_000_000) return String.format("%.1fB", amount / 1_000_000_000.0);
        if (amount >= 1_000_000) return String.format("%.1fM", amount / 1_000_000.0);
        if (amount >= 1_000) return String.format("%.1fK", amount / 1_000.0);
        return String.valueOf(amount);
    }
    
    private static String toRoman(final int tier) {
        return switch (tier) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            default -> String.valueOf(tier);
        };
    }
}
