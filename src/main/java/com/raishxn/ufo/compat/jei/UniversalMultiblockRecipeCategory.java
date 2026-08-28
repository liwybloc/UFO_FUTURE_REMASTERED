package com.raishxn.ufo.compat.jei;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.recipe.UniversalMultiblockMachineKind;
import com.raishxn.ufo.recipe.UniversalMultiblockRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

public final class UniversalMultiblockRecipeCategory
        implements IRecipeCategory<UniversalMultiblockRecipe> {

    public static final IRecipeType<UniversalMultiblockRecipe> QMF_RECIPE_TYPE =
            IRecipeType.create(
                    UfoMod.MOD_ID,
                    "universal_multiblock_qmf",
                    UniversalMultiblockRecipe.class
            );

    public static final IRecipeType<UniversalMultiblockRecipe> QUANTUM_SLICER_RECIPE_TYPE =
            IRecipeType.create(
                    UfoMod.MOD_ID,
                    "universal_multiblock_quantum_slicer",
                    UniversalMultiblockRecipe.class
            );

    public static final IRecipeType<UniversalMultiblockRecipe> QUANTUM_PROCESSOR_ASSEMBLER_RECIPE_TYPE =
            IRecipeType.create(
                    UfoMod.MOD_ID,
                    "universal_multiblock_quantum_processor_assembler",
                    UniversalMultiblockRecipe.class
            );

    public static final IRecipeType<UniversalMultiblockRecipe> QUANTUM_CRYOFORGE_RECIPE_TYPE =
            IRecipeType.create(
                    UfoMod.MOD_ID,
                    "universal_multiblock_quantum_cryoforge",
                    UniversalMultiblockRecipe.class
            );

    private static final Identifier BACKGROUND =
            UfoMod.id("textures/guis/dimensional_matter_assembler_jei_ui.png");

    private static final int WIDTH = 175;
    private static final int HEIGHT = 98;

    private static final int ENERGY_BAR_X = 9;
    private static final int ENERGY_BAR_Y = 81;
    private static final int ENERGY_BAR_W = 91;
    private static final int ENERGY_BAR_H = 10;

    private static final int PROGRESS_X = 105;
    private static final int PROGRESS_Y = 42;
    private static final int PROGRESS_W = 20;
    private static final int PROGRESS_H = 11;

    private static final int ITEM_OUTPUT_X = 132;
    private static final int ITEM_OUTPUT_Y = 21;

    private static final int FLUID_OUTPUT_X = 148;
    private static final int FLUID_OUTPUT_Y = 76;

    private static final int CONTROLLER_X = 150;
    private static final int CONTROLLER_Y = 2;
    private static final int CONTROLLER_SIZE = 16;

    private static final int FLUID_CAPACITY = 16_000;

    private final UniversalMultiblockMachineKind machineKind;
    private final Component title;
    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableAnimated progress;

    public UniversalMultiblockRecipeCategory(
            final IJeiHelpers helpers,
            final UniversalMultiblockMachineKind machineKind,
            final ItemStack iconStack,
            final Component title
    ) {
        this.machineKind = machineKind;
        this.title = title;

        final IGuiHelper guiHelper = helpers.getGuiHelper();

        this.background = guiHelper.createDrawable(
                BACKGROUND,
                0,
                0,
                WIDTH,
                HEIGHT
        );

        this.icon = guiHelper.createDrawableItemStack(iconStack);

        final IDrawableStatic progressDrawable = guiHelper.createDrawable(
                BACKGROUND,
                234,
                0,
                PROGRESS_W,
                PROGRESS_H
        );

        this.progress = guiHelper.createAnimatedDrawable(
                progressDrawable,
                60,
                IDrawableAnimated.StartDirection.LEFT,
                false
        );
    }

    public static IRecipeType<UniversalMultiblockRecipe> recipeTypeFor(
            final UniversalMultiblockMachineKind machineKind
    ) {
        return switch (machineKind) {
            case QMF -> QMF_RECIPE_TYPE;
            case QUANTUM_SLICER -> QUANTUM_SLICER_RECIPE_TYPE;
            case QUANTUM_PROCESSOR_ASSEMBLER -> QUANTUM_PROCESSOR_ASSEMBLER_RECIPE_TYPE;
            case QUANTUM_CRYOFORGE -> QUANTUM_CRYOFORGE_RECIPE_TYPE;
        };
    }

    @Override
    public @NonNull IRecipeType<UniversalMultiblockRecipe> getRecipeType() {
        return recipeTypeFor(this.machineKind);
    }

    @Override
    public @NonNull Component getTitle() {
        return this.title;
    }

    @Override
    public IDrawable getIcon() {
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

    @Override
    public void setRecipe(
            final @NonNull IRecipeLayoutBuilder builder,
            final UniversalMultiblockRecipe recipe,
            final @NonNull IFocusGroup focuses
    ) {
        final var itemInputs = recipe.itemInputs();

        for (int i = 0; i < itemInputs.size(); i++) {
            final var input = itemInputs.get(i);

            if (input == null || input.ingredient().isEmpty() || input.amount() <= 0) {
                continue;
            }

            final int column = i % 3;
            final int row = i / 3;

            builder.addInputSlot(
                            47 + column * 18,
                            21 + row * 18
                    )
                    .addItemStacks(UfoJeiPlugin.stackOfUniversal(input))
                    .addRichTooltipCallback(
                            (_, tooltip) ->
                                    tooltip.add(
                                            Component.literal(
                                                    "Required: %sx"
                                                            .formatted(formatAmount(input.amount()))
                                            )
                                    )
                    );
        }

        final var fluidInputs = recipe.fluidInputs();

        for (int i = 0; i < fluidInputs.size(); i++) {
            final var input = fluidInputs.get(i);

            if (input == null || input.amount() <= 0) {
                continue;
            }

            final int x = i == 0 ? 28 : 9;

            final FluidStack stack = new FluidStack(
                    input.fluid(),
                    safeInt(input.amount())
            );

            builder.addInputSlot(x, 21)
                    .setFluidRenderer(
                            FLUID_CAPACITY,
                            false,
                            12,
                            54
                    )
                    .add(NeoForgeTypes.FLUID_STACK, stack)
                    .addRichTooltipCallback(
                            (recipeSlotView, tooltip) ->
                                    tooltip.add(
                                            Component.literal(
                                                    "Required: %s mB"
                                                            .formatted(formatAmount(input.amount()))
                                            )
                                    )
                    );
        }

        if (!recipe.itemOutput().isEmpty() && recipe.itemOutputAmount() > 0) {
            final ItemStack output = recipe.getDisplayedItemOutput();

            builder.addOutputSlot(
                            ITEM_OUTPUT_X,
                            ITEM_OUTPUT_Y
                    )
                    .add(output)
                    .addRichTooltipCallback(
                            (recipeSlotView, tooltip) ->
                                    tooltip.add(
                                            Component.literal(
                                                    "Output: %sx"
                                                            .formatted(
                                                                    formatAmount(
                                                                            recipe.itemOutputAmount()
                                                                    )
                                                            )
                                            )
                                    )
                    );
        }

        if (!recipe.fluidOutput().isEmpty() && recipe.fluidOutputAmount() > 0) {
            final FluidStack output = recipe.fluidOutput()
                    .copyWithAmount(
                            safeInt(recipe.fluidOutputAmount())
                    );

            builder.addOutputSlot(
                            FLUID_OUTPUT_X,
                            FLUID_OUTPUT_Y
                    )
                    .setFluidRenderer(
                            FLUID_CAPACITY,
                            false,
                            14,
                            17
                    )
                    .add(NeoForgeTypes.FLUID_STACK, output)
                    .addRichTooltipCallback(
                            (recipeSlotView, tooltip) ->
                                    tooltip.add(
                                            Component.literal(
                                                    "Output: %s mB"
                                                            .formatted(
                                                                    formatAmount(
                                                                            recipe.fluidOutputAmount()
                                                                    )
                                                            )
                                            )
                                    )
                    );
        }
    }

    @Override
    public void draw(
            final UniversalMultiblockRecipe recipe,
            final @NonNull IRecipeSlotsView recipeSlotsView,
            final @NonNull GuiGraphicsExtractor guiGraphics,
            final double mouseX,
            final double mouseY
    ) {
        this.background.draw(guiGraphics, 0, 0);
        this.progress.draw(guiGraphics, PROGRESS_X, PROGRESS_Y);

        guiGraphics.item(
                controllerStackFor(this.machineKind),
                CONTROLLER_X,
                CONTROLLER_Y
        );

        guiGraphics.fill(
                ENERGY_BAR_X,
                ENERGY_BAR_Y,
                ENERGY_BAR_X + ENERGY_BAR_W,
                ENERGY_BAR_Y + ENERGY_BAR_H,
                0xFF101010
        );

        guiGraphics.fillGradient(
                ENERGY_BAR_X,
                ENERGY_BAR_Y,
                ENERGY_BAR_X + ENERGY_BAR_W,
                ENERGY_BAR_Y + ENERGY_BAR_H,
                0x880055FF,
                0xDD0022AA
        );

        final var font = Minecraft.getInstance().font;

        final String tierText = "MK" + recipe.requiredTier();
        final int tierX =
                CONTROLLER_X
                        + CONTROLLER_SIZE / 2
                        - font.width(tierText) / 2;

        guiGraphics.text(
                font,
                tierText,
                tierX,
                CONTROLLER_Y + 18,
                0xFFFFD966,
                true
        );

        final String energyText = formatEnergy(recipe.energy());
        final int energyTextX =
                ENERGY_BAR_X
                        + (ENERGY_BAR_W - font.width(energyText)) / 2;

        guiGraphics.text(
                font,
                energyText,
                energyTextX,
                ENERGY_BAR_Y + 1,
                0xFFFFFFFF,
                true
        );
    }

    @Override
    public void getTooltip(
            final @NonNull ITooltipBuilder tooltip,
            final UniversalMultiblockRecipe recipe,
            final @NonNull IRecipeSlotsView recipeSlotsView,
            final double mouseX,
            final double mouseY
    ) {
        if (isInside(
                mouseX,
                mouseY,
                ENERGY_BAR_X,
                ENERGY_BAR_Y,
                ENERGY_BAR_W,
                ENERGY_BAR_H
        )) {
            tooltip.add(
                    Component.literal(
                            "Energy: " + formatEnergy(recipe.energy())
                    )
            );

            tooltip.add(
                    Component.literal(
                            "Base Time: %.1fs (%d ticks)"
                                    .formatted(
                                            recipe.time() / 20.0,
                                            recipe.time()
                                    )
                    )
            );

            tooltip.add(
                    Component.literal(
                            "Required Tier: MK" + recipe.requiredTier()
                    )
            );

            return;
        }

        if (isInside(
                mouseX,
                mouseY,
                PROGRESS_X,
                PROGRESS_Y,
                PROGRESS_W,
                PROGRESS_H
        )) {
            tooltip.add(
                    Component.literal(
                            "Processing Time: %.1fs (%d ticks)"
                                    .formatted(
                                            recipe.time() / 20.0,
                                            recipe.time()
                                    )
                    )
            );

            tooltip.add(
                    Component.literal(
                            "Required Tier: MK" + recipe.requiredTier()
                    )
            );

            return;
        }

        if (isInside(
                mouseX,
                mouseY,
                CONTROLLER_X,
                CONTROLLER_Y,
                CONTROLLER_SIZE,
                28
        )) {
            tooltip.add(Component.literal("Controller"));

            tooltip.add(
                    Component.literal(
                            "Required Machine Tier: MK"
                                    + recipe.requiredTier()
                    )
            );

            tooltip.add(
                    Component.literal(
                            "Click the controller to open Multiblock Info"
                    )
            );
        }
    }

    private static ItemStack controllerStackFor(
            final UniversalMultiblockMachineKind machineKind
    ) {
        return switch (machineKind) {
            case QMF ->
                    MultiblockBlocks
                            .QUANTUM_MATTER_FABRICATOR_CONTROLLER
                            .get()
                            .asItem()
                            .getDefaultInstance();

            case QUANTUM_SLICER ->
                    MultiblockBlocks
                            .QUANTUM_SLICER_CONTROLLER
                            .get()
                            .asItem()
                            .getDefaultInstance();

            case QUANTUM_PROCESSOR_ASSEMBLER ->
                    MultiblockBlocks
                            .QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER
                            .get()
                            .asItem()
                            .getDefaultInstance();

            case QUANTUM_CRYOFORGE ->
                    MultiblockBlocks
                            .QUANTUM_CRYOFORGE_CONTROLLER
                            .get()
                            .asItem()
                            .getDefaultInstance();
        };
    }

    private static boolean isInside(
            final double mouseX,
            final double mouseY,
            final int x,
            final int y,
            final int width,
            final int height
    ) {
        return mouseX >= x
                && mouseX < x + width
                && mouseY >= y
                && mouseY < y + height;
    }

    private static int safeInt(final long value) {
        return (int) Math.min(
                Integer.MAX_VALUE,
                Math.max(0L, value)
        );
    }

    private static String formatEnergy(final long energy) {
        final int magnitude = energy >= 1_000_000_000L
                ? 3
                : energy >= 1_000_000L
                ? 2
                : energy >= 1_000L
                ? 1
                : 0;

        return switch (magnitude) {
            case 3 ->
                    formatScaledEnergy(
                            energy,
                            1_000_000_000.0,
                            "G"
                    );

            case 2 ->
                    formatScaledEnergy(
                            energy,
                            1_000_000.0,
                            "M"
                    );

            case 1 ->
                    formatScaledEnergy(
                            energy,
                            1_000.0,
                            "K"
                    );

            default -> energy + " AE";
        };
    }

    private static String formatScaledEnergy(
            final long energy,
            final double divisor,
            final String suffix
    ) {
        final double value = energy / divisor;

        return value == Math.rint(value)
                ? "%d%s AE".formatted((long) value, suffix)
                : "%.1f%s AE".formatted(value, suffix);
    }

    private static String formatAmount(final long amount) {
        return Long.toString(amount);
    }
}
