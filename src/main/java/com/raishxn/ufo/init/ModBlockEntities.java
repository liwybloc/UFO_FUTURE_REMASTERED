package com.raishxn.ufo.init;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.crafting.CraftingBlockEntity;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.block.entity.DimensionalMatterAssemblerBlockEntity;
import com.raishxn.ufo.block.entity.EntropicAssemblerMatrixBE;
import com.raishxn.ufo.block.entity.EntropicConvergenceEngineBE;
import com.raishxn.ufo.block.entity.EntropicMachinePartBE;
import com.raishxn.ufo.block.entity.MassiveOutputHatchBE;
import com.raishxn.ufo.block.entity.QmfControllerBE;
import com.raishxn.ufo.block.entity.QuantumCryoforgeControllerBE;
import com.raishxn.ufo.block.entity.QuantumPatternHatchBE;
import com.raishxn.ufo.block.entity.QuantumProcessorAssemblerControllerBE;
import com.raishxn.ufo.block.entity.QuantumSlicerControllerBE;
import com.raishxn.ufo.block.entity.StellarNexusControllerBE;
import com.raishxn.ufo.block.entity.StellarNexusPartBE;
import com.raishxn.ufo.block.entity.QuantumEnergyCellBlockEntity;
import com.raishxn.ufo.block.entity.UfoEnergyCellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Set;
import java.util.stream.Stream;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, UfoMod.MOD_ID);

    private ModBlockEntities() {
    }

    private static <T extends BlockEntity> BlockEntityType<T> createType(
            final BlockEntityType.BlockEntitySupplier<? extends T> factory, final Block... blocks) {
        return new BlockEntityType<>(factory, Set.of(blocks));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T extends AEBaseBlockEntity> void bindBlockEntity(
            final AEBaseEntityBlock<?> block,
            final Class<T> blockEntityClass,
            final BlockEntityType<T> blockEntityType,
            final BlockEntityTicker<T> serverTicker) {
        ((AEBaseEntityBlock) block).setBlockEntity(blockEntityClass, blockEntityType, null, serverTicker);
    }

    @SuppressWarnings("unchecked")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CraftingBlockEntity>> MEGA_CRAFTING_UNITS_BE =
            BLOCK_ENTITIES.register("mega_crafting_units_be", () -> {
                final AtomicReference<BlockEntityType<CraftingBlockEntity>> typeHolder = new AtomicReference<>();
                final var validBlocks = Stream.concat(
                        ModBlocks.CRAFTING_STORAGE_BLOCKS.values().stream(),
                        ModBlocks.CO_PROCESSOR_BLOCKS.values().stream()
                ).map(DeferredBlock::get).toArray(AEBaseEntityBlock[]::new);

                final var type = createType(
                        (pos, state) -> new CraftingBlockEntity(typeHolder.get(), pos, state),
                        validBlocks
                );

                typeHolder.set(type);

                for (final var block : validBlocks) {
                    ((AEBaseEntityBlock<CraftingBlockEntity>) block)
                            .setBlockEntity(CraftingBlockEntity.class, type, null, ModBlockEntities::tickMegaCraftingUnit);
                }

                return type;
            });

    private static void tickMegaCraftingUnit(
            final Level level,
            final BlockPos pos,
            final BlockState state,
            final CraftingBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel) || blockEntity.isFormed()) {
            return;
        }

        final long stagger = Math.floorMod(pos.asLong(), 20);
        if ((level.getGameTime() + stagger) % 20 == 0) {
            blockEntity.updateMultiBlock(pos);
        }
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DimensionalMatterAssemblerBlockEntity>> DIMENSIONAL_MATTER_ASSEMBLER_BE =
            BLOCK_ENTITIES.register("dimensional_matter_assembler", () -> {
                final AtomicReference<BlockEntityType<DimensionalMatterAssemblerBlockEntity>> typeHolder = new AtomicReference<>();
                final var type = createType(
                        (pos, state) -> new DimensionalMatterAssemblerBlockEntity(typeHolder.get(), pos, state),
                        ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get()
                );
                typeHolder.set(type);
                AEBaseBlockEntity.registerBlockEntityItem(type, ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get().asItem());
                ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get().setBlockEntity(
                        DimensionalMatterAssemblerBlockEntity.class,
                        type,
                        null,
                        (level, pos, state, blockEntity) -> blockEntity.serverTick()
                );
                return type;
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UfoEnergyCellBlockEntity>> UFO_ENERGY_CELL_BE =
            BLOCK_ENTITIES.register("ufo_energy_cell", () -> {
                final AtomicReference<BlockEntityType<UfoEnergyCellBlockEntity>> typeHolder = new AtomicReference<>();
                final var type = createType(
                        (pos, state) -> new UfoEnergyCellBlockEntity(typeHolder.get(), pos, state),
                        ModBlocks.UFO_ENERGY_CELL.get()
                );
                typeHolder.set(type);
                AEBaseBlockEntity.registerBlockEntityItem(type, ModBlocks.UFO_ENERGY_CELL.get().asItem());
                bindBlockEntity(ModBlocks.UFO_ENERGY_CELL.get(), UfoEnergyCellBlockEntity.class, type,
                        (level, pos, state, blockEntity) -> blockEntity.serverTick());
                return type;
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuantumEnergyCellBlockEntity>> QUANTUM_ENERGY_CELL_BE =
            BLOCK_ENTITIES.register("quantum_energy_cell", () -> {
                final AtomicReference<BlockEntityType<QuantumEnergyCellBlockEntity>> typeHolder = new AtomicReference<>();
                final var type = createType(
                        (pos, state) -> new QuantumEnergyCellBlockEntity(typeHolder.get(), pos, state),
                        ModBlocks.QUANTUM_ENERGY_CELL.get()
                );
                typeHolder.set(type);
                AEBaseBlockEntity.registerBlockEntityItem(type, ModBlocks.QUANTUM_ENERGY_CELL.get().asItem());
                bindBlockEntity(ModBlocks.QUANTUM_ENERGY_CELL.get(), QuantumEnergyCellBlockEntity.class, type,
                        (level, pos, state, blockEntity) -> blockEntity.serverTick());
                return type;
            });
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QmfControllerBE>> QMF_CONTROLLER =
            BLOCK_ENTITIES.register("qmf_controller", () -> createType(
                    (pos, state) -> new QmfControllerBE(pos, state),
                    MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuantumSlicerControllerBE>> QUANTUM_SLICER_CONTROLLER_BE =
            BLOCK_ENTITIES.register("quantum_slicer_controller", () -> createType(
                    (pos, state) -> new QuantumSlicerControllerBE(pos, state),
                    MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuantumProcessorAssemblerControllerBE>> QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER_BE =
            BLOCK_ENTITIES.register("quantum_processor_assembler_controller", () -> createType(
                    (pos, state) -> new QuantumProcessorAssemblerControllerBE(pos, state),
                    MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuantumCryoforgeControllerBE>> QUANTUM_CRYOFORGE_CONTROLLER_BE =
            BLOCK_ENTITIES.register("quantum_cryoforge_controller", () -> createType(
                    (pos, state) -> new QuantumCryoforgeControllerBE(pos, state),
                    MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuantumPatternHatchBE>> QUANTUM_PATTERN_HATCH_BE =
            BLOCK_ENTITIES.register("quantum_pattern_hatch", () -> {
                final var type = createType(
                        (pos, state) -> new QuantumPatternHatchBE(pos, state),
                        MultiblockBlocks.QUANTUM_PATTERN_HATCH.get()
                );
                bindBlockEntity(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get(),
                        QuantumPatternHatchBE.class, type, null);
                return type;
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EntropicMachinePartBE>> ENTROPIC_MACHINE_PART_BE =
            BLOCK_ENTITIES.register("entropic_machine_part", () -> {
                final AtomicReference<BlockEntityType<EntropicMachinePartBE>> typeHolder = new AtomicReference<>();
                final var type = createType(
                        (pos, state) -> new EntropicMachinePartBE(typeHolder.get(), pos, state),
                        MultiblockBlocks.ENTROPY_ASSEMBLER_CORE_CASING.get(),
                        MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get()
                );
                typeHolder.set(type);
                bindBlockEntity(MultiblockBlocks.ENTROPY_ASSEMBLER_CORE_CASING.get(),
                        EntropicMachinePartBE.class, type, null);
                bindBlockEntity(MultiblockBlocks.ENTROPY_SINGULARITY_CASING.get(),
                        EntropicMachinePartBE.class, type, null);
                return type;
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EntropicAssemblerMatrixBE>> ENTROPIC_ASSEMBLER_MATRIX_BE =
            BLOCK_ENTITIES.register("entropic_assembler_matrix", () -> {
                final var type = createType(
                        (pos, state) -> new EntropicAssemblerMatrixBE(pos, state),
                        MultiblockBlocks.ENTROPIC_ASSEMBLER_CASING.get()
                );
                AEBaseBlockEntity.registerBlockEntityItem(type,
                        MultiblockBlocks.ENTROPIC_ASSEMBLER_CASING.get().asItem());
                bindBlockEntity(MultiblockBlocks.ENTROPIC_ASSEMBLER_CASING.get(),
                        EntropicAssemblerMatrixBE.class, type, null);
                return type;
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EntropicConvergenceEngineBE>> ENTROPIC_CONVERGENCE_CASING_BE =
            BLOCK_ENTITIES.register("entropic_convergence_casing", () -> {
                final AtomicReference<BlockEntityType<EntropicConvergenceEngineBE>> typeHolder = new AtomicReference<>();
                final var type = createType(
                        (pos, state) -> new EntropicConvergenceEngineBE(typeHolder.get(), pos, state),
                        MultiblockBlocks.ENTROPIC_CONVERGENCE_CASING.get()
                );
                typeHolder.set(type);
                AEBaseBlockEntity.registerBlockEntityItem(type,
                        MultiblockBlocks.ENTROPIC_CONVERGENCE_CASING.get().asItem());
                bindBlockEntity(MultiblockBlocks.ENTROPIC_CONVERGENCE_CASING.get(),
                        EntropicConvergenceEngineBE.class, type, null);
                return type;
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EntropicConvergenceEngineBE>> ENTROPIC_CONVERGENCE_ENGINE_BE =
            BLOCK_ENTITIES.register("entropic_convergence_engine", () -> {
                final AtomicReference<BlockEntityType<EntropicConvergenceEngineBE>> typeHolder = new AtomicReference<>();
                final var type = createType(
                        (pos, state) -> new EntropicConvergenceEngineBE(typeHolder.get(), pos, state),
                        MultiblockBlocks.ENTROPY_COMPUTER_CONDENSATION_MATRIX.get(),
                        MultiblockBlocks.ENTROPIC_CONVERGENCE_ENGINE.get()
                );
                typeHolder.set(type);
                bindBlockEntity(MultiblockBlocks.ENTROPY_COMPUTER_CONDENSATION_MATRIX.get(),
                        EntropicConvergenceEngineBE.class, type, null);
                bindBlockEntity(MultiblockBlocks.ENTROPIC_CONVERGENCE_ENGINE.get(),
                        EntropicConvergenceEngineBE.class, type, null);
                return type;
            });
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StellarNexusControllerBE>> STELLAR_NEXUS_CONTROLLER_BE =
            BLOCK_ENTITIES.register("stellar_nexus_controller", () -> createType(
                    (pos, state) -> new StellarNexusControllerBE(
                            ModBlockEntities.STELLAR_NEXUS_CONTROLLER_BE.get(), pos, state),
                    MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StellarNexusPartBE>> STELLAR_NEXUS_PART_BE =
            BLOCK_ENTITIES.register("stellar_nexus_part", () -> createType(
                    (pos, state) -> new StellarNexusPartBE(
                            ModBlockEntities.STELLAR_NEXUS_PART_BE.get(), pos, state),
                    MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get(),
                    MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get(),
                    MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MassiveOutputHatchBE>> ME_MASSIVE_OUTPUT_HATCH_BE =
            BLOCK_ENTITIES.register("me_massive_output_hatch", () -> {
                final AtomicReference<BlockEntityType<MassiveOutputHatchBE>> typeHolder = new AtomicReference<>();
                final var type = createType(
                        (pos, state) -> new MassiveOutputHatchBE(typeHolder.get(), pos, state),
                        MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get(),
                        MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get(),
                        MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get(),
                        MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get()
                );
                typeHolder.set(type);
                AEBaseBlockEntity.registerBlockEntityItem(type,
                        MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get().asItem());
                return type;
            });

    public static void register(final IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
