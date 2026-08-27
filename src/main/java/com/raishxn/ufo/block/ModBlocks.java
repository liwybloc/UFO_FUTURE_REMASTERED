package com.raishxn.ufo.block;

import appeng.block.AEBaseBlockItem;
import appeng.block.crafting.CraftingUnitBlock;
import appeng.block.networking.CreativeEnergyCellBlock;
import appeng.block.networking.EnergyCellBlock;
import appeng.core.definitions.BlockDefinition;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.custom.MegaCoProcessorBlockItem;
import com.raishxn.ufo.core.MegaCoProcessorTier;
import com.raishxn.ufo.core.MegaCraftingStorageTier;
import com.raishxn.ufo.fluid.ModFluids;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import com.raishxn.ufo.item.ModItems;
import com.raishxn.ufo.item.custom.AnimatedNameBlockItem;
import com.raishxn.ufo.item.custom.MegaCraftingStorageBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;
import net.pedroksl.ae2addonlib.registry.BlockRegistry;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.EnumMap;

public class ModBlocks extends BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(UfoMod.MOD_ID);

    public static final ModBlocks INSTANCE = new ModBlocks();

    ModBlocks() {
        super(UfoMod.MOD_ID);
    }

    public static final EnumMap<MegaCraftingStorageTier, DeferredBlock<CraftingUnitBlock>> CRAFTING_STORAGE_BLOCKS = new EnumMap<>(MegaCraftingStorageTier.class);
    public static final EnumMap<MegaCoProcessorTier, DeferredBlock<CraftingUnitBlock>> CO_PROCESSOR_BLOCKS = new EnumMap<>(MegaCoProcessorTier.class);

    public static final DeferredBlock<Block> QUANTUM_LATTICE_FRAME = registerBlockWithAnimatedItem("quantum_lattice_frame",
            properties -> new Block(properties.strength(5.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> GRAVITON_PLATED_CASING = registerBlockWithAnimatedItem("graviton_plated_casing",
            properties -> new Block(properties.strength(5.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> WHITE_DWARF_FRAGMENT_BLOCK = registerBlockWithAnimatedItem("white_dwarf_fragment_block",
            properties -> new Block(properties.strength(6.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> PULSAR_FRAGMENT_BLOCK = PulsarBlockWithAnimatedItem("pulsar_fragment_block",
            properties -> new Block(properties.strength(6.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> NEUTRON_STAR_FRAGMENT_BLOCK = NeutronStarBlockWithAnimatedItem("neutron_star_fragment_block",
            properties -> new Block(properties.strength(6.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<LiquidBlock> NEUTRON_STAR_FRAGMENT_FLUID_BLOCK = BLOCKS.registerBlock("neutron_star_fragment_fluid_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_NEUTRON_STAR_FRAGMENT_FLUID.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA));

    public static final DeferredBlock<LiquidBlock> PULSAR_FRAGMENT_FLUID_BLOCK = BLOCKS.registerBlock("pulsar_fragment_fluid_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_PULSAR_FRAGMENT_FLUID.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA));

    public static final DeferredBlock<LiquidBlock> WHITE_DWARF_FRAGMENT_FLUID_BLOCK = BLOCKS.registerBlock("white_dwarf_fragment_fluid_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_WHITE_DWARF_FRAGMENT_FLUID.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA));

    public static final DeferredBlock<LiquidBlock> LIQUID_STARLIGHT_FLUID_BLOCK = BLOCKS.registerBlock("liquid_starlight_fluid_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_LIQUID_STARLIGHT_FLUID.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA));

    public static final DeferredBlock<LiquidBlock> PRIMORDIAL_MATTER_FLUID_BLOCK = BLOCKS.registerBlock("primordial_matter_fluid_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_PRIMORDIAL_MATTER_FLUID.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA));

    public static final DeferredBlock<LiquidBlock> RAW_STAR_MATTER_PLASMA_FLUID_BLOCK = BLOCKS.registerBlock("raw_star_matter_plasma_fluid_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_RAW_STAR_MATTER_PLASMA_FLUID.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA));

    public static final DeferredBlock<LiquidBlock> TRANSCENDING_MATTER_FLUID_BLOCK = BLOCKS.registerBlock("transcending_matter_fluid_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_TRANSCENDING_MATTER_FLUID.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA));

    public static final DeferredBlock<LiquidBlock> UU_MATTER_FLUID_BLOCK = BLOCKS.registerBlock("uu_matter_fluid_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_UU_MATTER_FLUID.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA));

    public static final DeferredBlock<LiquidBlock> UU_AMPLIFIER_FLUID_BLOCK = BLOCKS.registerBlock("uu_amplifier_fluid_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_UU_AMPLIFIER_FLUID.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER));

    public static final DeferredBlock<LiquidBlock> GELID_CRYOTHEUM_BLOCK = BLOCKS.registerBlock("gelid_cryotheum_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_GELID_CRYOTHEUM.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER));

    public static final DeferredBlock<LiquidBlock> STABLE_COOLANT_BLOCK = BLOCKS.registerBlock("stable_coolant_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_STABLE_COOLANT.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER));

    public static final DeferredBlock<LiquidBlock> TEMPORAL_FLUID_BLOCK = BLOCKS.registerBlock("temporal_fluid_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_TEMPORAL_FLUID.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER));

    public static final DeferredBlock<LiquidBlock> SPATIAL_FLUID_BLOCK = BLOCKS.registerBlock("spatial_fluid_block",
            properties -> new LiquidBlock(ModFluids.SOURCE_SPATIAL_FLUID.get(), properties.noLootTable()), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER));

    public static final DeferredBlock<com.raishxn.ufo.block.DimensionalMatterAssemblerBlock> DIMENSIONAL_MATTER_ASSEMBLER_BLOCK =
            registerBlockWithStaticItem("dimensional_matter_assembler", com.raishxn.ufo.block.DimensionalMatterAssemblerBlock::new);

    public static final DeferredBlock<EnergyCellBlock> UFO_ENERGY_CELL = BLOCKS.registerBlock("ufo_energy_cell",
            properties -> new EnergyCellBlock(properties.strength(5.0F), 1_000_000_000D, 16_000, 1_000_000));

    public static final DeferredBlock<CreativeEnergyCellBlock> QUANTUM_ENERGY_CELL = BLOCKS.registerBlock("quantum_energy_cell",
            properties -> new CreativeEnergyCellBlock(properties.strength(5.0F)));

    static {
        for (final var tier : MegaCraftingStorageTier.values()) {
            registerMegaCraftingBlock(tier);
        }
        for (final var tier : MegaCoProcessorTier.values()) {
            registerMegaCoProcessorBlock(tier);
        }
    }
    private static <T extends Block> DeferredBlock<T> registerBlockWithAnimatedItem(final String name, final java.util.function.Function<BlockBehaviour.Properties, T> blockFactory) {
        final DeferredBlock<T> block = BLOCKS.registerBlock(name, blockFactory);
        ModItems.ITEMS.registerItem(name, properties -> new AnimatedNameBlockItem(block.get(), properties,
                ChatFormatting.WHITE,
                ChatFormatting.GRAY,
                ChatFormatting.DARK_GRAY,
                ChatFormatting.BLACK,
                ChatFormatting.DARK_GRAY,
                ChatFormatting.GRAY));
        return block;
    }
    private static <T extends Block> DeferredBlock<T> registerBlockWithStaticItem(final String name, final java.util.function.Function<BlockBehaviour.Properties, T> blockFactory) {
        final DeferredBlock<T> block = BLOCKS.registerBlock(name, blockFactory);
        ModItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties));
        return block;
    }
    private static <T extends Block> DeferredBlock<T> PulsarBlockWithAnimatedItem(final String name, final java.util.function.Function<BlockBehaviour.Properties, T> blockFactory) {
        final DeferredBlock<T> block = BLOCKS.registerBlock(name, blockFactory);
        ModItems.ITEMS.registerItem(name, properties -> new AnimatedNameBlockItem(block.get(), properties,
                ChatFormatting.WHITE,
                ChatFormatting.GREEN,
                ChatFormatting.DARK_GREEN));
        return block;
    }
    private static <T extends Block> DeferredBlock<T> NeutronStarBlockWithAnimatedItem(final String name, final java.util.function.Function<BlockBehaviour.Properties, T> blockFactory) {
        final DeferredBlock<T> block = BLOCKS.registerBlock(name, blockFactory);
        ModItems.ITEMS.registerItem(name, properties -> new AnimatedNameBlockItem(block.get(), properties,
                ChatFormatting.WHITE,
                ChatFormatting.BLUE,
                ChatFormatting.DARK_BLUE,
                ChatFormatting.AQUA));
        return block;
    }
    private static void registerMegaCoProcessorBlock(final MegaCoProcessorTier tier) {
        final String registryName = tier.getRegistryId() + "_mega_co_processor";
        final DeferredBlock<CraftingUnitBlock> registeredBlock = BLOCKS.registerBlock(registryName,
                properties -> new CraftingUnitBlock(properties.strength(5.0F), tier));
        ModItems.ITEMS.registerItem(registryName, properties -> new MegaCoProcessorBlockItem(registeredBlock.get(), properties, tier));
        CO_PROCESSOR_BLOCKS.put(tier, registeredBlock);
    }
    private static void registerMegaCraftingBlock(final MegaCraftingStorageTier tier) {
        final String registryName = tier.getRegistryId() + "_mega_crafting_storage";
        final DeferredBlock<CraftingUnitBlock> registeredBlock = BLOCKS.registerBlock(registryName,
                properties -> new CraftingUnitBlock(properties.strength(5.0F), tier));
        registerBlockItem(registryName, registeredBlock, tier);
        CRAFTING_STORAGE_BLOCKS.put(tier, registeredBlock);
    }
    private static <T extends Block> void registerBlockItem(final String name, final DeferredBlock<T> block, final MegaCraftingStorageTier tier) {
        ModItems.ITEMS.registerItem(name, properties -> new MegaCraftingStorageBlockItem(block.get(), properties, tier));
    }
    public void register(final IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    protected static <T extends Block> BlockDefinition<T> block(
            final String englishName, final String id, final Supplier<T> blockSupplier) {
        return block(englishName, UfoMod.id(id), properties -> blockSupplier.get());
    }
    protected static <T extends Block> BlockDefinition<T> block(
            final String englishName,
            final String id,
            final Supplier<T> blockSupplier,
            @Nullable final BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        return block(englishName, UfoMod.id(id), properties -> blockSupplier.get(), itemFactory);
    }
}
