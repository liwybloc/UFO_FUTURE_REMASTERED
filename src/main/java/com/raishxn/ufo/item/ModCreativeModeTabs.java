package com.raishxn.ufo.item;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.block.MultiblockBlocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, UfoMod.MOD_ID);

    public static final Supplier<CreativeModeTab> UFO_ITEMS_TAB = CREATIVE_MODE_TAB.register("ufo_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WHITE_DWARF_FRAGMENT_INGOT.get()))
                    .title(Component.translatable("creativetab.ufomod.ufo_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.PHASE_SHIFT_COMPONENT_MATRIX.get());
                        output.accept(ModItems.HYPER_DENSE_COMPONENT_MATRIX.get());
                        output.accept(ModItems.TESSERACT_COMPONENT_MATRIX.get());
                        output.accept(ModItems.EVENT_HORIZON_COMPONENT_MATRIX.get());
                        output.accept(ModItems.COSMIC_STRING_COMPONENT_MATRIX.get());

                        output.accept(ModArmor.UFO_HELMET.get());
                        output.accept(ModArmor.UFO_CHESTPLATE.get());
                        output.accept(ModArmor.UFO_LEGGINGS.get());
                        output.accept(ModArmor.UFO_BOOTS.get());
                        output.accept(ModArmor.ASTRAL_NEXUS_HELMET.get());
                        output.accept(ModArmor.ASTRAL_NEXUS_CHESTPLATE.get());
                        output.accept(ModArmor.ASTRAL_NEXUS_LEGGINGS.get());
                        output.accept(ModArmor.ASTRAL_NEXUS_BOOTS.get());
                        output.accept(ModTools.REALITY_RIPPER.get());
                        output.accept(ModTools.UFO_STAFF);
                        output.accept(ModItems.DIMENSIONAL_PROCESSOR_PRESS.get());
                        output.accept(ModItems.DIMENSIONAL_PROCESSOR.get());
                        output.accept(ModItems.PRINTED_DIMENSIONAL_PROCESSOR.get());

                        output.accept(ModItems.WHITE_DWARF_FRAGMENT_INGOT.get());
                        output.accept(ModItems.WHITE_DWARF_FRAGMENT_DUST.get());
                        output.accept(ModItems.WHITE_DWARF_FRAGMENT_NUGGET.get());
                        output.accept(ModItems.WHITE_DWARF_FRAGMENT_ROD.get());
                        output.accept(ModItems.WHITE_DWARF_FRAGMENT_BUCKET.get());

                        output.accept(ModItems.NEUTRON_STAR_FRAGMENT_INGOT.get());
                        output.accept(ModItems.NEUTRON_STAR_FRAGMENT_DUST.get());
                        output.accept(ModItems.NEUTRON_STAR_FRAGMENT_NUGGET.get());
                        output.accept(ModItems.NEUTRON_STAR_FRAGMENT_ROD.get());
                        output.accept(ModItems.NEUTRON_STAR_FRAGMENT_BUCKET.get());

                        output.accept(ModItems.PULSAR_FRAGMENT_INGOT.get());
                        output.accept(ModItems.PULSAR_FRAGMENT_DUST.get());
                        output.accept(ModItems.PULSAR_FRAGMENT_NUGGET.get());
                        output.accept(ModItems.PULSAR_FRAGMENT_BUCKET.get());


                        output.accept(ModItems.CHRONO_CATALYST_T1.get());
                        output.accept(ModItems.CHRONO_CATALYST_T2.get());
                        output.accept(ModItems.CHRONO_CATALYST_T3.get());
                        output.accept(ModItems.MATTERFLOW_CATALYST_T1.get());
                        output.accept(ModItems.MATTERFLOW_CATALYST_T2.get());
                        output.accept(ModItems.MATTERFLOW_CATALYST_T3.get());
                        output.accept(ModItems.OVERFLUX_CATALYST_T1.get());
                        output.accept(ModItems.OVERFLUX_CATALYST_T2.get());
                        output.accept(ModItems.OVERFLUX_CATALYST_T3.get());
                        output.accept(ModItems.QUANTUM_CATALYST_T1.get());
                        output.accept(ModItems.QUANTUM_CATALYST_T2.get());
                        output.accept(ModItems.QUANTUM_CATALYST_T3.get());
                        output.accept(ModItems.DIMENSIONAL_CATALYST.get());

                        output.accept(ModArmor.THERMAL_RESISTOR_PLATING.get());
                        output.accept(ModArmor.THERMAL_RESISTOR_BOOTS.get());
                        output.accept(ModArmor.THERMAL_RESISTOR_CHEST.get());
                        output.accept(ModArmor.THERMAL_RESISTOR_MASK.get());
                        output.accept(ModArmor.THERMAL_RESISTOR_PANTS.get());

                        output.accept(ModItems.AETHER_CONTAINMENT_CAPSULE.get());
                        output.accept(ModItems.SAFE_CONTAINMENT_MATTER.get());
                        output.accept(ModItems.PROTO_MATTER.get());
                        output.accept(ModItems.CORPOREAL_MATTER.get());
                        output.accept(ModItems.WHITE_DWARF_MATTER.get());
                        output.accept(ModItems.NEUTRON_STAR_MATTER.get());
                        output.accept(ModItems.PULSAR_MATTER.get());
                        output.accept(ModItems.UU_MATTER_CRYSTAL.get());
                        output.accept(ModItems.DARK_MATTER.get());
                        output.accept(ModItems.UNSTABLE_WHITE_HOLE_MATTER.get());
                        output.accept(ModItems.QUANTUM_ANOMALY.get());
                        output.accept(ModItems.NUCLEAR_STAR.get());
                        output.accept(ModItems.NEUTRONIUM_SPHERE.get());
                        output.accept(ModItems.ENRICHED_NEUTRONIUM_SPHERE.get());
                        output.accept(ModItems.CHARGED_ENRICHED_NEUTRONIUM_SPHERE.get());
                        output.accept(ModItems.SCAR.get());
                        output.accept(ModItems.SCRAP.get());
                        output.accept(ModItems.SCRAP_BOX.get());
                        output.accept(ModItems.STRUCTURE_SCANNER.get());
                        output.accept(ModItems.DUST_CRYOTHEUM.get());
                        output.accept(ModItems.DUST_BLIZZ.get());
                        output.accept(ModItems.OBSIDIAN_MATRIX.get());

                        output.accept(ModCells.INFINITY_COBBLED_DEEPSLATE_CELL.get());
                        output.accept(ModCells.INFINITY_END_STONE_CELL.get());
                        output.accept(ModCells.INFINITY_NETHERRACK_CELL.get());
                        output.accept(ModCells.INFINITY_SAND_CELL.get());
                        output.accept(ModCells.INFINITY_LAVA_CELL.get());
                        output.accept(ModCells.INFINITY_SKY_STONE_CELL.get());

                        output.accept(ModCells.INFINITY_COBBLED_DEEPSLATE_CELL.get());
                        output.accept(ModCells.INFINITY_END_STONE_CELL.get());
                        output.accept(ModCells.INFINITY_NETHERRACK_CELL.get());
                        output.accept(ModCells.INFINITY_SAND_CELL.get());
                        output.accept(ModCells.INFINITY_LAVA_CELL.get());
                        output.accept(ModCells.INFINITY_SKY_STONE_CELL.get());

                        output.accept(ModCells.INFINITY_WATER_CELL.get());
                        output.accept(ModCells.INFINITY_COBBLESTONE_CELL.get());
                        output.accept(ModCells.INFINITY_OBSIDIAN_CELL.get());
                        output.accept(ModCells.INFINITY_GRAVEL_CELL.get());
                        output.accept(ModCells.INFINITY_OAK_LOG_CELL.get());
                        output.accept(ModCells.INFINITY_GLASS_CELL.get());
                        output.accept(ModCells.INFINITY_AMETHYST_SHARD_CELL.get());
                        output.accept(ModCells.INFINITY_WHITE_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_ORANGE_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_MAGENTA_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_LIGHT_BLUE_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_YELLOW_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_LIME_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_PINK_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_GRAY_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_LIGHT_GRAY_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_CYAN_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_PURPLE_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_BLUE_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_BROWN_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_GREEN_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_RED_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_BLACK_DYE_CELL.get());
                        output.accept(ModCells.INFINITY_GENESIS_CELL.get());


                        output.accept(ModCellItems.WHITE_DWARF_ITEM_CELL_HOUSING.get());
                        output.accept(ModCellItems.NEUTRON_FLUID_CELL_HOUSING.get());

                        output.accept(ModCellItems.ITEM_CELL_40M.get());
                        output.accept(ModCellItems.ITEM_CELL_100M.get());
                        output.accept(ModCellItems.ITEM_CELL_250M.get());
                        output.accept(ModCellItems.ITEM_CELL_750M.get());
                        output.accept(ModCellItems.ITEM_CELL_SINGULARITY.get());

                        output.accept(ModCellItems.FLUID_CELL_40M.get());
                        output.accept(ModCellItems.FLUID_CELL_100M.get());
                        output.accept(ModCellItems.FLUID_CELL_250M.get());
                        output.accept(ModCellItems.FLUID_CELL_750M.get());
                        output.accept(ModCellItems.FLUID_CELL_SINGULARITY.get());

                    }).build());

    public static final Supplier<CreativeModeTab> UFO_BLOCKS_TAB = CREATIVE_MODE_TAB.register("ufo_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.WHITE_DWARF_FRAGMENT_BLOCK.get()))
                    .title(Component.translatable("creativetab.ufomod.ufo_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.WHITE_DWARF_FRAGMENT_BLOCK.get());
                        output.accept(ModBlocks.GRAVITON_PLATED_CASING.get());
                        output.accept(ModBlocks.QUANTUM_LATTICE_FRAME.get());
                        output.accept(ModBlocks.NEUTRON_STAR_FRAGMENT_BLOCK.get());
                        output.accept(ModBlocks.PULSAR_FRAGMENT_BLOCK.get());
                        output.accept(ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get());
                        output.accept(ModBlocks.UFO_ENERGY_CELL.get());
                        output.accept(createQuantumEnergyCellVariant(false));
                        output.accept(createQuantumEnergyCellVariant(true));
                        ModBlocks.CRAFTING_STORAGE_BLOCKS.values().forEach(block -> output.accept(block.get()));
                        ModBlocks.CO_PROCESSOR_BLOCKS.values().forEach(block -> output.accept(block.get()));

                        output.accept(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get());
                        output.accept(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get());
                        output.accept(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get());
                        output.accept(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get());
                        output.accept(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get());
                        output.accept(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get());
                        output.accept(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get());
                        output.accept(ModItems.QUANTUM_PATTERN_PROVIDER_PART.get());
                        output.accept(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get());
                        output.accept(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get());
                        output.accept(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get());
                        output.accept(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get());
                        output.accept(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get());
                        output.accept(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get());
                        output.accept(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get());

                        output.accept(MultiblockBlocks.ENTROPY_ASSEMBLER_CORE_CASING.get());
                        output.accept(MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get());
                        output.accept(MultiblockBlocks.ENTROPIC_ASSEMBLER_CASING.get());
                        output.accept(MultiblockBlocks.ENTROPIC_CONVERGENCE_CASING.get());

                    }).build());




    public static void register(final IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }

    private static ItemStack createQuantumEnergyCellVariant(final boolean charged) {
        final ItemStack stack = new ItemStack(ModBlocks.QUANTUM_ENERGY_CELL.get());
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(charged
                ? "Quantum Energy Cell (Charged)"
                : "Quantum Energy Cell (Discharged)"));
        final CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean("ufoQuantumEnergyCellChargedPreview", charged);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }
}
