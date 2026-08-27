package com.raishxn.ufo.api.tutorial;

import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class UfoTutorialRegistry {
    private static final Map<Identifier, UfoTutorialEntry> ENTRIES = new LinkedHashMap<>();

    private UfoTutorialRegistry() {
    }

    public static void register(final UfoTutorialEntry entry) {
        ENTRIES.put(entry.id(), entry);
    }

    public static Optional<UfoTutorialEntry> get(final Identifier id) {
        return Optional.ofNullable(ENTRIES.get(id));
    }

    public static Optional<UfoTutorialEntry> get(final MultiblockControllerDefinitions.PreviewEntry previewEntry) {
        return get(previewEntry.id());
    }

    public static Collection<UfoTutorialEntry> entries() {
        return ENTRIES.values();
    }

    public static void clear() {
        ENTRIES.clear();
    }
}
