package com.raishxn.ufo.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import appeng.items.materials.UpgradeCardItem;

import java.util.List;

/**
 * Classe base para os 12 catalisadores T1-T3.
 * Extends AE2's UpgradeCardItem so it is accepted by AE2's upgrade slot validation.
 * Cuida automaticamente da lógica da tooltip (com SHIFT).
 */
public class BaseCatalystItem extends UpgradeCardItem {

    protected final String family;
    protected final int tier;

    public BaseCatalystItem(final Properties properties, final String family, final int tier) {
        super(properties);
        this.family = family;
        this.tier = tier;
    }

    public String getFamily() { return family; }
    public int getTier() { return tier; }

    public int getStaticHeat() {
        int h = 0;
        switch (family) {
            case "matterflow":
                if (tier == 1) h = 50;
                if (tier == 2) h = 100;
                if (tier == 3) h = 200;
                break;
            case "chrono":
                if (tier == 1) h = 100;
                if (tier == 2) h = 250;
                if (tier == 3) h = 400;
                break;
            case "overflux":
                if (tier == 1) h = -50;
                if (tier == 2) h = -100;
                if (tier == 3) h = -200;
                break;
            case "quantum":
                if (tier == 1) h = 75;
                if (tier == 2) h = 150;
                if (tier == 3) h = 300;
                break;
        }
        return h;
    }

    public double getPowerMultiplier() {
        if ("matterflow".equals(family)) {
            final double baseStat = -10.0;
            double tierMultiplier = 1.0;
            if (tier == 2) tierMultiplier = 2.5;
            if (tier == 3) tierMultiplier = 5.0;
            return Math.max(0.01, 1.0 + (baseStat * tierMultiplier / 100.0));
        }
        return 1.0;
    }

    public double getSpeedMultiplier() {
        if ("chrono".equals(family)) {
            final double baseStat = 25.0;
            double tierMultiplier = 1.0;
            if (tier == 2) tierMultiplier = 2.5;
            if (tier == 3) tierMultiplier = 5.0;
            return 1.0 + (baseStat * tierMultiplier / 100.0);
        }
        return 1.0;
    }

    public double getBonusDropChance() {
        if ("quantum".equals(family)) {
            double tierMultiplier = 1.0;
            if (tier == 2) tierMultiplier = 2.5;
            if (tier == 3) tierMultiplier = 5.0;
            return 0.10 * tierMultiplier;
        }
        return 0.0;
    }

    public int getBufferMultiplier() {
        if ("matterflow".equals(family) || "chrono".equals(family)) {
            if (tier == 1) return 10;
            if (tier == 2) return 100;
            if (tier == 3) return 1000;
        }
        return 1;
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        return CatalystUpgradeUseHelper.tryInstallHeldCatalyst(context);
    }

    public void appendHoverText(final ItemStack stack, final TooltipContext context, final List<Component> components, final TooltipFlag flag) {

        if (net.minecraft.client.Minecraft.getInstance().hasShiftDown()) {

            double baseStat = 0;
            String statName = "Unknown";
            double tierMultiplier = 1.0;
            int staticHeat = 0;

            if (tier == 2) tierMultiplier = 2.5; //
            if (tier == 3) tierMultiplier = 5.0; //

            switch (family) {
                case "matterflow" -> {
                    baseStat = -10.0; //
                    statName = "Energy Cost";
                    if (tier == 1) staticHeat = 50;   //
                    if (tier == 2) staticHeat = 100;  //
                    if (tier == 3) staticHeat = 200;  //
                }
                case "chrono" -> {
                    baseStat = 25.0; //
                    statName = "Speed";
                    if (tier == 1) staticHeat = 100;  //
                    if (tier == 2) staticHeat = 250;  //
                    if (tier == 3) staticHeat = 400;  //
                }
                case "overflux" -> {
                    baseStat = -10.0; //
                    statName = "Failure Chance";
                    if (tier == 1) staticHeat = -50;  //
                    if (tier == 2) staticHeat = -100; //
                    if (tier == 3) staticHeat = -200; //
                }
                case "quantum" -> {
                    baseStat = 10.0; //
                    statName = "Bonus Drop Chance";
                    if (tier == 1) staticHeat = 75;   //
                    if (tier == 2) staticHeat = 150;  //
                    if (tier == 3) staticHeat = 300;  //
                }
            }

            components.add(Component.literal("Family: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(capitalize(family)).withStyle(ChatFormatting.AQUA)));
            components.add(Component.literal("Tier: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.valueOf(tier)).withStyle(ChatFormatting.AQUA)));

            components.add(Component.literal("")); // Espaçador

            final double finalStat = baseStat * tierMultiplier;
            final String sign = finalStat > 0 ? "+" : "";
            final ChatFormatting statColor = (finalStat > 0) ? ChatFormatting.GREEN : ChatFormatting.RED;
            final String statText = String.format("%s%.1f%% %s", sign, finalStat, statName);

            components.add(Component.literal("Effect: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(statText).withStyle(statColor)));

            final double heatMult = 1.0 + Math.max(0, staticHeat / 100.0);
            final String heatColorFormat = (heatMult > 1.0) ? "§c" : "§b"; // Vermelho para debuff de calor, Azul nulo/bom
            final String heatText = String.format("%sx%.1f Heat Production", heatColorFormat, heatMult);

            components.add(Component.literal("Thermal: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(heatText)));

            components.add(Component.literal("")); // Espaçador
            components.add(Component.literal("Stacking Effect (Soft Cap):").withStyle(ChatFormatting.GOLD));
            components.add(Component.literal(" 2x: ").withStyle(ChatFormatting.GRAY).append(Component.literal("175%").withStyle(ChatFormatting.WHITE))); //
            components.add(Component.literal(" 3x: ").withStyle(ChatFormatting.GRAY).append(Component.literal("225%").withStyle(ChatFormatting.WHITE))); //
            components.add(Component.literal(" 4x: ").withStyle(ChatFormatting.GRAY).append(Component.literal("250%").withStyle(ChatFormatting.WHITE))); //


        } else {
            components.add(Component.literal("Hold <").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.literal("SHIFT").withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC))
                    .append(Component.literal("> for details.").withStyle(ChatFormatting.DARK_GRAY)));
        }

    }

    private String capitalize(final String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
