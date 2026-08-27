package com.raishxn.ufo.compat.jei;

import com.lowdragmc.lowdraglib2.client.scene.WorldSceneRenderer;
import com.lowdragmc.lowdraglib2.client.utils.RenderUtils;
import com.lowdragmc.lowdraglib2.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Scene;
import com.lowdragmc.lowdraglib2.integration.xei.IngredientIO;
import com.lowdragmc.lowdraglib2.integration.xei.jei.ModularUIRecipeCategory;
import com.lowdragmc.lowdraglib2.utils.data.BlockInfo;
import com.lowdragmc.lowdraglib2.utils.virtuallevel.TrackedDummyWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinition;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.api.multiblock.MultiblockPattern;
import com.raishxn.ufo.block.MultiblockBlocks;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.appliedenergistics.yoga.YogaPositionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiblockInfoCategory extends ModularUIRecipeCategory<MultiblockInfoWrapper> {

    public static final RecipeType<MultiblockInfoWrapper> RECIPE_TYPE =
            RecipeType.create(UfoMod.MOD_ID, "multiblock_info", MultiblockInfoWrapper.class);

    private static final int WIDTH = 184;
    private static final int HEIGHT = 184;
    private static final int MATERIAL_COLUMNS = 6;
    private static final int MATERIAL_ROWS = 2;
    private static final int MAX_MATERIAL_SLOTS = MATERIAL_COLUMNS * MATERIAL_ROWS;

    private final IDrawable background;
    private final IDrawable icon;

    public MultiblockInfoCategory(final IJeiHelpers helpers) {
        super(MultiblockInfoCategory::createUI);
        this.background = helpers.getGuiHelper().createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helpers.getGuiHelper().createDrawableItemStack(
                MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get().asItem().getDefaultInstance());
    }

    public static void registerRecipes(final IRecipeRegistration registration) {
        registration.addRecipes(RECIPE_TYPE,
                MultiblockControllerDefinitions.getPreviewEntries().stream()
                        .map(MultiblockInfoWrapper::new)
                        .toList());
    }

    private static ModularUI createUI(final MultiblockInfoWrapper recipe) {
        final var state = new PreviewState(recipe);

        final UIElement root = new UIElement();
        root.layout(layout -> layout.width(WIDTH).height(HEIGHT));

        final UIElement outer = new UIElement();
        outer.layout(layout -> layout.positionType(YogaPositionType.ABSOLUTE).left(0).top(0).width(WIDTH).height(HEIGHT));
        outer.style(style -> style.background(new ColorRectTexture(0xFFC6CDD8)));
        root.addChild(outer);

        final UIElement inner = new UIElement();
        inner.layout(layout -> layout.positionType(YogaPositionType.ABSOLUTE).left(1).top(1).width(WIDTH - 2).height(HEIGHT - 2));
        inner.style(style -> style.background(new ColorRectTexture(0xFFF7F9FB)));
        root.addChild(inner);

        root.addChild(text(recipe.entry().definition().name(), 6, 6, 172, 12, 0xFF3A3F46, true));
        root.addChild(text(Component.literal("Size: " + recipe.width() + "x" + recipe.height() + "x" + recipe.layers()),
                6, 20, 172, 10, 0xFF243447, false));
        root.addChild(text(Component.literal("Drag to rotate, scroll to zoom."),
                6, 30, 172, 10, 0xFF31465C, false));
        root.addChild(text(Component.literal("Click blue blocks to view variants."),
                6, 39, 172, 10, 0xFF31465C, false));

        final Scene scene = new Scene() {
            @Override
            public void renderBlockOverLay(final WorldSceneRenderer renderer) {
                super.renderBlockOverLay(renderer);
                final PoseStack poseStack = new PoseStack();
                for (final BlockPos variablePos : state.variablePositions) {
                    RenderUtils.renderBlockOverLay(poseStack, variablePos, 0.18f, 0.15f, 0.75f, 1.02f);
                }
                if (getLastHoverPosFace() != null) {
                    final BlockPos hoveredPos = getLastHoverPosFace().pos();
                    final char symbol = state.symbolByPos.getOrDefault(hoveredPos, '\0');
                    if (!state.pattern.getDisplayCandidates(symbol).isEmpty()) {
                        RenderUtils.renderBlockOverLay(poseStack, hoveredPos, 0.48f, 0.95f, 0.25f, 1.04f);
                    }
                }
            }
        };

        scene.layout(layout -> layout.positionType(YogaPositionType.ABSOLUTE).left(4).top(50).width(176).height(82));
        scene.style(style -> style.background(new ColorRectTexture(0xFF151A24)));
        scene.createScene(state.previewLevel)
                .setRenderedCore(state.allPositions)
                .setCameraYawAndPitch(-45f, 22f)
                .setRenderFacing(false)
                .setRenderSelect(true)
                .setShowHoverBlockTips(true)
                .setScalable(true)
                .setDraggable(true)
                .setOnSelected((pos, facing) -> state.updateCandidates(pos));
        root.addChild(scene);

        final Label label = text(Component.literal(state.selectedLabel), 4, 136, 124, 10, 0xFF16202A, false);
        root.addChild(label);

        root.addChild(createButton("MAT", 108, 134, 20, 16, "Toggle materials / variants", button -> {
            state.showingMaterials = !state.showingMaterials;
            state.displayPage = 0;
            state.refreshDisplayRow();
            label.setText(Component.literal(state.selectedLabel));
        }));

        root.addChild(createButton("ALL", 132, 134, 22, 16, "Show all layers", button -> {
            state.currentLayer = -1;
            state.applyLayer(scene);
            state.refreshDisplayRow();
            label.setText(Component.literal(state.selectedLabel));
        }));

        root.addChild(createButton("L:*", 156, 134, 24, 16, "Cycle visible layer", button -> {
            state.cycleLayer();
            state.applyLayer(scene);
            state.refreshDisplayRow();
            button.setText(state.currentLayer < 0 ? "L:*" : "L:" + state.currentLayer);
            label.setText(Component.literal(state.selectedLabel));
        }));

        final UIElement footer = new UIElement();
        footer.layout(layout -> layout.positionType(YogaPositionType.ABSOLUTE).left(4).top(152).width(176).height(28));
        footer.style(style -> style.background(new ColorRectTexture(0x66131A26)));
        root.addChild(footer);

        final ItemSlot[] displaySlots = new ItemSlot[MAX_MATERIAL_SLOTS];
        for (int i = 0; i < MAX_MATERIAL_SLOTS; i++) {
            final int col = i % MATERIAL_COLUMNS;
            final int row = i / MATERIAL_COLUMNS;
            final ItemSlot slot = new ItemSlot();
            slot.layout(layout -> layout.positionType(YogaPositionType.ABSOLUTE)
                    .left(34 + (col * 18))
                    .top(154 + (row * 18))
                    .width(18)
                    .height(18));
            slot.xeiRecipeSlot(IngredientIO.INPUT, 0.85f);
            slot.setItem(ItemStack.EMPTY);
            root.addChild(slot);
            displaySlots[i] = slot;
        }

        root.addChild(createButton("<", 6, 156, 18, 16, "Previous page", button -> {
            state.cycleDisplayPage(-1);
            state.refreshDisplayRow();
            label.setText(Component.literal(state.selectedLabel));
        }));
        root.addChild(createButton(">", 160, 156, 18, 16, "Next page", button -> {
            state.cycleDisplayPage(1);
            state.refreshDisplayRow();
            label.setText(Component.literal(state.selectedLabel));
        }));

        state.attachDisplaySlots(displaySlots);
        state.applyLayer(scene);
        state.refreshDisplayRow();
        label.setText(Component.literal(state.selectedLabel));

        return ModularUI.of(UI.of(root));
    }

    private static Label text(final Component component, final int x, final int y, final int width, final int height, final int color, final boolean shadow) {
        final Label element = new Label();
        element.setText(component);
        element.layout(layout -> layout.positionType(YogaPositionType.ABSOLUTE).left(x).top(y).width(width).height(height));
        element.textStyle(style -> {
            style.textColor(color);
            style.textShadow(shadow);
        });
        return element;
    }

    private static Button createButton(final String text, final int x, final int y, final int width, final int height, final String tooltip,
                                       final java.util.function.Consumer<Button> onClick) {
        final Button button = new Button();
        button.setText(text);
        button.layout(layout -> layout.positionType(YogaPositionType.ABSOLUTE).left(x).top(y).width(width).height(height));
        button.setOnClick(event -> onClick.accept((Button) event.currentElement));
        button.style(style -> style.tooltips(tooltip));
        button.buttonStyle(style -> {
            style.baseTexture(new ColorRectTexture(0xFF2A3140));
            style.hoverTexture(new ColorRectTexture(0xFF3B4759));
            style.pressedTexture(new ColorRectTexture(0xFF1E2530));
        });
        button.textStyle(style -> style.textShadow(true).textColor(0xFFF5F7FA));
        return button;
    }

    @Override
    public @Nullable Identifier getRegistryName(@NotNull final MultiblockInfoWrapper recipe) {
        return recipe.entry().id();
    }

    @Override
    public @NotNull RecipeType<MultiblockInfoWrapper> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.literal("Multiblock Info");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    private static final class PreviewState {
        private final MultiblockInfoWrapper recipe;
        private final MultiblockPattern pattern;
        private final BlockPos origin;
        private final TrackedDummyWorld previewLevel;
        private final List<BlockPos> allPositions = new ArrayList<>();
        private final List<BlockPos> variablePositions = new ArrayList<>();
        private final List<Integer> layers = new ArrayList<>();
        private final Map<BlockPos, Character> symbolByPos = new HashMap<>();
        private ItemSlot[] displaySlots = new ItemSlot[0];

        private boolean showingMaterials = true;
        private int currentLayer = -1;
        private int displayPage = 0;
        private String selectedLabel = "Material list";
        private List<ItemStack> currentCandidates = List.of();

        private PreviewState(final MultiblockInfoWrapper recipe) {
            this.recipe = recipe;
            this.pattern = recipe.entry().definition().pattern();
            this.previewLevel = new TrackedDummyWorld();
            this.origin = new BlockPos(0, 64, 0);
            buildPreviewWorld();
        }

        private void attachDisplaySlots(final ItemSlot[] slots) {
            this.displaySlots = slots;
        }

        private void buildPreviewWorld() {
            final MultiblockControllerDefinition definition = recipe.entry().definition();
            final char[][][] chars = pattern.getPattern();
            final BlockState controllerState = resolveControllerState(recipe.entry().iconStack());

            final Map<BlockPos, BlockInfo> blockMap = new HashMap<>();

            for (int y = 0; y < chars.length; y++) {
                for (int z = 0; z < chars[y].length; z++) {
                    for (int x = 0; x < chars[y][z].length; x++) {
                        final char symbol = chars[y][z][x];
                        final BlockState logicalState = symbol == pattern.getControllerChar()
                                ? controllerState
                                : definition.defaultCreativeStates().get(symbol);
                        if (logicalState == null || logicalState.isAir()) {
                            continue;
                        }

                        final BlockPos pos = origin.offset(x, y, z);
                        blockMap.put(pos, BlockInfo.fromBlockState(adaptPreviewState(logicalState)));
                        allPositions.add(pos);
                        symbolByPos.put(pos, symbol);
                        layers.add(pos.getY());

                        if (!pattern.getDisplayCandidates(symbol).isEmpty()) {
                            variablePositions.add(pos);
                        }
                    }
                }
            }

            previewLevel.addBlocks(blockMap);
            layers.sort(Comparator.naturalOrder());
        }

        private void applyLayer(final Scene scene) {
            final List<Integer> uniqueLayers = layers.stream().distinct().sorted().toList();
            if (currentLayer < 0 || currentLayer >= uniqueLayers.size()) {
                scene.setRenderedCore(allPositions);
                if (showingMaterials) {
                    selectedLabel = "Material list";
                }
                return;
            }

            final int worldY = uniqueLayers.get(currentLayer);
            scene.setRenderedCore(allPositions.stream().filter(pos -> pos.getY() == worldY).toList());
            if (showingMaterials) {
                selectedLabel = "Materials - layer " + (currentLayer + 1) + "/" + uniqueLayers.size();
            }
        }

        private void cycleLayer() {
            final List<Integer> uniqueLayers = layers.stream().distinct().sorted().toList();
            if (uniqueLayers.isEmpty()) {
                currentLayer = -1;
            } else if (currentLayer < 0) {
                currentLayer = 0;
            } else {
                currentLayer++;
                if (currentLayer >= uniqueLayers.size()) {
                    currentLayer = -1;
                }
            }
        }

        private void updateCandidates(final BlockPos pos) {
            final char symbol = symbolByPos.getOrDefault(pos, '\0');
            final List<BlockState> candidates = pattern.getDisplayCandidates(symbol);
            if (candidates.isEmpty()) {
                showingMaterials = true;
                displayPage = 0;
                currentCandidates = List.of();
                selectedLabel = pattern.getLegendName(symbol).getString();
                refreshDisplayRow();
                return;
            }

            showingMaterials = false;
            displayPage = 0;
            selectedLabel = pattern.getLegendName(symbol).getString();
            currentCandidates = candidates.stream()
                    .map(state -> new ItemStack(state.getBlock()))
                    .toList();
            refreshDisplayRow();
        }

        private void refreshDisplayRow() {
            if (displaySlots.length == 0) {
                return;
            }
            for (final ItemSlot slot : displaySlots) {
                slot.setItem(ItemStack.EMPTY);
            }

            if (showingMaterials) {
                final int pageCount = Math.max(1, (int) Math.ceil(recipe.materials().size() / (double) MAX_MATERIAL_SLOTS));
                if (displayPage >= pageCount) {
                    displayPage = 0;
                }
                final int start = displayPage * MAX_MATERIAL_SLOTS;
                for (int i = 0; i < MAX_MATERIAL_SLOTS && start + i < recipe.materials().size(); i++) {
                    final MultiblockInfoWrapper.MaterialStack material = recipe.materials().get(start + i);
                    displaySlots[i].setItem(material.stack().copyWithCount(material.count()), false);
                    displaySlots[i].style(style -> style.tooltips(
                            Component.literal(material.stack().getHoverName().getString()),
                            Component.literal("Required blocks: " + material.count())));
                }
            } else {
                for (int i = 0; i < MAX_MATERIAL_SLOTS && i < currentCandidates.size(); i++) {
                    final ItemStack stack = currentCandidates.get(i);
                    displaySlots[i].setItem(stack, false);
                    displaySlots[i].style(style -> style.tooltips(stack.getHoverName()));
                }
            }
        }

        private void cycleDisplayPage(final int delta) {
            if (!showingMaterials || recipe.materials().isEmpty()) {
                return;
            }
            final int pageCount = Math.max(1, (int) Math.ceil(recipe.materials().size() / (double) MAX_MATERIAL_SLOTS));
            displayPage = (displayPage + delta + pageCount) % pageCount;
        }
    }

    private static BlockState resolveControllerState(final ItemStack iconStack) {
        if (iconStack.getItem() instanceof final BlockItem blockItem) {
            return blockItem.getBlock().defaultBlockState();
        }
        return Blocks.IRON_BLOCK.defaultBlockState();
    }

    private static BlockState adaptPreviewState(final BlockState state) {
        final Identifier key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (key != null && "ae2".equals(key.getNamespace()) && "quartz_vibrant_glass".equals(key.getPath())) {
            return Blocks.GLASS.defaultBlockState();
        }
        return state;
    }
}
