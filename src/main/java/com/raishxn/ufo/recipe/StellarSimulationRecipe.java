package com.raishxn.ufo.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.raishxn.ufo.init.ModRecipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

/**
 * A Stellar Simulation recipe — the "programs" that run inside the Stellar Nexus.
 * <p>
 * Each recipe represents a complete stellar simulation cycle,
 * consuming massive amounts of energy, fuel (liquid), coolant, and item/fluid catalysts
 * to produce millions of items injected directly into the ME network.
 * <p>
 * <b>Terminology:</b>
 * <ul>
 *   <li><b>Energy</b> = AE power (e.g., 500M AE) — charged passively from AE network</li>
 *   <li><b>Fuel</b> = liquid combustible (e.g., Hydrogen) — extracted from ME storage on start</li>
 *   <li><b>Coolant</b> = liquid refrigerant with tiers — consumed during operation</li>
 * </ul>
 * <p>
 * Coolant Tiers:
 * <ul>
 *   <li>1 = Gelid Cryotheum (ufo:source_gelid_cryotheum)</li>
 *   <li>2 = Stable Coolant (ufo:source_stable_coolant)</li>
 *   <li>3 = Temporal Fluid (ufo:source_temporal_fluid)</li>
 * </ul>
 */
public class StellarSimulationRecipe implements Recipe<RecipeInput> {

    protected final List<IngredientStack.Item> itemInputs;
    protected final List<IngredientStack.Fluid> fluidInputs;
    protected final List<DimensionalMatterAssemblerRecipe.ItemOutput> itemOutputs;
    protected final List<DimensionalMatterAssemblerRecipe.FluidOutput> fluidOutputs;

    protected final String simulationName;      // Display name for the simulation
    protected final long energy;                 // Total AE power cost
    protected final int time;                    // Total ticks for the simulation
    protected final int coolingLevel;            // 0 = none, 1 = basic, 2 = advanced, 3 = extreme
    protected final int fieldTier;               // 1 = Mk.I, 2 = Mk.II, 3 = Mk.III
    protected final String fuelFluid;            // Identifier string of the fuel fluid
    protected final long fuelAmount;             // Amount of fuel fluid required (mB)
    protected final long coolantAmount;          // Amount of coolant required (mB)

    public StellarSimulationRecipe(
            final List<IngredientStack.Item> itemInputs,
            final List<IngredientStack.Fluid> fluidInputs,
            final List<DimensionalMatterAssemblerRecipe.ItemOutput> itemOutputs,
            final List<DimensionalMatterAssemblerRecipe.FluidOutput> fluidOutputs,
            final String simulationName,
            final long energy,
            final int time,
            final int coolingLevel,
            final int fieldTier,
            final String fuelFluid,
            final long fuelAmount,
            final long coolantAmount) {
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
        this.simulationName = simulationName != null && !simulationName.isEmpty() ? simulationName : "";
        this.energy = energy;
        this.time = time > 0 ? time : 24000;
        this.coolingLevel = Math.clamp(coolingLevel, 0, 3);
        this.fieldTier = Math.clamp(fieldTier, 1, 3);
        this.fuelFluid = fuelFluid != null ? fuelFluid : "";
        this.fuelAmount = fuelAmount;
        this.coolantAmount = coolantAmount;
    }

    public StellarSimulationRecipe(
            final List<IngredientStack.Item> itemInputs,
            final List<IngredientStack.Fluid> fluidInputs,
            final List<DimensionalMatterAssemblerRecipe.ItemOutput> itemOutputs,
            final List<DimensionalMatterAssemblerRecipe.FluidOutput> fluidOutputs,
            final int energy,
            final int time,
            final int coolingLevel,
            final int fieldTier) {
        this(itemInputs, fluidInputs, itemOutputs, fluidOutputs,
                "", energy, time, coolingLevel, fieldTier, "", 0, 0);
    }


