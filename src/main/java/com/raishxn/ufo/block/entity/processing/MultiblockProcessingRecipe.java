package com.raishxn.ufo.block.entity.processing;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe;
import com.raishxn.ufo.recipe.QMFRecipe;
import com.raishxn.ufo.recipe.UniversalMultiblockRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public record MultiblockProcessingRecipe(
        Identifier id,
        String name,
        List<ItemRequirement> itemInputs,
        List<FluidRequirement> fluidInputs,
        List<ChemicalRequirement> chemicalInputs,
        List<OutputStack> outputs,
        long energy,
        int time,
        int requiredTier) {

    public record ItemRequirement(Ingredient ingredient, long amount) {
    }

    public record FluidRequirement(FluidStack fluid, long amount) {
    }

    public record ChemicalRequirement(Identifier chemicalId, long amount) {
    }

    public record OutputStack(ItemStack item, FluidStack fluid, long amount) {
        public OutputStack {
            item = item == null ? ItemStack.EMPTY : item;
            fluid = fluid == null ? FluidStack.EMPTY : fluid;
            amount = Math.max(0L, amount);
        }
    }

    public OutputStack primaryOutput() {
        if (outputs.isEmpty()) {
            return new OutputStack(ItemStack.EMPTY, FluidStack.EMPTY, 0L);
        }
        return outputs.getFirst();
    }

    public static MultiblockProcessingRecipe fromQmf(final Identifier id, final QMFRecipe recipe) {
        final List<ItemRequirement> itemInputs = recipe.getItemInputs().stream()
                .map(input -> new ItemRequirement(input.ingredient(), input.amount()))
                .toList();
        final List<FluidRequirement> fluidInputs = recipe.getFluidInputs().stream()
                .map(input -> new FluidRequirement(input.fluid(), input.amount()))
                .toList();
        final List<ChemicalRequirement> chemicalInputs = recipe.getChemicalInputs().stream()
                .map(input -> new ChemicalRequirement(input.chemicalId(), input.amount()))
                .toList();
        final List<OutputStack> outputs = List.of(new OutputStack(normalizeItem(recipe.getResultItem()), FluidStack.EMPTY, recipe.getResultItem().getCount()));
        return new MultiblockProcessingRecipe(id, recipe.getRecipeName(), itemInputs, fluidInputs, chemicalInputs, outputs, recipe.getEnergy(), recipe.getTime(), recipe.getRequiredTier());
    }

    public static MultiblockProcessingRecipe fromUniversal(final Identifier id, final UniversalMultiblockRecipe recipe) {
        final List<ItemRequirement> itemInputs = recipe.itemInputs().stream()
                .map(input -> new ItemRequirement(input.ingredient(), input.amount()))
                .toList();
        final List<FluidRequirement> fluidInputs = recipe.fluidInputs().stream()
                .map(input -> new FluidRequirement(new FluidStack(input.fluid(), 1), input.amount()))
                .toList();
        final List<ChemicalRequirement> chemicalInputs = recipe.chemicalInputs().stream()
                .map(input -> new ChemicalRequirement(input.chemicalId(), input.amount()))
                .toList();

        final List<OutputStack> outputs = new ArrayList<>();
        if (!recipe.itemOutput().isEmpty()) {
            outputs.add(new OutputStack(normalizeItem(recipe.itemOutput()), FluidStack.EMPTY, recipe.itemOutputAmount()));
        }
        if (!recipe.fluidOutput().isEmpty() && recipe.fluidOutputAmount() > 0) {
            outputs.add(new OutputStack(ItemStack.EMPTY, recipe.fluidOutput(), recipe.fluidOutputAmount()));
        }

        return new MultiblockProcessingRecipe(id, recipe.recipeName(), itemInputs, fluidInputs, chemicalInputs, outputs, recipe.energy(), recipe.time(), recipe.requiredTier());
    }

    public static MultiblockProcessingRecipe fromDma(final Identifier id, final DimensionalMatterAssemblerRecipe recipe) {
        final List<ItemRequirement> itemInputs = recipe.itemInputs().stream()
                .filter(input -> input != null && !input.isEmpty())
                .map(input -> new ItemRequirement(input.getIngredient(), input.getAmount()))
                .toList();
        final List<FluidRequirement> fluidInputs = recipe.fluidInputs().stream()
                .filter(input -> input != null && !input.isEmpty())
                .map(input -> new FluidRequirement(new FluidStack(input.getIngredient().fluids().getFirst().value(), 1), input.getAmount()))
                .toList();
        final List<OutputStack> outputs = new ArrayList<>();
        for (final GenericStack output : recipe.itemOutputs()) {
            if (output.what() instanceof final AEItemKey itemKey) {
                outputs.add(new OutputStack(itemKey.toStack(1), FluidStack.EMPTY, output.amount()));
            }
        }
        for (final GenericStack output : recipe.fluidOutputs()) {
            if (output.what() instanceof final AEFluidKey fluidKey) {
                outputs.add(new OutputStack(ItemStack.EMPTY, new FluidStack(fluidKey.getFluid(), 1), output.amount()));
            }
        }
        return new MultiblockProcessingRecipe(id, id.getPath(), itemInputs, fluidInputs, List.of(), outputs, recipe.energy(), recipe.time(), 1);
    }

    private static ItemStack normalizeItem(final ItemStack stack) {
        final ItemStack copy = stack.copy();
        if (!copy.isEmpty()) {
            copy.setCount(1);
        }
        return copy;
    }
}
