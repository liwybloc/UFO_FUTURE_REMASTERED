package com.raishxn.ufo.datagen;

import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.block.MultiblockBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(final HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.WHITE_DWARF_FRAGMENT_BLOCK.get());
        this.dropSelf(ModBlocks.NEUTRON_STAR_FRAGMENT_BLOCK.get());
        this.dropSelf(ModBlocks.PULSAR_FRAGMENT_BLOCK.get());
        this.dropSelf(ModBlocks.GRAVITON_PLATED_CASING.get());
        this.dropSelf(ModBlocks.QUANTUM_LATTICE_FRAME.get());
        this.dropSelf(ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get());
        this.dropSelf(ModBlocks.UFO_ENERGY_CELL.get());
        this.dropSelf(ModBlocks.QUANTUM_ENERGY_CELL.get());
        this.dropSelf(MultiblockBlocks.ENTROPY_ASSEMBLER_CORE_CASING.get());
        this.dropSelf(MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get());
        this.dropSelf(MultiblockBlocks.ENTROPY_COMPUTER_CONDENSATION_MATRIX.get());
        this.dropSelf(MultiblockBlocks.ENTROPIC_ASSEMBLER_MATRIX.get());
        this.dropSelf(MultiblockBlocks.ENTROPIC_CONVERGENCE_ENGINE.get());
        this.dropSelf(MultiblockBlocks.ENTROPIC_ASSEMBLER_CASING.get());
        this.dropSelf(MultiblockBlocks.ENTROPIC_CONVERGENCE_CASING.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_ENTROPY_CASING.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get());
        this.dropSelf(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get());
        this.dropSelf(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get());
        this.dropSelf(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get());
        this.dropSelf(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get());
        this.dropSelf(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get());
        this.dropSelf(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get());
        this.dropSelf(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get());
        this.dropSelf(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get());
        this.dropSelf(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get());

        ModBlocks.CO_PROCESSOR_BLOCKS.values().forEach(blockRegistry -> {
            this.dropSelf(blockRegistry.get());
        });
        ModBlocks.CRAFTING_STORAGE_BLOCKS.values().forEach(blockRegistry -> {
            this.dropSelf(blockRegistry.get());
        });
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Stream.concat(
                ModBlocks.BLOCKS.getEntries().stream().map(Holder::value),
                Stream.concat(
                        Stream.concat(
                                ModBlocks.CO_PROCESSOR_BLOCKS.values().stream().map(Holder::value),
                                ModBlocks.CRAFTING_STORAGE_BLOCKS.values().stream().map(Holder::value)
                        ),
                        MultiblockBlocks.BLOCKS.getEntries().stream().map(Holder::value)
                )
        ).collect(Collectors.toSet());
    }
}