    @Override
    public boolean matches(@NotNull final RecipeInput recipeInput, @NotNull final Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull final RecipeInput inv) {
        return getResultItem().copy();
    }

    @Override
    public boolean showNotification() { return false; }

    @Override
    public String group() { return "ufo:stellar_simulation"; }

    @Override
    public PlacementInfo placementInfo() { return PlacementInfo.NOT_PLACEABLE; }

    @Override
    public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }

    public ItemStack getResultItem() {
        if (!this.itemOutputs.isEmpty()) {
            final var output = this.itemOutputs.getFirst();
            return new ItemStack(output.item(), (int) Math.min(output.amount(), Integer.MAX_VALUE));
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return StellarSimulationRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
        return ModRecipes.STELLAR_SIMULATION_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }


    public List<IngredientStack.Item> getItemInputs() {
        return itemInputs;
    }

    public List<IngredientStack.Fluid> getFluidInputs() {
        return fluidInputs;
    }

    public List<GenericStack> getItemOutputs() {
        return itemOutputs.stream()
                .map(output -> new GenericStack(AEItemKey.of(output.item()), output.amount()))
                .toList();
    }

    public List<GenericStack> getFluidOutputs() {
        return fluidOutputs.stream()
                .map(output -> new GenericStack(appeng.api.stacks.AEFluidKey.of(output.fluid()), output.amount()))
                .toList();
    }

    public List<DimensionalMatterAssemblerRecipe.ItemOutput> getItemOutputDefinitions() { return itemOutputs; }

    public List<DimensionalMatterAssemblerRecipe.FluidOutput> getFluidOutputDefinitions() { return fluidOutputs; }

    /**
     * All valid inputs combined (items + fluids) for recipe matching in the Controller.
     */
    public List<IngredientStack<?, ?>> getValidInputs() {
        final List<IngredientStack<?, ?>> validInputs = new ArrayList<>();

        for (final var input : this.itemInputs) {
            if (!input.isEmpty()) {
                validInputs.add(input.sample());
            }
        }

        for (final var input : this.fluidInputs) {
            if (!input.isEmpty()) {
                validInputs.add(input.sample());
            }
        }

        return validInputs;
    }

    /** Display name for this simulation program. */
    public String getSimulationName() {
        return simulationName;
    }

    /** Total AE power cost for the entire simulation. */
    public long getEnergyCost() {
        return energy;
    }

    /** @deprecated Use {@link #getEnergyCost()} instead. Kept for backward compat. */
    @Deprecated
    public int getFuelCost() {
        return (int) Math.min(energy, Integer.MAX_VALUE);
    }

    /** Total ticks for the simulation cycle. */
    public int getTime() {
        return time;
    }

    /** Minimum cooling score required (0-3). */
    public int getCoolingLevel() {
        return coolingLevel;
    }

    /** Minimum Stellar Field Generator tier required (1-3). */
    public int getFieldTier() {
        return fieldTier;
    }

    /** Identifier string of the required fuel fluid. Empty if none. */
    public String getFuelFluid() {
        return fuelFluid;
    }

    /** Amount of fuel fluid required in mB. */
    public long getFuelAmount() {
        return fuelAmount;
    }



    /** Amount of coolant fluid required in mB. */
    public long getCoolantAmount() {
        return coolantAmount;
    }

    /**
     * Total energy cost (same as getEnergyCost for display).
     */
    public long getTotalEnergy() {
        return energy;
    }

    /**
     * Formatted time string for display (e.g., "20m 00s").
     */
    public String getFormattedTime() {
        final int totalSeconds = time / 20;
        final int minutes = totalSeconds / 60;
        final int seconds = totalSeconds % 60;
        return String.format("%dm %02ds", minutes, seconds);
    }

    /**
     * Resolves the fuel fluid Identifier, or empty if not set.
     */
    public Optional<Identifier> getFuelFluidRL() {
        if (fuelFluid.isEmpty()) return Optional.empty();
        return Optional.of(Identifier.parse(fuelFluid));
    }


}
