package com.raishxn.ufo.block;

import com.mojang.serialization.MapCodec;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.entity.AbstractSimpleMultiblockControllerBE;
import appeng.api.orientation.IOrientableBlock;
import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Function;

public class MultiblockBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(UfoMod.MOD_ID);

    public static final DeferredBlock<EntropicMachineShellBlock> ENTROPY_ASSEMBLER_CORE_CASING = BLOCKS.register("entropy_assembler_core_casing",
            id -> new EntropicMachineShellBlock(properties(id).strength(5.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<EntropicMachineShellBlock> ENTROPY_SINGULARITY_CASING = BLOCKS.register("entropy_singularity_casing",
            id -> new EntropicMachineShellBlock(properties(id).strength(5.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<EntropicConvergenceEngineBlock> ENTROPY_COMPUTER_CONDENSATION_MATRIX = BLOCKS.register("entropy_computer_condensation_matrix",
            id -> new EntropicConvergenceEngineBlock(properties(id)));

    public static final DeferredBlock<EntropicAssemblerCasingBlock> ENTROPIC_ASSEMBLER_CASING = BLOCKS.register("entropic_assembler_casing",
            id -> new EntropicAssemblerCasingBlock(properties(id)));

    public static final DeferredBlock<EntropicConvergenceCasingBlock> ENTROPIC_CONVERGENCE_CASING = BLOCKS.register("entropic_convergence_casing",
            id -> new EntropicConvergenceCasingBlock(properties(id)));

    public static final DeferredBlock<EntropicAssemblerMatrixControllerBlock> ENTROPIC_ASSEMBLER_MATRIX = BLOCKS.register("entropic_assembler_matrix",
            id -> new EntropicAssemblerMatrixControllerBlock(properties(id)
                    .strength(30.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 12)));

    public static final DeferredBlock<EntropicConvergenceEngineBlock> ENTROPIC_CONVERGENCE_ENGINE = BLOCKS.register("entropic_convergence_engine",
            id -> new EntropicConvergenceEngineBlock(properties(id)));





    public static final DeferredBlock<Block> QUANTUM_ENTROPY_CASING = registerBlock("quantum_entropy_casing",
            id -> new Block(properties(id).strength(10.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> QUANTUM_HYPER_MECHANICAL_CASING = registerBlock("quantum_hyper_mechanical_casing",
            id -> new Block(properties(id).strength(12.0f, 1200.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<ControllerBlock> QUANTUM_MATTER_FABRICATOR_CONTROLLER = BLOCKS.register("quantum_matter_fabricator_controller",
            id -> new ControllerBlock(properties(id)
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(ControllerBlock.ACTIVE) ? 14 : 0)));

    public static final DeferredBlock<QuantumSlicerControllerBlock> QUANTUM_SLICER_CONTROLLER = BLOCKS.register("quantum_slicer_controller",
            id -> new QuantumSlicerControllerBlock(properties(id)
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(AbstractSimpleMultiblockControllerBlock.ACTIVE) ? 14 : 0)));

    public static final DeferredBlock<QuantumProcessorAssemblerControllerBlock> QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER = BLOCKS.register("quantum_processor_assembler_controller",
            id -> new QuantumProcessorAssemblerControllerBlock(properties(id)
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(AbstractSimpleMultiblockControllerBlock.ACTIVE) ? 14 : 0)));

    public static final DeferredBlock<QuantumCryoforgeControllerBlock> QUANTUM_CRYOFORGE_CONTROLLER = BLOCKS.register("quantum_cryoforge_controller",
            id -> new QuantumCryoforgeControllerBlock(properties(id)
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(AbstractSimpleMultiblockControllerBlock.ACTIVE) ? 14 : 0)));

    public static final DeferredBlock<QuantumPatternHatchBlock> QUANTUM_PATTERN_HATCH = BLOCKS.register("quantum_pattern_hatch",
            id -> new QuantumPatternHatchBlock(properties(id)));


    public static final DeferredBlock<StellarNexusControllerBlock> STELLAR_NEXUS_CONTROLLER = BLOCKS.register("stellar_nexus_controller",
            id -> new StellarNexusControllerBlock(properties(id)
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(StellarNexusControllerBlock.ASSEMBLED) ? 12 : 0)));

    public static final DeferredBlock<MassiveOutputHatchBlock> ME_MASSIVE_OUTPUT_HATCH = BLOCKS.register("me_massive_output_hatch",
            id -> new MassiveOutputHatchBlock(properties(id)
                    .strength(25.0f, 600.0f)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<MassiveOutputHatchBlock> ME_MASSIVE_FLUID_HATCH = BLOCKS.register("me_massive_fluid_hatch",
            id -> new MassiveOutputHatchBlock(properties(id)
                    .strength(25.0f, 600.0f)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<MassiveOutputHatchBlock> ME_MASSIVE_INPUT_HATCH = BLOCKS.register("me_massive_input_hatch",
            id -> new MassiveOutputHatchBlock(properties(id)
                    .strength(25.0f, 600.0f)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<MassiveOutputHatchBlock> AE_ENERGY_INPUT_HATCH = BLOCKS.register("ae_energy_input_hatch",
            id -> new MassiveOutputHatchBlock(properties(id)
                    .strength(25.0f, 600.0f)
                    .requiresCorrectToolForDrops()));




    public static final DeferredBlock<StellarNexusPartBlock> STELLAR_FIELD_GENERATOR_T1 = BLOCKS.register("stellar_field_generator_t1",
            id -> new StellarNexusPartBlock(properties(id)
                    .strength(25.0f, 600.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 4)));

    public static final DeferredBlock<StellarNexusPartBlock> STELLAR_FIELD_GENERATOR_T2 = BLOCKS.register("stellar_field_generator_t2",
            id -> new StellarNexusPartBlock(properties(id)
                    .strength(35.0f, 800.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 7)));

    public static final DeferredBlock<StellarNexusPartBlock> STELLAR_FIELD_GENERATOR_T3 = BLOCKS.register("stellar_field_generator_t3",
            id -> new StellarNexusPartBlock(properties(id)
                    .strength(50.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 10)));


    private static <T extends Block> DeferredBlock<T> registerBlock(final String name, final Function<Identifier, T> block) {
        return BLOCKS.register(name, block);
    }

    private static BlockBehaviour.Properties properties(final Identifier id) {
        return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id));
    }


    public static void register(final IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static class OrientableMultiblock extends DirectionalBlock {
        public static final MapCodec<OrientableMultiblock> CODEC = simpleCodec(OrientableMultiblock::new);

        public OrientableMultiblock(final Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        }

        @Override
        protected MapCodec<? extends DirectionalBlock> codec() {
            return CODEC;
        }

        @Override
        public BlockState getStateForPlacement(final BlockPlaceContext context) {
            return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
        }

        @Override
        protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING);
        }
    }

    public static class ControllerBlock extends DirectionalBlock implements net.minecraft.world.level.block.EntityBlock, IOrientableBlock {
        public static final MapCodec<ControllerBlock> CODEC = simpleCodec(ControllerBlock::new);
        public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

        public ControllerBlock(final Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
        }

        @Override
        protected MapCodec<? extends DirectionalBlock> codec() {
            return CODEC;
        }

        @Override
        public BlockState getStateForPlacement(final BlockPlaceContext context) {
            return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
        }

        @Override
        protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING, ACTIVE);
        }

        @Override
        public IOrientationStrategy getOrientationStrategy() {
            return OrientationStrategies.facing();
        }

        @Override
        protected InteractionResult useItemOn(final ItemStack stack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
            if (stack.is(Tags.Items.TOOLS_WRENCH) && player.isShiftKeyDown()) {
                if (!level.isClientSide()) {
                    level.destroyBlock(pos, true, player);
                }
                return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        @Override
        protected net.minecraft.world.InteractionResult useWithoutItem(final BlockState state, final net.minecraft.world.level.Level level, final net.minecraft.core.BlockPos pos, final net.minecraft.world.entity.player.Player player, final net.minecraft.world.phys.BlockHitResult hitResult) {
            if (player.isShiftKeyDown()) {
                if (level.isClientSide()) {
                    com.raishxn.ufo.client.GhostHologramRenderer.toggleHologram(pos, state.getValue(FACING));
                }
                return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }

            if (!level.isClientSide()) {
                final net.minecraft.world.level.block.entity.BlockEntity entity = level.getBlockEntity(pos);
                if (entity instanceof final com.raishxn.ufo.block.entity.QmfControllerBE controller) {
                    player.openMenu(controller, pos);
                }
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        @org.jetbrains.annotations.Nullable
        @Override
        public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(final net.minecraft.core.BlockPos pos, final BlockState state) {
            return new com.raishxn.ufo.block.entity.QmfControllerBE(pos, state);
        }

        @org.jetbrains.annotations.Nullable
        @Override
        public <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(final net.minecraft.world.level.Level level, final BlockState state, final net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
            if (level.isClientSide()) return null;
            return type == com.raishxn.ufo.init.ModBlockEntities.QMF_CONTROLLER.get()
                    ? (lvl, pos, st, be) -> ((com.raishxn.ufo.block.entity.QmfControllerBE) be).serverTick()
                    : null;
        }

        @Override
        protected void neighborChanged(final BlockState state, final net.minecraft.world.level.Level level, final net.minecraft.core.BlockPos pos, final Block changedBlock, final Orientation orientation, final boolean isMoving) {
            super.neighborChanged(state, level, pos, changedBlock, orientation, isMoving);
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof final com.raishxn.ufo.block.entity.QmfControllerBE be) {
                be.markStructureDirty();
            }
        }

        @Override
        protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final net.minecraft.core.BlockPos pos, final boolean moved) {
            if (level.getBlockEntity(pos) instanceof final com.raishxn.ufo.block.entity.QmfControllerBE be) {
                be.onControllerBroken();
            }
            super.affectNeighborsAfterRemoval(state, level, pos, moved);
        }
    }
}
