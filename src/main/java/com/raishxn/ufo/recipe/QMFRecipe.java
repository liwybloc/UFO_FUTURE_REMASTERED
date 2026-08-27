package com.raishxn.ufo.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.api.multiblock.MultiblockMachineTier;
import com.raishxn.ufo.init.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.List;

public class QMFRecipe implements Recipe<RecipeInput> {
    private final String recipeName;
    private final List<QMFRecipeIngredient> itemInputs;
    private final List<QMFFluidIngredient> fluidInputs;
    private final List<QMFChemicalIngredient> chemicalInputs;
    private final ItemStack itemOutput;
    private final long energy;
    private final int time;
    private final int requiredTier;

    public QMFRecipe(final String recipeName, final List<QMFRecipeIngredient> itemInputs, final List<QMFFluidIngredient> fluidInputs, final List<QMFChemicalIngredient> chemicalInputs, final ItemStack itemOutput, final long energy, final int time, final int requiredTier) {
        this.recipeName = recipeName;
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.chemicalInputs = chemicalInputs;
        this.itemOutput = itemOutput;
        this.energy = energy;
        this.time = time;
        this.requiredTier = Math.max(MultiblockMachineTier.MK1.level(), requiredTier);
    }

    public String getRecipeName() { return recipeName; }
    public List<QMFRecipeIngredient> getItemInputs() { return itemInputs; }
    public List<QMFFluidIngredient> getFluidInputs() { return fluidInputs; }
    public List<QMFChemicalIngredient> getChemicalInputs() { return chemicalInputs; }
    public long getEnergy() { return energy; }
    public int getTime() { return time; }
    public int getRequiredTier() { return requiredTier; }

    @Override
    public boolean matches(final RecipeInput pInput, final Level pLevel) { return false; }

    @Override
    public ItemStack assemble(final RecipeInput pInput) { return itemOutput.copy(); }

    @Override
    public boolean showNotification() { return false; }

    @Override
    public String group() { return "ufo:qmf"; }

    @Override
    public PlacementInfo placementInfo() { return PlacementInfo.NOT_PLACEABLE; }

    @Override
    public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }

    public ItemStack getResultItem() { return itemOutput.copy(); }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() { return ModRecipes.QMF_SERIALIZER.get(); }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() { return ModRecipes.QMF_TYPE.get(); }

    public record QMFRecipeIngredient(Ingredient ingredient, long amount) {
        public static final Codec<QMFRecipeIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(QMFRecipeIngredient::ingredient),
                Codec.LONG.fieldOf("amount").forGetter(QMFRecipeIngredient::amount)
        ).apply(instance, QMFRecipeIngredient::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, QMFRecipeIngredient> STREAM_CODEC = StreamCodec.of(
            (buf, ing) -> { Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing.ingredient); buf.writeLong(ing.amount); },
            buf -> new QMFRecipeIngredient(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readLong())
        );
    }

    public record QMFFluidIngredient(FluidStack fluid, long amount) {
         public static final Codec<QMFFluidIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FluidStack.CODEC.fieldOf("fluid").forGetter(QMFFluidIngredient::fluid),
                Codec.LONG.fieldOf("amount").forGetter(QMFFluidIngredient::amount)
        ).apply(instance, QMFFluidIngredient::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, QMFFluidIngredient> STREAM_CODEC = StreamCodec.of(
            (buf, ing) -> { FluidStack.STREAM_CODEC.encode(buf, ing.fluid); buf.writeLong(ing.amount); },
            buf -> new QMFFluidIngredient(FluidStack.STREAM_CODEC.decode(buf), buf.readLong())
        );
    }

    public record QMFChemicalIngredient(Identifier chemicalId, long amount) {
        public static final Codec<QMFChemicalIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("chemical").forGetter(QMFChemicalIngredient::chemicalId),
                Codec.LONG.fieldOf("amount").forGetter(QMFChemicalIngredient::amount)
        ).apply(instance, QMFChemicalIngredient::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, QMFChemicalIngredient> STREAM_CODEC = StreamCodec.of(
            (buf, ing) -> {
                buf.writeIdentifier(ing.chemicalId);
                buf.writeLong(ing.amount);
            },
            buf -> new QMFChemicalIngredient(buf.readIdentifier(), buf.readLong())
        );
    }

    public static final class Serializer {
        public static final MapCodec<QMFRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("recipe_name", "QMF Recipe").forGetter(QMFRecipe::getRecipeName),
                QMFRecipeIngredient.CODEC.listOf().fieldOf("item_inputs").forGetter(QMFRecipe::getItemInputs),
                QMFFluidIngredient.CODEC.listOf().optionalFieldOf("fluid_inputs", List.of()).forGetter(QMFRecipe::getFluidInputs),
                QMFChemicalIngredient.CODEC.listOf().optionalFieldOf("chemical_inputs", List.of()).forGetter(QMFRecipe::getChemicalInputs),
                ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.itemOutput),
                Codec.LONG.fieldOf("energy").forGetter(QMFRecipe::getEnergy),
                Codec.INT.fieldOf("time").forGetter(QMFRecipe::getTime),
                Codec.INT.optionalFieldOf("required_tier", MultiblockMachineTier.MK1.level()).forGetter(QMFRecipe::getRequiredTier)
        ).apply(instance, QMFRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, QMFRecipe> STREAM_CODEC = StreamCodec.of(
            (buf, recipe) -> {
                buf.writeUtf(recipe.recipeName);
                QMFRecipeIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.itemInputs);
                QMFFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.fluidInputs);
                QMFChemicalIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.chemicalInputs);
                ItemStack.STREAM_CODEC.encode(buf, recipe.itemOutput);
                buf.writeLong(recipe.energy);
                buf.writeInt(recipe.time);
                buf.writeInt(recipe.requiredTier);
            },
            buf -> new QMFRecipe(
                buf.readUtf(),
                QMFRecipeIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf),
                QMFFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf),
                QMFChemicalIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf),
                ItemStack.STREAM_CODEC.decode(buf),
                buf.readLong(),
                buf.readInt(),
                buf.readInt()
            )
        );

        public static final RecipeSerializer<QMFRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);

        private Serializer() {}
    }
}
