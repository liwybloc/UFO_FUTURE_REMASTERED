package com.raishxn.ufo.block.entity.pattern;

import com.raishxn.ufo.block.MultiblockBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuantumPatternPredicates {

    private static final Identifier AE2_QUARTZ_VIBRANT_GLASS = Identifier.fromNamespaceAndPath("ae2", "quartz_vibrant_glass");

    private QuantumPatternPredicates() {
    }

    public static Map<Character, BlockState> getDefaultCreativeStates() {
        final Map<Character, BlockState> map = new HashMap<>();
        map.put('C', MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get().defaultBlockState());
        map.put('F', MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get().defaultBlockState());

        final Block vibrantGlass = BuiltInRegistries.BLOCK.getValue(AE2_QUARTZ_VIBRANT_GLASS);
        if (vibrantGlass != null && vibrantGlass != Blocks.AIR) {
            map.put('G', vibrantGlass.defaultBlockState());
        }

        return map;
    }

    public static boolean isQuantumCasing(final BlockState state) {
        return state.is(MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get());
    }

    public static boolean isUniversalHatch(final BlockState state) {
        return state.is(MultiblockBlocks.QUANTUM_PATTERN_HATCH.get())
                || state.is(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get())
                || state.is(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get())
                || state.is(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get())
                || state.is(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get());
    }

    public static boolean isQuantumCasingOrUniversalHatch(final BlockState state) {
        return isQuantumCasing(state) || isUniversalHatch(state);
    }

    public static boolean isAnyFieldGenerator(final BlockState state) {
        return state.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get())
                || state.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get())
                || state.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get());
    }

    public static boolean isQuartzVibrantGlass(final BlockState state) {
        final Block block = BuiltInRegistries.BLOCK.getValue(AE2_QUARTZ_VIBRANT_GLASS);
        return block != null && block != Blocks.AIR && state.is(block);
    }

    public static Component casingName() {
        return Component.literal("Quantum Hyper Mechanical Casing");
    }

    public static Component casingOrHatchName() {
        return Component.literal("Quantum Hyper Mechanical Casing or Universal Hatch");
    }

    public static Component patternHatchName() {
        return Component.literal("Quantum Pattern Hatch");
    }

    public static Component fieldName() {
        return Component.literal("Stellar Field Generator Mk.I or better");
    }

    public static List<BlockState> fieldCandidates() {
        return List.of(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get().defaultBlockState());
    }

    public static List<BlockState> allFieldCandidates() {
        return List.of(
                MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get().defaultBlockState(),
                MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get().defaultBlockState(),
                MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get().defaultBlockState()
        );
    }

    public static Component glassName() {
        return Component.literal("AE2 Quartz Vibrant Glass");
    }

    public static List<BlockState> glassCandidates() {
        final Block block = BuiltInRegistries.BLOCK.getValue(AE2_QUARTZ_VIBRANT_GLASS);
        if (block == null || block == Blocks.AIR) {
            return List.of();
        }
        return List.of(block.defaultBlockState());
    }

    public static List<BlockState> casingAndHatchCandidates() {
        return List.of(
                MultiblockBlocks.QUANTUM_HYPER_MECHANICAL_CASING.get().defaultBlockState(),
                MultiblockBlocks.QUANTUM_PATTERN_HATCH.get().defaultBlockState(),
                MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get().defaultBlockState(),
                MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get().defaultBlockState(),
                MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get().defaultBlockState(),
                MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get().defaultBlockState()
        );
    }
}
