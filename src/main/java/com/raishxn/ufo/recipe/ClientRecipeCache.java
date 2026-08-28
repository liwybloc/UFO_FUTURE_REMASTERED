package com.raishxn.ufo.recipe;

import com.raishxn.ufo.UfoMod;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.crafting.RecipeHolder;

public final class ClientRecipeCache {
    private static List<DimensionalMatterAssemblerRecipe> dimensionalAssemblyRecipes = List.of();
    private static List<QMFRecipe> qmfRecipes = List.of();
    private static List<UniversalMultiblockRecipe> universalRecipes = List.of();
    private static List<StellarSimulationRecipe> stellarSimulationRecipes = List.of();
    private static List<RecipeHolder<StellarSimulationRecipe>> stellarSimulationRecipeHolders = List.of();
    private static Runnable updateListener = () -> {};

    private ClientRecipeCache() {}

    public static void replace(final List<RecipeHolder<?>> recipes) {
        dimensionalAssemblyRecipes = recipes.stream()
                .map(RecipeHolder::value)
                .filter(DimensionalMatterAssemblerRecipe.class::isInstance)
                .map(DimensionalMatterAssemblerRecipe.class::cast)
                .toList();
        qmfRecipes = recipes.stream()
                .map(RecipeHolder::value)
                .filter(QMFRecipe.class::isInstance)
                .map(QMFRecipe.class::cast)
                .toList();
        universalRecipes = recipes.stream()
                .map(RecipeHolder::value)
                .filter(UniversalMultiblockRecipe.class::isInstance)
                .map(UniversalMultiblockRecipe.class::cast)
                .toList();
        stellarSimulationRecipes = recipes.stream()
                .map(RecipeHolder::value)
                .filter(StellarSimulationRecipe.class::isInstance)
                .map(StellarSimulationRecipe.class::cast)
                .toList();
        final List<RecipeHolder<StellarSimulationRecipe>> stellarHolders = new ArrayList<>();
        for (final RecipeHolder<?> holder : recipes) {
            if (holder.value() instanceof final StellarSimulationRecipe recipe) {
                stellarHolders.add(new RecipeHolder<>(holder.id(), recipe));
            }
        }
        stellarSimulationRecipeHolders = List.copyOf(stellarHolders);
        UfoMod.LOGGER.info(
                "Received {} UFO machine recipes: {} DMA, {} QMF, {} universal, and {} stellar",
                recipes.size(),
                dimensionalAssemblyRecipes.size(),
                qmfRecipes.size(),
                universalRecipes.size(),
                stellarSimulationRecipes.size()
        );
        updateListener.run();
    }

    public static void setUpdateListener(final Runnable listener) {
        updateListener = listener;
    }

    public static List<DimensionalMatterAssemblerRecipe> dimensionalAssemblyRecipes() {
        return dimensionalAssemblyRecipes;
    }

    public static List<QMFRecipe> qmfRecipes() {
        return qmfRecipes;
    }

    public static List<UniversalMultiblockRecipe> universalRecipes() {
        return universalRecipes;
    }

    public static List<StellarSimulationRecipe> stellarSimulationRecipes() {
        return stellarSimulationRecipes;
    }

    public static List<RecipeHolder<StellarSimulationRecipe>> stellarSimulationRecipeHolders() {
        return stellarSimulationRecipeHolders;
    }
}
