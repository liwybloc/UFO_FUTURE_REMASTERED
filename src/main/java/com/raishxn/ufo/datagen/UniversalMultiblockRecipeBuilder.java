package com.raishxn.ufo.datagen;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.recipe.UniversalMultiblockMachineKind;
import com.raishxn.ufo.recipe.UniversalMultiblockRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class UniversalMultiblockRecipeBuilder {
    private final String name;
    private final UniversalMultiblockMachineKind machine;
    private final List<UniversalMultiblockRecipe.ItemRequirement> itemInputs = new ArrayList<>();
    private final List<UniversalMultiblockRecipe.FluidRequirement> fluidInputs = new ArrayList<>();
    private final List<UniversalMultiblockRecipe.ChemicalRequirement> chemicalInputs = new ArrayList<>();
    private ItemStack itemOutput = ItemStack.EMPTY;
    private long itemOutputAmount = 0L;
    private FluidStack fluidOutput = FluidStack.EMPTY;
    private long fluidOutputAmount = 0L;
    private long energy = 0L;
    private int time = 200;
    private int requiredTier = 1;

    private UniversalMultiblockRecipeBuilder(final String name, final UniversalMultiblockMachineKind machine) {
        this.name = name;
        this.machine = machine;
    }

    public static UniversalMultiblockRecipeBuilder create(final String name, final UniversalMultiblockMachineKind machine) {
        return new UniversalMultiblockRecipeBuilder(name, machine);
    }

    public UniversalMultiblockRecipeBuilder inputItem(final ItemLike item, final long amount) {
        this.itemInputs.add(new UniversalMultiblockRecipe.ItemRequirement(Ingredient.of(item), amount));
        return this;
    }

    public UniversalMultiblockRecipeBuilder inputFluid(final Fluid fluid, final long amount) {
        this.fluidInputs.add(new UniversalMultiblockRecipe.FluidRequirement(fluid, amount));
        return this;
    }

    public UniversalMultiblockRecipeBuilder inputChemical(final Identifier chemicalId, final long amount) {
        this.chemicalInputs.add(new UniversalMultiblockRecipe.ChemicalRequirement(chemicalId, amount));
        return this;
    }

    public UniversalMultiblockRecipeBuilder outputItem(final ItemLike item, final int amount) {
        this.itemOutput = new ItemStack(item, 1);
        this.itemOutputAmount = amount;
        return this;
    }

    public UniversalMultiblockRecipeBuilder outputFluid(final Fluid fluid, final long amount) {
        this.fluidOutput = new FluidStack(fluid, 1);
        this.fluidOutputAmount = amount;
        return this;
    }

    public UniversalMultiblockRecipeBuilder energy(final long energy) {
        this.energy = energy;
        return this;
    }

    public UniversalMultiblockRecipeBuilder time(final int time) {
        this.time = time;
        return this;
    }

    public UniversalMultiblockRecipeBuilder requiredTier(final int requiredTier) {
        this.requiredTier = requiredTier;
        return this;
    }

    public void save(final RecipeOutput output) {
        save(output, Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, this.name));
    }

    public void save(final RecipeOutput output, final Identifier id) {
        output.accept(id, new UniversalMultiblockRecipe(
                this.machine,
                this.name,
                this.itemInputs,
                this.fluidInputs,
                this.chemicalInputs,
                this.itemOutput,
                this.itemOutputAmount,
                this.fluidOutput,
                this.fluidOutputAmount,
                this.energy,
                this.time,
                this.requiredTier), null);
    }
}
