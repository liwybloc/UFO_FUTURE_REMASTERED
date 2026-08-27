package com.raishxn.ufo.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.stacks.GenericStack;

public final class StellarSimulationRecipeSerializer {

    private StellarSimulationRecipeSerializer() {}


    public static final MapCodec<StellarSimulationRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            IngredientStack.Item.CODEC.listOf().fieldOf("item_inputs")
                    .forGetter(StellarSimulationRecipe::getItemInputs),
            IngredientStack.Fluid.CODEC.listOf().fieldOf("fluid_inputs")
                    .forGetter(StellarSimulationRecipe::getFluidInputs),
            DimensionalMatterAssemblerRecipe.ItemOutput.CODEC.listOf().fieldOf("item_outputs")
                    .forGetter(StellarSimulationRecipe::getItemOutputDefinitions),
            DimensionalMatterAssemblerRecipe.FluidOutput.CODEC.listOf().fieldOf("fluid_outputs")
                    .forGetter(StellarSimulationRecipe::getFluidOutputDefinitions),
            Codec.STRING.optionalFieldOf("simulation_name", "")
                    .forGetter(StellarSimulationRecipe::getSimulationName),
            Codec.LONG.optionalFieldOf("energy", 0L)
                    .forGetter(StellarSimulationRecipe::getEnergyCost),
            Codec.INT.fieldOf("time")
                    .forGetter(StellarSimulationRecipe::getTime),
            Codec.INT.optionalFieldOf("cooling_level", 0)
                    .forGetter(StellarSimulationRecipe::getCoolingLevel),
            Codec.INT.optionalFieldOf("field_tier", 1)
                    .forGetter(StellarSimulationRecipe::getFieldTier),
            Codec.STRING.optionalFieldOf("fuel_fluid", "")
                    .forGetter(StellarSimulationRecipe::getFuelFluid),
            Codec.LONG.optionalFieldOf("fuel_amount", 0L)
                    .forGetter(StellarSimulationRecipe::getFuelAmount),
            Codec.LONG.optionalFieldOf("coolant_amount", 0L)
                    .forGetter(StellarSimulationRecipe::getCoolantAmount)
    ).apply(builder, StellarSimulationRecipe::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, StellarSimulationRecipe> STREAM_CODEC =
            StreamCodec.of(
                    StellarSimulationRecipeSerializer::encode,
                    StellarSimulationRecipeSerializer::decode
            );

    private static void encode(final RegistryFriendlyByteBuf buf, final StellarSimulationRecipe recipe) {
        IngredientStack.Item.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list())
                .encode(buf, recipe.getItemInputs());
        IngredientStack.Fluid.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list())
                .encode(buf, recipe.getFluidInputs());
        DimensionalMatterAssemblerRecipe.ItemOutput.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list())
                .encode(buf, recipe.getItemOutputDefinitions());
        DimensionalMatterAssemblerRecipe.FluidOutput.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list())
                .encode(buf, recipe.getFluidOutputDefinitions());
        buf.writeUtf(recipe.getSimulationName());
        buf.writeLong(recipe.getEnergyCost());
        buf.writeInt(recipe.getTime());
        buf.writeInt(recipe.getCoolingLevel());
        buf.writeInt(recipe.getFieldTier());
        buf.writeUtf(recipe.getFuelFluid());
        buf.writeLong(recipe.getFuelAmount());
        buf.writeLong(recipe.getCoolantAmount());
    }

    private static StellarSimulationRecipe decode(final RegistryFriendlyByteBuf buf) {
        final var itemInputs = IngredientStack.Item.STREAM_CODEC
                .apply(net.minecraft.network.codec.ByteBufCodecs.list()).decode(buf);
        final var fluidInputs = IngredientStack.Fluid.STREAM_CODEC
                .apply(net.minecraft.network.codec.ByteBufCodecs.list()).decode(buf);
        final var itemOutputs = DimensionalMatterAssemblerRecipe.ItemOutput.STREAM_CODEC
                .apply(net.minecraft.network.codec.ByteBufCodecs.list()).decode(buf);
        final var fluidOutputs = DimensionalMatterAssemblerRecipe.FluidOutput.STREAM_CODEC
                .apply(net.minecraft.network.codec.ByteBufCodecs.list()).decode(buf);
        final String simulationName = buf.readUtf();
        final long energy = buf.readLong();
        final int time = buf.readInt();
        final int coolingLevel = buf.readInt();
        final int fieldTier = buf.readInt();
        final String fuelFluid = buf.readUtf();
        final long fuelAmount = buf.readLong();
        final long coolantAmount = buf.readLong();

        return new StellarSimulationRecipe(
                itemInputs, fluidInputs, itemOutputs, fluidOutputs,
                simulationName, energy, time, coolingLevel, fieldTier,
                fuelFluid, fuelAmount, coolantAmount);
    }


    public static final RecipeSerializer<StellarSimulationRecipe> INSTANCE =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
