package com.raishxn.ufo.api.tutorial;

import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public record UfoTutorialEntry(
        Identifier id,
        Component title,
        MultiblockControllerDefinitions.PreviewEntry previewEntry,
        List<UfoTutorialScene> scenes) {

    public UfoTutorialEntry {
        scenes = List.copyOf(scenes);
    }
}
