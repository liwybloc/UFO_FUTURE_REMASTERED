package com.raishxn.ufo.compat.jei;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.recipe.QMFRecipe;
import com.raishxn.ufo.recipe.UniversalMultiblockMachineKind;
import com.raishxn.ufo.recipe.UniversalMultiblockRecipe;

import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

@JeiPlugin
public class UfoJeiPlugin implements IModPlugin {
    private static final Identifier ID = UfoMod.id("jei_plugin");
    private static IJeiRuntime runtime;

    public UfoJeiPlugin() {}

    @Override
    public Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(final IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    @Override
    public void registerCategories(final IRecipeCategoryRegistration registry) {
        final var jeiHelpers = registry.getJeiHelpers();
        registry.addRecipeCategories(new DimensionalMatterAssemblerRecipeCategory(jeiHelpers));
        registry.addRecipeCategories(new QmfRecipeCategory(jeiHelpers));
        registry.addRecipeCategories(new MultiblockInfoCategory(jeiHelpers));
        registry.addRecipeCategories(new UniversalMultiblockRecipeCategory(
                jeiHelpers,
                UniversalMultiblockMachineKind.QMF,
                MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().asItem().getDefaultInstance(),
                MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().getName()));
        registry.addRecipeCategories(new UniversalMultiblockRecipeCategory(
                jeiHelpers,
                UniversalMultiblockMachineKind.QUANTUM_SLICER,
                MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get().asItem().getDefaultInstance(),
                MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get().getName()));
        registry.addRecipeCategories(new UniversalMultiblockRecipeCategory(
                jeiHelpers,
                UniversalMultiblockMachineKind.QUANTUM_PROCESSOR_ASSEMBLER,
                MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get().asItem().getDefaultInstance(),
                MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get().getName()));
        registry.addRecipeCategories(new UniversalMultiblockRecipeCategory(
                jeiHelpers,
                UniversalMultiblockMachineKind.QUANTUM_CRYOFORGE,
                MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get().asItem().getDefaultInstance(),
                MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get().getName()));
        registry.addRecipeCategories(new StellarSimulationRecipeCategory(jeiHelpers));
    }

    @Override
    public void registerRecipes(final IRecipeRegistration registration) {
        registration.addRecipes(DimensionalMatterAssemblerRecipeCategory.RECIPE_TYPE, List.of());
        registration.addRecipes(QmfRecipeCategory.RECIPE_TYPE, List.of());
        MultiblockInfoCategory.registerRecipes(registration);
        final List<com.raishxn.ufo.recipe.UniversalMultiblockRecipe> universalRecipes = List.of();
        registration.addRecipes(
                UniversalMultiblockRecipeCategory.QMF_RECIPE_TYPE,
                universalRecipes.stream().filter(recipe -> recipe.machine() == UniversalMultiblockMachineKind.QMF).toList());
        registration.addRecipes(
                UniversalMultiblockRecipeCategory.QUANTUM_SLICER_RECIPE_TYPE,
                universalRecipes.stream().filter(recipe -> recipe.machine() == UniversalMultiblockMachineKind.QUANTUM_SLICER).toList());
        registration.addRecipes(
                UniversalMultiblockRecipeCategory.QUANTUM_PROCESSOR_ASSEMBLER_RECIPE_TYPE,
                universalRecipes.stream().filter(recipe -> recipe.machine() == UniversalMultiblockMachineKind.QUANTUM_PROCESSOR_ASSEMBLER).toList());
        registration.addRecipes(
                UniversalMultiblockRecipeCategory.QUANTUM_CRYOFORGE_RECIPE_TYPE,
                universalRecipes.stream().filter(recipe -> recipe.machine() == UniversalMultiblockMachineKind.QUANTUM_CRYOFORGE).toList());
        registration.addRecipes(StellarSimulationRecipeCategory.RECIPE_TYPE, List.of());
    }

    @Override
    public void registerRecipeCatalysts(final IRecipeCatalystRegistration registration) {
        final var dmaBlock = ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get().asItem().getDefaultInstance();
        registration.addRecipeCatalyst(dmaBlock, DimensionalMatterAssemblerRecipeCategory.RECIPE_TYPE);

        final var nexusController = MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get().asItem().getDefaultInstance();
        registration.addRecipeCatalyst(nexusController, StellarSimulationRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(nexusController, MultiblockInfoCategory.RECIPE_TYPE);

        final var qmfController = MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().asItem().getDefaultInstance();
        registration.addRecipeCatalyst(qmfController, QmfRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(qmfController, DimensionalMatterAssemblerRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(qmfController, UniversalMultiblockRecipeCategory.QMF_RECIPE_TYPE);
        registration.addRecipeCatalyst(qmfController, MultiblockInfoCategory.RECIPE_TYPE);

        final var quantumSlicerController = MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get().asItem().getDefaultInstance();
        registration.addRecipeCatalyst(quantumSlicerController, UniversalMultiblockRecipeCategory.QUANTUM_SLICER_RECIPE_TYPE);
        registration.addRecipeCatalyst(quantumSlicerController, MultiblockInfoCategory.RECIPE_TYPE);

        final var quantumProcessorAssemblerController = MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get().asItem().getDefaultInstance();
        registration.addRecipeCatalyst(quantumProcessorAssemblerController, UniversalMultiblockRecipeCategory.QUANTUM_PROCESSOR_ASSEMBLER_RECIPE_TYPE);
        registration.addRecipeCatalyst(quantumProcessorAssemblerController, MultiblockInfoCategory.RECIPE_TYPE);

        final var quantumCryoforgeController = MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get().asItem().getDefaultInstance();
        registration.addRecipeCatalyst(quantumCryoforgeController, UniversalMultiblockRecipeCategory.QUANTUM_CRYOFORGE_RECIPE_TYPE);
        registration.addRecipeCatalyst(quantumCryoforgeController, MultiblockInfoCategory.RECIPE_TYPE);
    }

    public static ItemStack getHoveredItemStack() {
        if (runtime == null) {
            return ItemStack.EMPTY;
        }
        return runtime.getRecipesGui().getIngredientUnderMouse(VanillaTypes.ITEM_STACK)
                .or(() -> runtime.getIngredientListOverlay().getIngredientUnderMouse().flatMap(ingredient -> ingredient.getItemStack()))
                .map(ItemStack::copy)
                .orElse(ItemStack.EMPTY);
    }

    public static Ingredient stackOf(final IngredientStack.Item stack) {
        if (!stack.isEmpty()) {
            return Ingredient.of(Arrays.stream(stack.getIngredient().getItems())
                    .map(oldStack -> oldStack.copyWithCount(stack.getAmount())));
        }
        return Ingredient.of(ItemStack.EMPTY);
    }

    public static List<FluidStack> stackOf(final IngredientStack.Fluid stack) {
        final FluidIngredient ingredient = stack.getIngredient();
        return Arrays.stream(ingredient.getStacks())
                .map(oldStack -> oldStack.copyWithAmount(stack.getAmount()))
                .toList();
    }

    public static Ingredient stackOfQmf(final QMFRecipe.QMFRecipeIngredient stack) {
        if (stack != null && !stack.ingredient().isEmpty() && stack.amount() > 0) {
            return Ingredient.of(Arrays.stream(stack.ingredient().getItems())
                    .map(oldStack -> oldStack.copyWithCount((int) Math.min(Integer.MAX_VALUE, stack.amount()))));
        }
        return Ingredient.of(ItemStack.EMPTY);
    }

    public static Ingredient stackOfUniversal(final UniversalMultiblockRecipe.ItemRequirement stack) {
        if (stack != null && !stack.ingredient().isEmpty() && stack.amount() > 0) {
            return Ingredient.of(Arrays.stream(stack.ingredient().getItems())
                    .map(oldStack -> oldStack.copyWithCount((int) Math.min(Integer.MAX_VALUE, stack.amount()))));
        }
        return Ingredient.of(ItemStack.EMPTY);
    }
}
