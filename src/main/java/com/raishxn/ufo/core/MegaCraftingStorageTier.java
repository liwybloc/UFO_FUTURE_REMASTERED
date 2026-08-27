package com.raishxn.ufo.core;

import appeng.block.crafting.ICraftingUnitType;
import com.raishxn.ufo.block.ModBlocks;
import net.minecraft.world.item.Item;

/**
 * Define os novos tiers de Crafting Storage de alta capacidade.
 */
public enum MegaCraftingStorageTier implements ICraftingUnitType {

    STORAGE_1B("1b", "1B", 1024L * 1024L * 1024L),

    STORAGE_50B("50b", "50B", 50L * 1024L * 1024L * 1024L),

    STORAGE_1T("1t", "1T", 1024L * 1024L * 1024L * 1024L),

    STORAGE_250T("250t", "250T", 250L * 1024L * 1024L * 1024L * 1024L),

    STORAGE_1QD("1qd", "1QD", 1024L * 1024L * 1024L * 1024L * 1024L);

    private final String registryId;
    private final String displayName;
    private final long bytes; // O nome da variável foi mantido

    MegaCraftingStorageTier(final String registryId, final String displayName, final long bytes) {
        this.registryId = registryId;
        this.displayName = displayName;
        this.bytes = bytes;
    }

    public String getRegistryId() {
        return registryId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getBytes() {
        return bytes;
    }


    @Override
    public long getStorageBytes() {
        return this.bytes;
    }

    @Override
    public int getAcceleratorThreads() {
        return 0;
    }

    @Override
    public Item getItemFromType() {
        return ModBlocks.CRAFTING_STORAGE_BLOCKS.get(this).get().asItem();
    }
}
