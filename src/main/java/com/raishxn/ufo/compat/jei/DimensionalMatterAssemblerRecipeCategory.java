package com.raishxn.ufo.compat.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import java.util.List;

public class DimensionalMatterAssemblerRecipeCategory implements IRecipeCategory<DimensionalMatterAssemblerRecipe> {

    public static final RecipeType<DimensionalMatterAssemblerRecipe> RECIPE_TYPE =
            RecipeType.create(UfoMod.MOD_ID, "dimensional_assembly", DimensionalMatterAssemblerRecipe.class);

    private static final Identifier BACKGROUND = UfoMod.id("textures/guis/dimensional_matter_assembler_jei_ui.png");

    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableAnimated progress;

    private static final int ENERGY_BAR_X = 9;
    private static final int ENERGY_BAR_Y = 81;
    private static final int ENERGY_BAR_W = 91;
    private static final int ENERGY_BAR_H = 10;

    public DimensionalMatterAssemblerRecipeCategory(final IJeiHelpers helpers) {
        final IGuiHelper guiHelper = helpers.getGuiHelper();
        background = guiHelper.createDrawable(BACKGROUND, 0, 0, 175, 98);
        icon = guiHelper.createDrawableItemStack(ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get().asItem().getDefaultInstance());

        final IDrawableStatic progressDrawable = guiHelper.createDrawable(BACKGROUND, 234, 0, 20, 11);
        this.progress =
                guiHelper.createAnimatedDrawable(progressDrawable, 60, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<DimensionalMatterAssemblerRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get().getName();
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public void setRecipe(final IRecipeLayoutBuilder builder, final DimensionalMatterAssemblerRecipe recipe, final IFocusGroup focuses) {

        final var itemInputs = recipe.itemInputs();
        for (int i = 0; i < itemInputs.size(); i++) {
            if (itemInputs.get(i) != null && !itemInputs.get(i).isEmpty()) {
                final int col = i % 3;
                final int row = i / 3;
                builder.addInputSlot(47 + (col * 18), 21 + (row * 18))
                       .addIngredients(UfoJeiPlugin.stackOf(itemInputs.get(i)));
            }
        }

        final var fluidInputs = recipe.fluidInputs();
        for (int i = 0; i < fluidInputs.size(); i++) {
            if (!fluidInputs.get(i).isEmpty()) {
                final var slot = builder.addInputSlot(28, 21).setFluidRenderer(16000, false, 12, 54);
                slot.addIngredients(NeoForgeTypes.FLUID_STACK, UfoJeiPlugin.stackOf(fluidInputs.get(i)));
            }
        }

        final var itemOutputs = recipe.itemOutputs();
        if (itemOutputs.size() > 0 && itemOutputs.get(0).what() instanceof final AEItemKey itemKey) {
            builder.addOutputSlot(132, 21).addItemStack(itemKey.toStack((int)itemOutputs.get(0).amount()));
        }
        if (itemOutputs.size() > 1 && itemOutputs.get(1).what() instanceof final AEItemKey itemKey) {
            builder.addOutputSlot(132, 49).addItemStack(itemKey.toStack((int)itemOutputs.get(1).amount()));
        }

        final var fluidOutputs = recipe.fluidOutputs();
        if (fluidOutputs.size() > 0 && fluidOutputs.get(0).what() instanceof final AEFluidKey fluidKey) {
            final var slot = builder.addOutputSlot(119, 75).setFluidRenderer(16000, false, 14, 17);
            slot.addFluidStack(fluidKey.getFluid(), (int)fluidOutputs.get(0).amount());
        }
        if (fluidOutputs.size() > 1 && fluidOutputs.get(1).what() instanceof final AEFluidKey fluidKey) {
            final var slot = builder.addOutputSlot(148, 76).setFluidRenderer(16000, false, 14, 17);
            slot.addFluidStack(fluidKey.getFluid(), (int)fluidOutputs.get(1).amount());
        }
    }

    @Override
    public void draw(
            final DimensionalMatterAssemblerRecipe recipe,
            final IRecipeSlotsView recipeSlotsView,
            final GuiGraphicsExtractor guiGraphics,
            final double mouseX,
            final double mouseY) {
        this.background.draw(guiGraphics);
        
        this.progress.draw(guiGraphics, 105, 42);

        guiGraphics.fill(9, 81, 100, 91, 0xFF101010); // Lighly offset black
        
        final var font = Minecraft.getInstance().font;
        final String energyText = "⚡ " + formatEnergy(recipe.energy());

        guiGraphics.fillGradient(
                ENERGY_BAR_X,
                ENERGY_BAR_Y,
                ENERGY_BAR_X + ENERGY_BAR_W,
                ENERGY_BAR_Y + ENERGY_BAR_H,
                0x880055FF, // Top Color
                0xDD0022AA  // Bottom Color
        );

        final int textWidth = font.width(energyText);
        final int textX = ENERGY_BAR_X + (ENERGY_BAR_W - textWidth) / 2;
        final int textY = ENERGY_BAR_Y + 1;
        guiGraphics.drawString(font, energyText, textX, textY, 0xFFFFFFFF, true);
    }

    @Override
    public List<Component> getTooltipStrings(final DimensionalMatterAssemblerRecipe recipe, final IRecipeSlotsView recipeSlotsView, final double mouseX, final double mouseY) {
        if (mouseX >= ENERGY_BAR_X && mouseX <= ENERGY_BAR_X + ENERGY_BAR_W
                && mouseY >= ENERGY_BAR_Y && mouseY <= ENERGY_BAR_Y + ENERGY_BAR_H) {
            final int baseTicks = recipe.time();
            final double seconds = baseTicks / 20.0;
            return List.of(
                    Component.literal("Energy: " + formatEnergy(recipe.energy())),
                    Component.literal(String.format("Base Time: %.1fs (%d ticks)", seconds, baseTicks)),
                    Component.literal("§7Chrono Catalysts reduce processing time")
            );
        }

        if (mouseX >= 105 && mouseX <= 125 && mouseY >= 42 && mouseY <= 53) {
            final int baseTicks = recipe.time();
            final double seconds = baseTicks / 20.0;
            return List.of(
                    Component.literal(String.format("Processing Time: %.1fs", seconds)),
                    Component.literal("§7(base, without Chrono Catalysts)")
            );
        }

        return List.of();
    }

    /**
     * Formats energy values into human-readable strings.
     * K = thousands, M = millions, G = billions
     */
    private static String formatEnergy(final int energy) {
        if (energy >= 1_000_000_000) {
            final double val = energy / 1_000_000_000.0;
            if (val == (long) val) return (long) val + "G AE";
            return String.format("%.1fG AE", val);
        } else if (energy >= 1_000_000) {
            final double val = energy / 1_000_000.0;
            if (val == (long) val) return (long) val + "M AE";
            return String.format("%.1fM AE", val);
        } else if (energy >= 1_000) {
            final double val = energy / 1_000.0;
            if (val == (long) val) return (long) val + "K AE";
            return String.format("%.1fK AE", val);
        }
        return energy + " AE";
    }
}
