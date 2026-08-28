package com.raishxn.ufo.datagen;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.recipe.StellarSimulationRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import java.util.ArrayList;
import java.util.List;

public final class StellarSimulationRecipeBuilder {
    private final String name;
    private String simulationName = "";
    private final List<IngredientStack.Item> itemInputs = new ArrayList<>();
    private final List<IngredientStack.Fluid> fluidInputs = new ArrayList<>();
    private final List<com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe.ItemOutput> itemOutputs = new ArrayList<>();
    private final List<com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe.FluidOutput> fluidOutputs = new ArrayList<>();
    private long energy = 0;
    private int time = 0;
    private int coolingLevel = 3;
    private int fieldTier = 3;
    private String fuelFluid = "";
    private long fuelAmount = 0;
    private long coolantAmount = 0;

    private StellarSimulationRecipeBuilder(final String name) {
        this.name = name;
    }

    public static StellarSimulationRecipeBuilder create(final String name) {
        return new StellarSimulationRecipeBuilder(name);
    }

    public StellarSimulationRecipeBuilder simulationName(final String simName) {
        this.simulationName = simName;
        return this;
    }

    public StellarSimulationRecipeBuilder inputItem(final ItemLike item, final int count) {
        this.itemInputs.add(new IngredientStack.Item(Ingredient.of(item), count));
        return this;
    }

    public StellarSimulationRecipeBuilder inputFluid(final Fluid fluid, final int amount) {
        this.fluidInputs.add(new IngredientStack.Fluid(FluidIngredient.of(fluid), amount));
        return this;
    }

    public StellarSimulationRecipeBuilder output(final ItemLike item, final long amount) {
        this.itemOutputs.add(new com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe.ItemOutput(item.asItem(), amount));
        return this;
    }

    public StellarSimulationRecipeBuilder outputFluid(final Fluid fluid, final long amount) {
        this.fluidOutputs.add(new com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe.FluidOutput(fluid, amount));
        return this;
    }

    public StellarSimulationRecipeBuilder fuel(final String fluidRegistryName, final long amount) {
        this.fuelFluid = fluidRegistryName;
        this.fuelAmount = amount;
        return this;
    }

    public StellarSimulationRecipeBuilder fuel(final Fluid fluid, final long amount) {
        this.fuelFluid = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(fluid).toString();
        this.fuelAmount = amount;
        return this;
    }

    public StellarSimulationRecipeBuilder coolant(final long amount) {
        this.coolantAmount = amount;
        return this;
    }

    public StellarSimulationRecipeBuilder coolant(final Fluid fluidPlaceholder, final long amount) {
        this.coolantAmount = amount;
        return this;
    }

    public StellarSimulationRecipeBuilder fieldLevel(final int level) {
        this.fieldTier = level;
        return this;
    }

    public StellarSimulationRecipeBuilder coolingLevel(final int level) {
        this.coolingLevel = level;
        return this;
    }

    public StellarSimulationRecipeBuilder energy(final long energy) {
        this.energy = energy;
        return this;
    }

    public StellarSimulationRecipeBuilder time(final int time) {
        this.time = time;
        return this;
    }

    public void save(final RecipeOutput output) {
        final var id = Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, this.name);
        final var recipe = new StellarSimulationRecipe(
                this.itemInputs,
                this.fluidInputs,
                this.itemOutputs,
                this.fluidOutputs,
                this.simulationName.isEmpty() ? id.getPath() : this.simulationName,
                this.energy,
                this.time,
                this.coolingLevel,
                this.fieldTier,
                this.fuelFluid,
                this.fuelAmount,
                this.coolantAmount
        );
        output.accept(id, recipe, null);
    }
}
