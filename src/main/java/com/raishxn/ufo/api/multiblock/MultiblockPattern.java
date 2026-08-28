package com.raishxn.ufo.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import java.util.*;

/**
 * A reusable 3D pattern matcher for multiblock structures.
 * <p>
 * The pattern is defined as a 3D char array (layer × row × column) together with
 * a legend map that binds each char to a predicate (block or tag check).
 * The controller position within the pattern is marked by a special character
 * (default {@code 'C'}).
 * <p>
 * <b>Usage:</b>
 * <pre>{@code
 * MultiblockPattern pattern = new MultiblockPattern.Builder()
 *     .layer(new String[]{
 *         "SSS",
 *         "SCS",
 *         "SSS"
 *     })
 *     .layer(new String[]{
 *         "SSS",
 *         "S S",
 *         "SSS"
 *     })
 *     .layer(new String[]{
 *         "SSS",
 *         "SSS",
 *         "SSS"
 *     })
 *     .where('S', block -> block instanceof MyCasingBlock)
 *     .where(' ', block -> true) // air / anything
 *     .build();
 * }</pre>
 */
public final class MultiblockPattern {

    /** Functional interface used to test whether a block satisfies a pattern slot. */
    @FunctionalInterface
    public interface BlockPredicate {
        boolean test(BlockState state, Level level, BlockPos pos);
    }

    private final char[][][] pattern;      // [layer][row][col]
    private final Map<Character, BlockPredicate> legend;
    private final Map<Character, Component> legendNames;
    private final Map<Character, List<BlockState>> displayCandidates;
    private final Character controllerChar;
    private final int controllerLayer;
    private final int controllerRow;
    private final int controllerCol;

    public char[][][] getPattern() { return pattern; }
    public char getControllerChar() { return controllerChar != null ? controllerChar : 'C'; }
    public int getControllerLayer() { return controllerLayer; }
    public int getControllerRow() { return controllerRow; }
    public int getControllerCol() { return controllerCol; }
    public Component getLegendName(final char symbol) { return legendNames.getOrDefault(symbol, Component.literal("Unknown Block")); }

    private MultiblockPattern(final char[][][] pattern, final Map<Character, BlockPredicate> legend, final Map<Character, Component> legendNames,
                              final Map<Character, List<BlockState>> displayCandidates, final char controllerChar) {
        this.pattern = pattern;
        this.legend = legend;
        this.legendNames = legendNames;
        this.displayCandidates = displayCandidates;
        this.controllerChar = controllerChar;

        int cLayer = -1, cRow = -1, cCol = -1;
        for (int y = 0; y < pattern.length; y++) {
            for (int z = 0; z < pattern[y].length; z++) {
                for (int x = 0; x < pattern[y][z].length; x++) {
                    if (pattern[y][z][x] == controllerChar) {
                        cLayer = y;
                        cRow = z;
                        cCol = x;
                    }
                }
            }
        }
        if (cLayer == -1) {
            throw new IllegalArgumentException("Controller char '" + controllerChar + "' not found in pattern!");
        }
        this.controllerLayer = cLayer;
        this.controllerRow = cRow;
        this.controllerCol = cCol;
    }

