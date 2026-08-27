package com.raishxn.ufo.item.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public class ThermalArmorItem extends Item implements IThermalArmor {

    public ThermalArmorItem(final ArmorMaterial material, final ArmorType type, final Item.Properties properties) {
        super(properties.humanoidArmor(material, type));
    }
}
