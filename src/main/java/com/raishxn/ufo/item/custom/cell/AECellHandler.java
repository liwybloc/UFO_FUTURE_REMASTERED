package com.raishxn.ufo.item.custom.cell;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import com.raishxn.ufo.datagen.ModDataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public final class AECellHandler implements ICellHandler {
    public static final AECellHandler INSTANCE = new AECellHandler();

    private AECellHandler() {}

    @Override
    public boolean isCell(final ItemStack itemStack) {
        return itemStack.getItem() instanceof IAECell && itemStack.getCount() == 1;
    }

    @Override
    public @Nullable StorageCell getCellInventory(
            final ItemStack itemStack,
            @Nullable final ISaveProvider saveProvider) {
        if (ServerLifecycleHooks.getCurrentServer() == null) return null;
        if (!(itemStack.getItem() instanceof final IAECell cellItem)) return null;
        if (itemStack.getCount() != 1) return null;

        final boolean hadCellId = itemStack.has(ModDataComponents.CELL_UUID.get());
        final AECellData cellData = AECellData.computeIfAbsent(itemStack);
        if (cellData == null) return null;
        if (!hadCellId && saveProvider != null) saveProvider.saveChanges();

        return new AECellInventory(cellData, itemStack, cellItem, saveProvider);
    }
}