    /**
     * Validates the multiblock structure by testing the world against the pattern,
     * centered on the given controller world position.
     *
     * @param level         the level to check
     * @param controllerPos the world position of the controller block
     * @return a {@link MatchResult} containing whether the structure matched and which positions are parts
     */
    public MatchResult match(final Level level, final BlockPos controllerPos, final net.minecraft.core.Direction facing) {
        final List<BlockPos> partPositions = new ArrayList<>();
        PatternError firstError = null;
        final List<PatternError> allErrors = new ArrayList<>();
        boolean valid = true;
        boolean hasUnloadedPositions = false;

        for (int y = 0; y < pattern.length; y++) {
            for (int z = 0; z < pattern[y].length; z++) {
                for (int x = 0; x < pattern[y][z].length; x++) {
                    final char c = pattern[y][z][x];

                    final int offsetX = x - controllerCol;
                    final int offsetY = y - controllerLayer;
                    final int offsetZ = z - controllerRow;

                    final BlockPos worldPos = getRotatedPos(controllerPos, offsetX, offsetY, offsetZ, facing);

                    if (worldPos.equals(controllerPos)) {
                        continue;
                    }

                    final BlockPredicate predicate = legend.get(c);
                    if (predicate == null) {
                        continue;
                    }

                    if (!level.isLoaded(worldPos)) {
                        valid = false;
                        hasUnloadedPositions = true;
                        final PatternError err = new PatternError(worldPos, Component.literal("Chunk not loaded"));
                        allErrors.add(err);
                        if (firstError == null) firstError = err;
                        continue;
                    }

                    final BlockState state = level.getBlockState(worldPos);
                    if (!predicate.test(state, level, worldPos)) {
                        valid = false;
                        final Component expected = legendNames.getOrDefault(c, Component.literal("Expected part"));
                        final PatternError err = new PatternError(worldPos, expected);
                        allErrors.add(err);
                        if (firstError == null) firstError = err;
                    } else {
                        partPositions.add(worldPos);
                    }
                }
            }
        }

        return new MatchResult(
                valid,
                valid ? Collections.unmodifiableList(partPositions) : Collections.emptyList(),
                Optional.ofNullable(firstError),
                Collections.unmodifiableList(allErrors),
                hasUnloadedPositions);
    }

    /**
     * Translates local pattern offsets into world coordinates based on the controller's facing direction.
     * Assumes pattern is built such that z=0 is the front face looking SOUTH (+Z). 
     * If facing is NORTH, the machine goes $+Z$ backwards.
     */
    private BlockPos getRotatedPos(final BlockPos center, final int localX, final int localY, final int localZ, final net.minecraft.core.Direction facing) {
        switch (facing) {
            case SOUTH:
                return center.offset(-localX, localY, -localZ);
            case WEST:
                return center.offset(localZ, localY, -localX);
            case EAST:
                return center.offset(-localZ, localY, localX);
            case NORTH:
            default:
                return center.offset(localX, localY, localZ);
        }
    }

