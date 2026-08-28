package com.raishxn.ufo.item.custom.cell;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.cells.CellState;
import appeng.api.upgrades.IUpgradeableItem;
import appeng.items.storage.StorageTier;
import com.raishxn.ufo.datagen.ModDataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface IAECell extends IUpgradeableItem {
    double getIdleDrain();

    StorageTier getTier();

    AEKeyType getKeyType();

    long getMaxBytes(ItemStack stack);

    int getMaxTypes(ItemStack stack);

    int getBytesPerType(ItemStack stack);

    default boolean isBlacklisted(final ItemStack stack, final AEKey requestedAddition) {
        return false;
    }

    static long getUsedBytes(final ItemStack stack) {
        return Math.max(0L, stack.getOrDefault(ModDataComponents.CELL_BYTE_USAGE.get(), 0L));
    }

    static void setUsedBytes(final ItemStack stack, final long usedBytes) {
        stack.set(ModDataComponents.CELL_BYTE_USAGE.get(), Math.max(0L, usedBytes));
    }

    static int getUsedTypes(final ItemStack stack) {
        return Math.max(0, stack.getOrDefault(ModDataComponents.CELL_TYPES_USAGE.get(), 0));
    }

    static void setUsedTypes(final ItemStack stack, final int usedTypes) {
        stack.set(ModDataComponents.CELL_TYPES_USAGE.get(), Math.max(0, usedTypes));
    }

    static CellState getCellState(final ItemStack stack) {
        final String state = stack.get(ModDataComponents.CELL_STATE.get());
        if (state == null) return CellState.EMPTY;

        try {
            return CellState.valueOf(state);
        } catch (final IllegalArgumentException exception) {
            return CellState.EMPTY;
        }
    }

    static void setCellState(final ItemStack stack, final CellState state) {
        stack.set(ModDataComponents.CELL_STATE.get(), state.name());
    }

    static List<GenericStack> getTooltipStacks(final ItemStack stack) {
        final List<GenericStack> stacks = stack.get(ModDataComponents.CELL_SHOW_TOOLTIP_STACKS.get());
        if (stacks == null || stacks.isEmpty()) return List.of();
        return Collections.unmodifiableList(new ArrayList<>(stacks));
    }

    static void setTooltipStacks(final ItemStack stack, final List<GenericStack> stacks) {
        if (stacks == null || stacks.isEmpty()) {
            stack.remove(ModDataComponents.CELL_SHOW_TOOLTIP_STACKS.get());
            return;
        }

        final List<GenericStack> validStacks = new ArrayList<>(stacks.size());
        for (final GenericStack genericStack : stacks) {
            if (genericStack != null) validStacks.add(genericStack);
        }

        if (validStacks.isEmpty()) {
            stack.remove(ModDataComponents.CELL_SHOW_TOOLTIP_STACKS.get());
        } else {
            stack.set(ModDataComponents.CELL_SHOW_TOOLTIP_STACKS.get(), validStacks);
        }
    }
}
