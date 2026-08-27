package com.raishxn.ufo.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import java.awt.Color;

public class ColorHelper {

    private static final ChatFormatting[] LOW_CONTRAST_DARKS = new ChatFormatting[] {
            ChatFormatting.BLACK,
            ChatFormatting.DARK_GRAY
    };

    public static MutableComponent getAnimatedColoredText(final String text, final ChatFormatting... colors) {
        return Component.literal(text).withStyle(pickReadableColor(colors));
    }

    public static MutableComponent getSolidColoredText(final String text, final ChatFormatting... colors) {
        return Component.literal(text).withStyle(pickReadableColor(colors));
    }

    public static ChatFormatting pickReadableColor(final ChatFormatting... colors) {
        if (colors == null || colors.length == 0) {
            return ChatFormatting.WHITE;
        }

        boolean hasWhiteFamily = false;
        boolean hasLowContrastDark = false;

        for (final ChatFormatting color : colors) {
            if (color == null) {
                continue;
            }

            if (color == ChatFormatting.WHITE || color == ChatFormatting.GRAY) {
                hasWhiteFamily = true;
            }

            for (final ChatFormatting dark : LOW_CONTRAST_DARKS) {
                if (color == dark) {
                    hasLowContrastDark = true;
                    break;
                }
            }
        }

        if (hasWhiteFamily && hasLowContrastDark) {
            return ChatFormatting.WHITE;
        }

        for (final ChatFormatting color : colors) {
            if (color == null || color == ChatFormatting.BLACK || color == ChatFormatting.DARK_GRAY) {
                continue;
            }
            return color;
        }

        return ChatFormatting.WHITE;
    }

    public static int getRainbowColor(final int offset) {
        final float hue = (System.currentTimeMillis() / 10 % 360 + offset) / 360f;
        return Color.HSBtoRGB(hue, 0.8f, 1.0f);
    }

    public static int getBluePurplePinkColor(final int offset) {
        float hue = (System.currentTimeMillis() / 10 % 360 + offset) / 360f;
        if (hue > 0.83f) {
            hue = 0.66f + (hue - 0.83f) * 0.5f;
        } else if (hue < 0.66f) {
            hue = 0.66f;
        }
        return java.awt.Color.HSBtoRGB(hue, 0.7f, 1.0f);
    }

    public static int getGrayBlackColor(final int offset) {
        final long time = System.currentTimeMillis() / 2;
        float brightness = 0.5f + 0.5f * (float) Math.sin((time + offset) / 500.0);
        brightness = Mth.clamp(brightness * 0.6f, 0.1f, 0.6f);
        return new java.awt.Color(brightness, brightness, brightness).getRGB();
    }

    public static int getRedCyanPinkColor(final int offset) {
        float hue = (System.currentTimeMillis() / 15 % 360 + offset) / 360f;
        if (hue < 0.33f) {
            hue = 0.0f;
        } else if (hue < 0.66f) {
            hue = 0.5f;
        } else {
            hue = 0.83f;
        }
        return java.awt.Color.HSBtoRGB(hue, 0.8f, 1.0f);
    }
}
