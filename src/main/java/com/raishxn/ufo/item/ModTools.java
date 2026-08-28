package com.raishxn.ufo.item;

import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.item.custom.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.util.Unit;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.raishxn.ufo.item.ModItems.ITEMS;

public final class ModTools {
    private static Item.Properties unbreakableToolProperties(final Item.Properties properties) {
        return properties.component(DataComponents.UNBREAKABLE, Unit.INSTANCE);
    }

    public static final DeferredItem<Item> UFO_STAFF = ITEMS.registerItem("ufo_staff",
            properties -> new UfoStaffItem(properties.component(ModDataComponents.TOOL_MODE_INDEX.get(), 0).stacksTo(1)));

    public static final DeferredItem<Item> UFO_SWORD = ITEMS.registerItem("ufo_sword",
            properties -> new UfoEnergySwordItem(ModToolTiers.UFO, unbreakableToolProperties(properties)
                    .sword(ModToolTiers.UFO, 5, -2.4f)
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0).stacksTo(1)));

    public static final DeferredItem<Item> REALITY_RIPPER = ITEMS.registerItem("reality_ripper",
            properties -> new RealityRipperItem(ModToolTiers.UFO, unbreakableToolProperties(properties)
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> UFO_PICKAXE = ITEMS.registerItem("ufo_pickaxe",
            properties -> new UfoEnergyPickaxeItem(ModToolTiers.UFO, unbreakableToolProperties(properties)
                    .pickaxe(ModToolTiers.UFO, 1.0F, -2.8f)
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0)
                    .component(ModDataComponents.FAST_MODE.get(), false).stacksTo(1)));

    public static final DeferredItem<ShovelItem> UFO_SHOVEL = ITEMS.registerItem("ufo_shovel",
            properties -> new UfoEnergyShovelItem(ModToolTiers.UFO, unbreakableToolProperties(properties)
                    .shovel(ModToolTiers.UFO, 1.5F, -3.0f)
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0)
                    .component(ModDataComponents.FAST_MODE.get(), false).stacksTo(1)));

    public static final DeferredItem<AxeItem> UFO_AXE = ITEMS.registerItem("ufo_axe",
            properties -> new UfoEnergyAxeItem(ModToolTiers.UFO, unbreakableToolProperties(properties)
                    .axe(ModToolTiers.UFO, 6.0F, -3.2f)
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0)
                    .component(ModDataComponents.FAST_MODE.get(), false).stacksTo(1)));

    public static final DeferredItem<HoeItem> UFO_HOE = ITEMS.registerItem("ufo_hoe",
            properties -> new UfoEnergyHoeItem(ModToolTiers.UFO, unbreakableToolProperties(properties)
                    .hoe(ModToolTiers.UFO, 0F, -3.0f)
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0)
                    .component(DataComponents.CUSTOM_DATA, CustomData.of(new CompoundTag())).stacksTo(1)));

    public static final DeferredItem<FishingRodItem> UFO_FISHING_ROD = ITEMS.registerItem("ufo_fishing_rod",
            properties -> new UfoEnergyFishingRodItem(unbreakableToolProperties(properties).durability(500)
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0).stacksTo(1)));

    public static final DeferredItem<Item> UFO_GREATSWORD = ITEMS.registerItem("ufo_greatsword",
            properties -> new UfoEnergyGreatswordItem(ModToolTiers.UFO, unbreakableToolProperties(properties)
                    .sword(ModToolTiers.UFO, 8, -3.0f)
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0).stacksTo(1)));

    public static final DeferredItem<HammerItem> UFO_HAMMER = ITEMS.registerItem("ufo_hammer",
            properties -> new HammerItem(ModToolTiers.UFO, unbreakableToolProperties(properties)
                    .tool(ModToolTiers.UFO, net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE, 7.0F, -3.4f, 0.0F)
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0).stacksTo(1)));

    public static final DeferredItem<BowItem> UFO_BOW = ITEMS.registerItem("ufo_bow",
            properties -> new UfoEnergyBowItem(unbreakableToolProperties(properties).durability(5000)
                    .component(ModDataComponents.TOOL_MODE_INDEX.get(), 0)
                    .component(ModDataComponents.BOW_FAST_MODE.get(), false).stacksTo(1)));

    public static void register(final IEventBus eventBus) {
    }
}
