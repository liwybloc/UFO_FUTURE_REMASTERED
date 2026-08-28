package com.raishxn.ufo.compat.jei;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;

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
import org.jspecify.annotations.NonNull;

public final class DimensionalMatterAssemblerRecipeCategory
        implements IRecipeCategory<DimensionalMatterAssemblerRecipe> {

    public static final IRecipeType<DimensionalMatterAssemblerRecipe> RECIPE_TYPE =
            IRecipeType.create(
                    UfoMod.MOD_ID,
                    "dimensional_assembly",
                    DimensionalMatterAssemblerRecipe.class
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

    private static final int FLUID_CAPACITY = 16_000;

    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableAnimated progress;

    public DimensionalMatterAssemblerRecipeCategory(final IJeiHelpers helpers) {
        final IGuiHelper guiHelper = helpers.getGuiHelper();

        this.background = guiHelper.createDrawable(
                BACKGROUND,
                0,
                0,
                WIDTH,
                HEIGHT
        );

        this.icon = guiHelper.createDrawableItemStack(
                ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK
                        .get()
                        .asItem()
                        .getDefaultInstance()
        );

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

    @Override
    public @NonNull IRecipeType<DimensionalMatterAssemblerRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NonNull Component getTitle() {
        return ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK
                .get()
                .getName();
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
            final DimensionalMatterAssemblerRecipe recipe,
            final @NonNull IFocusGroup focuses
    ) {
        final var itemInputs = recipe.itemInputs();

        for (int i = 0; i < itemInputs.size(); i++) {
            final var input = itemInputs.get(i);

            if (input == null || input.isEmpty()) {
                continue;
            }

            final int column = i % 3;
            final int row = i / 3;

            builder.addInputSlot(
                            47 + column * 18,
                            21 + row * 18
                    )
                    .addItemStacks(UfoJeiPlugin.stackOf(input))
                    .addRichTooltipCallback(
                            (_, tooltip) -> tooltip.add(
                                    Component.literal("Required: " + input.getAmount() + "x")
                            )
                    );
        }

        final var fluidInputs = recipe.fluidInputs();

        for (int i = 0; i < fluidInputs.size(); i++) {
            final var input = fluidInputs.get(i);

            if (input == null || input.isEmpty()) {
                continue;
            }

            final int x = i == 0 ? 28 : 9;

            builder.addInputSlot(x, 21)
                    .setFluidRenderer(
                            FLUID_CAPACITY,
                            false,
                            12,
                            54
                    )
                    .addIngredients(
                            NeoForgeTypes.FLUID_STACK,
                            UfoJeiPlugin.stackOf(input)
                    );
        }

        final var itemOutputs = recipe.itemOutputs();

        for (int i = 0; i < Math.min(itemOutputs.size(), 2); i++) {
            final var output = itemOutputs.get(i);

            if (!(output.what() instanceof final AEItemKey itemKey)) {
                continue;
            }

            final int y = switch (i) {
                case 0 -> 21;
                case 1 -> 49;
                default -> throw new IllegalStateException();
            };

            builder.addOutputSlot(132, y)
                    .add(itemKey.toStack(safeInt(output.amount())));
        }

        final var fluidOutputs = recipe.fluidOutputs();

        for (int i = 0; i < Math.min(fluidOutputs.size(), 2); i++) {
            final var output = fluidOutputs.get(i);

            if (!(output.what() instanceof final AEFluidKey fluidKey)) {
                continue;
            }

            final int x = switch (i) {
                case 0 -> 119;
                case 1 -> 148;
                default -> throw new IllegalStateException();
            };

            final int y = switch (i) {
                case 0 -> 75;
                case 1 -> 76;
                default -> throw new IllegalStateException();
            };

            builder.addOutputSlot(x, y)
                    .setFluidRenderer(
                            FLUID_CAPACITY,
                            false,
                            14,
                            17
                    )
                    .add(
                            NeoForgeTypes.FLUID_STACK,
                            new net.neoforged.neoforge.fluids.FluidStack(
                                    fluidKey.getFluid(),
                                    safeInt(output.amount())
                            )
                    );
        }
    }

    @Override
    public void draw(
            final DimensionalMatterAssemblerRecipe recipe,
            final @NonNull IRecipeSlotsView recipeSlotsView,
            final @NonNull GuiGraphicsExtractor guiGraphics,
            final double mouseX,
            final double mouseY
    ) {
        this.background.draw(guiGraphics, 0, 0);
        this.progress.draw(guiGraphics, PROGRESS_X, PROGRESS_Y);

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
        final String energyText = "⚡ " + formatEnergy(recipe.energy());
        final int textX = ENERGY_BAR_X + (ENERGY_BAR_W - font.width(energyText)) / 2;
        final int textY = ENERGY_BAR_Y + 1;

        guiGraphics.text(
                font,
                energyText,
                textX,
                textY,
                0xFFFFFFFF,
                true
        );
    }

    @Override
    public void getTooltip(
            final @NonNull ITooltipBuilder tooltip,
            final DimensionalMatterAssemblerRecipe recipe,
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
            final int ticks = recipe.time();

            tooltip.add(
                    Component.literal(
                            "Energy: " + formatEnergy(recipe.energy())
                    )
            );

            tooltip.add(
                    Component.literal(
                            "Base Time: %.1fs (%d ticks)"
                                    .formatted(ticks / 20.0, ticks)
                    )
            );

            tooltip.add(
                    Component.literal(
                            "Chrono Catalysts reduce processing time"
                    ).withStyle(style -> style.withColor(0xAAAAAA))
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
            final int ticks = recipe.time();

            tooltip.add(
                    Component.literal(
                            "Processing Time: %.1fs"
                                    .formatted(ticks / 20.0)
                    )
            );

            tooltip.add(
                    Component.literal(
                            "(base, without Chrono Catalysts)"
                    ).withStyle(style -> style.withColor(0xAAAAAA))
            );
        }
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
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, value));
    }

    private static String formatEnergy(final int energy) {
        final int magnitude = energy >= 1_000_000_000
                ? 3
                : energy >= 1_000_000
                ? 2
                : energy >= 1_000
                ? 1
                : 0;

        return switch (magnitude) {
            case 3 -> formatScaledEnergy(energy, 1_000_000_000.0, "G");
            case 2 -> formatScaledEnergy(energy, 1_000_000.0, "M");
            case 1 -> formatScaledEnergy(energy, 1_000.0, "K");
            default -> energy + " AE";
        };
    }

    private static String formatScaledEnergy(
            final int energy,
            final double divisor,
            final String suffix
    ) {
        final double value = energy / divisor;

        return value == Math.rint(value)
                ? "%d%s AE".formatted((long) value, suffix)
                : "%.1f%s AE".formatted(value, suffix);
    }
}
