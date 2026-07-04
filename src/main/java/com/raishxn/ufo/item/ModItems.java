package com.raishxn.ufo.item;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.block.networking.EnergyCellBlockItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.fluid.ModFluids;
import com.raishxn.ufo.init.ModEntities;
import com.raishxn.ufo.item.custom.*;
import com.raishxn.ufo.item.InfinityCell;
import com.raishxn.ufo.item.custom.ThermalArmorItem;
import com.raishxn.ufo.part.QuantumPatternProviderPart;
import mekanism.common.registries.MekanismItems;
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

    public static final DeferredItem<Item> BISMUTH = ITEMS.register("bismuth",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> DIMENSIONAL_PROCESSOR_PRESS = ITEMS.register("dimensional_processor_press",
            () -> new AnimatedNameItem(new Item.Properties(),
                    ChatFormatting.WHITE,
                    ChatFormatting.GRAY,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.BLACK,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.GRAY));
    public static final DeferredItem<Item> DIMENSIONAL_PROCESSOR = ITEMS.register("dimensional_processor",
            () -> new AnimatedNameItem(new Item.Properties(),
                    ChatFormatting.WHITE,
                    ChatFormatting.GRAY,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.BLACK,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.GRAY));
    public static final DeferredItem<Item> PHASE_SHIFT_COMPONENT_MATRIX = ITEMS.register("phase_shift_component_matrix",
            () -> new AnimatedNameItem(new Item.Properties(),
                    ChatFormatting.WHITE,
                    ChatFormatting.RED,
                    ChatFormatting.DARK_RED,
                    ChatFormatting.RED));
    public static final DeferredItem<Item> HYPER_DENSE_COMPONENT_MATRIX = ITEMS.register("hyper_dense_component_matrix",
            () -> new AnimatedNameItem(new Item.Properties(),
                    ChatFormatting.WHITE,
                    ChatFormatting.LIGHT_PURPLE,
                    ChatFormatting.DARK_PURPLE,
                    ChatFormatting.LIGHT_PURPLE));
    public static final DeferredItem<Item> TESSERACT_COMPONENT_MATRIX = ITEMS.register("tesseract_component_matrix",
            () -> new AnimatedNameItem(new Item.Properties(),
                    ChatFormatting.WHITE,
                    ChatFormatting.AQUA,
                    ChatFormatting.DARK_AQUA,
                    ChatFormatting.AQUA));
    public static final DeferredItem<Item> EVENT_HORIZON_COMPONENT_MATRIX = ITEMS.register("event_horizon_component_matrix",
            () -> new AnimatedNameItem(new Item.Properties(),
                    ChatFormatting.WHITE,
                    ChatFormatting.BLUE,
                    ChatFormatting.DARK_BLUE,
                    ChatFormatting.BLUE));
    public static final DeferredItem<Item> COSMIC_STRING_COMPONENT_MATRIX = ITEMS.register("cosmic_string_component_matrix",
            () -> new AnimatedNameItem(new Item.Properties(),
                    ChatFormatting.WHITE,
                    ChatFormatting.GREEN,
                    ChatFormatting.DARK_GREEN,
                    ChatFormatting.GREEN));
    public static final DeferredItem<Item> PRINTED_DIMENSIONAL_PROCESSOR = ITEMS.register("printed_dimensional_processor",
            () -> new AnimatedNameItem(new Item.Properties(),
                    ChatFormatting.WHITE,
                    ChatFormatting.GRAY,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.BLACK,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.GRAY));
    public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_INGOT = ITEMS.register("white_dwarf_fragment_ingot",
            () -> new AnimatedNameItem(new Item.Properties(),
                    ChatFormatting.WHITE,
                    ChatFormatting.GRAY,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.GRAY));
     public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_ROD = ITEMS.register("white_dwarf_fragment_rod",
             () -> new AnimatedNameItem(new Item.Properties(),
                     ChatFormatting.WHITE,
                     ChatFormatting.GRAY,
                     ChatFormatting.DARK_GRAY,
                     ChatFormatting.GRAY));
     public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_NUGGET = ITEMS.register("white_dwarf_fragment_nugget",
             () -> new AnimatedNameItem(new Item.Properties(),
                     ChatFormatting.WHITE,
                     ChatFormatting.GRAY,
                     ChatFormatting.DARK_GRAY,
                     ChatFormatting.GRAY));
     public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_DUST = ITEMS.register("white_dwarf_fragment_dust",
             () -> new AnimatedNameItem(new Item.Properties(),
                     ChatFormatting.WHITE,
                     ChatFormatting.GRAY,
                     ChatFormatting.DARK_GRAY,
                     ChatFormatting.GRAY));
     public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_INGOT = ITEMS.register("neutron_star_fragment_ingot",
             () -> new AnimatedNameItem(new Item.Properties(),
                     ChatFormatting.WHITE,
                     ChatFormatting.BLUE,
                     ChatFormatting.DARK_BLUE,
                     ChatFormatting.AQUA));
     public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_ROD = ITEMS.register("neutron_star_fragment_rod",
             () -> new AnimatedNameItem(new Item.Properties(),
                     ChatFormatting.WHITE,
                     ChatFormatting.BLUE,
                     ChatFormatting.DARK_BLUE,
                     ChatFormatting.AQUA));
     public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_NUGGET = ITEMS.register("neutron_star_fragment_nugget",
             () -> new AnimatedNameItem(new Item.Properties(),
                     ChatFormatting.WHITE,
                     ChatFormatting.BLUE,
                     ChatFormatting.DARK_BLUE,
                     ChatFormatting.AQUA));
     public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_DUST = ITEMS.register("neutron_star_fragment_dust",
             () -> new AnimatedNameItem(new Item.Properties(),
                     ChatFormatting.WHITE,
                     ChatFormatting.BLUE,
                     ChatFormatting.DARK_BLUE,
                     ChatFormatting.AQUA));
     public static final DeferredItem<Item> PULSAR_FRAGMENT_INGOT = ITEMS.register("pulsar_fragment_ingot",
             () -> new AnimatedNameItem(new Item.Properties(),
                     ChatFormatting.WHITE,
                     ChatFormatting.GREEN,
                     ChatFormatting.DARK_GREEN));
     public static final DeferredItem<Item> PULSAR_FRAGMENT_NUGGET = ITEMS.register("pulsar_fragment_nugget",
             () -> new AnimatedNameItem(new Item.Properties(),
                     ChatFormatting.WHITE,
                     ChatFormatting.GREEN,
                     ChatFormatting.DARK_GREEN));
     public static final DeferredItem<Item> PULSAR_FRAGMENT_DUST = ITEMS.register("pulsar_fragment_dust",
             () -> new AnimatedNameItem(new Item.Properties(),
                     ChatFormatting.WHITE,
                     ChatFormatting.GREEN,
                     ChatFormatting.DARK_GREEN));

    // infinite items cells
    // Infinite Cells have been moved to ModCells.java


    // Tools and Armor have been moved to ModTools.java and ModArmor.java


     public static final DeferredItem<Item> ENTROPY_ASSEMBLER_CORE_CASING = ITEMS.register("entropy_assembler_core_casing",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPY_ASSEMBLER_CORE_CASING.get(), new Item.Properties(),
                     ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.RED));

     // entropy_singularity_casing: variacoes de cinza e preto
     public static final DeferredItem<Item> ENTROPY_SINGULARITY_CASING = ITEMS.register("entropy_singularity_casing",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get(), new Item.Properties(),
                     ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY, ChatFormatting.BLACK, ChatFormatting.DARK_GRAY, ChatFormatting.GRAY));



     // entropy_computer_condensation_matrix: variacoes de vermelho,ciano e rosa
     public static final DeferredItem<Item> ENTROPY_COMPUTER_CONDENSATION_MATRIX = ITEMS.register("entropy_computer_condensation_matrix",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPY_COMPUTER_CONDENSATION_MATRIX.get(), new Item.Properties(),
                     ChatFormatting.RED, ChatFormatting.DARK_RED, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.LIGHT_PURPLE));

     public static final DeferredItem<Item> ENTROPIC_ASSEMBLER_MATRIX = ITEMS.register("entropic_assembler_matrix",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPIC_ASSEMBLER_MATRIX.get(), new Item.Properties(),
                     ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.RED, ChatFormatting.GOLD));

     public static final DeferredItem<Item> ENTROPIC_CONVERGENCE_ENGINE = ITEMS.register("entropic_convergence_engine",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPIC_CONVERGENCE_ENGINE.get(), new Item.Properties(),
                     ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.RED));

     public static final DeferredItem<Item> ENTROPIC_ASSEMBLER_CASING = ITEMS.register("entropic_assembler_casing",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPIC_ASSEMBLER_CASING.get(), new Item.Properties(),
                     ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.RED, ChatFormatting.GOLD));

     public static final DeferredItem<Item> ENTROPIC_CONVERGENCE_CASING = ITEMS.register("entropic_convergence_casing",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.ENTROPIC_CONVERGENCE_CASING.get(), new Item.Properties(),
                     ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.RED));



     // ═══════════════════════════════════════════════════════════
     //  QUANTUM MATTER FABRICATOR — Block Items
     // ═══════════════════════════════════════════════════════════

     public static final DeferredItem<Item> QUANTUM_ENTROPY_CASING = ITEMS.register("quantum_entropy_casing",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_ENTROPY_CASING.get(), new Item.Properties(),
                     ChatFormatting.WHITE, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE, ChatFormatting.DARK_AQUA, ChatFormatting.AQUA));

     public static final DeferredItem<Item> QUANTUM_HYPER_MECHANICAL_CASING = ITEMS.register("quantum_hyper_mechanical_casing",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get(), new Item.Properties(),
                     ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_AQUA));

     public static final DeferredItem<Item> QUANTUM_MATTER_FABRICATOR_CONTROLLER = ITEMS.register("quantum_matter_fabricator_controller",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get(), new Item.Properties(),
                     ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE));

     public static final DeferredItem<Item> QUANTUM_SLICER_CONTROLLER = ITEMS.register("quantum_slicer_controller",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get(), new Item.Properties(),
                     ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_AQUA));

     public static final DeferredItem<Item> QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER = ITEMS.register("quantum_processor_assembler_controller",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get(), new Item.Properties(),
                     ChatFormatting.LIGHT_PURPLE, ChatFormatting.AQUA, ChatFormatting.BLUE, ChatFormatting.WHITE));

     public static final DeferredItem<Item> QUANTUM_CRYOFORGE_CONTROLLER = ITEMS.register("quantum_cryoforge_controller",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get(), new Item.Properties(),
                     ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_AQUA));

     public static final DeferredItem<Item> QUANTUM_PATTERN_HATCH = ITEMS.register("quantum_pattern_hatch",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get(), new Item.Properties(),
                     ChatFormatting.AQUA, ChatFormatting.WHITE, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE));

     public static final DeferredItem<Item> QUANTUM_PATTERN_PROVIDER_PART = registerPartItem(
             "quantum_pattern_provider_part",
             QuantumPatternProviderPart.class,
             QuantumPatternProviderPart::new);

     // ═══════════════════════════════════════════════════════════
     //  STELLAR NEXUS — Block Items
     // ═══════════════════════════════════════════════════════════

     public static final DeferredItem<Item> STELLAR_NEXUS_CONTROLLER = ITEMS.register("stellar_nexus_controller",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get(), new Item.Properties(),
                     ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.WHITE, ChatFormatting.AQUA, ChatFormatting.BLUE, ChatFormatting.LIGHT_PURPLE));

     public static final DeferredItem<Item> ME_MASSIVE_OUTPUT_HATCH = ITEMS.register("me_massive_output_hatch",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get(), new Item.Properties(),
                     ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.WHITE));

     public static final DeferredItem<Item> ME_MASSIVE_FLUID_HATCH = ITEMS.register("me_massive_fluid_hatch",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get(), new Item.Properties(),
                     ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE));

     public static final DeferredItem<Item> ME_MASSIVE_INPUT_HATCH = ITEMS.register("me_massive_input_hatch",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get(), new Item.Properties(),
                     ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.WHITE));

     public static final DeferredItem<Item> AE_ENERGY_INPUT_HATCH = ITEMS.register("ae_energy_input_hatch",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get(), new Item.Properties(),
                     ChatFormatting.YELLOW, ChatFormatting.GOLD, ChatFormatting.WHITE));

     public static final DeferredItem<Item> UFO_ENERGY_CELL = ITEMS.register("ufo_energy_cell",
             () -> new EnergyCellBlockItem(com.raishxn.ufo.block.ModBlocks.UFO_ENERGY_CELL.get(), new Item.Properties()));

      public static final DeferredItem<Item> QUANTUM_ENERGY_CELL = ITEMS.register("quantum_energy_cell",
             () -> new BlockItem(com.raishxn.ufo.block.ModBlocks.QUANTUM_ENERGY_CELL.get(), new Item.Properties()));



     public static final DeferredItem<Item> STELLAR_FIELD_GENERATOR_T1 = ITEMS.register("stellar_field_generator_t1",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get(), new Item.Properties(),
                     ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.YELLOW));

     public static final DeferredItem<Item> STELLAR_FIELD_GENERATOR_T2 = ITEMS.register("stellar_field_generator_t2",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get(), new Item.Properties(),
                     ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.WHITE));

     public static final DeferredItem<Item> STELLAR_FIELD_GENERATOR_T3 = ITEMS.register("stellar_field_generator_t3",
             () -> new AnimatedNameBlockItem(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get(), new Item.Properties(),
                     ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE, ChatFormatting.GOLD, ChatFormatting.YELLOW));

    public static final DeferredItem<Item> NEUTRON_STAR_FRAGMENT_BUCKET = ITEMS.register("neutron_star_fragment_bucket",
            () -> new BucketItem(ModFluids.SOURCE_NEUTRON_STAR_FRAGMENT_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> PULSAR_FRAGMENT_BUCKET = ITEMS.register("pulsar_fragment_bucket",
             () -> new BucketItem(ModFluids.SOURCE_PULSAR_FRAGMENT_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> WHITE_DWARF_FRAGMENT_BUCKET = ITEMS.register("white_dwarf_fragment_bucket",
             () -> new BucketItem(ModFluids.SOURCE_WHITE_DWARF_FRAGMENT_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> LIQUID_STARLIGHT_BUCKET = ITEMS.register("liquid_starlight_bucket",
             () -> new BucketItem(ModFluids.SOURCE_LIQUID_STARLIGHT_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> PRIMORDIAL_MATTER_BUCKET = ITEMS.register("primordial_matter_bucket",
             () -> new BucketItem(ModFluids.SOURCE_PRIMORDIAL_MATTER_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> RAW_STAR_MATTER_PLASMA_BUCKET = ITEMS.register("raw_star_matter_plasma_bucket",
             () -> new BucketItem(ModFluids.SOURCE_RAW_STAR_MATTER_PLASMA_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> TRANSCENDING_MATTER_BUCKET = ITEMS.register("transcending_matter_bucket",
             () -> new BucketItem(ModFluids.SOURCE_TRANSCENDING_MATTER_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> UU_MATTER_BUCKET = ITEMS.register("uu_matter_bucket",
             () -> new BucketItem(ModFluids.SOURCE_UU_MATTER_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

     public static final DeferredItem<Item> UU_AMPLIFIER_BUCKET = ITEMS.register("uu_amplifier_bucket",
             () -> new BucketItem(ModFluids.SOURCE_UU_AMPLIFIER_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> GELID_CRYOTHEUM_BUCKET = ITEMS.register("gelid_cryotheum_bucket",
            () -> new BucketItem(ModFluids.SOURCE_GELID_CRYOTHEUM.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> STABLE_COOLANT_BUCKET = ITEMS.register("stable_coolant_bucket",
            () -> new BucketItem(ModFluids.SOURCE_STABLE_COOLANT.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> TEMPORAL_FLUID_BUCKET = ITEMS.register("temporal_fluid_bucket",
            () -> new BucketItem(ModFluids.SOURCE_TEMPORAL_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> SPATIAL_FLUID_BUCKET = ITEMS.register("spatial_fluid_bucket",
            () -> new BucketItem(ModFluids.SOURCE_SPATIAL_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));


    public static final DeferredItem<Item> QUANTUM_ANOMALY = ITEMS.register("quantum_anomaly",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .fireResistant()));

    public static final DeferredItem<Item> NUCLEAR_STAR = ITEMS.register("nuclear_star",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> SCAR = ITEMS.register("scar",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)));

    public static final DeferredItem<Item> SCRAP = ITEMS.register("scrap",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)));

    public static final DeferredItem<Item> SCRAP_BOX = ITEMS.register("scrap_box",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> STRUCTURE_SCANNER = ITEMS.register("structure_scanner",
            () -> new StructureScannerItem(new Item.Properties()
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> APOCALYPSE_TYPE_A_SPAWN_EGG = ITEMS.register("apocalypse_type_a_spawn_egg",
            () -> new SpawnEggItem(ModEntities.APOCALYPSE_TYPE_A.get(), 0x1a1025, 0x8be9ff, new Item.Properties()));

    // ---------- Esferas / componentes avançados ----------
    public static final DeferredItem<Item> NEUTRONIUM_SPHERE = ITEMS.register("neutronium_sphere",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> ENRICHED_NEUTRONIUM_SPHERE = ITEMS.register("enriched_neutronium_sphere",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> CHARGED_ENRICHED_NEUTRONIUM_SPHERE = ITEMS.register("charged_enriched_neutronium_sphere",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    // ---------- Stages de matter ----------
    public static final DeferredItem<Item> PROTO_MATTER = ITEMS.register("proto_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> CORPOREAL_MATTER = ITEMS.register("corporeal_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> WHITE_DWARF_MATTER = ITEMS.register("white_dwarf_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> NEUTRON_STAR_MATTER = ITEMS.register("neutron_star_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> PULSAR_MATTER = ITEMS.register("pulsar_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> DARK_MATTER = ITEMS.register("dark_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    // ---------- Estrutural / utilitário ----------
    public static final DeferredItem<Item> OBSIDIAN_MATRIX = ITEMS.register("obsidian_matrix",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> UU_MATTER_CRYSTAL = ITEMS.register("uu_matter_crystal",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.RARE)));

    public static final DeferredItem<Item> DUST_CRYOTHEUM = ITEMS.register("dust_cryotheum",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> DUST_BLIZZ = ITEMS.register("dust_blizz",
            () -> new Item(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> UNSTABLE_WHITE_HOLE_MATTER = ITEMS.register("unstable_white_hole_matter",
            () -> new Item(new Item.Properties()
                    .stacksTo(8)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));
    public static final DeferredItem<Item> AETHER_CONTAINMENT_CAPSULE = ITEMS.register("aether_containment_capsule",
            () -> new AetherContainmentCapsuleItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> SAFE_CONTAINMENT_MATTER = ITEMS.register("safe_containment_matter",
            () -> new SafeContainmentMatterItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    private static Item.Properties catalystProps(Rarity rarity) {
        return new Item.Properties().stacksTo(1).rarity(rarity);
    }

    // --- 3) MATTERFLOW CATALYST (Eficiência Energética) [cite: 67] ---

    public static final DeferredItem<Item> MATTERFLOW_CATALYST_T1 = ITEMS.register("matterflow_catalyst_t1",
            () -> new BaseCatalystItem(new Item.Properties(), "matterflow", 1));
    public static final DeferredItem<Item> MATTERFLOW_CATALYST_T2 = ITEMS.register("matterflow_catalyst_t2",
            () -> new BaseCatalystItem(new Item.Properties(), "matterflow", 2));
    public static final DeferredItem<Item> MATTERFLOW_CATALYST_T3 = ITEMS.register("matterflow_catalyst_t3",
            () -> new BaseCatalystItem(new Item.Properties(), "matterflow", 3));

    public static final DeferredItem<Item> CHRONO_CATALYST_T1 = ITEMS.register("chrono_catalyst_t1",
            () -> new BaseCatalystItem(new Item.Properties(), "chrono", 1));
    public static final DeferredItem<Item> CHRONO_CATALYST_T2 = ITEMS.register("chrono_catalyst_t2",
            () -> new BaseCatalystItem(new Item.Properties(), "chrono", 2));
    public static final DeferredItem<Item> CHRONO_CATALYST_T3 = ITEMS.register("chrono_catalyst_t3",
            () -> new BaseCatalystItem(new Item.Properties(), "chrono", 3));

    public static final DeferredItem<Item> OVERFLUX_CATALYST_T1 = ITEMS.register("overflux_catalyst_t1",
            () -> new BaseCatalystItem(new Item.Properties(), "overflux", 1));
    public static final DeferredItem<Item> OVERFLUX_CATALYST_T2 = ITEMS.register("overflux_catalyst_t2",
            () -> new BaseCatalystItem(new Item.Properties(), "overflux", 2));
    public static final DeferredItem<Item> OVERFLUX_CATALYST_T3 = ITEMS.register("overflux_catalyst_t3",
            () -> new BaseCatalystItem(new Item.Properties(), "overflux", 3));

    public static final DeferredItem<Item> QUANTUM_CATALYST_T1 = ITEMS.register("quantum_catalyst_t1",
            () -> new BaseCatalystItem(new Item.Properties(), "quantum", 1));
    public static final DeferredItem<Item> QUANTUM_CATALYST_T2 = ITEMS.register("quantum_catalyst_t2",
            () -> new BaseCatalystItem(new Item.Properties(), "quantum", 2));
    public static final DeferredItem<Item> QUANTUM_CATALYST_T3 = ITEMS.register("quantum_catalyst_t3",
            () -> new BaseCatalystItem(new Item.Properties(), "quantum", 3));

    // E o criativo:
    public static final DeferredItem<Item> DIMENSIONAL_CATALYST = ITEMS.register("dimensional_catalyst",
            () -> new DimensionalCatalystItem(new Item.Properties()));

    // Thermal Resistor suit has been moved to ModArmor.java

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        ModCells.register(eventBus);
        ModTools.register(eventBus);
        ModArmor.register(eventBus);
        // Add new grouped registries as we create them
    }

    private static <T extends IPart> DeferredItem<Item> registerPartItem(
            String id,
            Class<T> partClass,
            java.util.function.Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return ITEMS.register(id, () -> new PartItem<>(new Item.Properties(), partClass, factory));
    }
}
