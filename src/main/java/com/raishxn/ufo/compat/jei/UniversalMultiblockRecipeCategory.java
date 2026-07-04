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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

    private static final ResourceLocation BACKGROUND = UfoMod.id("textures/guis/dimensional_matter_assembler_jei_ui.png");

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

    public UniversalMultiblockRecipeCategory(IJeiHelpers helpers,
                                             UniversalMultiblockMachineKind machineKind,
                                             ItemStack iconStack,
                                             Component title) {
        this.machineKind = machineKind;
        this.title = title;

        IGuiHelper guiHelper = helpers.getGuiHelper();
        this.background = guiHelper.createDrawable(BACKGROUND, 0, 0, 175, 98);
        this.icon = guiHelper.createDrawableItemStack(iconStack);
        IDrawableStatic progressDrawable = guiHelper.createDrawable(BACKGROUND, 234, 0, 20, 11);
        this.progress = guiHelper.createAnimatedDrawable(progressDrawable, 60, IDrawableAnimated.StartDirection.LEFT, false);
    }

    public static RecipeType<UniversalMultiblockRecipe> recipeTypeFor(UniversalMultiblockMachineKind machineKind) {
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
    public void setRecipe(IRecipeLayoutBuilder builder, UniversalMultiblockRecipe recipe, IFocusGroup focuses) {
        var itemInputs = recipe.getItemInputs();
        for (int i = 0; i < itemInputs.size(); i++) {
            var ingredient = itemInputs.get(i);
            int col = i % 3;
            int row = i / 3;
            builder.addInputSlot(47 + (col * 18), 21 + (row * 18))
                    .addIngredients(UfoJeiPlugin.stackOfUniversal(ingredient))
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Required: " + formatAmount(ingredient.amount()) + "x")));
        }

        var fluidInputs = recipe.getFluidInputs();
        for (int i = 0; i < fluidInputs.size(); i++) {
            var ingredient = fluidInputs.get(i);
            int x = i == 0 ? 28 : 9;
            builder.addInputSlot(x, 21)
                    .setFluidRenderer(16000, false, 12, 54)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, ingredient.fluid().copyWithAmount((int) ingredient.amount()))
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Required: " + formatAmount(ingredient.amount()) + " mB")));
        }

        if (!recipe.getItemOutput().isEmpty()) {
            ItemStack itemOutput = recipe.getDisplayedItemOutput();
            builder.addOutputSlot(ITEM_OUTPUT_X, ITEM_OUTPUT_Y)
                    .addItemStack(itemOutput)
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Output: " + formatAmount(recipe.getItemOutputAmount()) + "x")));
        }

        if (!recipe.getFluidOutput().isEmpty() && recipe.getFluidOutputAmount() > 0) {
            FluidStack fluidOutput = recipe.getFluidOutput().copyWithAmount((int) recipe.getFluidOutputAmount());
            builder.addOutputSlot(148, 76)
                    .setFluidRenderer(16000, false, 14, 17)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, fluidOutput)
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.literal("Output: " + formatAmount(recipe.getFluidOutputAmount()) + " mB")));
        }
    }

    @Override
    public void draw(UniversalMultiblockRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        this.progress.draw(guiGraphics, 105, 42);
        guiGraphics.renderItem(controllerStackFor(this.machineKind), CONTROLLER_X, CONTROLLER_Y);

        guiGraphics.fill(9, 81, 100, 91, 0xFF101010);

        var font = net.minecraft.client.Minecraft.getInstance().font;
        String tierText = "MK" + recipe.getRequiredTier();
        int tierX = CONTROLLER_X + 8 - font.width(tierText) / 2;
        guiGraphics.drawString(font, tierText, tierX, CONTROLLER_Y + 18, 0xFFFFD966, true);

        String energyText = "AE " + formatEnergy(recipe.getEnergy());
        guiGraphics.fillGradient(
                ENERGY_BAR_X,
                ENERGY_BAR_Y,
                ENERGY_BAR_X + ENERGY_BAR_W,
                ENERGY_BAR_Y + ENERGY_BAR_H,
                0x880055FF,
                0xDD0022AA
        );

        int textWidth = font.width(energyText);
        int textX = ENERGY_BAR_X + (ENERGY_BAR_W - textWidth) / 2;
        guiGraphics.drawString(font, energyText, textX, ENERGY_BAR_Y + 1, 0xFFFFFFFF, true);

    }

    @Override
    public List<Component> getTooltipStrings(UniversalMultiblockRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
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

        if (mouseX >= CONTROLLER_X && mouseX <= CONTROLLER_X + 16
                && mouseY >= CONTROLLER_Y && mouseY <= CONTROLLER_Y + 28) {
            return List.of(
                    Component.literal("Controller"),
                    Component.literal("Required Machine Tier: MK" + recipe.getRequiredTier()),
                    Component.literal("Click the controller to open Multiblock Info")
            );
        }

        return List.of();
    }

    private static ItemStack controllerStackFor(UniversalMultiblockMachineKind machineKind) {
        return switch (machineKind) {
            case QMF -> MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().asItem().getDefaultInstance();
            case QUANTUM_SLICER -> MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get().asItem().getDefaultInstance();
            case QUANTUM_PROCESSOR_ASSEMBLER -> MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get().asItem().getDefaultInstance();
            case QUANTUM_CRYOFORGE -> MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get().asItem().getDefaultInstance();
        };
    }

    private static String formatEnergy(long energy) {
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

    private static String formatAmount(long amount) {
        return Long.toString(amount);
    }
}
