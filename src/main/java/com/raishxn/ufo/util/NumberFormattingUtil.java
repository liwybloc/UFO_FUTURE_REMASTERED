package com.raishxn.ufo.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class NumberFormattingUtil {

    private static final DecimalFormat COMPACT_FORMAT = new DecimalFormat("#.##");
    private static final String[] SI_UNITS = {"", "K", "M", "G", "T", "P", "E"}; // Base 1000

    private static final NumberFormat DETAILED_FORMAT = NumberFormat.getNumberInstance(Locale.US);

    /**
     * Formata um número grande em uma forma compacta (ex: 1.2M, 3.5G).
     * Usa base 1024 para cálculos de bytes (KiB, MiB, GiB), mas exibe com K, M, G.
     */
    public static String formatBytes(final long bytes) {
        if (bytes < 1024) return Long.toString(bytes);
        int unit = (int) (Math.log(bytes) / Math.log(1024));
        if (unit >= SI_UNITS.length) {
            unit = SI_UNITS.length - 1;
        }
        final double value = bytes / Math.pow(1024, unit);
        return COMPACT_FORMAT.format(value) + SI_UNITS[unit];
    }

    /**
     * Formata um número com separadores de milhar.
     */
    public static String formatNumberWithCommas(final long number) {
        return DETAILED_FORMAT.format(number);
    }
}
