package com.raishxn.ufo.datagen;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable final ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, UfoMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.QUANTUM_LATTICE_FRAME.get())
                .add(ModBlocks.GRAVITON_PLATED_CASING.get())
                .add(ModBlocks.WHITE_DWARF_FRAGMENT_BLOCK.get())
                .add(MultiblockBlocks.QUANTUM_ENTROPY_CASING.get())
                .add(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get())
                .add(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get())
                .add(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get())
                .add(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get())
                .add(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get())
                .add(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get())
                .add(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get())
                .add(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get())
                .add(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get())
                .add(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get())
                .add(MultiblockBlocks.ENTROPY_ASSEMBLER_CORE_CASING.get())
                .add(MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get())
                .add(MultiblockBlocks.ENTROPY_COMPUTER_CONDENSATION_MATRIX.get())
                .add(MultiblockBlocks.ENTROPIC_ASSEMBLER_CASING.get())
                .add(MultiblockBlocks.ENTROPIC_CONVERGENCE_CASING.get())
                .add(MultiblockBlocks.ENTROPIC_ASSEMBLER_MATRIX.get())
                .add(MultiblockBlocks.ENTROPIC_CONVERGENCE_ENGINE.get());


        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.QUANTUM_LATTICE_FRAME.get())
                .add(ModBlocks.GRAVITON_PLATED_CASING.get())
                .add(ModBlocks.WHITE_DWARF_FRAGMENT_BLOCK.get())
                .add(MultiblockBlocks.QUANTUM_ENTROPY_CASING.get())
                .add(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get())
                .add(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get())
                .add(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get())
                .add(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get())
                .add(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get())
                .add(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get())
                .add(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get())
                .add(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get())
                .add(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get())
                .add(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get())
                .add(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get())
                .add(MultiblockBlocks.ENTROPY_ASSEMBLER_CORE_CASING.get())
                .add(MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get())
                .add(MultiblockBlocks.ENTROPY_COMPUTER_CONDENSATION_MATRIX.get())
                .add(MultiblockBlocks.ENTROPIC_ASSEMBLER_CASING.get())
                .add(MultiblockBlocks.ENTROPIC_CONVERGENCE_CASING.get())
                .add(MultiblockBlocks.ENTROPIC_ASSEMBLER_MATRIX.get())
                .add(MultiblockBlocks.ENTROPIC_CONVERGENCE_ENGINE.get());


        tag(BlockTags.NEEDS_DIAMOND_TOOL);


        tag(ModTags.Blocks.NEEDS_UFO_TOOL)
                .addTag(BlockTags.NEEDS_IRON_TOOL)
                .addTag(BlockTags.NEEDS_DIAMOND_TOOL)
                .addOptionalTag(Identifier.parse("allthemodium:needs_allthemodium_tool"))
                .addOptionalTag(Identifier.parse("allthemodium:needs_vibranium_tool"))
                .addOptionalTag(Identifier.parse("allthemodium:needs_unobtainium_tool"));

        tag(ModTags.Blocks.INCORRECT_FOR_UFO_TOOL)
                .addTag(BlockTags.INCORRECT_FOR_IRON_TOOL)
                .addTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
                .remove(ModTags.Blocks.NEEDS_UFO_TOOL);
    }
}
