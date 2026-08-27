package com.raishxn.ufo.item.custom.cell;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.cells.CellState;
import appeng.api.upgrades.IUpgradeableItem;
import appeng.items.storage.StorageTier;
import com.raishxn.ufo.datagen.ModDataComponents;
import net.minecraft.world.item.ItemStack;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Define algumas interfaces de obtenção de informações e alguns métodos para definir/obter rapidamente informações para o ItemStack
 * <p>
 * A principal diferença do IAEUniversalCell é que ele fornece um caminho e métodos simplificados para a interface necessária para o BigInteger.
 *
 * @author Frostbite
 */
public interface IAEBigIntegerCell extends IUpgradeableItem {

    double getIdleDrain();

    StorageTier getTier();
    AEKeyType getKeyType();

    long getMaxBytes(ItemStack stack);
    int getMaxTypes(ItemStack stack);
    int getBytesPerType(ItemStack stack);

    default boolean isBlackListed(final ItemStack stack, final AEKey requestedAddition) {
        return false;
    }

    static BigInteger getUsedBytes(final ItemStack stack) {
        final BigInteger v = stack.get(ModDataComponents.CELL_BYTE_USAGE_BIG.get());
        return v == null ? BigInteger.ZERO : v;
    }

    static void setUsedBytes(final ItemStack stack, final BigInteger usedBytes) {
        stack.set(ModDataComponents.CELL_BYTE_USAGE_BIG.get(), usedBytes);
    }

    static long getUsedTypes(final ItemStack stack) {
        final Long v = stack.get(ModDataComponents.CELL_TYPES_USAGE.get());
        return v == null ? 0L : v;
    }

    static void setUsedTypes(final ItemStack stack, final long usedTypes) {
        stack.set(ModDataComponents.CELL_TYPES_USAGE.get(), usedTypes);
    }

    static CellState getCellState(final ItemStack stack) {
        final String s = stack.get(ModDataComponents.CELL_STATE.get());
        if (s == null) return CellState.EMPTY;
        try {
            return CellState.valueOf(s);
        } catch (final IllegalArgumentException ex) {
            return CellState.EMPTY;
        }
    }

    static void setCellState(final ItemStack stack, final CellState newState) {
        stack.set(ModDataComponents.CELL_STATE.get(), newState.name());
    }

    static List<GenericStack> getTooltipShowStacks(final ItemStack stack) {
        final List<GenericStack> raw = stack.get(ModDataComponents.CELL_SHOW_TOOLTIP_STACKS.get());
        if (raw == null || raw.isEmpty()) return List.of();
        return Collections.unmodifiableList(new ArrayList<>(raw));
    }

    static void setTooltipShowStacks(final ItemStack stack, final List<GenericStack> showStacks) {
        if (showStacks == null || showStacks.isEmpty()) {
            stack.remove(ModDataComponents.CELL_SHOW_TOOLTIP_STACKS.get());
            return;
        }
        final List<GenericStack> cleaned = new ArrayList<>(showStacks.size());
        for (final GenericStack gs : showStacks) {
            if (gs != null) cleaned.add(gs);
        }
        if (cleaned.isEmpty()) {
            stack.remove(ModDataComponents.CELL_SHOW_TOOLTIP_STACKS.get());
        } else {
            stack.set(ModDataComponents.CELL_SHOW_TOOLTIP_STACKS.get(), cleaned);
        }
    }
}
