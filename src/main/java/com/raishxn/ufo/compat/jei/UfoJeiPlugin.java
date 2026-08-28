package com.raishxn.ufo.compat.jei;

import java.util.List;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.recipe.QMFRecipe;
import com.raishxn.ufo.recipe.ClientRecipeCache;
import com.raishxn.ufo.recipe.UniversalMultiblockMachineKind;
import com.raishxn.ufo.recipe.UniversalMultiblockRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import net.pedroksl.ae2addonlib.recipes.IngredientStack;
import org.jspecify.annotations.NonNull;

@JeiPlugin
public final class UfoJeiPlugin implements IModPlugin {

    private static final Identifier ID = UfoMod.id("jei_plugin");

    private static IJeiRuntime runtime;

    @Override
    public @NonNull Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(final @NonNull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        ClientRecipeCache.setUpdateListener(UfoJeiPlugin::addCachedRecipes);
        addCachedRecipes();
    }

    @Override
    public void onRuntimeUnavailable() {
        ClientRecipeCache.setUpdateListener(() -> {});
        runtime = null;
    }

    private static void addCachedRecipes() {
        if (runtime == null) {
            return;
        }

        final var recipeManager = runtime.getRecipeManager();
        recipeManager.addRecipes(
                DimensionalMatterAssemblerRecipeCategory.RECIPE_TYPE,
                ClientRecipeCache.dimensionalAssemblyRecipes()
        );
        recipeManager.addRecipes(
                QmfRecipeCategory.RECIPE_TYPE,
                ClientRecipeCache.qmfRecipes()
        );

        final List<UniversalMultiblockRecipe> universalRecipes = ClientRecipeCache.universalRecipes();
        recipeManager.addRecipes(
                UniversalMultiblockRecipeCategory.QMF_RECIPE_TYPE,
                universalRecipes.stream()
                        .filter(recipe -> recipe.machine() == UniversalMultiblockMachineKind.QMF)
                        .toList()
        );
        recipeManager.addRecipes(
                UniversalMultiblockRecipeCategory.QUANTUM_SLICER_RECIPE_TYPE,
                universalRecipes.stream()
                        .filter(recipe -> recipe.machine() == UniversalMultiblockMachineKind.QUANTUM_SLICER)
                        .toList()
        );
        recipeManager.addRecipes(
                UniversalMultiblockRecipeCategory.QUANTUM_PROCESSOR_ASSEMBLER_RECIPE_TYPE,
                universalRecipes.stream()
                        .filter(recipe -> recipe.machine() == UniversalMultiblockMachineKind.QUANTUM_PROCESSOR_ASSEMBLER)
                        .toList()
        );
        recipeManager.addRecipes(
                UniversalMultiblockRecipeCategory.QUANTUM_CRYOFORGE_RECIPE_TYPE,
                universalRecipes.stream()
                        .filter(recipe -> recipe.machine() == UniversalMultiblockMachineKind.QUANTUM_CRYOFORGE)
                        .toList()
        );
        recipeManager.addRecipes(
                StellarSimulationRecipeCategory.RECIPE_TYPE,
                ClientRecipeCache.stellarSimulationRecipes()
        );
    }