    /**
     * Instantly assembles the structure unconditionally, replacing non-matching blocks 
     * using the provided map of default states. Does not replace the controller.
     */
    public void assembleAsCreative(final Level level, final BlockPos controllerPos, final net.minecraft.core.Direction facing, final Map<Character, BlockState> defaultStates) {
        for (int y = 0; y < pattern.length; y++) {
            for (int z = 0; z < pattern[y].length; z++) {
                for (int x = 0; x < pattern[y][z].length; x++) {
                    final char c = pattern[y][z][x];
                    
                    final int offsetX = x - controllerCol;
                    final int offsetY = y - controllerLayer;
                    final int offsetZ = z - controllerRow;

                    final BlockPos worldPos = getRotatedPos(controllerPos, offsetX, offsetY, offsetZ, facing);

                    if (worldPos.equals(controllerPos)) continue;

                    if (!level.isInWorldBounds(worldPos)) continue;
                    if (!level.hasChunkAt(worldPos)) continue;

                    final BlockPredicate predicate = legend.get(c);
                    final BlockState targetState = defaultStates.get(c);

                    if (predicate != null && targetState != null) {
                        final BlockState currentState = level.getBlockState(worldPos);
                        if (!predicate.test(currentState, level, worldPos)) {
                            level.setBlockAndUpdate(worldPos, targetState);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the exact world positions for a specific character in the pattern.
     */
    public List<BlockPos> getExpectedPositions(final BlockPos controllerPos, final net.minecraft.core.Direction facing, final char targetChar) {
        final List<BlockPos> list = new ArrayList<>();
        for (int y = 0; y < pattern.length; y++) {
            for (int z = 0; z < pattern[y].length; z++) {
                for (int x = 0; x < pattern[y][z].length; x++) {
                    if (pattern[y][z][x] == targetChar) {
                        final int offsetX = x - controllerCol;
                        final int offsetY = y - controllerLayer;
                        final int offsetZ = z - controllerRow;
                        final BlockPos pos = getRotatedPos(controllerPos, offsetX, offsetY, offsetZ, facing);
                        list.add(pos);
                    }
                }
            }
        }
        return list;
    }

    public List<BlockState> getDisplayCandidates(final char symbol) {
        return displayCandidates.getOrDefault(symbol, List.of());
    }

    public Optional<Character> getSymbolAt(final BlockPos controllerPos, final net.minecraft.core.Direction facing, final BlockPos worldPos) {
        for (int y = 0; y < pattern.length; y++) {
            for (int z = 0; z < pattern[y].length; z++) {
                for (int x = 0; x < pattern[y][z].length; x++) {
                    final int offsetX = x - controllerCol;
                    final int offsetY = y - controllerLayer;
                    final int offsetZ = z - controllerRow;
                    final BlockPos expectedPos = getRotatedPos(controllerPos, offsetX, offsetY, offsetZ, facing);
                    if (expectedPos.equals(worldPos)) {
                        return Optional.of(pattern[y][z][x]);
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Represents a specific error in pattern matching.
     */
    public record PatternError(BlockPos pos, Component expected) {}

    /**
     * Result of a pattern match attempt.
     */
    public record MatchResult(
            boolean isValid,
            List<BlockPos> partPositions,
            Optional<PatternError> error,
            List<PatternError> allErrors,
            boolean hasUnloadedPositions) {}


    public static final class Builder {
        private final List<String[]> layers = new ArrayList<>();
        private final Map<Character, BlockPredicate> legend = new HashMap<>();
        private final Map<Character, Component> legendNames = new HashMap<>();
        private final Map<Character, List<BlockState>> displayCandidates = new HashMap<>();
        private char controllerChar = 'C';

        /**
         * Adds a horizontal layer to the pattern (bottom to top).
         * Each string represents a row (north to south); each char a column (west to east).
         */
        public Builder layer(final String[] rows) {
            this.layers.add(rows);
            return this;
        }

        /**
         * Defines what block a character in the pattern maps to.
         */
        public Builder where(final char c, final BlockPredicate predicate) {
            return where(c, predicate, Component.literal("Unknown Block"));
        }

        public Builder where(final char c, final BlockPredicate predicate, final Component expectedName) {
            this.legend.put(c, predicate);
            this.legendNames.put(c, expectedName);
            return this;
        }

        /**
         * Convenience: maps a char to a specific block class.
         */
        public Builder where(final char c, final Block block) {
            return where(c, (state, level, pos) -> state.is(block), block.getName());
        }

        public Builder candidates(final char c, final BlockState... states) {
            return candidates(c, Arrays.asList(states));
        }

        public Builder candidates(final char c, final List<BlockState> states) {
            final List<BlockState> cleaned = states.stream()
                    .filter(Objects::nonNull)
                    .toList();
            if (!cleaned.isEmpty()) {
                this.displayCandidates.put(c, cleaned);
            }
            return this;
        }

        /**
         * Sets the character that represents the controller in the pattern.
         * Defaults to {@code 'C'}.
         */
        public Builder controllerChar(final char c) {
            this.controllerChar = c;
            return this;
        }

        public MultiblockPattern build() {
            final char[][][] patternArray = new char[layers.size()][][];
            for (int y = 0; y < layers.size(); y++) {
                final String[] rows = layers.get(y);
                patternArray[y] = new char[rows.length][];
                for (int z = 0; z < rows.length; z++) {
                    patternArray[y][z] = rows[z].toCharArray();
                }
            }
            return new MultiblockPattern(patternArray, legend, legendNames, new HashMap<>(displayCandidates), controllerChar);
        }
    }
}
