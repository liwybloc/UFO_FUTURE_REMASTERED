package com.raishxn.ufo.screen;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.SlotSemantics;
import com.mojang.blaze3d.systems.RenderSystem;
import com.raishxn.ufo.api.multiblock.IMultiblockController;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinition;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.api.multiblock.MultiblockPattern;
import com.raishxn.ufo.block.entity.UniversalDisplayedRecipe;
import com.raishxn.ufo.client.render.StructureHighlightRenderer;
import com.raishxn.ufo.client.tutorial.UfoTutorialScreens;
import com.raishxn.ufo.network.ModPackets;
import com.raishxn.ufo.network.packet.PacketScanUniversalStructure;
import com.raishxn.ufo.network.packet.PacketToggleUniversalOverclock;
import com.raishxn.ufo.network.packet.PacketToggleUniversalSafeMode;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractUniversalMultiblockControllerScreen<M extends AbstractUniversalMultiblockControllerMenu<?>>
        extends UpgradeableScreen<M> {

    private Button safeModeButton;
    private Button overclockButton;
    private Button scanButton;
    private Button tutorialButton;
    private List<UniversalDisplayedRecipe> cachedDisplayedRecipes = List.of();
    private List<GroupedRecipe> cachedGroupedRecipes = List.of();
    private int cachedRecipeSignature = 0;

    protected AbstractUniversalMultiblockControllerScreen(M menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.inventoryLabelY = 1000;
        this.titleLabelY = 1000;
    }

    @Override
    protected void init() {
        super.init();
        setSlotsHidden(SlotSemantics.PLAYER_INVENTORY, true);
        setSlotsHidden(SlotSemantics.PLAYER_HOTBAR, true);

        this.scanButton = this.addRenderableWidget(Button.builder(Component.literal("Scan"), btn -> {
                    BlockPos pos = this.menu.getBlockEntity().getBlockPos();
                    ModPackets.sendToServer(new PacketScanUniversalStructure(pos));
                    runLocalStructureScan(pos);
                })
                .bounds(this.leftPos + this.imageWidth - 88, this.topPos + this.imageHeight - 24, 42, 20)
                .tooltip(Tooltip.create(Component.literal("Force a multiblock scan and report missing blocks")))
                .build());

        this.tutorialButton = this.addRenderableWidget(Button.builder(Component.literal("?"), btn ->
                        UfoTutorialScreens.openFor(this.menu.getBlockEntity()))
                .bounds(this.leftPos + this.imageWidth - 110, this.topPos + this.imageHeight - 24, 20, 20)
                .tooltip(Tooltip.create(Component.translatable("ufo.tutorial.open")))
                .build());

        this.safeModeButton = this.addRenderableWidget(Button.builder(Component.literal("Safe"), btn ->
                        ModPackets.sendToServer(new PacketToggleUniversalSafeMode(this.menu.getBlockEntity().getBlockPos())))
                .bounds(this.leftPos + this.imageWidth - 44, this.topPos + this.imageHeight - 24, 20, 20)
                .tooltip(Tooltip.create(Component.literal("Toggle Safe Mode")))
                .build());

        this.overclockButton = this.addRenderableWidget(Button.builder(Component.literal("OC"), btn ->
                        ModPackets.sendToServer(new PacketToggleUniversalOverclock(this.menu.getBlockEntity().getBlockPos())))
                .bounds(this.leftPos + this.imageWidth - 22, this.topPos + this.imageHeight - 24, 20, 20)
                .tooltip(Tooltip.create(Component.literal("Toggle Overclock")))
                .build());
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTick) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTick);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        renderTemperatureBar(guiGraphics);
        renderRecipeList(guiGraphics);
    }

    private void renderTemperatureBar(GuiGraphics guiGraphics) {
        int barX = this.leftPos + 14;
        int barY = this.topPos + 9;
        int barWidth = 146;
        int barHeight = 10;
        int filled = (int) (barWidth * Math.min(1.0F, this.menu.getTemperature() / (float) this.menu.getMaxTemperature()));

        if (filled > 0) {
            guiGraphics.fill(barX, barY, barX + filled, barY + barHeight, 0xCCB32020);
        }
    }

    private void renderRecipeList(GuiGraphics guiGraphics) {
        int listX = this.leftPos + 7;
        int listY = this.topPos + 30;
        int lineHeight = 10;
        int maxTextWidth = 156;
        List<GroupedRecipe> recipes = buildGroupedRecipes();

        guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(getScreenTitle().getString(), maxTextWidth), listX, listY, 0xF0F0F0, false);
        guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(buildStatusLine(), maxTextWidth), listX, listY + 10, 0xD0D7E6, false);
        renderSummaryLine(guiGraphics, listX, listY + 20, maxTextWidth, recipes);

        for (int i = 0; i < 8; i++) {
            int rowY = listY + 32 + i * lineHeight;
            if (i < recipes.size()) {
                renderRecipeRow(guiGraphics, recipes.get(i), listX, rowY);
            } else if (i == 0 && recipes.isEmpty()) {
                guiGraphics.drawString(this.font, this.menu.isAssembled() ? "No active recipes." : "Structure incomplete.", listX, rowY, 0x8A91A6, false);
            }
        }
    }

    private void renderRecipeRow(GuiGraphics guiGraphics, GroupedRecipe groupedRecipe, int x, int y) {
        UniversalDisplayedRecipe recipe = groupedRecipe.recipe();
        ItemStack iconStack = recipe.itemIcon();
        boolean fluidRecipe = false;
        if (iconStack.isEmpty()) {
            FluidStack fluid = recipe.fluidIcon();
            if (!fluid.isEmpty()) {
                iconStack = new ItemStack(fluid.getFluid().getBucket());
                fluidRecipe = true;
            }
        }

        int textX = x;
        if (!iconStack.isEmpty()) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(x, y - 1, 0.0F);
            guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
            guiGraphics.renderItem(iconStack, 0, 0);
            guiGraphics.pose().popPose();
            textX += 10;
        }

        String amount = fluidRecipe ? formatAmount(groupedRecipe.totalOutputAmount()) + "mB" : formatAmount(groupedRecipe.totalOutputAmount()) + "x";
        String time = groupedRecipe.displayMaxProgress() > 0
                ? formatSeconds(groupedRecipe.displayProgress()) + "/" + formatSeconds(groupedRecipe.displayMaxProgress()) + "s"
                : "-/-";
        int timeWidth = this.font.width(time);
        int availableWidth = Math.max(20, 156 - (textX - x) - timeWidth - 4);
        String leftText = amount + " " + recipe.label().getString();
        if (groupedRecipe.copyCount() > 1) {
            leftText += " [" + groupedRecipe.copyCount() + "]";
        }
        guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(leftText, availableWidth), textX, y, 0xE6EBF5, false);
        guiGraphics.drawString(this.font, time, this.leftPos + 162 - timeWidth, y, 0xB9D8FF, false);
    }

    private String buildStatusLine() {
        if (!this.menu.isAssembled()) {
            return "Status: Incomplete";
        }
        StringBuilder builder = new StringBuilder(this.menu.isRunning() ? "RUN" : "IDLE");
        builder.append(" | MK").append(this.menu.getMachineTier());
        builder.append(" | ").append(this.menu.isSafeMode() ? "SAFE" : "RISK");
        builder.append(" | ").append(this.menu.isOverclocked() ? "OC" : "STD");
        return builder.toString();
    }

    private void renderSummaryLine(GuiGraphics guiGraphics, int x, int y, int maxTextWidth, List<GroupedRecipe> recipes) {
        String summaryText = buildSummaryText(recipes);
        guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(summaryText, maxTextWidth), x, y, 0xB9D8FF, false);
    }

    private String buildSummaryText(List<GroupedRecipe> recipes) {
        return "AE " + formatAmount(this.menu.getStoredEnergy())
                + " | A" + this.menu.getActiveParallels()
                + " U" + recipes.size()
                + " " + buildParallelSummaryText();
    }

    private String buildParallelSummaryText() {
        return "P" + this.menu.getActiveParallels() + "/" + this.menu.getMaxParallels();
    }

    private String buildParallelText() {
        return "Parallel: " + this.menu.getActiveParallels() + "/" + this.menu.getMaxParallels();
    }

    private Component getScreenTitle() {
        String raw = this.title.getString();
        if (raw.endsWith(" Controller")) {
            raw = raw.substring(0, raw.length() - " Controller".length());
        }
        return Component.literal(raw);
    }

    private static String formatAmount(long amount) {
        if (amount >= 1_000_000_000L) {
            return String.format(Locale.ROOT, "%.1fB", amount / 1_000_000_000.0);
        }
        if (amount >= 1_000_000L) {
            return String.format(Locale.ROOT, "%.1fM", amount / 1_000_000.0);
        }
        if (amount >= 1_000L) {
            return String.format(Locale.ROOT, "%.1fK", amount / 1_000.0);
        }
        return Long.toString(amount);
    }

    private static String formatSeconds(int ticks) {
        double seconds = ticks / 20.0;
        return seconds >= 100
                ? String.format(Locale.ROOT, "%.0f", seconds)
                : String.format(Locale.ROOT, "%.1f", seconds);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        refreshRecipeCache();
        if (this.safeModeButton != null) {
            boolean safe = this.menu.isSafeMode();
            this.safeModeButton.setMessage(Component.literal(safe ? "S" : "!"));
            this.safeModeButton.setTooltip(Tooltip.create(Component.literal(safe ? "Safe Mode enabled" : "Safe Mode disabled")));
            this.safeModeButton.active = !this.menu.isRunning() && this.menu.getDisplayedRecipes().isEmpty();
        }
        if (this.overclockButton != null) {
            boolean oc = this.menu.isOverclocked();
            this.overclockButton.setMessage(Component.literal(oc ? "OC" : ">"));
            this.overclockButton.setTooltip(Tooltip.create(Component.literal(oc ? "Overclock enabled" : "Overclock disabled")));
            this.overclockButton.active = !this.menu.isRunning() && this.menu.getDisplayedRecipes().isEmpty();
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovering(14, 9, 146, 10, mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font,
                    Component.literal("Temperature: " + this.menu.getTemperature() + " / " + this.menu.getMaxTemperature()),
                    mouseX, mouseY);
            return;
        }
        if (isHoveringParallelText(mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, buildParallelTooltip().stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
            return;
        }
        if (isHoveringSummaryLine(mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, buildSummaryTooltip().stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
            return;
        }
        GroupedRecipe hoveredRecipe = getHoveredGroupedRecipe(mouseX, mouseY);
        if (hoveredRecipe != null) {
            guiGraphics.renderTooltip(this.font, buildGroupedRecipeTooltip(hoveredRecipe).stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
            return;
        }
        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private boolean isHoveringParallelText(int mouseX, int mouseY) {
        int listX = this.leftPos + 7;
        int lineY = this.topPos + 50;
        String energyText = "AE " + formatAmount(this.menu.getStoredEnergy()) + " | A" + this.menu.getActiveParallels()
                + " U" + buildGroupedRecipes().size() + " | ";
        String parallelText = buildParallelSummaryText();
        String fullText = energyText + parallelText;
        int maxTextWidth = 156;
        int fullWidth = this.font.width(fullText);

        if (fullWidth > maxTextWidth) {
            return false;
        }

        int parallelX = listX + this.font.width(energyText);
        int parallelWidth = this.font.width(parallelText);
        return mouseX >= parallelX
                && mouseX < parallelX + parallelWidth
                && mouseY >= lineY
                && mouseY < lineY + this.font.lineHeight;
    }

    private boolean isHoveringSummaryLine(int mouseX, int mouseY) {
        int listX = this.leftPos + 7;
        int lineY = this.topPos + 50;
        return mouseX >= listX
                && mouseX < listX + 156
                && mouseY >= lineY
                && mouseY < lineY + this.font.lineHeight;
    }

    private List<Component> buildSummaryTooltip() {
        List<GroupedRecipe> groupedRecipes = buildGroupedRecipes();
        long totalOutput = 0L;
        double totalPerSecond = 0.0D;
        for (GroupedRecipe recipe : groupedRecipes) {
            totalOutput += recipe.totalOutputAmount();
            if (recipe.displayMaxProgress() > 0) {
                totalPerSecond += recipe.totalOutputAmount() * 20.0D / recipe.displayMaxProgress();
            }
        }

        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("AE Energy: " + formatAmount(this.menu.getStoredEnergy()) + " AE"));
        lines.add(Component.literal("Active recipe copies: " + this.menu.getActiveParallels()));
        lines.add(Component.literal("Unique recipe groups: " + groupedRecipes.size()));
        lines.add(Component.literal("Parallel usage: " + this.menu.getActiveParallels() + " / " + this.menu.getMaxParallels()));
        lines.add(Component.literal("Grouped output on screen: " + formatAmount(totalOutput)));
        lines.add(Component.literal("Estimated output rate: " + formatAmount((long) totalPerSecond) + "/s"));
        return lines;
    }

    private java.util.List<Component> buildParallelTooltip() {
        var recipes = this.menu.getDisplayedRecipes();
        Map<String, Integer> recipeCounts = new LinkedHashMap<>();
        for (UniversalDisplayedRecipe recipe : recipes) {
            recipeCounts.merge(recipe.label().getString(), 1, Integer::sum);
        }

        int active = this.menu.getActiveParallels();
        int distinct = recipeCounts.size();
        int repeated = Math.max(0, active - distinct);
        java.util.List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("Parallel: " + active + " / " + this.menu.getMaxParallels()));
        lines.add(Component.literal("Different recipes: " + distinct));
        lines.add(Component.literal("Repeated instances: " + repeated));

        recipeCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(6)
                .forEach(entry -> lines.add(Component.literal(entry.getValue() + "x " + entry.getKey())));

        return lines;
    }

    private List<GroupedRecipe> buildGroupedRecipes() {
        refreshRecipeCache();
        return this.cachedGroupedRecipes;
    }

    private void refreshRecipeCache() {
        List<UniversalDisplayedRecipe> displayedRecipes = this.menu.getDisplayedRecipes();
        int signature = computeRecipeSignature(displayedRecipes);
        if (signature == this.cachedRecipeSignature && displayedRecipes.size() == this.cachedDisplayedRecipes.size()) {
            return;
        }

        this.cachedDisplayedRecipes = displayedRecipes;
        this.cachedRecipeSignature = signature;

        Map<RecipeGroupKey, GroupAccumulator> groups = new LinkedHashMap<>();
        for (UniversalDisplayedRecipe recipe : displayedRecipes) {
            RecipeGroupKey key = RecipeGroupKey.of(recipe);
            groups.computeIfAbsent(key, ignored -> new GroupAccumulator(recipe)).add(recipe);
        }

        List<GroupedRecipe> groupedRecipes = new ArrayList<>();
        for (GroupAccumulator accumulator : groups.values()) {
            groupedRecipes.add(accumulator.toGroupedRecipe());
        }
        this.cachedGroupedRecipes = groupedRecipes;
    }

    private static int computeRecipeSignature(List<UniversalDisplayedRecipe> recipes) {
        int signature = 1;
        for (UniversalDisplayedRecipe recipe : recipes) {
            signature = 31 * signature + recipe.progress();
            signature = 31 * signature + recipe.maxProgress();
            signature = 31 * signature + Long.hashCode(recipe.outputAmount());
            signature = 31 * signature + recipe.label().getString().hashCode();
            signature = 31 * signature + Objects.hashCode(BuiltInRegistries.ITEM.getKey(recipe.itemIcon().getItem()));
            signature = 31 * signature + Objects.hashCode(BuiltInRegistries.FLUID.getKey(recipe.fluidIcon().getFluid()));
        }
        return signature;
    }

    private GroupedRecipe getHoveredGroupedRecipe(int mouseX, int mouseY) {
        int listX = this.leftPos + 7;
        int listY = this.topPos + 30;
        int lineHeight = 10;
        List<GroupedRecipe> recipes = buildGroupedRecipes();
        for (int i = 0; i < Math.min(8, recipes.size()); i++) {
            int rowY = listY + 32 + i * lineHeight;
            if (mouseX >= listX && mouseX < listX + 156 && mouseY >= rowY && mouseY < rowY + lineHeight) {
                return recipes.get(i);
            }
        }
        return null;
    }

    private List<Component> buildGroupedRecipeTooltip(GroupedRecipe groupedRecipe) {
        UniversalDisplayedRecipe recipe = groupedRecipe.recipe();
        List<Component> lines = new ArrayList<>();
        lines.add(recipe.label());
        lines.add(Component.literal("Parallel copies: " + groupedRecipe.copyCount()));
        lines.add(Component.literal("Total output: " + formatAmount(groupedRecipe.totalOutputAmount())
                + (recipe.fluidIcon().isEmpty() ? "x" : "mB")));
        lines.add(Component.literal("Displayed time: " + formatSeconds(groupedRecipe.displayProgress())
                + "/" + formatSeconds(groupedRecipe.displayMaxProgress()) + "s"));
        if (groupedRecipe.hasMixedProgress()) {
            lines.add(Component.literal("Progress spread: " + formatSeconds(groupedRecipe.minProgress())
                    + "s to " + formatSeconds(groupedRecipe.maxProgress()) + "s"));
        }
        return lines;
    }

    private record GroupedRecipe(
            UniversalDisplayedRecipe recipe,
            int copyCount,
            long totalOutputAmount,
            int displayProgress,
            int displayMaxProgress,
            int minProgress,
            int maxProgress,
            boolean hasMixedProgress) {
    }

    private record RecipeGroupKey(String itemId, String fluidId, String label, long outputAmount, int maxProgress) {
        private static RecipeGroupKey of(UniversalDisplayedRecipe recipe) {
            String itemId = recipe.itemIcon().isEmpty()
                    ? ""
                    : String.valueOf(BuiltInRegistries.ITEM.getKey(recipe.itemIcon().getItem()));
            String fluidId = recipe.fluidIcon().isEmpty()
                    ? ""
                    : String.valueOf(BuiltInRegistries.FLUID.getKey(recipe.fluidIcon().getFluid()));
            return new RecipeGroupKey(itemId, fluidId, recipe.label().getString(), recipe.outputAmount(), recipe.maxProgress());
        }
    }

    private static final class GroupAccumulator {
        private final UniversalDisplayedRecipe representative;
        private int count;
        private long totalOutput;
        private int totalProgress;
        private int minProgress = Integer.MAX_VALUE;
        private int maxProgress;

        private GroupAccumulator(UniversalDisplayedRecipe representative) {
            this.representative = representative;
        }

        private void add(UniversalDisplayedRecipe recipe) {
            this.count++;
            this.totalOutput += recipe.outputAmount();
            this.totalProgress += recipe.progress();
            this.minProgress = Math.min(this.minProgress, recipe.progress());
            this.maxProgress = Math.max(this.maxProgress, recipe.progress());
        }

        private GroupedRecipe toGroupedRecipe() {
            int averageProgress = this.count == 0 ? 0 : this.totalProgress / this.count;
            return new GroupedRecipe(
                    this.representative,
                    this.count,
                    this.totalOutput,
                    averageProgress,
                    this.representative.maxProgress(),
                    this.minProgress == Integer.MAX_VALUE ? 0 : this.minProgress,
                    this.maxProgress,
                    this.minProgress != this.maxProgress);
        }
    }

    private void runLocalStructureScan(BlockPos pos) {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
            return;
        }

        var blockEntity = this.minecraft.level.getBlockEntity(pos);
        if (!(blockEntity instanceof IMultiblockController controller)) {
            return;
        }

        var definition = MultiblockControllerDefinitions.getDefinition(blockEntity);
        if (definition.isEmpty()) {
            return;
        }

        var state = this.minecraft.level.getBlockState(pos);
        Direction facing = MultiblockControllerDefinitions.getPatternFacing(blockEntity, state);

        MultiblockPattern.MatchResult result = definition.get().pattern().match(this.minecraft.level, pos, facing);
        if (result.isValid()) {
            if (controller.isAssembled()) {
                this.minecraft.player.displayClientMessage(
                        Component.translatable("message.ufo.structure_formed").withStyle(ChatFormatting.GREEN), true);
            } else {
                this.minecraft.player.displayClientMessage(
                        definition.get().name().copy().append(Component.literal(": structure shape is valid, but extra controller validation failed.")
                                .withStyle(ChatFormatting.RED)),
                        false);
            }
            return;
        }

        reportStructureErrors(definition.get(), result.allErrors());
    }

    private void reportStructureErrors(MultiblockControllerDefinition definition, List<MultiblockPattern.PatternError> errors) {
        if (this.minecraft == null || this.minecraft.player == null || errors == null || errors.isEmpty()) {
            return;
        }

        int shown = Math.min(errors.size(), 10);
        this.minecraft.player.displayClientMessage(
                definition.name().copy()
                        .append(Component.literal(": " + errors.size() + " block(s) missing or misplaced.").withStyle(ChatFormatting.RED)),
                false);

        for (int i = 0; i < shown; i++) {
            var error = errors.get(i);
            BlockPos errorPos = error.pos();
            Component message = Component.literal("  [" + errorPos.getX() + ", " + errorPos.getY() + ", " + errorPos.getZ() + "] Expected: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(error.expected().copy().withStyle(ChatFormatting.YELLOW));
            this.minecraft.player.displayClientMessage(message, false);
        }

        if (errors.size() > shown) {
            this.minecraft.player.displayClientMessage(
                    Component.literal("  ... and " + (errors.size() - shown) + " more.").withStyle(ChatFormatting.GRAY),
                    false);
        }

        int maxHighlight = Math.min(errors.size(), 50);
        for (int i = 0; i < maxHighlight; i++) {
            StructureHighlightRenderer.highlight(errors.get(i).pos(), 5000);
        }
    }
}
