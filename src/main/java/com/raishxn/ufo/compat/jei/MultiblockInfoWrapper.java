package com.raishxn.ufo.compat.jei;

import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinition;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.api.multiblock.MultiblockPattern;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public final class MultiblockInfoWrapper {

    public record MaterialStack(ItemStack stack, int count) {
    }

    private final MultiblockControllerDefinitions.PreviewEntry entry;
    private final List<MaterialStack> materials;
    private final List<Component> legendLines;
    private final int width;
    private final int height;
    private final int layers;

    public MultiblockInfoWrapper(final MultiblockControllerDefinitions.PreviewEntry entry) {
        this.entry = entry;

        final MultiblockControllerDefinition definition = entry.definition();
        final MultiblockPattern pattern = definition.pattern();
        final char[][][] chars = pattern.getPattern();

        this.layers = chars.length;
        this.height = chars.length == 0 ? 0 : chars[0].length;
        this.width = chars.length == 0 || chars[0].length == 0 ? 0 : chars[0][0].length;
        this.materials = collectMaterials(entry, definition, chars);
        this.legendLines = collectLegend(pattern, chars);
    }

    public MultiblockControllerDefinitions.PreviewEntry entry() {
        return entry;
    }

    public List<MaterialStack> materials() {
        return materials;
    }

    public List<Component> legendLines() {
        return legendLines;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int layers() {
        return layers;
    }

    private static List<MaterialStack> collectMaterials(final MultiblockControllerDefinitions.PreviewEntry entry,
                                                        final MultiblockControllerDefinition definition,
                                                        final char[][][] chars) {
        final Map<String, MaterialStack> materials = new LinkedHashMap<>();
        final MultiblockPattern pattern = definition.pattern();
        final BlockState controllerState = resolveControllerState(entry.iconStack());

        for (final char[][] layer : chars) {
            for (final char[] row : layer) {
                for (final char symbol : row) {
                    final BlockState state = symbol == pattern.getControllerChar()
                            ? controllerState
                            : definition.defaultCreativeStates().get(symbol);
                    if (state == null || state.isAir()) {
                        continue;
                    }

                    final ItemStack stack = state.getBlock().asItem().getDefaultInstance();
                    if (stack.isEmpty()) {
                        continue;
                    }

                    final String key = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                    materials.compute(key, (ignored, existing) -> existing == null
                            ? new MaterialStack(stack.copyWithCount(1), 1)
                            : new MaterialStack(existing.stack(), existing.count() + 1));
                }
            }
        }

        return materials.values().stream()
                .sorted(Comparator.comparingInt(MaterialStack::count).reversed())
                .toList();
    }

    private static List<Component> collectLegend(final MultiblockPattern pattern, final char[][][] chars) {
        final LinkedHashSet<Character> symbols = new LinkedHashSet<>();
        for (final char[][] layer : chars) {
            for (final char[] row : layer) {
                for (final char symbol : row) {
                    if (!Character.isWhitespace(symbol)) {
                        symbols.add(symbol);
                    }
                }
            }
        }

        final List<Component> lines = new ArrayList<>();
        for (final char symbol : symbols) {
            final Component base = pattern.getLegendName(symbol);
            final List<BlockState> candidates = pattern.getDisplayCandidates(symbol);
            if (candidates.isEmpty()) {
                lines.add(Component.literal(symbol + " - ").append(base));
            } else {
                lines.add(Component.literal(symbol + " - ").append(base).append(" (variants: " + candidates.size() + ")"));
            }
        }
        return lines;
    }

    private static BlockState resolveControllerState(final ItemStack iconStack) {
        if (iconStack.getItem() instanceof final BlockItem blockItem) {
            return blockItem.getBlock().defaultBlockState();
        }
        return Blocks.IRON_BLOCK.defaultBlockState();
    }
}
