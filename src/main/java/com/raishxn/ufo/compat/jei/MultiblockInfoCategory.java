package com.raishxn.ufo.compat.jei;

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
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinition;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.api.multiblock.MultiblockPattern;
import com.raishxn.ufo.block.MultiblockBlocks;
import dev.vfyjxf.taffy.style.TaffyPosition;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public final class MultiblockInfoCategory
        extends ModularUIRecipeCategory<MultiblockInfoWrapper> {

    public static final IRecipeType<MultiblockInfoWrapper> RECIPE_TYPE =
            IRecipeType.create(
                    UfoMod.MOD_ID,
                    "multiblock_info",
                    MultiblockInfoWrapper.class
            );

    private static final int WIDTH = 184;
    private static final int HEIGHT = 184;
    private static final int MATERIAL_COLUMNS = 6;
    private static final int MATERIAL_ROWS = 2;
    private static final int MAX_MATERIAL_SLOTS = MATERIAL_COLUMNS * MATERIAL_ROWS;

    private final IDrawable icon;

    public MultiblockInfoCategory(final IJeiHelpers helpers) {
        super(MultiblockInfoCategory::createUI);

        this.icon = helpers.getGuiHelper().createDrawableItemStack(
                MultiblockBlocks.STELLAR_NEXUS_CONTROLLER
                        .get()
                        .asItem()
                        .getDefaultInstance()
        );
    }

    public static void registerRecipes(final IRecipeRegistration registration) {
        registration.addRecipes(
                RECIPE_TYPE,
                MultiblockControllerDefinitions.getPreviewEntries()
                        .stream()
                        .map(MultiblockInfoWrapper::new)
                        .toList()
        );
    }

    private static ModularUI createUI(final MultiblockInfoWrapper recipe) {
        final PreviewState state = new PreviewState(recipe);

        final UIElement root = new UIElement();
        root.layout(layout ->
                layout.width(WIDTH).height(HEIGHT)
        );

        final UIElement outer = new UIElement();
        outer.layout(layout ->
                layout.positionType(TaffyPosition.ABSOLUTE)
                        .left(0)
                        .top(0)
                        .width(WIDTH)
                        .height(HEIGHT)
        );
        outer.style(style ->
                style.background(new ColorRectTexture(0xFFC6CDD8))
        );
        root.addChild(outer);

        final UIElement inner = new UIElement();
        inner.layout(layout ->
                layout.positionType(TaffyPosition.ABSOLUTE)
                        .left(1)
                        .top(1)
                        .width(WIDTH - 2)
                        .height(HEIGHT - 2)
        );
        inner.style(style ->
                style.background(new ColorRectTexture(0xFFF7F9FB))
        );
        root.addChild(inner);

        root.addChild(
                text(
                        recipe.entry().definition().name(),
                        6,
                        6,
                        172,
                        12,
                        0xFF3A3F46,
                        true
                )
        );

        root.addChild(
                text(
                        Component.literal(
                                "Size: "
                                        + recipe.width()
                                        + "x"
                                        + recipe.height()
                                        + "x"
                                        + recipe.layers()
                        ),
                        6,
                        20,
                        172,
                        10,
                        0xFF243447,
                        false
                )
        );

        root.addChild(
                text(
                        Component.literal("Drag to rotate, scroll to zoom."),
                        6,
                        30,
                        172,
                        10,
                        0xFF31465C,
                        false
                )
        );

        root.addChild(
                text(
                        Component.literal("Click blue blocks to view variants."),
                        6,
                        39,
                        172,
                        10,
                        0xFF31465C,
                        false
                )
        );

        final Scene scene = new Scene();

        scene.layout(layout ->
                layout.positionType(TaffyPosition.ABSOLUTE)
                        .left(4)
                        .top(50)
                        .width(176)
                        .height(82)
        );

        scene.style(style ->
                style.background(new ColorRectTexture(0xFF151A24))
        );

        scene.createScene(state.previewLevel)
                .setRenderedCore(state.allPositions)
                .setCameraYawAndPitch(-45F, 22F)
                .setRenderFacing(false)
                .setRenderSelect(true)
                .setShowHoverBlockTips(true)
                .setScalable(true)
                .setDraggable(true)
                .setOnSelected((pos, _) -> state.updateCandidates(pos));

        root.addChild(scene);

        scene.layout(layout ->
                layout.positionType(TaffyPosition.ABSOLUTE)
                        .left(4)
                        .top(50)
                        .width(176)
                        .height(82)
        );

        scene.style(style ->
                style.background(new ColorRectTexture(0xFF151A24))
        );

        scene.createScene(state.previewLevel)
                .setRenderedCore(state.allPositions)
                .setCameraYawAndPitch(-45F, 22F)
                .setRenderFacing(false)
                .setRenderSelect(true)
                .setShowHoverBlockTips(true)
                .setScalable(true)
                .setDraggable(true)
                .setOnSelected((pos, facing) -> state.updateCandidates(pos));

        root.addChild(scene);

        final Label label = text(
                Component.literal(state.selectedLabel),
                4,
                136,
                124,
                10,
                0xFF16202A,
                false
        );

        root.addChild(label);

        root.addChild(
                createButton(
                        "MAT",
                        108,
                        134,
                        20,
                        16,
                        "Toggle materials / variants",
                        button -> {
                            state.showingMaterials = !state.showingMaterials;
                            state.displayPage = 0;
                            state.refreshDisplayRow();
                            label.setText(Component.literal(state.selectedLabel));
                        }
                )
        );

        root.addChild(
                createButton(
                        "ALL",
                        132,
                        134,
                        22,
                        16,
                        "Show all layers",
                        button -> {
                            state.currentLayer = -1;
                            state.applyLayer(scene);
                            state.refreshDisplayRow();
                            label.setText(Component.literal(state.selectedLabel));
                        }
                )
        );

        root.addChild(
                createButton(
                        "L:*",
                        156,
                        134,
                        24,
                        16,
                        "Cycle visible layer",
                        button -> {
                            state.cycleLayer();
                            state.applyLayer(scene);
                            state.refreshDisplayRow();

                            button.setText(
                                    state.currentLayer < 0
                                            ? "L:*"
                                            : "L:" + state.currentLayer
                            );

                            label.setText(Component.literal(state.selectedLabel));
                        }
                )
        );

        final UIElement footer = new UIElement();
        footer.layout(layout ->
                layout.positionType(TaffyPosition.ABSOLUTE)
                        .left(4)
                        .top(152)
                        .width(176)
                        .height(28)
        );
        footer.style(style ->
                style.background(new ColorRectTexture(0x66131A26))
        );
        root.addChild(footer);

        final ItemSlot[] displaySlots = new ItemSlot[MAX_MATERIAL_SLOTS];

        for (int i = 0; i < MAX_MATERIAL_SLOTS; i++) {
            final int column = i % MATERIAL_COLUMNS;
            final int row = i / MATERIAL_COLUMNS;

            final ItemSlot slot = new ItemSlot();

            slot.layout(layout ->
                    layout.positionType(TaffyPosition.ABSOLUTE)
                            .left(34 + column * 18)
                            .top(154 + row * 18)
                            .width(18)
                            .height(18)
            );

            slot.xeiRecipeSlot(IngredientIO.INPUT, 0.85F);
            slot.setItem(ItemStack.EMPTY);

            root.addChild(slot);
            displaySlots[i] = slot;
        }

        root.addChild(
                createButton(
                        "<",
                        6,
                        156,
                        18,
                        16,
                        "Previous page",
                        button -> {
                            state.cycleDisplayPage(-1);
                            state.refreshDisplayRow();
                            label.setText(Component.literal(state.selectedLabel));
                        }
                )
        );

        root.addChild(
                createButton(
                        ">",
                        160,
                        156,
                        18,
                        16,
                        "Next page",
                        button -> {
                            state.cycleDisplayPage(1);
                            state.refreshDisplayRow();
                            label.setText(Component.literal(state.selectedLabel));
                        }
                )
        );

        state.attachDisplaySlots(displaySlots);
        state.applyLayer(scene);
        state.refreshDisplayRow();

        label.setText(Component.literal(state.selectedLabel));

        return ModularUI.of(UI.of(root));
    }

    private static Label text(
            final Component component,
            final int x,
            final int y,
            final int width,
            final int height,
            final int color,
            final boolean shadow
    ) {
        final Label element = new Label();

        element.setText(component);

        element.layout(layout ->
                layout.positionType(TaffyPosition.ABSOLUTE)
                        .left(x)
                        .top(y)
                        .width(width)
                        .height(height)
        );

        element.textStyle(style -> {
            style.textColor(color);
            style.textShadow(shadow);
        });

        return element;
    }

    private static Button createButton(
            final String text,
            final int x,
            final int y,
            final int width,
            final int height,
            final String tooltip,
            final Consumer<Button> onClick
    ) {
        final Button button = new Button();

        button.setText(text);

        button.layout(layout ->
                layout.positionType(TaffyPosition.ABSOLUTE)
                        .left(x)
                        .top(y)
                        .width(width)
                        .height(height)
        );

        button.setOnClick(event ->
                onClick.accept((Button) event.currentElement)
        );

        button.style(style ->
                style.tooltips(tooltip)
        );

        button.buttonStyle(style -> {
            style.baseTexture(new ColorRectTexture(0xFF2A3140));
            style.hoverTexture(new ColorRectTexture(0xFF3B4759));
            style.pressedTexture(new ColorRectTexture(0xFF1E2530));
        });

        button.textStyle(style ->
                style.textShadow(true)
                        .textColor(0xFFF5F7FA)
        );

        return button;
    }

    @Override
    public @Nullable Identifier getIdentifier(
            @NotNull final MultiblockInfoWrapper recipe
    ) {
        return recipe.entry().id();
    }

    @Override
    public @NotNull IRecipeType<MultiblockInfoWrapper> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.literal("Multiblock Info");
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
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
        private int displayPage;
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
            final MultiblockControllerDefinition definition =
                    this.recipe.entry().definition();

            final char[][][] patternChars = this.pattern.getPattern();

            final BlockState controllerState =
                    resolveControllerState(this.recipe.entry().iconStack());

            final Map<BlockPos, BlockInfo> blockMap = new HashMap<>();

            for (int y = 0; y < patternChars.length; y++) {
                for (int z = 0; z < patternChars[y].length; z++) {
                    for (int x = 0; x < patternChars[y][z].length; x++) {
                        final char symbol = patternChars[y][z][x];

                        final BlockState logicalState =
                                symbol == this.pattern.getControllerChar()
                                        ? controllerState
                                        : definition.defaultCreativeStates()
                                        .get(symbol);

                        if (logicalState == null || logicalState.isAir()) {
                            continue;
                        }

                        final BlockPos pos =
                                this.origin.offset(x, y, z);

                        blockMap.put(
                                pos,
                                BlockInfo.fromBlockState(
                                        adaptPreviewState(logicalState)
                                )
                        );

                        this.allPositions.add(pos);
                        this.symbolByPos.put(pos, symbol);
                        this.layers.add(pos.getY());

                        if (!this.pattern
                                .getDisplayCandidates(symbol)
                                .isEmpty()) {
                            this.variablePositions.add(pos);
                        }
                    }
                }
            }

            this.previewLevel.addBlocks(blockMap);
            this.layers.sort(Comparator.naturalOrder());
        }

        private void applyLayer(final Scene scene) {
            final List<Integer> uniqueLayers = getUniqueLayers();

            if (this.currentLayer < 0
                    || this.currentLayer >= uniqueLayers.size()) {
                scene.setRenderedCore(this.allPositions);

                if (this.showingMaterials) {
                    this.selectedLabel = "Material list";
                }

                return;
            }

            final int worldY = uniqueLayers.get(this.currentLayer);

            scene.setRenderedCore(
                    this.allPositions.stream()
                            .filter(pos -> pos.getY() == worldY)
                            .toList()
            );

            if (this.showingMaterials) {
                this.selectedLabel =
                        "Materials - layer "
                                + (this.currentLayer + 1)
                                + "/"
                                + uniqueLayers.size();
            }
        }

        private void cycleLayer() {
            final List<Integer> uniqueLayers = getUniqueLayers();

            if (uniqueLayers.isEmpty()) {
                this.currentLayer = -1;
                return;
            }

            if (this.currentLayer < 0) {
                this.currentLayer = 0;
                return;
            }

            this.currentLayer++;

            if (this.currentLayer >= uniqueLayers.size()) {
                this.currentLayer = -1;
            }
        }

        private List<Integer> getUniqueLayers() {
            return this.layers.stream()
                    .distinct()
                    .sorted()
                    .toList();
        }

        private void updateCandidates(final BlockPos pos) {
            final char symbol =
                    this.symbolByPos.getOrDefault(pos, '\0');

            final List<BlockState> candidates =
                    this.pattern.getDisplayCandidates(symbol);

            this.displayPage = 0;
            this.selectedLabel =
                    this.pattern.getLegendName(symbol).getString();

            if (candidates.isEmpty()) {
                this.showingMaterials = true;
                this.currentCandidates = List.of();
                refreshDisplayRow();
                return;
            }

            this.showingMaterials = false;

            this.currentCandidates = candidates.stream()
                    .map(state -> new ItemStack(state.getBlock()))
                    .toList();

            refreshDisplayRow();
        }

        private void refreshDisplayRow() {
            if (this.displaySlots.length == 0) {
                return;
            }

            for (final ItemSlot slot : this.displaySlots) {
                slot.setItem(ItemStack.EMPTY);
            }

            if (this.showingMaterials) {
                refreshMaterialSlots();
                return;
            }

            refreshCandidateSlots();
        }

        private void refreshMaterialSlots() {
            final List<MultiblockInfoWrapper.MaterialStack> materials =
                    this.recipe.materials();

            final int pageCount =
                    Math.max(
                            1,
                            (materials.size() + MAX_MATERIAL_SLOTS - 1)
                                    / MAX_MATERIAL_SLOTS
                    );

            if (this.displayPage >= pageCount) {
                this.displayPage = 0;
            }

            final int start = this.displayPage * MAX_MATERIAL_SLOTS;
            final int count = Math.min(
                    MAX_MATERIAL_SLOTS,
                    materials.size() - start
            );

            for (int i = 0; i < count; i++) {
                final MultiblockInfoWrapper.MaterialStack material =
                        materials.get(start + i);

                this.displaySlots[i].setItem(
                        material.stack().copyWithCount(material.count()),
                        false
                );

                this.displaySlots[i].style(style ->
                        style.tooltips(
                                material.stack().getHoverName(),
                                Component.literal(
                                        "Required blocks: "
                                                + material.count()
                                )
                        )
                );
            }
        }

        private void refreshCandidateSlots() {
            final int count = Math.min(
                    MAX_MATERIAL_SLOTS,
                    this.currentCandidates.size()
            );

            for (int i = 0; i < count; i++) {
                final ItemStack stack = this.currentCandidates.get(i);

                this.displaySlots[i].setItem(stack, false);

                this.displaySlots[i].style(style ->
                        style.tooltips(stack.getHoverName())
                );
            }
        }

        private void cycleDisplayPage(final int delta) {
            if (!this.showingMaterials
                    || this.recipe.materials().isEmpty()) {
                return;
            }

            final int pageCount =
                    Math.max(
                            1,
                            (
                                    this.recipe.materials().size()
                                            + MAX_MATERIAL_SLOTS
                                            - 1
                            ) / MAX_MATERIAL_SLOTS
                    );

            this.displayPage =
                    (this.displayPage + delta + pageCount)
                            % pageCount;
        }
    }

    private static BlockState resolveControllerState(
            final ItemStack iconStack
    ) {
        if (iconStack.getItem() instanceof final BlockItem blockItem) {
            return blockItem.getBlock().defaultBlockState();
        }

        return Blocks.IRON_BLOCK.defaultBlockState();
    }

    private static BlockState adaptPreviewState(
            final BlockState state
    ) {
        final Identifier key =
                BuiltInRegistries.BLOCK.getKey(state.getBlock());

        if ("ae2".equals(key.getNamespace())
                && "quartz_vibrant_glass".equals(key.getPath())) {
            return Blocks.GLASS.defaultBlockState();
        }

        return state;
    }
}