package com.raishxn.ufo.item;

import appeng.api.stacks.AEKeyType;
import appeng.items.materials.StorageComponentItem;
import appeng.items.storage.StorageTier;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.item.custom.AnimatedNameItem;
import com.raishxn.ufo.item.custom.cell.AEBigIntegerCellItem;
import com.raishxn.ufo.item.custom.cell.AnimatedAEBigIntegerCellItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCellItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(UfoMod.MOD_ID);

    public static final DeferredHolder<Item, Item> WHITE_DWARF_ITEM_CELL_HOUSING = ITEMS.registerItem("white_dwarf_item_cell_housing",
            properties -> new AnimatedNameItem(properties, ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY));
    public static final DeferredHolder<Item, Item> NEUTRON_FLUID_CELL_HOUSING = ITEMS.registerItem("neutron_fluid_cell_housing",
            properties -> new AnimatedNameItem(properties, ChatFormatting.BLUE, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA));

    public static final DeferredHolder<Item, Item> CELL_COMPONENT_40M = component("40m", 40 * 1024);
    public static final DeferredHolder<Item, Item> CELL_COMPONENT_100M = component("100m", 100 * 1024);
    public static final DeferredHolder<Item, Item> CELL_COMPONENT_250M = component("250m", 250 * 1024);
    public static final DeferredHolder<Item, Item> CELL_COMPONENT_750M = component("750m", 750 * 1024);
    public static final DeferredHolder<Item, Item> CELL_COMPONENT_INFINITY = component("infinity", Integer.MAX_VALUE);
    public static final StorageTier TIER_40M = new StorageTier(11, "40m", 40_000_000, 5.5D, CELL_COMPONENT_40M);
    public static final StorageTier TIER_100M = new StorageTier(12, "100m", 100_000_000, 6.0D, CELL_COMPONENT_100M);
    public static final StorageTier TIER_250M = new StorageTier(13, "250m", 250_000_000, 6.5D, CELL_COMPONENT_250M);
    public static final StorageTier TIER_750M = new StorageTier(14, "750m", 750_000_000, 7.0D, CELL_COMPONENT_750M);
    public static final StorageTier TIER_INFINITY = new StorageTier(15, "infinity", Integer.MAX_VALUE, 7.5D, CELL_COMPONENT_INFINITY);

    
    public static final DeferredHolder<Item, AEBigIntegerCellItem> ITEM_CELL_40M = ITEMS.registerItem("white_dwarf_cell_echo",
            properties -> new AnimatedAEBigIntegerCellItem(properties.stacksTo(1), TIER_40M.idleDrain(), AEKeyType.items(), TIER_40M, "item.ufo.white_dwarf_item_cell", "ufo.cell_tier.echo", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY}, ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.RED));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> ITEM_CELL_100M = ITEMS.registerItem("white_dwarf_cell_beaco",
            properties -> new AnimatedAEBigIntegerCellItem(properties.stacksTo(1), TIER_100M.idleDrain(), AEKeyType.items(), TIER_100M, "item.ufo.white_dwarf_item_cell", "ufo.cell_tier.beacon", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY}, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> ITEM_CELL_250M = ITEMS.registerItem("white_dwarf_cell_nexus",
            properties -> new AnimatedAEBigIntegerCellItem(properties.stacksTo(1), TIER_250M.idleDrain(), AEKeyType.items(), TIER_250M, "item.ufo.white_dwarf_item_cell", "ufo.cell_tier.nexus", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY}, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> ITEM_CELL_750M = ITEMS.registerItem("white_dwarf_cell_core",
            properties -> new AnimatedAEBigIntegerCellItem(properties.stacksTo(1), TIER_750M.idleDrain(), AEKeyType.items(), TIER_750M, "item.ufo.white_dwarf_item_cell", "ufo.cell_tier.core", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY}, ChatFormatting.BLUE, ChatFormatting.DARK_PURPLE));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> ITEM_CELL_SINGULARITY = ITEMS.registerItem("white_dwarf_cell_singularity",
            properties -> new AnimatedAEBigIntegerCellItem(properties.stacksTo(1), 7.5D, AEKeyType.items(), TIER_INFINITY, "item.ufo.white_dwarf_item_cell", "ufo.cell_tier.singularity", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY}, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN));

    public static final DeferredHolder<Item, AEBigIntegerCellItem> FLUID_CELL_40M = ITEMS.registerItem("neutron_star_reservoir_echo",
            properties -> new AnimatedAEBigIntegerCellItem(properties.stacksTo(1), TIER_40M.idleDrain(), AEKeyType.fluids(), TIER_40M, "item.ufo.neutron_star_fluid_cell", "ufo.cell_tier.echo", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA}, ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.RED));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> FLUID_CELL_100M = ITEMS.registerItem("neutron_star_reservoir_beaco",
            properties -> new AnimatedAEBigIntegerCellItem(properties.stacksTo(1), TIER_100M.idleDrain(), AEKeyType.fluids(), TIER_100M, "item.ufo.neutron_star_fluid_cell", "ufo.cell_tier.beacon", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA}, ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> FLUID_CELL_250M = ITEMS.registerItem("neutron_star_reservoir_nexus",
            properties -> new AnimatedAEBigIntegerCellItem(properties.stacksTo(1), TIER_250M.idleDrain(), AEKeyType.fluids(), TIER_250M, "item.ufo.neutron_star_fluid_cell", "ufo.cell_tier.nexus", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA}, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> FLUID_CELL_750M = ITEMS.registerItem("neutron_star_reservoir_core",
            properties -> new AnimatedAEBigIntegerCellItem(properties.stacksTo(1), TIER_750M.idleDrain(), AEKeyType.fluids(), TIER_750M, "item.ufo.neutron_star_fluid_cell", "ufo.cell_tier.core", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA}, ChatFormatting.BLUE, ChatFormatting.DARK_PURPLE));
    public static final DeferredHolder<Item, AEBigIntegerCellItem> FLUID_CELL_SINGULARITY = ITEMS.registerItem("neutron_star_reservoir_singularity",
            properties -> new AnimatedAEBigIntegerCellItem(properties.stacksTo(1), 7.5D, AEKeyType.fluids(), TIER_INFINITY, "item.ufo.neutron_star_fluid_cell", "ufo.cell_tier.singularity", new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.BLUE, ChatFormatting.DARK_BLUE, ChatFormatting.AQUA}, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN));

    private static DeferredHolder<Item, Item> component(final String idSuffix, final int kibiBytes) {
        final String id = "storage_cell_side_" + idSuffix;
        return ITEMS.registerItem(id, properties -> new StorageComponentItem(properties, kibiBytes));
    }

    public static void register(final IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
