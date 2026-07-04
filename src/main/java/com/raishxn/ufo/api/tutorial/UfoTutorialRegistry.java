package com.raishxn.ufo.api.tutorial;

import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class UfoTutorialRegistry {
    private static final Map<ResourceLocation, UfoTutorialEntry> ENTRIES = new LinkedHashMap<>();

    private UfoTutorialRegistry() {
    }

    public static void register(UfoTutorialEntry entry) {
        ENTRIES.put(entry.id(), entry);
    }

    public static Optional<UfoTutorialEntry> get(ResourceLocation id) {
        return Optional.ofNullable(ENTRIES.get(id));
    }

    public static Optional<UfoTutorialEntry> get(MultiblockControllerDefinitions.PreviewEntry previewEntry) {
        return get(previewEntry.id());
    }

    public static Collection<UfoTutorialEntry> entries() {
        return ENTRIES.values();
    }

    public static void clear() {
        ENTRIES.clear();
    }
}
