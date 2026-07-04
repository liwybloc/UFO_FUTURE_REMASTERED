package com.raishxn.ufo.client.tutorial;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.api.multiblock.MultiblockPattern;
import com.raishxn.ufo.api.tutorial.UfoTutorialEntry;
import com.raishxn.ufo.api.tutorial.UfoTutorialRegistry;
import com.raishxn.ufo.api.tutorial.UfoTutorialScene;
import com.raishxn.ufo.api.tutorial.UfoTutorialStep;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

public final class UfoTutorials {
    private UfoTutorials() {
    }

    public static void registerDefaults() {
        UfoTutorialRegistry.clear();
        for (MultiblockControllerDefinitions.PreviewEntry entry : MultiblockControllerDefinitions.getPreviewEntries()) {
            UfoTutorialRegistry.register(createGenericMultiblockTutorial(entry));
        }
    }

    private static UfoTutorialEntry createGenericMultiblockTutorial(MultiblockControllerDefinitions.PreviewEntry entry) {
        if ("quantum_matter_fabricator".equals(entry.id().getPath())) {
            return createQmfTutorial(entry);
        }

        MultiblockPattern pattern = entry.definition().pattern();
        char[][][] blocks = pattern.getPattern();
        Set<Character> structuralSymbols = collectStructuralSymbols(pattern, blocks);
        ResourceLocation sceneId = ResourceLocation.fromNamespaceAndPath(UfoMod.MOD_ID, entry.id().getPath() + "/structure");

        UfoTutorialScene scene = new UfoTutorialScene(
                sceneId,
                Component.translatable("ufo.tutorial.scene.structure"),
                List.of(
                        new UfoTutorialStep(
                                Component.translatable("ufo.tutorial.step.overview"),
                                Component.translatable("ufo.tutorial.text.overview"),
                                OptionalInt.empty(),
                                structuralSymbols),
                        new UfoTutorialStep(
                                Component.translatable("ufo.tutorial.step.base"),
                                Component.translatable("ufo.tutorial.text.base"),
                                OptionalInt.of(0),
                                structuralSymbols),
                        new UfoTutorialStep(
                                Component.translatable("ufo.tutorial.step.controller"),
                                Component.translatable("ufo.tutorial.text.controller"),
                                OptionalInt.empty(),
                                Set.of(pattern.getControllerChar())),
                        new UfoTutorialStep(
                                Component.translatable("ufo.tutorial.step.finish"),
                                Component.translatable("ufo.tutorial.text.finish"),
                                OptionalInt.empty(),
                                structuralSymbols)));

        return new UfoTutorialEntry(entry.id(), entry.definition().name(), entry, List.of(scene));
    }

    private static UfoTutorialEntry createQmfTutorial(MultiblockControllerDefinitions.PreviewEntry entry) {
        ResourceLocation sceneId = ResourceLocation.fromNamespaceAndPath(UfoMod.MOD_ID, "quantum_matter_fabricator/structure");
        UfoTutorialScene scene = new UfoTutorialScene(
                sceneId,
                Component.translatable("ufo.tutorial.qmf.scene.structure"),
                List.of(
                        new UfoTutorialStep(
                                Component.translatable("ufo.tutorial.qmf.step.frame"),
                                Component.translatable("ufo.tutorial.qmf.text.frame"),
                                OptionalInt.empty(),
                                Set.of('C')),
                        new UfoTutorialStep(
                                Component.translatable("ufo.tutorial.qmf.step.field"),
                                Component.translatable("ufo.tutorial.qmf.text.field"),
                                OptionalInt.empty(),
                                Set.of('F')),
                        new UfoTutorialStep(
                                Component.translatable("ufo.tutorial.qmf.step.glass"),
                                Component.translatable("ufo.tutorial.qmf.text.glass"),
                                OptionalInt.empty(),
                                Set.of('G')),
                        new UfoTutorialStep(
                                Component.translatable("ufo.tutorial.qmf.step.hatch"),
                                Component.translatable("ufo.tutorial.qmf.text.hatch"),
                                OptionalInt.of(2),
                                Set.of('H', 'P')),
                        new UfoTutorialStep(
                                Component.translatable("ufo.tutorial.qmf.step.scan"),
                                Component.translatable("ufo.tutorial.qmf.text.scan"),
                                OptionalInt.empty(),
                                Set.of('C', 'F', 'G', 'H', 'P'))));

        return new UfoTutorialEntry(entry.id(), entry.definition().name(), entry, List.of(scene));
    }

    private static Set<Character> collectStructuralSymbols(MultiblockPattern pattern, char[][][] blocks) {
        LinkedHashSet<Character> symbols = new LinkedHashSet<>();
        for (char[][] layer : blocks) {
            for (char[] row : layer) {
                for (char symbol : row) {
                    if (!Character.isWhitespace(symbol) && symbol != 'A' && symbol != pattern.getControllerChar()) {
                        symbols.add(symbol);
                    }
                }
            }
        }
        return symbols;
    }
}
