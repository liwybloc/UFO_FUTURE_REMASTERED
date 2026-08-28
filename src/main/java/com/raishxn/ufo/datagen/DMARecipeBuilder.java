package com.raishxn.ufo.datagen;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.util.ModTags;
import com.raishxn.ufo.recipe.UniversalMultiblockMachineKind;
import com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class DMARecipeBuilder {
    private static final int BULK_QMF_FACTOR = 64;
    private final String name;
    private final List<IngredientStack.Item> itemInputs = new ArrayList<>();
    private final List<IngredientStack.Fluid> fluidInputs = new ArrayList<>();
    private final List<DimensionalMatterAssemblerRecipe.ItemOutput> itemOutputs = new ArrayList<>();
    private final List<DimensionalMatterAssemblerRecipe.FluidOutput> fluidOutputs = new ArrayList<>();
    private int energy = 0;
    private int time = 0; // The recipe class doesn't seem to use time, but we keep it for API compatibility.
    private boolean bulkQmfMirrorEnabled = true;

    private DMARecipeBuilder(final String name) {
        this.name = name;
    }

    public static DMARecipeBuilder create(final String name) {
        return new DMARecipeBuilder(name);
    }

    public DMARecipeBuilder inputItem(final ItemLike item) {
        return inputItem(item, 1);
    }

    public DMARecipeBuilder inputItem(final ItemLike item, final int count) {
        this.itemInputs.add(new IngredientStack.Item(Ingredient.of(item), count));
        return this;
    }
    
    public DMARecipeBuilder inputFluid(final Fluid fluid, final int amount) {
        this.fluidInputs.add(new IngredientStack.Fluid(FluidIngredient.of(fluid), amount));
        return this;
    }

    /**
     * @deprecated Coolant is now player-managed and not part of recipes.
     * Kept for KubeJS backward compatibility — this is a NO-OP.
     */
    @Deprecated
    public DMARecipeBuilder inputCoolant(final Fluid fluid, final int amount) {
        return this;
    }

    public DMARecipeBuilder output(final ItemLike item) {
        return output(item, 1);
    }

    public DMARecipeBuilder output(final ItemLike item, final int amount) {
        this.itemOutputs.add(new DimensionalMatterAssemblerRecipe.ItemOutput(item.asItem(), amount));
        return this;
    }

    public DMARecipeBuilder output(final ItemLike item, final int amount, final float chance) {
        return output(item, amount);
    }

    public DMARecipeBuilder outputFluid(final Fluid fluid, final int amount, final float chance) {
        this.fluidOutputs.add(new DimensionalMatterAssemblerRecipe.FluidOutput(fluid, amount));
        return this;
    }

    public DMARecipeBuilder energy(final int energy) {
        this.energy = energy;
        return this;
    }

    public DMARecipeBuilder time(final int time) {
        this.time = time;
        return this;
    }

    public DMARecipeBuilder noBulkQmfMirror() {
        this.bulkQmfMirrorEnabled = false;
        return this;
    }

    public void save(final RecipeOutput output) {
        save(output, Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, this.name));
    }
    
    public void save(final RecipeOutput output, final Identifier id) {
        final var recipe = new DimensionalMatterAssemblerRecipe(
                this.itemInputs,
                this.fluidInputs,
                this.itemOutputs,
                this.fluidOutputs,
                this.energy,
                this.time
        );
        output.accept(id, recipe, null);
        saveBulkQmfMirror(output);
    }

    private void saveBulkQmfMirror(final RecipeOutput output) {
        if (!this.bulkQmfMirrorEnabled) {
            return;
        }

        if (!this.name.startsWith("dma/")) {
            return;
        }

        if (this.itemOutputs.size() > 1 || this.fluidOutputs.size() > 1) {
            return;
        }

        final var builder = UniversalMultiblockRecipeBuilder.create(
                "universal/qmf/bulk/" + this.name.substring("dma/".length()),
                UniversalMultiblockMachineKind.QMF);

        for (final var input : this.itemInputs) {
            builder.inputItem(resolveSingleItem(input.getIngredient()), (long) input.getAmount() * BULK_QMF_FACTOR);
        }

        for (final var input : this.fluidInputs) {
            final var stacks = input.getIngredient().getStacks();
            if (stacks.length == 0) {
                return;
            }
            builder.inputFluid(stacks[0].getFluid(), (long) input.getAmount() * BULK_QMF_FACTOR);
        }

        if (!this.itemOutputs.isEmpty()) {
            final var itemOutput = this.itemOutputs.getFirst();
            if (new ItemStack(itemOutput.item()).is(ModTags.Items.CATALYST)) {
                return;
            }
            builder.outputItem(itemOutput.item(), (int) (itemOutput.amount() * BULK_QMF_FACTOR));
        }

        if (!this.fluidOutputs.isEmpty()) {
            final var fluidOutput = this.fluidOutputs.getFirst();
            builder.outputFluid(fluidOutput.fluid(), fluidOutput.amount() * BULK_QMF_FACTOR);
        }

        builder.energy((long) this.energy * BULK_QMF_FACTOR)
                .time(this.time)
                .requiredTier(1)
                .save(output);
    }

    private static Item resolveSingleItem(final Ingredient ingredient) {
        final var stacks = ingredient.getItems();
        if (stacks.length == 0) {
            throw new IllegalStateException("Cannot mirror DMA recipe with empty ingredient");
        }
        return stacks[0].getItem();
    }
}
