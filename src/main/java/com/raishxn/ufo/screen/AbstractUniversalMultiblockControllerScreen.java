package com.raishxn.ufo.screen;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.SlotSemantics;
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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
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

    protected AbstractUniversalMultiblockControllerScreen(final M menu, final Inventory playerInventory, final Component title, final ScreenStyle style) {
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
                    final BlockPos pos = this.menu.getBlockEntity().getBlockPos();
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
    public void drawBG(final GuiGraphicsExtractor guiGraphics, final int offsetX, final int offsetY, final int mouseX, final int mouseY, final float partialTick) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTick);
        renderTemperatureBar(guiGraphics);
        renderRecipeList(guiGraphics);
    }

    private void renderTemperatureBar(final GuiGraphicsExtractor guiGraphics) {
        final int barX = this.leftPos + 14;
        final int barY = this.topPos + 9;
        final int barWidth = 146;
        final int barHeight = 10;
        final int filled = (int) (barWidth * Math.min(1.0F, this.menu.getTemperature() / (float) this.menu.getMaxTemperature()));

        if (filled > 0) {
            guiGraphics.fill(barX, barY, barX + filled, barY + barHeight, 0xCCB32020);
        }
    }

    private void renderRecipeList(final GuiGraphicsExtractor guiGraphics) {
        final int listX = this.leftPos + 7;
        final int listY = this.topPos + 30;
        final int lineHeight = 10;
        final int maxTextWidth = 156;
        final List<GroupedRecipe> recipes = buildGroupedRecipes();

        guiGraphics.text(this.font, this.font.plainSubstrByWidth(getScreenTitle().getString(), maxTextWidth), listX, listY, 0xF0F0F0, false);
        guiGraphics.text(this.font, this.font.plainSubstrByWidth(buildStatusLine(), maxTextWidth), listX, listY + 10, 0xD0D7E6, false);
        renderSummaryLine(guiGraphics, listX, listY + 20, maxTextWidth, recipes);

        for (int i = 0; i < 8; i++) {
            final int rowY = listY + 32 + i * lineHeight;
            if (i < recipes.size()) {
                renderRecipeRow(guiGraphics, recipes.get(i), listX, rowY);
            } else if (i == 0 && recipes.isEmpty()) {
                guiGraphics.text(this.font, this.menu.isAssembled() ? "No active recipes." : "Structure incomplete.", listX, rowY, 0x8A91A6, false);
            }
        }
    }

    private void renderRecipeRow(final GuiGraphicsExtractor guiGraphics, final GroupedRecipe groupedRecipe, final int x, final int y) {
        final UniversalDisplayedRecipe recipe = groupedRecipe.recipe();
        ItemStack iconStack = recipe.itemIcon();
        boolean fluidRecipe = false;
        if (iconStack.isEmpty()) {
            final FluidStack fluid = recipe.fluidIcon();
            if (!fluid.isEmpty()) {
                iconStack = new ItemStack(fluid.getFluid().getBucket());
                fluidRecipe = true;
            }
        }

        int textX = x;
        if (!iconStack.isEmpty()) {
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(x, y - 1);
            guiGraphics.pose().scale(0.5F, 0.5F);
            guiGraphics.item(iconStack, 0, 0);
            guiGraphics.pose().popMatrix();
            textX += 10;
        }

        final String amount = fluidRecipe ? formatAmount(groupedRecipe.totalOutputAmount()) + "mB" : formatAmount(groupedRecipe.totalOutputAmount()) + "x";
        final String time = groupedRecipe.displayMaxProgress() > 0
                ? formatSeconds(groupedRecipe.displayProgress()) + "/" + formatSeconds(groupedRecipe.displayMaxProgress()) + "s"
                : "-/-";
        final int timeWidth = this.font.width(time);
        final int availableWidth = Math.max(20, 156 - (textX - x) - timeWidth - 4);
        String leftText = amount + " " + recipe.label().getString();
        if (groupedRecipe.copyCount() > 1) {
            leftText += " [" + groupedRecipe.copyCount() + "]";
        }
        guiGraphics.text(this.font, this.font.plainSubstrByWidth(leftText, availableWidth), textX, y, 0xE6EBF5, false);
        guiGraphics.text(this.font, time, this.leftPos + 162 - timeWidth, y, 0xB9D8FF, false);
    }

    private String buildStatusLine() {
        if (!this.menu.isAssembled()) {
            return "Status: Incomplete";
        }
        String builder = (this.menu.isRunning() ? "RUN" : "IDLE") + " | MK" + this.menu.getMachineTier() +
                " | " + (this.menu.isSafeMode() ? "SAFE" : "RISK") +
                " | " + (this.menu.isOverclocked() ? "OC" : "STD");
        return builder;
    }

    private void renderSummaryLine(final GuiGraphicsExtractor guiGraphics, final int x, final int y, final int maxTextWidth, final List<GroupedRecipe> recipes) {
        final String summaryText = buildSummaryText(recipes);
        guiGraphics.text(this.font, this.font.plainSubstrByWidth(summaryText, maxTextWidth), x, y, 0xB9D8FF, false);
    }

    private String buildSummaryText(final List<GroupedRecipe> recipes) {
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

    private static String formatAmount(final long amount) {
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

    private static String formatSeconds(final int ticks) {
        final double seconds = ticks / 20.0;
        return seconds >= 100
                ? String.format(Locale.ROOT, "%.0f", seconds)
                : String.format(Locale.ROOT, "%.1f", seconds);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        refreshRecipeCache();
        if (this.safeModeButton != null) {
            final boolean safe = this.menu.isSafeMode();
            this.safeModeButton.setMessage(Component.literal(safe ? "S" : "!"));
            this.safeModeButton.setTooltip(Tooltip.create(Component.literal(safe ? "Safe Mode enabled" : "Safe Mode disabled")));
            this.safeModeButton.active = !this.menu.isRunning() && this.menu.getDisplayedRecipes().isEmpty();
        }
        if (this.overclockButton != null) {
            final boolean oc = this.menu.isOverclocked();
            this.overclockButton.setMessage(Component.literal(oc ? "OC" : ">"));
            this.overclockButton.setTooltip(Tooltip.create(Component.literal(oc ? "Overclock enabled" : "Overclock disabled")));
            this.overclockButton.active = !this.menu.isRunning() && this.menu.getDisplayedRecipes().isEmpty();
        }
    }

    @Override
    public void extractRenderState(
            final GuiGraphicsExtractor guiGraphics,
            final int mouseX,
            final int mouseY,
            final float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        if (isHovering(14, 9, 146, 10, mouseX, mouseY)) {
            guiGraphics.setTooltipForNextFrame(this.font,
                    Component.literal("Temperature: " + this.menu.getTemperature() + " / " + this.menu.getMaxTemperature()), mouseX, mouseY);
            return;
        }
        if (isHoveringParallelText(mouseX, mouseY)) {
            guiGraphics.setComponentTooltipForNextFrame(this.font, buildParallelTooltip(), mouseX, mouseY);
            return;
        }
        if (isHoveringSummaryLine(mouseX, mouseY)) {
            guiGraphics.setComponentTooltipForNextFrame(this.font, buildSummaryTooltip(), mouseX, mouseY);
            return;
        }
        final GroupedRecipe hoveredRecipe = getHoveredGroupedRecipe(mouseX, mouseY);
        if (hoveredRecipe != null) {
            guiGraphics.setComponentTooltipForNextFrame(this.font, buildGroupedRecipeTooltip(hoveredRecipe), mouseX, mouseY);
        }
    }

    private boolean isHoveringParallelText(final int mouseX, final int mouseY) {
        final int listX = this.leftPos + 7;
        final int lineY = this.topPos + 50;
        final String energyText = "AE " + formatAmount(this.menu.getStoredEnergy()) + " | A" + this.menu.getActiveParallels()
                + " U" + buildGroupedRecipes().size() + " | ";
        final String parallelText = buildParallelSummaryText();
        final String fullText = energyText + parallelText;
        final int maxTextWidth = 156;
        final int fullWidth = this.font.width(fullText);

        if (fullWidth > maxTextWidth) {
            return false;
        }

        final int parallelX = listX + this.font.width(energyText);
        final int parallelWidth = this.font.width(parallelText);
        return mouseX >= parallelX
                && mouseX < parallelX + parallelWidth
                && mouseY >= lineY
                && mouseY < lineY + this.font.lineHeight;
    }

    private boolean isHoveringSummaryLine(final int mouseX, final int mouseY) {
        final int listX = this.leftPos + 7;
        final int lineY = this.topPos + 50;
        return mouseX >= listX
                && mouseX < listX + 156
                && mouseY >= lineY
                && mouseY < lineY + this.font.lineHeight;
    }

    private List<Component> buildSummaryTooltip() {
        final List<GroupedRecipe> groupedRecipes = buildGroupedRecipes();
        long totalOutput = 0L;
        double totalPerSecond = 0.0D;
        for (final GroupedRecipe recipe : groupedRecipes) {
            totalOutput += recipe.totalOutputAmount();
            if (recipe.displayMaxProgress() > 0) {
                totalPerSecond += recipe.totalOutputAmount() * 20.0D / recipe.displayMaxProgress();
            }
        }

        final List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("AE Energy: " + formatAmount(this.menu.getStoredEnergy()) + " AE"));
        lines.add(Component.literal("Active recipe copies: " + this.menu.getActiveParallels()));
        lines.add(Component.literal("Unique recipe groups: " + groupedRecipes.size()));
        lines.add(Component.literal("Parallel usage: " + this.menu.getActiveParallels() + " / " + this.menu.getMaxParallels()));
        lines.add(Component.literal("Grouped output on screen: " + formatAmount(totalOutput)));
        lines.add(Component.literal("Estimated output rate: " + formatAmount((long) totalPerSecond) + "/s"));
        return lines;
    }

    private java.util.List<Component> buildParallelTooltip() {
        final var recipes = this.menu.getDisplayedRecipes();
        final Map<String, Integer> recipeCounts = new LinkedHashMap<>();
        for (final UniversalDisplayedRecipe recipe : recipes) {
            recipeCounts.merge(recipe.label().getString(), 1, Integer::sum);
        }

        final int active = this.menu.getActiveParallels();
        final int distinct = recipeCounts.size();
        final int repeated = Math.max(0, active - distinct);
        final java.util.List<Component> lines = new ArrayList<>();
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
        final List<UniversalDisplayedRecipe> displayedRecipes = this.menu.getDisplayedRecipes();
        final int signature = computeRecipeSignature(displayedRecipes);
        if (signature == this.cachedRecipeSignature && displayedRecipes.size() == this.cachedDisplayedRecipes.size()) {
            return;
        }

        this.cachedDisplayedRecipes = displayedRecipes;
        this.cachedRecipeSignature = signature;

        final Map<RecipeGroupKey, GroupAccumulator> groups = new LinkedHashMap<>();
        for (final UniversalDisplayedRecipe recipe : displayedRecipes) {
            final RecipeGroupKey key = RecipeGroupKey.of(recipe);
            groups.computeIfAbsent(key, ignored -> new GroupAccumulator(recipe)).add(recipe);
        }

        final List<GroupedRecipe> groupedRecipes = new ArrayList<>();
        for (final GroupAccumulator accumulator : groups.values()) {
            groupedRecipes.add(accumulator.toGroupedRecipe());
        }
        this.cachedGroupedRecipes = groupedRecipes;
    }

    private static int computeRecipeSignature(final List<UniversalDisplayedRecipe> recipes) {
        int signature = 1;
        for (final UniversalDisplayedRecipe recipe : recipes) {
            signature = 31 * signature + recipe.progress();
            signature = 31 * signature + recipe.maxProgress();
            signature = 31 * signature + Long.hashCode(recipe.outputAmount());
            signature = 31 * signature + recipe.label().getString().hashCode();
            signature = 31 * signature + Objects.hashCode(BuiltInRegistries.ITEM.getKey(recipe.itemIcon().getItem()));
            signature = 31 * signature + Objects.hashCode(BuiltInRegistries.FLUID.getKey(recipe.fluidIcon().getFluid()));
        }
        return signature;
    }

    private GroupedRecipe getHoveredGroupedRecipe(final int mouseX, final int mouseY) {
        final int listX = this.leftPos + 7;
        final int listY = this.topPos + 30;
        final int lineHeight = 10;
        final List<GroupedRecipe> recipes = buildGroupedRecipes();
        for (int i = 0; i < Math.min(8, recipes.size()); i++) {
            final int rowY = listY + 32 + i * lineHeight;
            if (mouseX >= listX && mouseX < listX + 156 && mouseY >= rowY && mouseY < rowY + lineHeight) {
                return recipes.get(i);
            }
        }
        return null;
    }

    private List<Component> buildGroupedRecipeTooltip(final GroupedRecipe groupedRecipe) {
        final UniversalDisplayedRecipe recipe = groupedRecipe.recipe();
        final List<Component> lines = new ArrayList<>();
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
        private static RecipeGroupKey of(final UniversalDisplayedRecipe recipe) {
            final String itemId = recipe.itemIcon().isEmpty()
                    ? ""
                    : String.valueOf(BuiltInRegistries.ITEM.getKey(recipe.itemIcon().getItem()));
            final String fluidId = recipe.fluidIcon().isEmpty()
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

        private GroupAccumulator(final UniversalDisplayedRecipe representative) {
            this.representative = representative;
        }

        private void add(final UniversalDisplayedRecipe recipe) {
            this.count++;
            this.totalOutput += recipe.outputAmount();
            this.totalProgress += recipe.progress();
            this.minProgress = Math.min(this.minProgress, recipe.progress());
            this.maxProgress = Math.max(this.maxProgress, recipe.progress());
        }

        private GroupedRecipe toGroupedRecipe() {
            final int averageProgress = this.count == 0 ? 0 : this.totalProgress / this.count;
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

    private void runLocalStructureScan(final BlockPos pos) {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
            return;
        }

        final var blockEntity = this.minecraft.level.getBlockEntity(pos);
        if (!(blockEntity instanceof final IMultiblockController controller)) {
            return;
        }

        final var definition = MultiblockControllerDefinitions.getDefinition(blockEntity);
        if (definition.isEmpty()) {
            return;
        }

        final var state = this.minecraft.level.getBlockState(pos);
        final Direction facing = MultiblockControllerDefinitions.getPatternFacing(blockEntity, state);

        final MultiblockPattern.MatchResult result = definition.get().pattern().match(this.minecraft.level, pos, facing);
        if (result.isValid()) {
            if (controller.isAssembled()) {
                this.minecraft.player.sendOverlayMessage(Component.translatable("message.ufo.structure_formed").withStyle(ChatFormatting.GREEN));
            } else {
                this.minecraft.player.sendSystemMessage(
                        definition.get().name().copy().append(Component.literal(": structure shape is valid, but extra controller validation failed.")
                                .withStyle(ChatFormatting.RED)));
            }
            return;
        }

        reportStructureErrors(definition.get(), result.allErrors());
    }

    private void reportStructureErrors(final MultiblockControllerDefinition definition, final List<MultiblockPattern.PatternError> errors) {
        if (this.minecraft == null || this.minecraft.player == null || errors == null || errors.isEmpty()) {
            return;
        }

        final int shown = Math.min(errors.size(), 10);
        this.minecraft.player.sendSystemMessage(
                definition.name().copy()
                        .append(Component.literal(": " + errors.size() + " block(s) missing or misplaced.").withStyle(ChatFormatting.RED)));

        for (int i = 0; i < shown; i++) {
            final var error = errors.get(i);
            final BlockPos errorPos = error.pos();
            final Component message = Component.literal("  [" + errorPos.getX() + ", " + errorPos.getY() + ", " + errorPos.getZ() + "] Expected: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(error.expected().copy().withStyle(ChatFormatting.YELLOW));
            this.minecraft.player.sendSystemMessage(message);
        }

        if (errors.size() > shown) {
            this.minecraft.player.sendSystemMessage(Component.literal("  ... and " + (errors.size() - shown) + " more.").withStyle(ChatFormatting.GRAY));
        }

        final int maxHighlight = Math.min(errors.size(), 50);
        for (int i = 0; i < maxHighlight; i++) {
            StructureHighlightRenderer.highlight(errors.get(i).pos(), 5000);
        }
    }
}
