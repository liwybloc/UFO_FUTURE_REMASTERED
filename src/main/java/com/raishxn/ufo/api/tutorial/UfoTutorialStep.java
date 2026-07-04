package com.raishxn.ufo.api.tutorial;

import net.minecraft.network.chat.Component;

import java.util.OptionalInt;
import java.util.Set;

public record UfoTutorialStep(
        Component title,
        Component text,
        OptionalInt visibleLayer,
        Set<Character> highlightedSymbols) {

    public UfoTutorialStep {
        highlightedSymbols = Set.copyOf(highlightedSymbols);
    }
}
