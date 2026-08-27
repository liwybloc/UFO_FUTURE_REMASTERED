package com.raishxn.ufo.compat.jei;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.recipe.UniversalMultiblockMachineKind;
import com.raishxn.ufo.recipe.UniversalMultiblockRecipe;
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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class UniversalMultiblockRecipeCategory implements IRecipeCategory<UniversalMultiblockRecipe> {
    public static final RecipeType<UniversalMultiblockRecipe> QMF_RECIPE_TYPE =
            RecipeType.create(UfoMod.MOD_ID, "universal_multiblock_qmf", UniversalMultiblockRecipe.class);
    public static final RecipeType<UniversalMultiblockRecipe> QUANTUM_SLICER_RECIPE_TYPE =
            RecipeType.create(UfoMod.MOD_ID, "universal_multiblock_quantum_slicer", UniversalMultiblockRecipe.class);
    public static final RecipeType<UniversalMultiblockRecipe> QUANTUM_PROCESSOR_ASSEMBLER_RECIPE_TYPE =
            RecipeType.create(UfoMod.MOD_ID, "universal_multiblock_quantum_processor_assembler", UniversalMultiblockRecipe.class);
    public static final RecipeType<UniversalMultiblockRecipe> QUANTUM_CRYOFORGE_RECIPE_TYPE =
            RecipeType.create(UfoMod.MOD_ID, "universal_multiblock_quantum_cryoforge", UniversalMultiblockRecipe.class);

    private static final Identifier BACKGROUND = UfoMod.id("textures/guis/dimensional_matter_assembler_jei_ui.png");

    private static final int ENERGY_BAR_X = 9;
    private static final int ENERGY_BAR_Y = 81;
    private static final int ENERGY_BAR_W = 91;
    private static final int ENERGY_BAR_H = 10;
    private static final int ITEM_OUTPUT_X = 132;
    private static final int ITEM_OUTPUT_Y = 21;
    private static final int CONTROLLER_X = 150;
    private static final int CONTROLLER_Y = 2;

    private final UniversalMultiblockMachineKind machineKind;
    private final Component title;
    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableAnimated progress;

    public UniversalMultiblockRecipeCategory(final IJeiHelpers helpers,
                                             final UniversalMultiblockMachineKind machineKind,
                                             final ItemStack iconStack,
                                             final Component title) {
        this.machineKind = machineKind;
        this.title = title;

        final IGuiHelper guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createDrawable(BACKGROUND, 0, 0, 175, 98);
        this.icon = guiHelper.createDrawableItemStack(iconStack);
        final IDrawableStatic progressDrawable = guiHelper.createDrawable(BACKGROUND, 234, 0, 20, 11);
        this.progress = guiHelper.createAnimatedDrawable(progressDrawable, 60, IDrawableAnimated.StartDirection.LEFT, false);
    }

    public static RecipeType<UniversalMultiblockRecipe> recipeTypeFor(final UniversalMultiblockMachineKind machineKind) {
        return switch (machineKind) {
            case QMF -> QMF_RECIPE_TYPE;
            case QUANTUM_SLICER -> QUANTUM_SLICER_RECIPE_TYPE;
            case QUANTUM_PROCESSOR_ASSEMBLER -> QUANTUM_PROCESSOR_ASSEMBLER_RECIPE_TYPE;
            case QUANTUM_CRYOFORGE -> QUANTUM_CRYOFORGE_RECIPE_TYPE;
        };
    }

    @Override
    public RecipeType<UniversalMultiblockRecipe> getRecipeType() {
        return recipeTypeFor(this.machineKind);
    }

    @Override
    public Component getTitle() {
        return this.title;
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
    public void setRecipe(final IRecipeLayoutBuilder builder, final UniversalMultiblockRecipe recipe, final IFocusGroup focuses) {
        final var itemInputs = recipe.itemInputs();
        for (int i = 0; i < itemInputs.size(); i++) {
            final var ingredient = itemInputs.get(i);
            final int col = i % 3;
            final int row = i / 3;
            builder.addInputSlot(47 + (col * 18), 21 + (row * 18))
                    .addIngredients(UfoJeiPlugin.stackOfUniversal(ingredient))
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Required: " + formatAmount(ingredient.amount()) + "x")));
        }

        final var fluidInputs = recipe.fluidInputs();
        for (int i = 0; i < fluidInputs.size(); i++) {
            final var ingredient = fluidInputs.get(i);
            final int x = i == 0 ? 28 : 9;
            builder.addInputSlot(x, 21)
                    .setFluidRenderer(16000, false, 12, 54)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, new FluidStack(ingredient.fluid(), (int) ingredient.amount()))
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Required: " + formatAmount(ingredient.amount()) + " mB")));
        }

        if (!recipe.itemOutput().isEmpty()) {
            final ItemStack itemOutput = recipe.getDisplayedItemOutput();
            builder.addOutputSlot(ITEM_OUTPUT_X, ITEM_OUTPUT_Y)
                    .addItemStack(itemOutput)
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Output: " + formatAmount(recipe.itemOutputAmount()) + "x")));
        }

        if (!recipe.fluidOutput().isEmpty() && recipe.fluidOutputAmount() > 0) {
            final FluidStack fluidOutput = recipe.fluidOutput().copyWithAmount((int) recipe.fluidOutputAmount());
            builder.addOutputSlot(148, 76)
                    .setFluidRenderer(16000, false, 14, 17)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, fluidOutput)
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Output: " + formatAmount(recipe.fluidOutputAmount()) + " mB")));
        }
    }

    @Override
    public void draw(final UniversalMultiblockRecipe recipe, final IRecipeSlotsView recipeSlotsView, final GuiGraphicsExtractor guiGraphics, final double mouseX, final double mouseY) {
        this.background.draw(guiGraphics);
        this.progress.draw(guiGraphics, 105, 42);
        guiGraphics.renderItem(controllerStackFor(this.machineKind), CONTROLLER_X, CONTROLLER_Y);

        guiGraphics.fill(9, 81, 100, 91, 0xFF101010);

        final var font = net.minecraft.client.Minecraft.getInstance().font;
        final String tierText = "MK" + recipe.requiredTier();
        final int tierX = CONTROLLER_X + 8 - font.width(tierText) / 2;
        guiGraphics.drawString(font, tierText, tierX, CONTROLLER_Y + 18, 0xFFFFD966, true);

        final String energyText = "AE " + formatEnergy(recipe.energy());
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
    public List<Component> getTooltipStrings(final UniversalMultiblockRecipe recipe, final IRecipeSlotsView recipeSlotsView, final double mouseX, final double mouseY) {
        if (mouseX >= ENERGY_BAR_X && mouseX <= ENERGY_BAR_X + ENERGY_BAR_W
                && mouseY >= ENERGY_BAR_Y && mouseY <= ENERGY_BAR_Y + ENERGY_BAR_H) {
            return List.of(
                    Component.literal("Energy: " + formatEnergy(recipe.energy())),
                    Component.literal(String.format("Base Time: %.1fs (%d ticks)", recipe.time() / 20.0, recipe.time())),
                    Component.literal("Required Tier: MK" + recipe.requiredTier())
            );
        }

        if (mouseX >= 105 && mouseX <= 125 && mouseY >= 42 && mouseY <= 53) {
            return List.of(
                    Component.literal(String.format("Processing Time: %.1fs (%d ticks)", recipe.time() / 20.0, recipe.time())),
                    Component.literal("Required Tier: MK" + recipe.requiredTier())
            );
        }

        if (mouseX >= CONTROLLER_X && mouseX <= CONTROLLER_X + 16
                && mouseY >= CONTROLLER_Y && mouseY <= CONTROLLER_Y + 28) {
            return List.of(
                    Component.literal("Controller"),
                    Component.literal("Required Machine Tier: MK" + recipe.requiredTier()),
                    Component.literal("Click the controller to open Multiblock Info")
            );
        }

        return List.of();
    }

    private static ItemStack controllerStackFor(final UniversalMultiblockMachineKind machineKind) {
        return switch (machineKind) {
            case QMF -> MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().asItem().getDefaultInstance();
            case QUANTUM_SLICER -> MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get().asItem().getDefaultInstance();
            case QUANTUM_PROCESSOR_ASSEMBLER -> MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get().asItem().getDefaultInstance();
            case QUANTUM_CRYOFORGE -> MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get().asItem().getDefaultInstance();
        };
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
