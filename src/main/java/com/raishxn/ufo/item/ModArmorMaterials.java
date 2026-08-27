package com.raishxn.ufo.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

public final class ModArmorMaterials {
    private static final Map<ArmorType, Integer> DEFENSE = Map.of(
            ArmorType.BOOTS, 4, ArmorType.LEGGINGS, 7, ArmorType.CHESTPLATE, 9,
            ArmorType.HELMET, 4, ArmorType.BODY, 12);

    public static final ArmorMaterial THERMAL_EXOSUIT = new ArmorMaterial(
            38, DEFENSE, 15, SoundEvents.ARMOR_EQUIP_IRON, 3.2F, 0.15F,
            ItemTags.REPAIRS_NETHERITE_ARMOR, EquipmentAssets.NETHERITE);
    public static final ArmorMaterial UFO_ARMOR = new ArmorMaterial(
            40, DEFENSE, 15, SoundEvents.ARMOR_EQUIP_DIAMOND, 3.0F, 0.2F,
            ItemTags.REPAIRS_DIAMOND_ARMOR, EquipmentAssets.DIAMOND);
    public static final ArmorMaterial ASTRAL_NEXUS = new ArmorMaterial(
            42, DEFENSE, 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.4F, 0.22F,
            ItemTags.REPAIRS_NETHERITE_ARMOR, EquipmentAssets.NETHERITE);

    private ModArmorMaterials() {}
}
