package com.raishxn.ufo.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.raishxn.ufo.api.multiblock.MultiblockMachineTier;
import com.raishxn.ufo.init.ModRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public record UniversalMultiblockRecipe(UniversalMultiblockMachineKind machine, String recipeName,
                                        List<ItemRequirement> itemInputs, List<FluidRequirement> fluidInputs,
                                        List<ChemicalRequirement> chemicalInputs, Item itemOutputItem,
                                        long itemOutputAmount, Fluid fluidOutputFluid, long fluidOutputAmount,
                                        long energy, int time, int requiredTier) implements Recipe<RecipeInput> {
    public UniversalMultiblockRecipe(
            final UniversalMultiblockMachineKind machine,
            final String recipeName,
            final List<ItemRequirement> itemInputs,
            final List<FluidRequirement> fluidInputs,
            final List<ChemicalRequirement> chemicalInputs,
            final Item itemOutputItem,
            final long itemOutputAmount,
            final Fluid fluidOutputFluid,
            final long fluidOutputAmount,
            final long energy,
            final int time,
            final int requiredTier) {
        this.machine = machine;
        this.recipeName = recipeName;
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.chemicalInputs = chemicalInputs;
        this.itemOutputItem = itemOutputItem == null ? net.minecraft.world.item.Items.AIR : itemOutputItem;
        this.itemOutputAmount = this.itemOutputItem == net.minecraft.world.item.Items.AIR ? 0L : Math.max(0L, itemOutputAmount);
        this.fluidOutputFluid = fluidOutputFluid == null ? Fluids.EMPTY : fluidOutputFluid;
        this.fluidOutputAmount = this.fluidOutputFluid == Fluids.EMPTY ? 0L : Math.max(0L, fluidOutputAmount);
        this.energy = energy;
        this.time = time;
        this.requiredTier = Math.max(MultiblockMachineTier.MK1.level(), requiredTier);
    }

    public UniversalMultiblockRecipe(
            final UniversalMultiblockMachineKind machine,
            final String recipeName,
            final List<ItemRequirement> itemInputs,
            final List<FluidRequirement> fluidInputs,
            final List<ChemicalRequirement> chemicalInputs,
            final ItemStack itemOutput,
            final long itemOutputAmount,
            final FluidStack fluidOutput,
            final long fluidOutputAmount,
            final long energy,
            final int time,
            final int requiredTier) {
        this(machine, recipeName, itemInputs, fluidInputs, chemicalInputs,
                itemOutput == null || itemOutput.isEmpty() ? net.minecraft.world.item.Items.AIR : itemOutput.getItem(),
                itemOutputAmount,
                fluidOutput == null || fluidOutput.isEmpty() ? Fluids.EMPTY : fluidOutput.getFluid(),
                fluidOutputAmount, energy, time, requiredTier);
    }

    public ItemStack itemOutput() {
        return this.itemOutputItem == net.minecraft.world.item.Items.AIR
                ? ItemStack.EMPTY
                : new ItemStack(this.itemOutputItem);
    }

    public ItemStack getDisplayedItemOutput() {
        final ItemStack stack = itemOutput();
        if (!stack.isEmpty()) {
            stack.setCount((int) Math.min(Integer.MAX_VALUE, Math.max(1L, this.itemOutputAmount)));
        }
        return stack;
    }

    public FluidStack fluidOutput() {
        return this.fluidOutputFluid == Fluids.EMPTY
                ? FluidStack.EMPTY
                : new FluidStack(this.fluidOutputFluid, 1);
    }

    @Override
    public boolean matches(final RecipeInput pInput, final Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(final RecipeInput pInput) {
        return getDisplayedItemOutput();
    }

    @Override
    public boolean showNotification() { return false; }

    @Override
    public String group() { return "ufo:universal_multiblock"; }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.itemInputs.stream()
                .map(ItemRequirement::ingredient)
                .toList());
    }

    @Override
    public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return ModRecipes.UNIVERSAL_MULTIBLOCK_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return ModRecipes.UNIVERSAL_MULTIBLOCK_TYPE.get();
    }

    public record ItemRequirement(Ingredient ingredient, long amount) {
        public static final Codec<ItemRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(ItemRequirement::ingredient),
                Codec.LONG.fieldOf("amount").forGetter(ItemRequirement::amount)
        ).apply(instance, ItemRequirement::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemRequirement> STREAM_CODEC = StreamCodec.of(
                (buf, ingredient) -> {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient.ingredient);
                    buf.writeLong(ingredient.amount);
                },
                buf -> new ItemRequirement(Ingredient.CONTENTS_STREAM_CODEC.decode(buf), buf.readLong())
        );
    }

    public record FluidRequirement(Fluid fluid, long amount) {
        public static final Codec<FluidRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidRequirement::fluid),
                Codec.LONG.fieldOf("amount").forGetter(FluidRequirement::amount)
        ).apply(instance, FluidRequirement::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FluidRequirement> STREAM_CODEC = StreamCodec.of(
                (buf, ingredient) -> {
                    buf.writeIdentifier(BuiltInRegistries.FLUID.getKey(ingredient.fluid));
                    buf.writeLong(ingredient.amount);
                },
                buf -> new FluidRequirement(BuiltInRegistries.FLUID.getValue(buf.readIdentifier()), buf.readLong())
        );
    }

    public record ChemicalRequirement(Identifier chemicalId, long amount) {
        public static final Codec<ChemicalRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("chemical").forGetter(ChemicalRequirement::chemicalId),
                Codec.LONG.fieldOf("amount").forGetter(ChemicalRequirement::amount)
        ).apply(instance, ChemicalRequirement::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ChemicalRequirement> STREAM_CODEC = StreamCodec.of(
                (buf, ingredient) -> {
                    buf.writeIdentifier(ingredient.chemicalId);
                    buf.writeLong(ingredient.amount);
                },
                buf -> new ChemicalRequirement(buf.readIdentifier(), buf.readLong())
        );
    }

    public record ItemOutputDefinition(Item item, long amount) {
        public static final MapCodec<ItemOutputDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(ItemOutputDefinition::item),
                Codec.LONG.optionalFieldOf("count", 1L).forGetter(ItemOutputDefinition::amount)
        ).apply(instance, ItemOutputDefinition::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemOutputDefinition> STREAM_CODEC = StreamCodec.of(
                (buf, output) -> {
                    buf.writeIdentifier(BuiltInRegistries.ITEM.getKey(output.item));
                    buf.writeLong(output.amount);
                },
                buf -> new ItemOutputDefinition(BuiltInRegistries.ITEM.getValue(buf.readIdentifier()), buf.readLong())
        );

    }

    public record FluidOutputDefinition(Fluid fluid) {
        public static final MapCodec<FluidOutputDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("id").forGetter(FluidOutputDefinition::fluid)
        ).apply(instance, FluidOutputDefinition::new));
    }

    public static final class Serializer {
        public static final MapCodec<UniversalMultiblockRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                UniversalMultiblockMachineKind.CODEC.fieldOf("machine").forGetter(UniversalMultiblockRecipe::machine),
                Codec.STRING.optionalFieldOf("recipe_name", "Universal Multiblock Recipe").forGetter(UniversalMultiblockRecipe::recipeName),
                ItemRequirement.CODEC.listOf().fieldOf("item_inputs").forGetter(UniversalMultiblockRecipe::itemInputs),
                FluidRequirement.CODEC.listOf().optionalFieldOf("fluid_inputs", List.of()).forGetter(UniversalMultiblockRecipe::fluidInputs),
                ChemicalRequirement.CODEC.listOf().optionalFieldOf("chemical_inputs", List.of()).forGetter(UniversalMultiblockRecipe::chemicalInputs),
                ItemOutputDefinition.CODEC.codec().optionalFieldOf("item_output").forGetter((UniversalMultiblockRecipe recipe) -> recipe.itemOutputItem == net.minecraft.world.item.Items.AIR
                        ? Optional.empty()
                        : Optional.of(new ItemOutputDefinition(recipe.itemOutputItem, recipe.itemOutputAmount))),
                FluidOutputDefinition.CODEC.codec().optionalFieldOf("fluid_output").forGetter((UniversalMultiblockRecipe recipe) -> recipe.fluidOutputFluid == Fluids.EMPTY
                        ? Optional.empty()
                        : Optional.of(new FluidOutputDefinition(recipe.fluidOutputFluid))),
                Codec.LONG.optionalFieldOf("fluid_output_amount", 0L).forGetter(UniversalMultiblockRecipe::fluidOutputAmount),
                Codec.LONG.fieldOf("energy").forGetter(UniversalMultiblockRecipe::energy),
                Codec.INT.fieldOf("time").forGetter(UniversalMultiblockRecipe::time),
                Codec.INT.optionalFieldOf("required_tier", MultiblockMachineTier.MK1.level()).forGetter(UniversalMultiblockRecipe::requiredTier)
        ).apply(instance, (machine, recipeName, itemInputs, fluidInputs, chemicalInputs, itemOutput, fluidOutput, fluidOutputAmount, energy, time, requiredTier) ->
                new UniversalMultiblockRecipe(
                        machine,
                        recipeName,
                        itemInputs,
                        fluidInputs,
                        chemicalInputs,
                        itemOutput.map(ItemOutputDefinition::item).orElse(net.minecraft.world.item.Items.AIR),
                        itemOutput.map(ItemOutputDefinition::amount).orElse(0L),
                        fluidOutput.map(FluidOutputDefinition::fluid).orElse(Fluids.EMPTY),
                        fluidOutputAmount,
                        energy,
                        time,
                        requiredTier
                )));

        public static final StreamCodec<RegistryFriendlyByteBuf, UniversalMultiblockRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.machine.serializedName());
                    buf.writeUtf(recipe.recipeName);
                    ItemRequirement.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.itemInputs);
                    FluidRequirement.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.fluidInputs);
                    ChemicalRequirement.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.chemicalInputs);
                    boolean hasItemOutput = recipe.itemOutputItem != net.minecraft.world.item.Items.AIR;
                    buf.writeBoolean(hasItemOutput);
                    if (hasItemOutput) {
                        ItemOutputDefinition.STREAM_CODEC.encode(buf, new ItemOutputDefinition(recipe.itemOutputItem, recipe.itemOutputAmount));
                    }
                    boolean hasFluidOutput = recipe.fluidOutputFluid != Fluids.EMPTY && recipe.fluidOutputAmount > 0;
                    buf.writeBoolean(hasFluidOutput);
                    if (hasFluidOutput) {
                        buf.writeIdentifier(BuiltInRegistries.FLUID.getKey(recipe.fluidOutputFluid));
                        buf.writeLong(recipe.fluidOutputAmount);
                    }
                    buf.writeLong(recipe.energy);
                    buf.writeInt(recipe.time);
                    buf.writeInt(recipe.requiredTier);
                },
                buf -> {
                    var machine = UniversalMultiblockMachineKind.fromSerializedName(buf.readUtf());
                    var recipeName = buf.readUtf();
                    var itemInputs = ItemRequirement.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
                    var fluidInputs = FluidRequirement.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
                    var chemicalInputs = ChemicalRequirement.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
                    boolean hasItemOutput = buf.readBoolean();
                    var itemOutput = hasItemOutput ? ItemOutputDefinition.STREAM_CODEC.decode(buf) : null;
                    boolean hasFluidOutput = buf.readBoolean();
                    var fluidOutput = hasFluidOutput ? BuiltInRegistries.FLUID.getValue(buf.readIdentifier()) : Fluids.EMPTY;
                    long fluidOutputAmount = hasFluidOutput ? buf.readLong() : 0L;
                    long energy = buf.readLong();
                    int time = buf.readInt();
                    int requiredTier = buf.readInt();
                    return new UniversalMultiblockRecipe(
                            machine,
                            recipeName,
                            itemInputs,
                            fluidInputs,
                            chemicalInputs,
                            itemOutput != null ? itemOutput.item() : net.minecraft.world.item.Items.AIR,
                            itemOutput != null ? itemOutput.amount() : 0L,
                            fluidOutput,
                            fluidOutputAmount,
                            energy,
                            time,
                            requiredTier
                    );
                }
        );

        public static final RecipeSerializer<UniversalMultiblockRecipe> INSTANCE =
                new RecipeSerializer<>(CODEC, STREAM_CODEC);

        private Serializer() {}
    }
}
