package com.raishxn.ufo.recipe;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.raishxn.ufo.init.ModRecipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import net.minecraft.world.level.material.Fluid;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DimensionalMatterAssemblerRecipe(List<IngredientStack.Item> itemInputs,
                                               List<IngredientStack.Fluid> fluidInputs, List<ItemOutput> itemOutputDefinitions,
                                               List<FluidOutput> fluidOutputDefinitions, int energy,
                                               int time) implements Recipe<RecipeInput> {

    public DimensionalMatterAssemblerRecipe(
            final List<IngredientStack.Item> itemInputs,
            final List<IngredientStack.Fluid> fluidInputs,
            final List<ItemOutput> itemOutputDefinitions,
            final List<FluidOutput> fluidOutputDefinitions,
            final int energy,
            final int time) {
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.itemOutputDefinitions = itemOutputDefinitions;
        this.fluidOutputDefinitions = fluidOutputDefinitions;
        this.energy = energy;
        this.time = time > 0 ? time : 200;
    }

    public List<GenericStack> itemOutputs() {
        return this.itemOutputDefinitions.stream()
                .map(output -> new GenericStack(AEItemKey.of(output.item), output.amount))
                .toList();
    }

    public List<GenericStack> fluidOutputs() {
        return this.fluidOutputDefinitions.stream()
                .map(output -> new GenericStack(AEFluidKey.of(output.fluid), output.amount))
                .toList();
    }

    public record ItemOutput(Item item, long amount) {
        public static final Codec<ItemOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(ItemOutput::item),
                Codec.LONG.fieldOf("#").forGetter(ItemOutput::amount)
        ).apply(instance, ItemOutput::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ItemOutput> STREAM_CODEC = StreamCodec.of(
                (buf, output) -> { buf.writeIdentifier(BuiltInRegistries.ITEM.getKey(output.item)); buf.writeLong(output.amount); },
                buf -> new ItemOutput(BuiltInRegistries.ITEM.getValue(buf.readIdentifier()), buf.readLong()));
    }

    public record FluidOutput(Fluid fluid, long amount) {
        public static final Codec<FluidOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("id").forGetter(FluidOutput::fluid),
                Codec.LONG.fieldOf("#").forGetter(FluidOutput::amount)
        ).apply(instance, FluidOutput::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, FluidOutput> STREAM_CODEC = StreamCodec.of(
                (buf, output) -> { buf.writeIdentifier(BuiltInRegistries.FLUID.getKey(output.fluid)); buf.writeLong(output.amount); },
                buf -> new FluidOutput(BuiltInRegistries.FLUID.getValue(buf.readIdentifier()), buf.readLong()));
    }

    @Override
    public boolean matches(@NotNull final RecipeInput recipeInput, @NotNull final Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull final RecipeInput inv) {
        return getResultItem().copy();
    }

    public boolean canCraftInDimensions(final int width, final int height) {
        return true;
    }

    public @NotNull ItemStack getResultItem(final HolderLookup.@NotNull Provider registries) {
        return getResultItem();
    }

    public ItemStack getResultItem() {
        if (!this.itemOutputDefinitions.isEmpty()) {
            final ItemOutput output = this.itemOutputDefinitions.getFirst();
            return new ItemStack(output.item(), (int) output.amount());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return DimensionalMatterAssemblerRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
        return ModRecipes.DMA_RECIPE_TYPE.get();
    }

    @Override
    public boolean showNotification() { return false; }

    @Override
    public String group() { return "ufo:dma"; }

    @Override
    public PlacementInfo placementInfo() { return PlacementInfo.NOT_PLACEABLE; }

    @Override
    public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }

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

    @Override
    public boolean isSpecial() {
        return true;
    }
}
