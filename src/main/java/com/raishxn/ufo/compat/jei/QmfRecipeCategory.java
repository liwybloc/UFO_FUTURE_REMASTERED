package com.raishxn.ufo.compat.jei;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.recipe.QMFRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class QmfRecipeCategory implements IRecipeCategory<QMFRecipe> {
    public static final RecipeType<QMFRecipe> RECIPE_TYPE =
            RecipeType.create(UfoMod.MOD_ID, "qmf_recipe", QMFRecipe.class);

    private static final Identifier BACKGROUND = UfoMod.id("textures/guis/dimensional_matter_assembler_jei_ui.png");

    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableAnimated progress;

    private static final int ENERGY_BAR_X = 9;
    private static final int ENERGY_BAR_Y = 81;
    private static final int ENERGY_BAR_W = 91;
    private static final int ENERGY_BAR_H = 10;

    public QmfRecipeCategory(final IJeiHelpers helpers) {
        final IGuiHelper guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createDrawable(BACKGROUND, 0, 0, 175, 98);
        this.icon = guiHelper.createDrawableItemStack(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().asItem().getDefaultInstance());
        final IDrawableStatic progressDrawable = guiHelper.createDrawable(BACKGROUND, 234, 0, 20, 11);
        this.progress = guiHelper.createAnimatedDrawable(progressDrawable, 60, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<QMFRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().getName();
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(final IRecipeLayoutBuilder builder, final QMFRecipe recipe, final IFocusGroup focuses) {
        final var itemInputs = recipe.getItemInputs();
        for (int i = 0; i < itemInputs.size(); i++) {
            final var ingredient = itemInputs.get(i);
            final int col = i % 3;
            final int row = i / 3;
            builder.addInputSlot(47 + (col * 18), 21 + (row * 18))
                    .addIngredients(UfoJeiPlugin.stackOfQmf(ingredient))
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Required: " + formatAmount(ingredient.amount()) + "x")));
        }

        final var fluidInputs = recipe.getFluidInputs();
        for (int i = 0; i < fluidInputs.size(); i++) {
            final var ingredient = fluidInputs.get(i);
            final int x = i == 0 ? 28 : 9;
            builder.addInputSlot(x, 21)
                    .setFluidRenderer(16000, false, 12, 54)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, ingredient.fluid().copyWithAmount((int) ingredient.amount()))
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Required: " + formatAmount(ingredient.amount()) + " mB")));
        }

        final ItemStack output = recipe.getResultItem();
        builder.addOutputSlot(132, 21)
                .addItemStack(output.copy())
                .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Output: " + formatAmount(output.getCount()) + "x")));
    }

    @Override
    public void draw(final QMFRecipe recipe, final IRecipeSlotsView recipeSlotsView, final GuiGraphicsExtractor guiGraphics, final double mouseX, final double mouseY) {
        this.background.draw(guiGraphics);
        this.progress.draw(guiGraphics, 105, 42);

        guiGraphics.fill(9, 81, 100, 91, 0xFF101010);

        final var font = net.minecraft.client.Minecraft.getInstance().font;
        final String energyText = "AE " + formatEnergy(recipe.getEnergy());
        guiGraphics.fillGradient(
                ENERGY_BAR_X,
                ENERGY_BAR_Y,
                ENERGY_BAR_X + ENERGY_BAR_W,
                ENERGY_BAR_Y + ENERGY_BAR_H,
                0x880055FF,
                0xDD0022AA
        );

        final int textWidth = font.width(energyText);
        final int textX = ENERGY_BAR_X + (ENERGY_BAR_W - textWidth) / 2;
        guiGraphics.drawString(font, energyText, textX, ENERGY_BAR_Y + 1, 0xFFFFFFFF, true);
    }

    @Override
    public List<Component> getTooltipStrings(final QMFRecipe recipe, final IRecipeSlotsView recipeSlotsView, final double mouseX, final double mouseY) {
        if (mouseX >= ENERGY_BAR_X && mouseX <= ENERGY_BAR_X + ENERGY_BAR_W
                && mouseY >= ENERGY_BAR_Y && mouseY <= ENERGY_BAR_Y + ENERGY_BAR_H) {
            return List.of(
                    Component.literal("Energy: " + formatEnergy(recipe.getEnergy())),
                    Component.literal(String.format("Base Time: %.1fs (%d ticks)", recipe.getTime() / 20.0, recipe.getTime())),
                    Component.literal("Required Tier: MK" + recipe.getRequiredTier())
            );
        }

        if (mouseX >= 105 && mouseX <= 125 && mouseY >= 42 && mouseY <= 53) {
            return List.of(
                    Component.literal(String.format("Processing Time: %.1fs (%d ticks)", recipe.getTime() / 20.0, recipe.getTime())),
                    Component.literal("Required Tier: MK" + recipe.getRequiredTier())
            );
        }

        return List.of();
    }

    private static String formatEnergy(final long energy) {
        if (energy >= 1_000_000_000L) {
            return String.format("%.1fG", energy / 1_000_000_000.0) + " AE";
        }
        if (energy >= 1_000_000L) {
            return String.format("%.1fM", energy / 1_000_000.0) + " AE";
        }
        if (energy >= 1_000L) {
            return String.format("%.1fK", energy / 1_000.0) + " AE";
        }
        return energy + " AE";
    }

    private static String formatAmount(final long amount) {
        return Long.toString(amount);
    }
}
