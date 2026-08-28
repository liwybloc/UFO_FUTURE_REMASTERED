package com.raishxn.ufo.item.custom.cell;

import appeng.api.stacks.AEKey;
import com.raishxn.ufo.datagen.ModDataComponents;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AECellData {
    private static final Map<UUID, AECellData> RUNTIME_DATA = new ConcurrentHashMap<>();

    private final Object2LongMap<AEKey> storage;

    private AECellData(final Object2LongMap<AEKey> storage) {
        this.storage = storage;
        this.storage.defaultReturnValue(0L);
    }

    public Object2LongMap<AEKey> getStorage() {
        return this.storage;
    }

    public void setDirty() {
    }

    public static @Nullable AECellData getCellDataByUuid(final UUID uuid) {
        if (ServerLifecycleHooks.getCurrentServer() == null) return null;
        return RUNTIME_DATA.get(uuid);
    }

    public static @Nullable AECellData computeIfAbsent(final ItemStack stack) {
        if (ServerLifecycleHooks.getCurrentServer() == null) return null;

        final UUID existingUuid = stack.get(ModDataComponents.CELL_UUID.get());
        if (existingUuid != null) {
            final AECellData existingData = getCellDataByUuid(existingUuid);
            if (existingData != null) return existingData;
        }

        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (RUNTIME_DATA.containsKey(uuid));

        stack.set(ModDataComponents.CELL_UUID.get(), uuid);
        final AECellData data = new AECellData(new Object2LongOpenHashMap<>());
        RUNTIME_DATA.put(uuid, data);
        return data;
    }
}
