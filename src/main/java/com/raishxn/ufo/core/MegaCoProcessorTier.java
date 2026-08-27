package com.raishxn.ufo.core;

import appeng.block.crafting.ICraftingUnitType;
import com.raishxn.ufo.block.ModBlocks;
import net.minecraft.world.item.Item;

public enum MegaCoProcessorTier implements ICraftingUnitType {
    COPROCESSOR_50M("50m", "50M", 50_000_000),
    COPROCESSOR_150M("150m", "150M", 150_000_000),
    COPROCESSOR_300M("300m", "300M", 300_000_000),
    COPROCESSOR_750M("750m", "750M", 750_000_000),
    COPROCESSOR_2B("2b", "2B", 2_000_000_000);

    private final String registryId;
    private final String displayName;
    private final int acceleratorThreads;

    MegaCoProcessorTier(final String registryId, final String displayName, final int acceleratorThreads) {
        this.registryId = registryId;
        this.displayName = displayName;
        this.acceleratorThreads = acceleratorThreads;
    }

    public String getRegistryId() {
        return registryId;
    }

    public String getDisplayName() {
        return displayName;
    }


    @Override
    public int getAcceleratorThreads() {
        return this.acceleratorThreads;
    }

    @Override
    public long getStorageBytes() {
        return 0;
    }

    @Override
    public Item getItemFromType() {
        return ModBlocks.CO_PROCESSOR_BLOCKS.get(this).get().asItem();
    }
}
