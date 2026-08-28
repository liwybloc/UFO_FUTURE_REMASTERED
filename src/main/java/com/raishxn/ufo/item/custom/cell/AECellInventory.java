package com.raishxn.ufo.item.custom.cell;

import appeng.api.config.Actionable;
import appeng.api.config.IncludeExclude;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.core.definitions.AEItems;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.IPartitionList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class AECellInventory implements StorageCell {
    private final AECellData cellData;
    private final Object2LongMap<AEKey> storage;
    private final ItemStack itemStack;
    private final IAECell cellType;
    private final @Nullable ISaveProvider saveContainer;
    private final IPartitionList partitionList;
    private final IncludeExclude partitionMode;

    private long usedBytes;
    private boolean persisted;

    public AECellInventory(
            final AECellData cellData,
            final ItemStack itemStack,
            final IAECell cellType,
            @Nullable final ISaveProvider saveProvider) {
        this.cellData = cellData;
        this.storage = cellData.getStorage();
        this.itemStack = itemStack;
        this.cellType = cellType;
        this.saveContainer = saveProvider;

        final var partitionBuilder = IPartitionList.builder();
        final var upgrades = cellType.getUpgrades(itemStack);
        final ConfigInventory config = cellType instanceof final ICellWorkbenchItem workbenchItem
                ? workbenchItem.getConfigInventory(itemStack)
                : null;
        if (config != null) partitionBuilder.addAll(config.keySet());
        if (upgrades.isInstalled(AEItems.FUZZY_CARD) && cellType instanceof final ICellWorkbenchItem workbenchItem) {
            partitionBuilder.fuzzyMode(workbenchItem.getFuzzyMode(itemStack));
        }
        this.partitionList = partitionBuilder.build();
        this.partitionMode = upgrades.isInstalled(AEItems.INVERTER_CARD)
                ? IncludeExclude.BLACKLIST
                : IncludeExclude.WHITELIST;

        this.usedBytes = calculateUsedBytes();
        updateItemState();
    }

    @Override
    public CellState getStatus() {
        if (this.storage.isEmpty()) return CellState.EMPTY;
        if (this.usedBytes >= this.cellType.getMaxBytes(this.itemStack)) return CellState.FULL;
        if (this.storage.size() >= this.cellType.getMaxTypes(this.itemStack)) return CellState.TYPES_FULL;
        return CellState.NOT_EMPTY;
    }

    @Override
    public double getIdleDrain() {
        return this.cellType.getIdleDrain();
    }

    @Override
    public boolean canFitInsideCell() {
        return this.storage.isEmpty();
    }

    @Override
    public void persist() {
        if (this.persisted) return;
        updateItemState();
        this.persisted = true;
    }

    @Override
    public long insert(final AEKey key, final long amount, final Actionable mode, final IActionSource source) {
        if (amount <= 0L || key.getType() != this.cellType.getKeyType()) return 0L;
        if (this.cellType.isBlacklisted(this.itemStack, key)) return 0L;
        if (!this.partitionList.matchesFilter(key, this.partitionMode)) return 0L;
        if (!canStore(key)) return 0L;

        final long currentAmount = this.storage.getLong(key);
        if (currentAmount == 0L && this.storage.size() >= this.cellType.getMaxTypes(this.itemStack)) return 0L;

        final long amountPerByte = Math.max(1L, key.getType().getAmountPerByte());
        final long currentValueBytes = bytesForAmount(currentAmount, amountPerByte);
        final long currentOverhead = currentAmount > 0L ? this.cellType.getBytesPerType(this.itemStack) : 0L;
        final long otherUsedBytes = Math.max(0L, this.usedBytes - currentValueBytes - currentOverhead);
        final long newOverhead = this.cellType.getBytesPerType(this.itemStack);
        final long availableValueBytes = this.cellType.getMaxBytes(this.itemStack) - otherUsedBytes - newOverhead;
        if (availableValueBytes <= 0L) return 0L;

        final long maximumAmount = saturatingMultiply(availableValueBytes, amountPerByte);
        final long accepted = Math.min(amount, Math.max(0L, maximumAmount - currentAmount));
        if (accepted <= 0L) return 0L;

        if (mode == Actionable.MODULATE) {
            final long newAmount = currentAmount + accepted;
            this.storage.put(key, newAmount);
            this.usedBytes = saturatingAdd(
                    saturatingAdd(otherUsedBytes, newOverhead),
                    bytesForAmount(newAmount, amountPerByte));
            markChanged();
        }
        return accepted;
    }

    @Override
    public long extract(final AEKey key, final long amount, final Actionable mode, final IActionSource source) {
        if (amount <= 0L) return 0L;

        final long currentAmount = this.storage.getLong(key);
        final long extracted = Math.min(amount, currentAmount);
        if (extracted <= 0L) return 0L;

        if (mode == Actionable.MODULATE) {
            final long remaining = currentAmount - extracted;
            if (remaining == 0L) {
                this.storage.removeLong(key);
            } else {
                this.storage.put(key, remaining);
            }
            this.usedBytes = calculateUsedBytes();
            markChanged();
        }
        return extracted;
    }

    @Override
    public void getAvailableStacks(final KeyCounter output) {
        for (final Object2LongMap.Entry<AEKey> entry : this.storage.object2LongEntrySet()) {
            final long amount = entry.getLongValue();
            if (amount <= 0L) continue;

            final long existing = output.get(entry.getKey());
            final long headroom = existing >= Long.MAX_VALUE ? 0L : Long.MAX_VALUE - Math.max(0L, existing);
            final long added = Math.min(amount, headroom);
            if (added > 0L) output.add(entry.getKey(), added);
        }
    }

    @Override
    public Component getDescription() {
        return this.itemStack.getHoverName();
    }

    private boolean canStore(final AEKey key) {
        if (!(key instanceof final AEItemKey itemKey)) return true;
        final StorageCell nestedCell = StorageCells.getCellInventory(itemKey.toStack(), null);
        return nestedCell == null || nestedCell.canFitInsideCell();
    }

    private long calculateUsedBytes() {
        long total = 0L;
        final int bytesPerType = this.cellType.getBytesPerType(this.itemStack);
        for (final Object2LongMap.Entry<AEKey> entry : this.storage.object2LongEntrySet()) {
            final long amount = entry.getLongValue();
            if (amount <= 0L) continue;

            final long amountPerByte = Math.max(1L, entry.getKey().getType().getAmountPerByte());
            total = saturatingAdd(total, bytesPerType);
            total = saturatingAdd(total, bytesForAmount(amount, amountPerByte));
        }
        return total;
    }

    private void markChanged() {
        this.cellData.setDirty();
        this.persisted = false;
        if (this.saveContainer != null) {
            this.saveContainer.saveChanges();
        } else {
            persist();
        }
    }

    private void updateItemState() {
        IAECell.setUsedBytes(this.itemStack, this.usedBytes);
        IAECell.setUsedTypes(this.itemStack, this.storage.size());
        IAECell.setCellState(this.itemStack, getStatus());

        final List<GenericStack> tooltipStacks = new ArrayList<>(5);
        for (final Object2LongMap.Entry<AEKey> entry : this.storage.object2LongEntrySet()) {
            if (entry.getLongValue() <= 0L) continue;
            tooltipStacks.add(new GenericStack(entry.getKey(), entry.getLongValue()));
            if (tooltipStacks.size() == 5) break;
        }
        IAECell.setTooltipStacks(this.itemStack, tooltipStacks);
    }

    private static long bytesForAmount(final long amount, final long amountPerByte) {
        if (amount <= 0L) return 0L;
        return 1L + (amount - 1L) / amountPerByte;
    }

    private static long saturatingAdd(final long first, final long second) {
        if (second > 0L && first > Long.MAX_VALUE - second) return Long.MAX_VALUE;
        return first + second;
    }

    private static long saturatingMultiply(final long first, final long second) {
        if (first <= 0L || second <= 0L) return 0L;
        if (first > Long.MAX_VALUE / second) return Long.MAX_VALUE;
        return first * second;
    }
}
