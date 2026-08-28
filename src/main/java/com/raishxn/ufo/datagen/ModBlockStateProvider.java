package com.raishxn.ufo.datagen;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.block.MultiblockBlocks; // Importa a nova classe
import com.raishxn.ufo.core.MegaCoProcessorTier;
import com.raishxn.ufo.core.MegaCraftingStorageTier;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public final class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(final PackOutput output, final ExistingFileHelper exFileHelper) {
        super(output, UfoMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.QUANTUM_LATTICE_FRAME);
        simpleBlockWithItem(ModBlocks.GRAVITON_PLATED_CASING);
        blockWithFluidTexture(ModBlocks.WHITE_DWARF_FRAGMENT_BLOCK, "white_dwarf_fragment");
        blockWithFluidTexture(ModBlocks.PULSAR_FRAGMENT_BLOCK, "pulsar_fragment");
        blockWithFluidTexture(ModBlocks.NEUTRON_STAR_FRAGMENT_BLOCK, "neutron_star_fragment");

        multiblockCube(MultiblockBlocks.ENTROPY_ASSEMBLER_CORE_CASING);
        multiblockCube(MultiblockBlocks.ENTROPY_SINGULARITY_CASING);
        craftingLikeCube(MultiblockBlocks.ENTROPY_COMPUTER_CONDENSATION_MATRIX, "entropy_computer_condensation_matrix");
        multiblockCubeWithTexture(MultiblockBlocks.ENTROPIC_ASSEMBLER_MATRIX, "entropy_assembler_core_casing");
        craftingLikeCube(MultiblockBlocks.ENTROPIC_CONVERGENCE_ENGINE, "entropy_computer_condensation_matrix");
        entropicMachineCube(MultiblockBlocks.ENTROPIC_ASSEMBLER_CASING, "entropic_assembler_casing");
        entropicMachineCube(MultiblockBlocks.ENTROPIC_CONVERGENCE_CASING, "entropic_convergence_casing");
        multiblockCubeWithTexture(MultiblockBlocks.QUANTUM_ENTROPY_CASING, "quantum_hyper_mechanical_casing");
        multiblockCube(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING);
        qmfControllerBlock(MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER);
        controllerWithBase(MultiblockBlocks.QUANTUM_SLICER_CONTROLLER, "quantum_hyper_mechanical_casing");
        controllerWithBase(MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER, "quantum_hyper_mechanical_casing");
        controllerWithBase(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER, "quantum_hyper_mechanical_casing");

        stellarNexusControllerBlock(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER);
        hatchWithOverlay(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH, "me_massive_output_hatch_overlay");
        hatchWithOverlay(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH, "me_massive_fluid_hatch_overlay");
        hatchWithOverlay(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH, "me_massive_input_hatch_overlay");
        hatchWithOverlay(MultiblockBlocks.AE_ENERGY_INPUT_HATCH, "ae_energy_input_hatch_overlay");
        multiblockCube(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1);
        multiblockCube(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2);
        multiblockCube(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3);


        for (final var tier : MegaCraftingStorageTier.values()) {
            final var block = ModBlocks.CRAFTING_STORAGE_BLOCKS.get(tier);
            final String registryName = block.getId().getPath();
            final ModelFile unformedModel = models().cubeAll(registryName, modLoc("block/" + registryName));
            final ModelFile formedModel = models().getBuilder(registryName + "_formed");

            getVariantBuilder(block.get())
                    .partialState().with(AbstractCraftingUnitBlock.FORMED, false)
                    .setModels(new ConfiguredModel(unformedModel))
                    .partialState().with(AbstractCraftingUnitBlock.FORMED, true)
                    .setModels(new ConfiguredModel(formedModel));

            simpleBlockItem(block.get(), unformedModel);
        }

        for (final var tier : MegaCoProcessorTier.values()) {
            final var block = ModBlocks.CO_PROCESSOR_BLOCKS.get(tier);
            final String registryName = block.getId().getPath();
            final ModelFile unformedModel = models().cubeAll(registryName, modLoc("block/" + registryName));
            final ModelFile formedModel = models().getBuilder(registryName + "_formed");

            getVariantBuilder(block.get())
                    .partialState().with(AbstractCraftingUnitBlock.FORMED, false)
                    .setModels(new ConfiguredModel(unformedModel))
                    .partialState().with(AbstractCraftingUnitBlock.FORMED, true)
                    .setModels(new ConfiguredModel(formedModel));

            simpleBlockItem(block.get(), unformedModel);
        }
    }

    private void simpleBlockWithItem(final DeferredBlock<Block> blockHolder) {
        final String registryName = blockHolder.getId().getPath();
        final ModelFile model = models().cubeAll(registryName, modLoc("block/" + registryName));
        simpleBlock(blockHolder.get(), model);
        simpleBlockItem(blockHolder.get(), model);
    }


    /**
     * Registra um bloco de multiblock que é um cubo simples.
     */
    private void multiblockCube(final DeferredBlock<? extends Block> block) {
        final String name = block.getId().getPath();
        final Identifier texture = modLoc("block/multiblock/" + name);
        simpleBlock(block.get(), models().cubeAll(name, texture));
        simpleBlockItem(block.get(), models().getExistingFile(modLoc("block/" + name)));
    }

    private void multiblockCubeWithTexture(final DeferredBlock<? extends Block> block, final String textureName) {
        final String name = block.getId().getPath();
        final Identifier texture = modLoc("block/multiblock/" + textureName);
        simpleBlock(block.get(), models().cubeAll(name, texture));
        simpleBlockItem(block.get(), models().getExistingFile(modLoc("block/" + name)));
    }

    private void craftingLikeCube(final DeferredBlock<? extends Block> block, final String textureName) {
        final String name = block.getId().getPath();
        final Identifier texture = modLoc("block/multiblock/" + textureName);
        final ModelFile model = models().cubeAll(name, texture);

        getVariantBuilder(block.get())
                .partialState().with(AbstractCraftingUnitBlock.FORMED, false)
                .setModels(new ConfiguredModel(model))
                .partialState().with(AbstractCraftingUnitBlock.FORMED, true)
                .setModels(new ConfiguredModel(model));

        simpleBlockItem(block.get(), model);
    }

    private void entropicMachineCube(final DeferredBlock<? extends Block> block, final String textureName) {
        final String name = block.getId().getPath();
        final Identifier texture = modLoc("block/multiblock/" + textureName);
        final ModelFile model = models().cubeAll(name, texture);

        getVariantBuilder(block.get()).forAllStates(state -> {
            final boolean formed = getBooleanPropertyByName(state, "formed");
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .build();
        });

        simpleBlockItem(block.get(), model);
    }

    private boolean getBooleanPropertyByName(final BlockState state, final String propertyName) {
        for (final var property : state.getProperties()) {
            if (property.getName().equals(propertyName) && property instanceof final BooleanProperty booleanProperty) {
                return state.getValue(booleanProperty);
            }
        }
        return false;
    }

    /**
     * Directional multiblock cube — same texture on all faces, rotated by FACING.
     */
    private void directionalMultiblockCube(final DeferredBlock<? extends Block> block) {
        final String name = block.getId().getPath();
        final Identifier texture = modLoc("block/multiblock/" + name);
        final ModelFile model = models().cubeAll(name, texture);

        getVariantBuilder(block.get()).forAllStates(state -> {
            final Direction dir = state.getValue(DirectionalBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), model);
    }

    private void blockWithFluidTexture(final DeferredBlock<Block> block, final String fluidTextureName) {
        final String name = block.getId().getPath();
        final Identifier texture = modLoc("block/fluid/" + fluidTextureName);
        final ModelFile model = models().cubeAll(name, texture);
        simpleBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }
    private void multiblockComponentBlock(final DeferredBlock<Block> block) {
        final String name = block.getId().getPath();
        final Identifier baseTexture = modLoc("block/multiblock/entropy_assembler_core_casing_base");
        final Identifier overlayTexture = modLoc("block/multiblock/" + name);

        final ModelFile modelFile = models().withExistingParent(name, "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayTexture)
                .element()
                .from(0, 0, 0).to(16, 16, 16)
                .allFaces((direction, faceBuilder) -> faceBuilder.texture("#base").cullface(direction))
                .end()
                .element()
                .from(0, 0, 0).to(16, 16, 16)
                .face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end()
                .end();

        getVariantBuilder(block.get()).forAllStates(state -> {
            final Direction dir = state.getValue(DirectionalBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), modelFile);
    }

    private void controllerBlock(final DeferredBlock<Block> block) {
        final Identifier baseTexture = modLoc("block/multiblock/entropy_assembler_core_casing_base");
        final Identifier overlayInactive = modLoc("block/general1/overlay_front");
        final Identifier overlayActive = modLoc("block/general1/overlay_front_active");

        final var inactiveModel = models().withExistingParent(block.getId().getPath(), "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayInactive)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end();

        final var activeModel = models().withExistingParent(block.getId().getPath() + "_active", "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayActive)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end();

        getVariantBuilder(block.get()).forAllStates(state -> {
            final Direction dir = state.getValue(DirectionalBlock.FACING);
            final boolean isActive = state.getValue(MultiblockBlocks.ControllerBlock.ACTIVE);
            return ConfiguredModel.builder()
                    .modelFile((isActive ? activeModel : inactiveModel).end())
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), inactiveModel.end());
    }

    private void controllerWithBase(final DeferredBlock<? extends Block> block, final String baseTextureName) {
        final String name = block.getId().getPath();
        final Identifier baseTexture = modLoc("block/multiblock/" + baseTextureName);
        final Identifier overlayTexture = modLoc("block/multiblock/overlay_front");

        final ModelFile model = models().withExistingParent(name, "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayTexture)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end().end();

        getVariantBuilder(block.get()).forAllStates(state -> {
            final Direction dir = state.getValue(DirectionalBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), model);
    }

    private void qmfControllerBlock(final DeferredBlock<? extends Block> block) {
        final String name = block.getId().getPath();
        final Identifier baseTexture = modLoc("block/multiblock/quantum_hyper_mechanical_casing");
        final Identifier overlayInactive = modLoc("block/qmf/overlay_front");
        final Identifier overlayActive = modLoc("block/qmf/overlay_front_active");

        final ModelFile inactiveModel = models().withExistingParent(name, "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayInactive)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end().end();

        final ModelFile activeModel = models().withExistingParent(name + "_active", "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayActive)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end().end();

        getVariantBuilder(block.get()).forAllStates(state -> {
            final Direction dir = state.getValue(DirectionalBlock.FACING);
            final boolean active = state.getValue(com.raishxn.ufo.block.MultiblockBlocks.ControllerBlock.ACTIVE);
            return ConfiguredModel.builder()
                    .modelFile(active ? activeModel : inactiveModel)
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), inactiveModel);
    }

    private void stellarNexusControllerBlock(final DeferredBlock<? extends Block> block) {
        final String name = block.getId().getPath();
        final Identifier baseTexture = modLoc("block/multiblock/entropy_singularity_casing");
        final Identifier overlayTexture = modLoc("block/multiblock/overlay_front");

        final ModelFile normalModel = models().withExistingParent(name, "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayTexture)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end().end();

        final Identifier assembledBase = modLoc("block/multiblock/entropy_assembler_core_casing");
        final ModelFile assembledModel = models().withExistingParent(name + "_assembled", "block/block")
                .renderType("cutout")
                .texture("particle", assembledBase)
                .texture("base", assembledBase)
                .texture("overlay", overlayTexture)
                .element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture("#base").cullface(dir)).end()
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end().end();

        getVariantBuilder(block.get()).forAllStates(state -> {
            final Direction dir = state.getValue(DirectionalBlock.FACING);
            final boolean assembled = state.getValue(com.raishxn.ufo.block.StellarNexusControllerBlock.ASSEMBLED);
            return ConfiguredModel.builder()
                    .modelFile(assembled ? assembledModel : normalModel)
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), normalModel);
    }

    /**
     * Hatch block with entropy_singularity_casing as base + a per-hatch overlay on the front face.
     * Uses cutout render type to support animated overlay textures.
     */
    private void hatchWithOverlay(final DeferredBlock<? extends Block> block, final String overlayName) {
        final String name = block.getId().getPath();
        final Identifier baseTexture = modLoc("block/multiblock/entropy_singularity_casing");
        final Identifier overlayTexture = modLoc("block/multiblock/" + overlayName);

        final ModelFile modelFile = models().withExistingParent(name, "block/block")
                .renderType("cutout")
                .texture("particle", baseTexture)
                .texture("base", baseTexture)
                .texture("overlay", overlayTexture)
                .element()
                    .from(0, 0, 0).to(16, 16, 16)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#base").cullface(direction))
                .end()
                .element()
                    .from(0, 0, 0).to(16, 16, 16)
                    .face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).end()
                .end();

        getVariantBuilder(block.get()).forAllStates(state -> {
            final Direction dir = state.getValue(DirectionalBlock.FACING);
            return ConfiguredModel.builder()
                    .modelFile(modelFile)
                    .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        simpleBlockItem(block.get(), modelFile);
    }
}