    @Override
    public void registerCategories(final IRecipeCategoryRegistration registry) {
        final var jeiHelpers = registry.getJeiHelpers();

        registry.addRecipeCategories(
                new DimensionalMatterAssemblerRecipeCategory(jeiHelpers)
        );

        registry.addRecipeCategories(
                new QmfRecipeCategory(jeiHelpers)
        );

        registry.addRecipeCategories(
                new UniversalMultiblockRecipeCategory(
                        jeiHelpers,
                        UniversalMultiblockMachineKind.QMF,
                        MultiblockBlocks
                                .QUANTUM_MATTER_FABRICATOR_CONTROLLER
                                .get()
                                .asItem()
                                .getDefaultInstance(),
                        MultiblockBlocks
                                .QUANTUM_MATTER_FABRICATOR_CONTROLLER
                                .get()
                                .getName()
                )
        );

        registry.addRecipeCategories(
                new UniversalMultiblockRecipeCategory(
                        jeiHelpers,
                        UniversalMultiblockMachineKind.QUANTUM_SLICER,
                        MultiblockBlocks
                                .QUANTUM_SLICER_CONTROLLER
                                .get()
                                .asItem()
                                .getDefaultInstance(),
                        MultiblockBlocks
                                .QUANTUM_SLICER_CONTROLLER
                                .get()
                                .getName()
                )
        );

        registry.addRecipeCategories(
                new UniversalMultiblockRecipeCategory(
                        jeiHelpers,
                        UniversalMultiblockMachineKind.QUANTUM_PROCESSOR_ASSEMBLER,
                        MultiblockBlocks
                                .QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER
                                .get()
                                .asItem()
                                .getDefaultInstance(),
                        MultiblockBlocks
                                .QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER
                                .get()
                                .getName()
                )
        );

        registry.addRecipeCategories(
                new UniversalMultiblockRecipeCategory(
                        jeiHelpers,
                        UniversalMultiblockMachineKind.QUANTUM_CRYOFORGE,
                        MultiblockBlocks
                                .QUANTUM_CRYOFORGE_CONTROLLER
                                .get()
                                .asItem()
                                .getDefaultInstance(),
                        MultiblockBlocks
                                .QUANTUM_CRYOFORGE_CONTROLLER
                                .get()
                                .getName()
                )
        );

        registry.addRecipeCategories(
                new StellarSimulationRecipeCategory(jeiHelpers)
        );
    }

    @Override
    public void registerRecipes(final IRecipeRegistration registration) {

        registration.addRecipes(
                DimensionalMatterAssemblerRecipeCategory.RECIPE_TYPE,
                List.of()
        );

        registration.addRecipes(
                QmfRecipeCategory.RECIPE_TYPE,
                List.of()
        );

        final List<UniversalMultiblockRecipe> universalRecipes = List.of();

        registration.addRecipes(
                UniversalMultiblockRecipeCategory.QMF_RECIPE_TYPE,
                universalRecipes.stream()
                        .filter(recipe ->
                                recipe.machine() == UniversalMultiblockMachineKind.QMF)
                        .toList()
        );

        registration.addRecipes(
                UniversalMultiblockRecipeCategory.QUANTUM_SLICER_RECIPE_TYPE,
                universalRecipes.stream()
                        .filter(recipe ->
                                recipe.machine() == UniversalMultiblockMachineKind.QUANTUM_SLICER)
                        .toList()
        );

        registration.addRecipes(
                UniversalMultiblockRecipeCategory.QUANTUM_PROCESSOR_ASSEMBLER_RECIPE_TYPE,
                universalRecipes.stream()
                        .filter(recipe ->
                                recipe.machine() == UniversalMultiblockMachineKind.QUANTUM_PROCESSOR_ASSEMBLER)
                        .toList()
        );

        registration.addRecipes(
                UniversalMultiblockRecipeCategory.QUANTUM_CRYOFORGE_RECIPE_TYPE,
                universalRecipes.stream()
                        .filter(recipe ->
                                recipe.machine() == UniversalMultiblockMachineKind.QUANTUM_CRYOFORGE)
                        .toList()
        );

        registration.addRecipes(
                StellarSimulationRecipeCategory.RECIPE_TYPE,
                List.of()
        );
    }

