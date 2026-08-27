package com.raishxn.ufo.item.custom.cell;

import appeng.core.localization.GuiText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.math.BigInteger;

import static appeng.core.localization.Tooltips.*;

/** Tooltips com formatação humanizada (K, M, B, T) para bytes e types */
public class AEUniversalTooltips
{

    /**
     * Formata um número longo com sufixos humanizados.
     * Ex: 1024 → "1.02K", 40000000 → "40M", 750000000 → "750M"
     * Números abaixo de 1000 são exibidos normalmente.
     */
    private static String formatHumanReadable(final long value) {
        if (value < 0) return "∞";
        if (value < 1_000L) return String.valueOf(value);
        if (value < 1_000_000L) {
            final double v = value / 1_000.0;
            return (v == (long) v) ? (long) v + "K" : String.format("%.2gK", v);
        }
        if (value < 1_000_000_000L) {
            final double v = value / 1_000_000.0;
            return (v == (long) v) ? (long) v + "M" : String.format("%.2gM", v);
        }
        if (value < 1_000_000_000_000L) {
            final double v = value / 1_000_000_000.0;
            return (v == (long) v) ? (long) v + "B" : String.format("%.2gB", v);
        }
        final double v = value / 1_000_000_000_000.0;
        return (v == (long) v) ? (long) v + "T" : String.format("%.2gT", v);
    }

    /**
     * Formata um BigInteger com sufixos humanizados.
     */
    private static String formatHumanReadable(final BigInteger value) {
        if (value.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0 && value.signum() >= 0) {
            return formatHumanReadable(value.longValue());
        }
        final BigInteger T = BigInteger.valueOf(1_000_000_000_000L);
        final BigInteger[] divRem = value.divideAndRemainder(T);
        return divRem[0].toString() + "T";
    }


    public static Component bytesUsed(final long bytes, final long max)
    {
        if (max <= 0) {
            final MutableComponent inf = Component.literal("∞").withStyle(GREEN);
            return of(GuiText.BytesUsed,
                    of(
                            ofUnformattedNumberWithRatioColor(bytes, 0.0, false),
                            of(" "),
                            of(GuiText.Of),
                            of(" "),
                            inf
                    )
            );
        }

        final MutableComponent maxComp = Component.literal(formatHumanReadable(max)).withStyle(ChatFormatting.GRAY);
        return of(GuiText.BytesUsed,
                of(
                        ofUnformattedNumberWithRatioColor(bytes, (double) bytes / (double) max, false),
                        of(" "),
                        of(GuiText.Of),
                        of(" "),
                        maxComp
                )
        );
    }

    public static Component bytesUsed(final BigInteger bytes, final long max)
    {
        final String bytesFormatted = formatHumanReadable(bytes);
        final MutableComponent bytesString = Component.literal(bytesFormatted).withStyle(GREEN).withStyle(colorFromRatio(0.0, false));

        if (max <= 0) {
            final MutableComponent inf = Component.literal("∞").withStyle(GREEN);
            return of(GuiText.BytesUsed,
                    of(
                            bytesString,
                            of(" "),
                            of(GuiText.Of),
                            of(" "),
                            inf
                    )
            );
        }

        final MutableComponent maxComp = Component.literal(formatHumanReadable(max)).withStyle(ChatFormatting.GRAY);
        return of(GuiText.BytesUsed,
                of(
                        bytesString,
                        of(" "),
                        of(GuiText.Of),
                        of(" "),
                        maxComp
                )
        );
    }


    public static Component typesUsed(final long types, final long max)
    {
        if (max <= 0) {
            final MutableComponent inf = Component.literal("∞").withStyle(GREEN);
            return of(
                    ofUnformattedNumberWithRatioColor(types, 0.0, false),
                    of(" "),
                    of(GuiText.Of),
                    of(" "),
                    inf,
                    of(" "),
                    of(GuiText.Types)
            );
        }

        final MutableComponent maxComp = Component.literal(formatHumanReadable(max)).withStyle(ChatFormatting.GRAY);
        return of(
                ofUnformattedNumberWithRatioColor(types, (double) types / (double) max, false),
                of(" "),
                of(GuiText.Of),
                of(" "),
                maxComp,
                of(" "),
                of(GuiText.Types)
        );
    }
}
