package com.raishxn.ufo.api.tutorial;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record UfoTutorialScene(
        ResourceLocation id,
        Component title,
        List<UfoTutorialStep> steps) {

    public UfoTutorialScene {
        steps = List.copyOf(steps);
    }
}