    @Override
    public void registerRecipeCatalysts(
            final IRecipeCatalystRegistration registration
    ) {
        final var dmaBlock = ModBlocks
                .DIMENSIONAL_MATTER_ASSEMBLER_BLOCK
                .get()
                .asItem()
                .getDefaultInstance();

        registration.addCraftingStation(
                DimensionalMatterAssemblerRecipeCategory.RECIPE_TYPE,
                dmaBlock
        );

        final var nexusController = MultiblockBlocks
                .STELLAR_NEXUS_CONTROLLER
                .get()
                .asItem()
                .getDefaultInstance();

        registration.addCraftingStation(
                StellarSimulationRecipeCategory.RECIPE_TYPE,
                nexusController
        );

        final var qmfController = MultiblockBlocks
                .QUANTUM_MATTER_FABRICATOR_CONTROLLER
                .get()
                .asItem()
                .getDefaultInstance();

        registration.addCraftingStation(
                QmfRecipeCategory.RECIPE_TYPE,
                qmfController
        );

        registration.addCraftingStation(
                DimensionalMatterAssemblerRecipeCategory.RECIPE_TYPE,
                qmfController
        );

        registration.addCraftingStation(
                UniversalMultiblockRecipeCategory.QMF_RECIPE_TYPE,
                qmfController
        );

        final var quantumSlicerController = MultiblockBlocks
                .QUANTUM_SLICER_CONTROLLER
                .get()
                .asItem()
                .getDefaultInstance();

        registration.addCraftingStation(
                UniversalMultiblockRecipeCategory.QUANTUM_SLICER_RECIPE_TYPE,
                quantumSlicerController
        );

        final var quantumProcessorAssemblerController = MultiblockBlocks
                .QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER
                .get()
                .asItem()
                .getDefaultInstance();

        registration.addCraftingStation(
                UniversalMultiblockRecipeCategory
                        .QUANTUM_PROCESSOR_ASSEMBLER_RECIPE_TYPE,
                quantumProcessorAssemblerController
        );

        final var quantumCryoforgeController = MultiblockBlocks
                .QUANTUM_CRYOFORGE_CONTROLLER
                .get()
                .asItem()
                .getDefaultInstance();

        registration.addCraftingStation(
                UniversalMultiblockRecipeCategory
                        .QUANTUM_CRYOFORGE_RECIPE_TYPE,
                quantumCryoforgeController
        );

    }

    public static ItemStack getHoveredItemStack() {
        if (runtime == null) {
            return ItemStack.EMPTY;
        }

        final var recipeStack = runtime
                .getRecipesGui()
                .getIngredientUnderMouse(VanillaTypes.ITEM_STACK);

        if (recipeStack.isPresent()) {
            return recipeStack.get().copy();
        }

        final ItemStack overlayStack = runtime
                .getIngredientListOverlay()
                .getIngredientUnderMouse(VanillaTypes.ITEM_STACK);

        if (overlayStack != null) {
            return overlayStack.copy();
        }

        return ItemStack.EMPTY;
    }

    public static List<ItemStack> stackOf(final IngredientStack.Item stack) {
        if (stack == null || stack.isEmpty()) {
            return List.of();
        }

        return displayStacks(stack.getIngredient(), stack.getAmount());
    }

    public static List<FluidStack> stackOf(final IngredientStack.Fluid stack) {
        if (stack == null || stack.isEmpty()) {
            return List.of();
        }

        final FluidIngredient ingredient = stack.getIngredient();
        final int amount = stack.getAmount();

        return ingredient.fluids()
                .stream()
                .map(fluid -> new FluidStack(fluid, amount))
                .toList();
    }

    public static List<ItemStack> stackOfQmf(
            final QMFRecipe.QMFRecipeIngredient stack
    ) {
        if (stack == null
                || stack.ingredient().isEmpty()
                || stack.amount() <= 0) {
            return List.of();
        }

        return displayStacks(stack.ingredient(), stack.amount());
    }

    public static List<ItemStack> stackOfUniversal(
            final UniversalMultiblockRecipe.ItemRequirement stack
    ) {
        if (stack == null
                || stack.ingredient().isEmpty()
                || stack.amount() <= 0) {
            return List.of();
        }

        return displayStacks(stack.ingredient(), stack.amount());
    }

    private static List<ItemStack> displayStacks(final Ingredient ingredient, final long amount) {
        final int displayedAmount = (int) Math.min(Integer.MAX_VALUE, Math.max(1L, amount));
        return ingredient.items()
                .map(item -> new ItemStack(item.value(), displayedAmount))
                .toList();
    }

}
