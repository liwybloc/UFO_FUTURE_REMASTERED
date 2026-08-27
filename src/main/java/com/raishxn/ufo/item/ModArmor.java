package com.raishxn.ufo.item;

import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.item.custom.AstralNexusArmorItem;
import com.raishxn.ufo.item.custom.UfoArmorItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

import static com.raishxn.ufo.item.ModItems.ITEMS;

public class ModArmor {

    public static final DeferredItem<Item> UFO_HELMET = ITEMS.registerItem("ufo_helmet",
            properties -> new UfoArmorItem(ModArmorMaterials.UFO_ARMOR, ArmorType.HELMET, properties
                    .component(ModDataComponents.ENERGY.get(), 0).stacksTo(1)));

    public static final DeferredItem<Item> UFO_CHESTPLATE = ITEMS.registerItem("ufo_chestplate",
            properties -> new UfoArmorItem(ModArmorMaterials.UFO_ARMOR, ArmorType.CHESTPLATE, properties
                    .component(ModDataComponents.ENERGY.get(), 0).stacksTo(1)));

    public static final DeferredItem<Item> UFO_LEGGINGS = ITEMS.registerItem("ufo_leggings",
            properties -> new UfoArmorItem(ModArmorMaterials.UFO_ARMOR, ArmorType.LEGGINGS, properties
                    .component(ModDataComponents.ENERGY.get(), 0).stacksTo(1)));

    public static final DeferredItem<Item> UFO_BOOTS = ITEMS.registerItem("ufo_boots",
            properties -> new UfoArmorItem(ModArmorMaterials.UFO_ARMOR, ArmorType.BOOTS, properties
                    .component(ModDataComponents.ENERGY.get(), 0).stacksTo(1)));

    public static final DeferredItem<Item> ASTRAL_NEXUS_HELMET = ITEMS.registerItem("astral_nexus_helmet",
            properties -> new AstralNexusArmorItem(ModArmorMaterials.ASTRAL_NEXUS, ArmorType.HELMET, properties
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> ASTRAL_NEXUS_CHESTPLATE = ITEMS.registerItem("astral_nexus_chestplate",
            properties -> new AstralNexusArmorItem(ModArmorMaterials.ASTRAL_NEXUS, ArmorType.CHESTPLATE, properties
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> ASTRAL_NEXUS_LEGGINGS = ITEMS.registerItem("astral_nexus_leggings",
            properties -> new AstralNexusArmorItem(ModArmorMaterials.ASTRAL_NEXUS, ArmorType.LEGGINGS, properties
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final DeferredItem<Item> ASTRAL_NEXUS_BOOTS = ITEMS.registerItem("astral_nexus_boots",
            properties -> new AstralNexusArmorItem(ModArmorMaterials.ASTRAL_NEXUS, ArmorType.BOOTS, properties
                    .rarity(Rarity.EPIC)
                    .fireResistant()));

    public static final Supplier<Item> THERMAL_RESISTOR_PLATING = ITEMS.registerItem(
            "thermal_resistor_plating",
            properties -> new Item(properties.fireResistant())
    );

    public static final Supplier<Item> THERMAL_RESISTOR_MASK = ITEMS.registerItem("thermal_resistor_mask",
            properties -> new UfoArmorItem(ModArmorMaterials.THERMAL_EXOSUIT, ArmorType.HELMET, properties));

    public static final Supplier<Item> THERMAL_RESISTOR_CHEST = ITEMS.registerItem("thermal_resistor_chest",
            properties -> new UfoArmorItem(ModArmorMaterials.THERMAL_EXOSUIT, ArmorType.CHESTPLATE, properties));

    public static final Supplier<Item> THERMAL_RESISTOR_PANTS = ITEMS.registerItem("thermal_resistor_pants",
            properties -> new UfoArmorItem(ModArmorMaterials.THERMAL_EXOSUIT, ArmorType.LEGGINGS, properties));

    public static final Supplier<Item> THERMAL_RESISTOR_BOOTS = ITEMS.registerItem("thermal_resistor_boots",
            properties -> new UfoArmorItem(ModArmorMaterials.THERMAL_EXOSUIT, ArmorType.BOOTS, properties));

    public static void register(final IEventBus eventBus) {
    }
}
