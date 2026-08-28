package com.raishxn.ufo.item;

import appeng.api.ids.AEBlockIds;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.raishxn.ufo.item.ModItems.ITEMS;

public final class ModCells {

    public static final DeferredItem<Item> INFINITY_WATER_CELL = ITEMS.registerItem("infinity_water_cell",
            properties -> new InfinityCell(properties, () -> AEFluidKey.of(Fluids.WATER),
                    ChatFormatting.WHITE, ChatFormatting.AQUA, ChatFormatting.BLUE, ChatFormatting.AQUA));
    public static final DeferredItem<Item> INFINITY_COBBLESTONE_CELL = ITEMS.registerItem("infinity_cobblestone_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.COBBLESTONE),
                    ChatFormatting.WHITE, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY, ChatFormatting.GRAY));
    public static final DeferredItem<Item> INFINITY_COBBLED_DEEPSLATE_CELL = ITEMS.registerItem("infinity_cobbled_deepslate_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.COBBLED_DEEPSLATE),
                    ChatFormatting.GRAY, ChatFormatting.DARK_GRAY, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY));
    public static final DeferredItem<Item> INFINITY_END_STONE_CELL = ITEMS.registerItem("infinity_end_stone_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.END_STONE),
                    ChatFormatting.WHITE, ChatFormatting.YELLOW, ChatFormatting.GOLD, ChatFormatting.YELLOW));
    public static final DeferredItem<Item> INFINITY_LAVA_CELL = ITEMS.registerItem("infinity_lava_cell",
            properties -> new InfinityCell(properties, () -> AEFluidKey.of(Fluids.LAVA),
                    ChatFormatting.WHITE, ChatFormatting.YELLOW, ChatFormatting.GOLD, ChatFormatting.RED));
    public static final DeferredItem<Item> INFINITY_NETHERRACK_CELL = ITEMS.registerItem("infinity_netherrack_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.NETHERRACK),
                    ChatFormatting.RED, ChatFormatting.DARK_RED, ChatFormatting.RED));
    public static final DeferredItem<Item> INFINITY_SAND_CELL = ITEMS.registerItem("infinity_sand_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.SAND),
                    ChatFormatting.YELLOW, ChatFormatting.WHITE, ChatFormatting.GOLD, ChatFormatting.WHITE));
    public static final DeferredItem<Item> INFINITY_SKY_STONE_CELL = ITEMS.registerItem("infinity_sky_stone_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(BuiltInRegistries.BLOCK.getValue(AEBlockIds.SKY_STONE_BLOCK)),
                    ChatFormatting.DARK_GRAY, ChatFormatting.GRAY, ChatFormatting.BLACK, ChatFormatting.GRAY));
    public static final DeferredItem<Item> INFINITY_OBSIDIAN_CELL = ITEMS.registerItem("infinity_obsidian_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.OBSIDIAN),
                    ChatFormatting.DARK_PURPLE, ChatFormatting.BLACK, ChatFormatting.DARK_PURPLE));
    public static final DeferredItem<Item> INFINITY_GRAVEL_CELL = ITEMS.registerItem("infinity_gravel_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.GRAVEL),
                    ChatFormatting.GRAY, ChatFormatting.DARK_GRAY, ChatFormatting.GRAY));
    public static final DeferredItem<Item> INFINITY_OAK_LOG_CELL = ITEMS.registerItem("infinity_oak_log_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.OAK_LOG),
                    ChatFormatting.GOLD, ChatFormatting.DARK_RED, ChatFormatting.GOLD));
    public static final DeferredItem<Item> INFINITY_GLASS_CELL = ITEMS.registerItem("infinity_glass_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.GLASS),
                    ChatFormatting.WHITE, ChatFormatting.AQUA, ChatFormatting.WHITE));
    public static final DeferredItem<Item> INFINITY_AMETHYST_SHARD_CELL = ITEMS.registerItem("infinity_amethyst_shard_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.AMETHYST_SHARD),
                    ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE));

    public static final DeferredItem<Item> INFINITY_WHITE_DYE_CELL = ITEMS.registerItem("infinity_white_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.WHITE_DYE), ChatFormatting.WHITE, ChatFormatting.GRAY));
    public static final DeferredItem<Item> INFINITY_ORANGE_DYE_CELL = ITEMS.registerItem("infinity_orange_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.ORANGE_DYE), ChatFormatting.GOLD, ChatFormatting.RED));
    public static final DeferredItem<Item> INFINITY_MAGENTA_DYE_CELL = ITEMS.registerItem("infinity_magenta_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.MAGENTA_DYE), ChatFormatting.LIGHT_PURPLE, ChatFormatting.RED));
    public static final DeferredItem<Item> INFINITY_LIGHT_BLUE_DYE_CELL = ITEMS.registerItem("infinity_light_blue_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.LIGHT_BLUE_DYE), ChatFormatting.AQUA, ChatFormatting.WHITE));
    public static final DeferredItem<Item> INFINITY_YELLOW_DYE_CELL = ITEMS.registerItem("infinity_yellow_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.YELLOW_DYE), ChatFormatting.YELLOW, ChatFormatting.WHITE));
    public static final DeferredItem<Item> INFINITY_LIME_DYE_CELL = ITEMS.registerItem("infinity_lime_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.LIME_DYE), ChatFormatting.GREEN, ChatFormatting.YELLOW));
    public static final DeferredItem<Item> INFINITY_PINK_DYE_CELL = ITEMS.registerItem("infinity_pink_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.PINK_DYE), ChatFormatting.RED, ChatFormatting.WHITE));
    public static final DeferredItem<Item> INFINITY_GRAY_DYE_CELL = ITEMS.registerItem("infinity_gray_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.GRAY_DYE), ChatFormatting.GRAY, ChatFormatting.DARK_GRAY));
    public static final DeferredItem<Item> INFINITY_LIGHT_GRAY_DYE_CELL = ITEMS.registerItem("infinity_light_gray_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.LIGHT_GRAY_DYE), ChatFormatting.GRAY, ChatFormatting.WHITE));
    public static final DeferredItem<Item> INFINITY_CYAN_DYE_CELL = ITEMS.registerItem("infinity_cyan_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.CYAN_DYE), ChatFormatting.DARK_AQUA, ChatFormatting.AQUA));
    public static final DeferredItem<Item> INFINITY_PURPLE_DYE_CELL = ITEMS.registerItem("infinity_purple_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.PURPLE_DYE), ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE));
    public static final DeferredItem<Item> INFINITY_BLUE_DYE_CELL = ITEMS.registerItem("infinity_blue_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.BLUE_DYE), ChatFormatting.DARK_BLUE, ChatFormatting.BLUE));
    public static final DeferredItem<Item> INFINITY_BROWN_DYE_CELL = ITEMS.registerItem("infinity_brown_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.BROWN_DYE), ChatFormatting.DARK_RED, ChatFormatting.GOLD));
    public static final DeferredItem<Item> INFINITY_GREEN_DYE_CELL = ITEMS.registerItem("infinity_green_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.GREEN_DYE), ChatFormatting.DARK_GREEN, ChatFormatting.GREEN));
    public static final DeferredItem<Item> INFINITY_RED_DYE_CELL = ITEMS.registerItem("infinity_red_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.RED_DYE), ChatFormatting.DARK_RED, ChatFormatting.RED));
    public static final DeferredItem<Item> INFINITY_BLACK_DYE_CELL = ITEMS.registerItem("infinity_black_dye_cell",
            properties -> new InfinityCell(properties, () -> AEItemKey.of(Items.BLACK_DYE), ChatFormatting.BLACK, ChatFormatting.DARK_GRAY));

    public static final DeferredItem<Item> INFINITY_GENESIS_CELL = ITEMS.registerItem("infinity_genesis_cell",
            properties -> new InfinityGenesisCell(properties));

    public static void register(final IEventBus eventBus) {
    }
}
