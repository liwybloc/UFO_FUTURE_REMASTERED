package com.raishxn.ufo.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface IHasModeHUD {
    Component getModeHudComponent(ItemStack stack);
}
