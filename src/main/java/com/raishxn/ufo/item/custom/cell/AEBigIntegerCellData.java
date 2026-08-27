package com.raishxn.ufo.item.custom.cell;

import appeng.api.stacks.AEKey;
import com.raishxn.ufo.init.OCDataComponents;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/** 基于AEUniversalCellData的BigInteger版本，无其他逻辑更变，
 *  所有调试信息版本也仍使用AEUniversalCellData统一通知。
 *
 * @author Frostbite
 */
public class AEBigIntegerCellData
{
    public void setDirty() {
    }

    public static final String INV_SAVED_TAG = "inventory";
    private static final String ENTRIES_TAG = "entries";
    private static final String ERROR_ENTRIES_TAG = "error_entries";
    private static final String ENTRY_KEY_TAG = "key";
    private static final String ENTRY_AMOUNT_TAG = "amount";
    private static final String SAVED_FOLDER_NAME = "ae_universal_cell_data";
    private final Object2ObjectMap<AEKey, BigInteger> storage;
    private final ObjectArrayList<CompoundTag> pendingReadErrors;

    public AEBigIntegerCellData(@NotNull final Object2ObjectMap<AEKey, BigInteger> storage)
    {
        this(storage, new ObjectArrayList<>());
    }

    private AEBigIntegerCellData(@NotNull final Object2ObjectMap<AEKey, BigInteger> storage,
                                @NotNull final ObjectArrayList<CompoundTag> pendingReadErrors)
    {
        this.storage = storage;
        this.storage.defaultReturnValue(BigInteger.ZERO);
        this.pendingReadErrors = pendingReadErrors;
    }
    private static final java.util.Map<UUID, AEBigIntegerCellData> RUNTIME_DATA = new java.util.concurrent.ConcurrentHashMap<>();
    public @NotNull Object2ObjectMap<AEKey, BigInteger> getOriginalStorage()
    {
        return storage;
    }
    public static @Nullable AEBigIntegerCellData getCellDataByUUID(@NotNull final UUID uuid)
    {
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return null;

        ensureSaveDirExists(server);

        return RUNTIME_DATA.get(uuid);
    }
    public static @Nullable AEBigIntegerCellData computeIfAbsentCellDataForItemStack(@NotNull final ItemStack itemStack)
    {
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return null;

        ensureSaveDirExists(server);

        final UUID existing = itemStack.get(OCDataComponents.CELL_UUID.get());
        if (existing != null) {
            final AEBigIntegerCellData data = getCellDataByUUID(existing);
            if (data != null) {
                return data;
            }
        }
        UUID fresh;
        do {
            fresh = UUID.randomUUID();
        } while (getCellDataByUUID(fresh) != null);

        itemStack.set(OCDataComponents.CELL_UUID.get(), fresh);
        final Object2ObjectOpenHashMap<AEKey, BigInteger> s = new Object2ObjectOpenHashMap<>();
        s.defaultReturnValue(BigInteger.ZERO);
        final AEBigIntegerCellData newData = new AEBigIntegerCellData(s);
        RUNTIME_DATA.put(fresh, newData);
        return newData;
    }

    private static String makeKey(@NotNull final UUID uuid)
    {
        return SAVED_FOLDER_NAME + "/" + uuid;
    }

    private static void ensureSaveDirExists(@NotNull final MinecraftServer server)
    {
        final Path dir = server.getWorldPath(LevelResource.ROOT)
                .resolve("data")
                .resolve(SAVED_FOLDER_NAME);
        try
        {
            Files.createDirectories(dir);
        }
        catch(final IOException e)
        {
            System.err.println("[AEUniversalCellData] Failed to create save directory: " + dir + " : " + e);
        }
    }
    private static void addTo(final Object2ObjectMap<AEKey, BigInteger> map, final AEKey key, final BigInteger delta)
    {
        if (delta == null) return;
        if (delta.signum() <= 0) return;
        final BigInteger prev = map.getOrDefault(key, BigInteger.ZERO);
        final BigInteger now = prev.add(delta);
        if (now.signum() == 0)
        {
            map.remove(key);
        }
        else
        {
            map.put(key, now);
        }
    }
}
