package com.raishxn.ufo.compat.jei;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.recipe.QMFRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

public final class QmfRecipeCategory implements IRecipeCategory<QMFRecipe> {

    public static final IRecipeType<QMFRecipe> RECIPE_TYPE =
            IRecipeType.create(
                    UfoMod.MOD_ID,
                    "qmf_recipe",
                    QMFRecipe.class
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

    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableAnimated progress;

    public QmfRecipeCategory(final IJeiHelpers helpers) {
        final IGuiHelper guiHelper = helpers.getGuiHelper();

        this.background = guiHelper.createDrawable(
                BACKGROUND,
                0,
                0,
                WIDTH,
                HEIGHT
        );

        this.icon = guiHelper.createDrawableItemStack(
                MultiblockBlocks
                        .QUANTUM_MATTER_FABRICATOR_CONTROLLER
                        .get()
                        .asItem()
                        .getDefaultInstance()
        );

        final IDrawableStatic progressDrawable =
                guiHelper.createDrawable(
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
    public @NonNull IRecipeType<QMFRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NonNull Component getTitle() {
        return MultiblockBlocks
                .QUANTUM_MATTER_FABRICATOR_CONTROLLER
                .get()
                .getName();
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
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(
            final @NonNull IRecipeLayoutBuilder builder,
            final QMFRecipe recipe,
            final @NonNull IFocusGroup focuses
    ) {
        final var itemInputs = recipe.getItemInputs();

        for (int i = 0; i < itemInputs.size(); i++) {
            final var ingredient = itemInputs.get(i);

            final int col = i % 3;
            final int row = i / 3;

            builder.addInputSlot(
                            47 + (col * 18),
                            21 + (row * 18)
                    )
                    .addItemStacks(UfoJeiPlugin.stackOfQmf(ingredient))
                    .addRichTooltipCallback(
                            (_, tooltip) ->
                                    tooltip.add(
                                            Component.literal(
                                                    "Required: "
                                                            + formatAmount(ingredient.amount())
                                                            + "x"
                                            )
                                    )
                    );
        }

        final var fluidInputs = recipe.getFluidInputs();

        for (int i = 0; i < fluidInputs.size(); i++) {
            final var ingredient = fluidInputs.get(i);

            final int x = i == 0 ? 28 : 9;

            builder.addSlot(
                            RecipeIngredientRole.INPUT,
                            x,
                            21
                    )
                    .setFluidRenderer(
                            16_000L,
                            false,
                            12,
                            54
                    )

                    .add(
                            NeoForgeTypes.FLUID_STACK,
                            new FluidStack(
                                    ingredient.fluid().getFluid(),
                                    safeInt(ingredient.amount())
                            )
                    )
                    .addRichTooltipCallback(
                            (recipeSlotView, tooltip) ->
                                    tooltip.add(
                                            Component.literal(
                                                    "Required: "
                                                            + formatAmount(ingredient.amount())
                                                            + " mB"
                                            )
                                    )
                    );
        }

        final ItemStack output = recipe.getResultItem();

        builder.addSlot(
                        RecipeIngredientRole.OUTPUT,
                        132,
                        21
                )
                .add(output.copy())
                .addRichTooltipCallback(
                        (recipeSlotView, tooltip) ->
                                tooltip.add(
                                        Component.literal(
                                                "Output: "
                                                        + formatAmount(output.getCount())
                                                        + "x"
                                        )
                                )
                );
    }

    private static int safeInt(final long amount) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, amount));
    }

    @Override
    public void draw(
            final QMFRecipe recipe,
            final @NonNull IRecipeSlotsView recipeSlotsView,
            final @NonNull GuiGraphicsExtractor guiGraphics,
            final double mouseX,
            final double mouseY
    ) {
        this.background.draw(guiGraphics, 0, 0);

        this.progress.draw(
                guiGraphics,
                PROGRESS_X,
                PROGRESS_Y
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

        final String energyText =
                "AE " + formatEnergy(recipe.getEnergy());

        final int textWidth = font.width(energyText);

        final int textX =
                ENERGY_BAR_X
                        + (ENERGY_BAR_W - textWidth) / 2;

        guiGraphics.text(
                font,
                energyText,
                textX,
                ENERGY_BAR_Y + 1,
                0xFFFFFFFF,
                true
        );
    }

    @Override
    public void getTooltip(
            final @NonNull ITooltipBuilder tooltip,
            final QMFRecipe recipe,
            final @NonNull IRecipeSlotsView recipeSlotsView,
            final double mouseX,
            final double mouseY
    ) {
        if (mouseX >= ENERGY_BAR_X
                && mouseX <= ENERGY_BAR_X + ENERGY_BAR_W
                && mouseY >= ENERGY_BAR_Y
                && mouseY <= ENERGY_BAR_Y + ENERGY_BAR_H) {

            tooltip.add(
                    Component.literal(
                            "Energy: " + formatEnergy(recipe.getEnergy()) + " AE"
                    )
            );

            tooltip.add(
                    Component.literal(
                            String.format(
                                    "Base Time: %.1fs (%d ticks)",
                                    recipe.getTime() / 20.0,
                                    recipe.getTime()
                            )
                    )
            );

            tooltip.add(
                    Component.literal(
                            "Required Tier: MK"
                                    + recipe.getRequiredTier()
                    )
            );

            return;
        }

        if (mouseX >= PROGRESS_X
                && mouseX <= PROGRESS_X + PROGRESS_W
                && mouseY >= PROGRESS_Y
                && mouseY <= PROGRESS_Y + PROGRESS_H) {

            tooltip.add(
                    Component.literal(
                            String.format(
                                    "Processing Time: %.1fs (%d ticks)",
                                    recipe.getTime() / 20.0,
                                    recipe.getTime()
                            )
                    )
            );

            tooltip.add(
                    Component.literal(
                            "Required Tier: MK"
                                    + recipe.getRequiredTier()
                    )
            );
        }
    }

    private static String formatEnergy(final long energy) {
        if (energy >= 1_000_000_000L) {
            return String.format(
                    "%.1fG",
                    energy / 1_000_000_000.0
            );
        }

        if (energy >= 1_000_000L) {
            return String.format(
                    "%.1fM",
                    energy / 1_000_000.0
            );
        }

        if (energy >= 1_000L) {
            return String.format(
                    "%.1fK",
                    energy / 1_000.0
            );
        }

        return Long.toString(energy);
    }

    private static String formatAmount(final long amount) {
        return Long.toString(amount);
    }
}
