package com.raishxn.ufo.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

public final class DimensionalMatterAssemblerRecipeSerializer {

    public static final MapCodec<DimensionalMatterAssemblerRecipe> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
                    IngredientStack.Item.CODEC.listOf().fieldOf("item_inputs").forGetter(DimensionalMatterAssemblerRecipe::itemInputs),
                    IngredientStack.Fluid.CODEC.listOf().fieldOf("fluid_inputs").forGetter(DimensionalMatterAssemblerRecipe::fluidInputs),
                    DimensionalMatterAssemblerRecipe.ItemOutput.CODEC.listOf().fieldOf("item_outputs").forGetter(DimensionalMatterAssemblerRecipe::itemOutputDefinitions),
                    DimensionalMatterAssemblerRecipe.FluidOutput.CODEC.listOf().fieldOf("fluid_outputs").forGetter(DimensionalMatterAssemblerRecipe::fluidOutputDefinitions),
                    Codec.INT.fieldOf("energy").forGetter(DimensionalMatterAssemblerRecipe::energy),
                    Codec.INT.fieldOf("time").forGetter(DimensionalMatterAssemblerRecipe::time))
            .apply(builder, DimensionalMatterAssemblerRecipe::new));
            
    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionalMatterAssemblerRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    IngredientStack.Item.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    DimensionalMatterAssemblerRecipe::itemInputs,
                    IngredientStack.Fluid.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    DimensionalMatterAssemblerRecipe::fluidInputs,
                    DimensionalMatterAssemblerRecipe.ItemOutput.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    DimensionalMatterAssemblerRecipe::itemOutputDefinitions,
                    DimensionalMatterAssemblerRecipe.FluidOutput.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    DimensionalMatterAssemblerRecipe::fluidOutputDefinitions,
                    ByteBufCodecs.INT,
                    DimensionalMatterAssemblerRecipe::energy,
                    ByteBufCodecs.INT,
                    DimensionalMatterAssemblerRecipe::time,
                    DimensionalMatterAssemblerRecipe::new);

    public static final RecipeSerializer<DimensionalMatterAssemblerRecipe> INSTANCE =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private DimensionalMatterAssemblerRecipeSerializer() {}
}
