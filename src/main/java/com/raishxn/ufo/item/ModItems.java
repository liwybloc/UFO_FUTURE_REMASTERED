package com.raishxn.ufo.item;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.block.networking.EnergyCellBlockItem;
import appeng.items.parts.PartItem;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.fluid.ModFluids;
import com.raishxn.ufo.init.ModEntities;
import com.raishxn.ufo.item.custom.*;
import com.raishxn.ufo.item.InfinityCell;
import com.raishxn.ufo.item.custom.ThermalArmorItem;
import com.raishxn.ufo.part.QuantumPatternProviderPart;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import appeng.api.ids.AEBlockIds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(UfoMod.MOD_ID);

    public static final DeferredItem<Item> BISMUTH = ITEMS.registerItem("bismuth",
            properties -> new Item(properties));

    public static final DeferredItem<Item> DIMENSIONAL_PROCESSOR_PRESS = ITEMS.registerItem("dimensional_processor_press",
            properties -> new AnimatedNameItem(properties,
                    ChatFormatting.WHITE,
                    ChatFormatting.GRAY,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.BLACK,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.GRAY));
    public static final DeferredItem<Item> DIMENSIONAL_PROCESSOR = ITEMS.registerItem("dimensional_processor",
            properties -> new AnimatedNameItem(properties,
                    ChatFormatting.WHITE,
                    ChatFormatting.GRAY,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.BLACK,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.GRAY));
    public static final DeferredItem<Item> PHASE_SHIFT_COMPONENT_MATRIX = ITEMS.registerItem("phase_shift_component_matrix",
            properties -> new AnimatedNameItem(properties,
                    ChatFormatting.WHITE,
                    ChatFormatting.RED,
                    ChatFormatting.DARK_RED,
                    ChatFormatting.RED));
    public static final DeferredItem<Item> HYPER_DENSE_COMPONENT_MATRIX = ITEMS.registerItem("hyper_dense_component_matrix",
            properties -> new AnimatedNameItem(properties,
                    ChatFormatting.WHITE,
                    ChatFormatting.LIGHT_PURPLE,
                    ChatFormatting.DARK_PURPLE,
                    ChatFormatting.LIGHT_PURPLE));
    public static final DeferredItem<Item> TESSERACT_COMPONENT_MATRIX = ITEMS.registerItem("tesseract_component_matrix",
            properties -> new AnimatedNameItem(properties,
                    ChatFormatting.WHITE,
                    ChatFormatting.AQUA,
                    ChatFormatting.DARK_AQUA,
                    ChatFormatting.AQUA));
    public static final DeferredItem<Item> EVENT_HORIZON_COMPONENT_MATRIX = ITEMS.registerItem("event_horizon_component_matrix",
            properties -> new AnimatedNameItem(properties,
                    ChatFormatting.WHITE,
                    ChatFormatting.BLUE,
                    ChatFormatting.DARK_BLUE,
                    ChatFormatting.BLUE));
    public static final DeferredItem<Item> COSMIC_STRING_COMPONENT_MATRIX = ITEMS.registerItem("cosmic_string_component_matrix",
            properties -> new AnimatedNameItem(properties,
                    ChatFormatting.WHITE,
                    ChatFormatting.GREEN,
                    ChatFormatting.DARK_GREEN,
                    ChatFormatting.GREEN));
    public static final DeferredItem<Item> PRINTED_DIMENSIONAL_PROCESSOR = ITEMS.registerItem("printed_dimensional_processor",
            properties -> new AnimatedNameItem(properties,
                    ChatFormatting.WHITE,
                    ChatFormatting.GRAY,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.BLACK,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.GRAY));
    public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_INGOT = ITEMS.registerItem("white_dwarf_fragment_ingot",
            properties -> new AnimatedNameItem(properties,
                    ChatFormatting.WHITE,
                    ChatFormatting.GRAY,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.GRAY));
     public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_ROD = ITEMS.registerItem("white_dwarf_fragment_rod",
             properties -> new AnimatedNameItem(properties,
                     ChatFormatting.WHITE,
                     ChatFormatting.GRAY,
                     ChatFormatting.DARK_GRAY,
                     ChatFormatting.GRAY));
     public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_NUGGET = ITEMS.registerItem("white_dwarf_fragment_nugget",
             properties -> new AnimatedNameItem(properties,
                     ChatFormatting.WHITE,
                     ChatFormatting.GRAY,
                     ChatFormatting.DARK_GRAY,
                     ChatFormatting.GRAY));
     public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_DUST = ITEMS.registerItem("white_dwarf_fragment_dust",
             properties -> new AnimatedNameItem(properties,
                     ChatFormatting.WHITE,
                     ChatFormatting.GRAY,
                     ChatFormatting.DARK_GRAY,
                     ChatFormatting.GRAY));
     public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_INGOT = ITEMS.registerItem("neutron_star_fragment_ingot",
             properties -> new AnimatedNameItem(properties,
                     ChatFormatting.WHITE,
                     ChatFormatting.BLUE,
                     ChatFormatting.DARK_BLUE,
                     ChatFormatting.AQUA));
     public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_ROD = ITEMS.registerItem("neutron_star_fragment_rod",
             properties -> new AnimatedNameItem(properties,
                     ChatFormatting.WHITE,
                     ChatFormatting.BLUE,
                     ChatFormatting.DARK_BLUE,
                     ChatFormatting.AQUA));
     public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_NUGGET = ITEMS.registerItem("neutron_star_fragment_nugget",
             properties -> new AnimatedNameItem(properties,
                     ChatFormatting.WHITE,
                     ChatFormatting.BLUE,
                     ChatFormatting.DARK_BLUE,
                     ChatFormatting.AQUA));
     public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_DUST = ITEMS.registerItem("neutron_star_fragment_dust",
             properties -> new AnimatedNameItem(properties,
                     ChatFormatting.WHITE,
                     ChatFormatting.BLUE,
                     ChatFormatting.DARK_BLUE,
                     ChatFormatting.AQUA));
     public static final DeferredItem<Item> PULSAR_FRAGMENT_INGOT = ITEMS.registerItem("pulsar_fragment_ingot",
             properties -> new AnimatedNameItem(properties,
                     ChatFormatting.WHITE,
                     ChatFormatting.GREEN,
                     ChatFormatting.DARK_GREEN));
     public static final DeferredItem<Item> PULSAR_FRAGMENT_NUGGET = ITEMS.registerItem("pulsar_fragment_nugget",
             properties -> new AnimatedNameItem(properties,
                     ChatFormatting.WHITE,
                     ChatFormatting.GREEN,
                     ChatFormatting.DARK_GREEN));
     public static final DeferredItem<Item> PULSAR_FRAGMENT_DUST = ITEMS.registerItem("pulsar_fragment_dust",
             properties -> new AnimatedNameItem(properties,
                     ChatFormatting.WHITE,
                     ChatFormatting.GREEN,
                     ChatFormatting.DARK_GREEN));





     public static final DeferredItem<Item> ENTROPY_ASSEMBLER_CORE_CASING = ITEMS.registerItem("entropy_assembler_core_casing",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPY_ASSEMBLER_CORE_CASING.get(), properties,
                     ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.RED));

     public static final DeferredItem<Item> ENTROPY_SINGULARITY_CASING = ITEMS.registerItem("entropy_singularity_casing",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get(), properties,
                     ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY, ChatFormatting.BLACK, ChatFormatting.DARK_GRAY, ChatFormatting.GRAY));



     public static final DeferredItem<Item> ENTROPY_COMPUTER_CONDENSATION_MATRIX = ITEMS.registerItem("entropy_computer_condensation_matrix",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPY_COMPUTER_CONDENSATION_MATRIX.get(), properties,
                     ChatFormatting.RED, ChatFormatting.DARK_RED, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.LIGHT_PURPLE));

     public static final DeferredItem<Item> ENTROPIC_ASSEMBLER_MATRIX = ITEMS.registerItem("entropic_assembler_matrix",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPIC_ASSEMBLER_MATRIX.get(), properties,
                     ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.RED, ChatFormatting.GOLD));

     public static final DeferredItem<Item> ENTROPIC_CONVERGENCE_ENGINE = ITEMS.registerItem("entropic_convergence_engine",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPIC_CONVERGENCE_ENGINE.get(), properties,
                     ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.RED));

     public static final DeferredItem<Item> ENTROPIC_ASSEMBLER_CASING = ITEMS.registerItem("entropic_assembler_casing",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPIC_ASSEMBLER_CASING.get(), properties,
                     ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.RED, ChatFormatting.GOLD));

     public static final DeferredItem<Item> ENTROPIC_CONVERGENCE_CASING = ITEMS.registerItem("entropic_convergence_casing",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPIC_CONVERGENCE_CASING.get(), properties,
                     ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.RED));




     public static final DeferredItem<Item> QUANTUM_ENTROPY_CASING = ITEMS.registerItem("quantum_entropy_casing",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_ENTROPY_CASING.get(), properties,
                     ChatFormatting.WHITE, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE, ChatFormatting.DARK_AQUA, ChatFormatting.AQUA));

     public static final DeferredItem<Item> QUANTUM_HYPER_MECHANICAL_CASING = ITEMS.registerItem("quantum_hyper_mechanical_casing",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get(), properties,
                     ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_AQUA));

     public static final DeferredItem<Item> QUANTUM_MATTER_FABRICATOR_CONTROLLER = ITEMS.registerItem("quantum_matter_fabricator_controller",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get(), properties,
                     ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE));

     public static final DeferredItem<Item> QUANTUM_SLICER_CONTROLLER = ITEMS.registerItem("quantum_slicer_controller",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get(), properties,
                     ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_AQUA));

     public static final DeferredItem<Item> QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER = ITEMS.registerItem("quantum_processor_assembler_controller",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get(), properties,
                     ChatFormatting.LIGHT_PURPLE, ChatFormatting.AQUA, ChatFormatting.BLUE, ChatFormatting.WHITE));

     public static final DeferredItem<Item> QUANTUM_CRYOFORGE_CONTROLLER = ITEMS.registerItem("quantum_cryoforge_controller",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get(), properties,
                     ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_AQUA));

     public static final DeferredItem<Item> QUANTUM_PATTERN_HATCH = ITEMS.registerItem("quantum_pattern_hatch",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get(), properties,
                     ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE));

     public static final DeferredItem<Item> QUANTUM_PATTERN_PROVIDER_PART = registerPartItem(
             "quantum_pattern_provider_part",
             QuantumPatternProviderPart.class,
             QuantumPatternProviderPart::new);


     public static final DeferredItem<Item> STELLAR_NEXUS_CONTROLLER = ITEMS.registerItem("stellar_nexus_controller",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get(), properties,
                     ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.WHITE, ChatFormatting.AQUA, ChatFormatting.BLUE, ChatFormatting.LIGHT_PURPLE));

     public static final DeferredItem<Item> ME_MASSIVE_OUTPUT_HATCH = ITEMS.registerItem("me_massive_output_hatch",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get(), properties,
                     ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.WHITE));

     public static final DeferredItem<Item> ME_MASSIVE_FLUID_HATCH = ITEMS.registerItem("me_massive_fluid_hatch",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get(), properties,
                     ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE));

     public static final DeferredItem<Item> ME_MASSIVE_INPUT_HATCH = ITEMS.registerItem("me_massive_input_hatch",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get(), properties,
                     ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.WHITE));

     public static final DeferredItem<Item> AE_ENERGY_INPUT_HATCH = ITEMS.registerItem("ae_energy_input_hatch",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get(), properties,
                     ChatFormatting.YELLOW, ChatFormatting.GOLD, ChatFormatting.WHITE));

     public static final DeferredItem<Item> UFO_ENERGY_CELL = ITEMS.registerItem("ufo_energy_cell",
             properties -> new EnergyCellBlockItem(com.raishxn.ufo.block.ModBlocks.UFO_ENERGY_CELL.get(), properties));

      public static final DeferredItem<Item> QUANTUM_ENERGY_CELL = ITEMS.registerItem("quantum_energy_cell",
             properties -> new BlockItem(com.raishxn.ufo.block.ModBlocks.QUANTUM_ENERGY_CELL.get(), properties));



     public static final DeferredItem<Item> STELLAR_FIELD_GENERATOR_T1 = ITEMS.registerItem("stellar_field_generator_t1",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get(), properties,
                     ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.YELLOW));

     public static final DeferredItem<Item> STELLAR_FIELD_GENERATOR_T2 = ITEMS.registerItem("stellar_field_generator_t2",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get(), properties,
                     ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.WHITE));

     public static final DeferredItem<Item> STELLAR_FIELD_GENERATOR_T3 = ITEMS.registerItem("stellar_field_generator_t3",
             properties -> new AnimatedNameBlockItem(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get(), properties,
                     ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE, ChatFormatting.GOLD, ChatFormatting.YELLOW));

    public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_BUCKET = ITEMS.registerItem("neutron_star_fragment_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_NEUTRON_STAR_FRAGMENT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> PULSAR_FRAGMENT_BUCKET = ITEMS.registerItem("pulsar_fragment_bucket",
             properties -> new BucketItem(ModFluids.SOURCE_PULSAR_FRAGMENT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_BUCKET = ITEMS.registerItem("white_dwarf_fragment_bucket",
             properties -> new BucketItem(ModFluids.SOURCE_WHITE_DWARF_FRAGMENT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> LIQUID_STARLIGHT_BUCKET = ITEMS.registerItem("liquid_starlight_bucket",
             properties -> new BucketItem(ModFluids.SOURCE_LIQUID_STARLIGHT_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> PRIMORDIAL_MATTER_BUCKET = ITEMS.registerItem("primordial_matter_bucket",
             properties -> new BucketItem(ModFluids.SOURCE_PRIMORDIAL_MATTER_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> RAW_STAR_MATTER_PLASMA_BUCKET = ITEMS.registerItem("raw_star_matter_plasma_bucket",
             properties -> new BucketItem(ModFluids.SOURCE_RAW_STAR_MATTER_PLASMA_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> TRANSCENDING_MATTER_BUCKET = ITEMS.registerItem("transcending_matter_bucket",
             properties -> new BucketItem(ModFluids.SOURCE_TRANSCENDING_MATTER_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> UU_MATTER_BUCKET = ITEMS.registerItem("uu_matter_bucket",
             properties -> new BucketItem(ModFluids.SOURCE_UU_MATTER_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> UU_AMPLIFIER_BUCKET = ITEMS.registerItem("uu_amplifier_bucket",
             properties -> new BucketItem(ModFluids.SOURCE_UU_AMPLIFIER_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> GELID_CRYOTHEUM_BUCKET = ITEMS.registerItem("gelid_cryotheum_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_GELID_CRYOTHEUM.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> STABLE_COOLANT_BUCKET = ITEMS.registerItem("stable_coolant_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_STABLE_COOLANT.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> TEMPORAL_FLUID_BUCKET = ITEMS.registerItem("temporal_fluid_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_TEMPORAL_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> SPATIAL_FLUID_BUCKET = ITEMS.registerItem("spatial_fluid_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_SPATIAL_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));


    public static final DeferredItem<Item> QUANTUM_ANOMALY = ITEMS.registerItem("quantum_anomaly",
            properties -> new Item(properties
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .fireResistant()));

    public static final DeferredItem<Item> NUCLEAR_STAR = ITEMS.registerItem("nuclear_star",
            properties -> new Item(properties
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> SCAR = ITEMS.registerItem("scar",
            properties -> new Item(properties
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)));

    public static final DeferredItem<Item> SCRAP = ITEMS.registerItem("scrap",
            properties -> new Item(properties
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)));

    public static final DeferredItem<Item> SCRAP_BOX = ITEMS.registerItem("scrap_box",
            properties -> new Item(properties
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> STRUCTURE_SCANNER = ITEMS.registerItem("structure_scanner",
            properties -> new StructureScannerItem(properties
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> APOCALYPSE_TYPE_A_SPAWN_EGG = ITEMS.registerItem("apocalypse_type_a_spawn_egg",
            properties -> new SpawnEggItem(properties.spawnEgg(ModEntities.APOCALYPSE_TYPE_A.get())));

    public static final DeferredItem<Item> NEUTRONIUM_SPHERE = ITEMS.registerItem("neutronium_sphere",
            properties -> new Item(properties
                    .stacksTo(16)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> ENRICHED_NEUTRONIUM_SPHERE = ITEMS.registerItem("enriched_neutronium_sphere",
            properties -> new Item(properties
                    .stacksTo(8)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> CHARGED_ENRICHED_NEUTRONIUM_SPHERE = ITEMS.registerItem("charged_enriched_neutronium_sphere",
            properties -> new Item(properties
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> PROTO_MATTER = ITEMS.registerItem("proto_matter",
            properties -> new Item(properties
                    .stacksTo(8)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> CORPOREAL_MATTER = ITEMS.registerItem("corporeal_matter",
            properties -> new Item(properties
                    .stacksTo(8)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> WHITE_DWARF_MATTER = ITEMS.registerItem("white_dwarf_matter",
            properties -> new Item(properties
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> NEUTRON_STAR_MATTER = ITEMS.registerItem("neutron_star_matter",
            properties -> new Item(properties
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> PULSAR_MATTER = ITEMS.registerItem("pulsar_matter",
            properties -> new Item(properties
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> DARK_MATTER = ITEMS.registerItem("dark_matter",
            properties -> new Item(properties
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> OBSIDIAN_MATRIX = ITEMS.registerItem("obsidian_matrix",
            properties -> new Item(properties
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> UU_MATTER_CRYSTAL = ITEMS.registerItem("uu_matter_crystal",
            properties -> new Item(properties
                    .stacksTo(8)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> DUST_CRYOTHEUM = ITEMS.registerItem("dust_cryotheum",
            properties -> new Item(properties
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> DUST_BLIZZ = ITEMS.registerItem("dust_blizz",
            properties -> new Item(properties
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> UNSTABLE_WHITE_HOLE_MATTER = ITEMS.registerItem("unstable_white_hole_matter",
            properties -> new Item(properties
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));
    public static final DeferredItem<Item> AETHER_CONTAINMENT_CAPSULE = ITEMS.registerItem("aether_containment_capsule",
            properties -> new AetherContainmentCapsuleItem(properties
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> SAFE_CONTAINMENT_MATTER = ITEMS.registerItem("safe_containment_matter",
            properties -> new SafeContainmentMatterItem(properties
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> MATTERFLOW_CATALYST_T1 = ITEMS.registerItem("matterflow_catalyst_t1",
            properties -> new BaseCatalystItem(properties, "matterflow", 1));
    public static final DeferredItem<Item> MATTERFLOW_CATALYST_T2 = ITEMS.registerItem("matterflow_catalyst_t2",
            properties -> new BaseCatalystItem(properties, "matterflow", 2));
    public static final DeferredItem<Item> MATTERFLOW_CATALYST_T3 = ITEMS.registerItem("matterflow_catalyst_t3",
            properties -> new BaseCatalystItem(properties, "matterflow", 3));

    public static final DeferredItem<Item> CHRONO_CATALYST_T1 = ITEMS.registerItem("chrono_catalyst_t1",
            properties -> new BaseCatalystItem(properties, "chrono", 1));
    public static final DeferredItem<Item> CHRONO_CATALYST_T2 = ITEMS.registerItem("chrono_catalyst_t2",
            properties -> new BaseCatalystItem(properties, "chrono", 2));
    public static final DeferredItem<Item> CHRONO_CATALYST_T3 = ITEMS.registerItem("chrono_catalyst_t3",
            properties -> new BaseCatalystItem(properties, "chrono", 3));

    public static final DeferredItem<Item> OVERFLUX_CATALYST_T1 = ITEMS.registerItem("overflux_catalyst_t1",
            properties -> new BaseCatalystItem(properties, "overflux", 1));
    public static final DeferredItem<Item> OVERFLUX_CATALYST_T2 = ITEMS.registerItem("overflux_catalyst_t2",
            properties -> new BaseCatalystItem(properties, "overflux", 2));
    public static final DeferredItem<Item> OVERFLUX_CATALYST_T3 = ITEMS.registerItem("overflux_catalyst_t3",
            properties -> new BaseCatalystItem(properties, "overflux", 3));

    public static final DeferredItem<Item> QUANTUM_CATALYST_T1 = ITEMS.registerItem("quantum_catalyst_t1",
            properties -> new BaseCatalystItem(properties, "quantum", 1));
    public static final DeferredItem<Item> QUANTUM_CATALYST_T2 = ITEMS.registerItem("quantum_catalyst_t2",
            properties -> new BaseCatalystItem(properties, "quantum", 2));
    public static final DeferredItem<Item> QUANTUM_CATALYST_T3 = ITEMS.registerItem("quantum_catalyst_t3",
            properties -> new BaseCatalystItem(properties, "quantum", 3));

    public static final DeferredItem<Item> DIMENSIONAL_CATALYST = ITEMS.registerItem("dimensional_catalyst",
            properties -> new DimensionalCatalystItem(properties));


    public static void register(final IEventBus eventBus) {
        ITEMS.register(eventBus);
        ModCells.register(eventBus);
        ModTools.register(eventBus);
        ModArmor.register(eventBus);
    }

    private static <T extends IPart> DeferredItem<Item> registerPartItem(
            final String id,
            final Class<T> partClass,
            final java.util.function.Function<IPartItem<T>, T> factory) {
        return ITEMS.registerItem(id, properties -> new PartItem<>(properties, partClass, factory));
    }
}
