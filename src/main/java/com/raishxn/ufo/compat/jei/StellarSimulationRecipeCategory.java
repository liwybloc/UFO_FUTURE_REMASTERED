package com.raishxn.ufo.compat.jei;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.recipe.StellarSimulationRecipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;

public class StellarSimulationRecipeCategory implements IRecipeCategory<StellarSimulationRecipe> {

    public static final RecipeType<StellarSimulationRecipe> RECIPE_TYPE =
            RecipeType.create(UfoMod.MOD_ID, "stellar_simulation", StellarSimulationRecipe.class);

    private static final int WIDTH = 191;
    private static final int HEIGHT = 128;
    private static final int CONTROLLER_X = 170;
    private static final int CONTROLLER_Y = 4;
    private static final Identifier BACKGROUND =
            UfoMod.id("textures/guis/stellar_nexus_jei.png");

    private final IDrawable icon;
    private final IDrawable background;

    public StellarSimulationRecipeCategory(final IJeiHelpers helpers) {
        final var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(
                MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get().asItem().getDefaultInstance());
        this.background = guiHelper.createDrawable(BACKGROUND, 0, 0, WIDTH, HEIGHT);
    }

    @Override
    public RecipeType<StellarSimulationRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.ufo.stellar_simulation");
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(final IRecipeLayoutBuilder builder, final StellarSimulationRecipe recipe, final IFocusGroup focuses) {
        builder.addInputSlot(CONTROLLER_X, CONTROLLER_Y)
                .addItemStack(MultiblockBlocks.STELLAR_NEXUS_CONTROLLER.get().asItem().getDefaultInstance())
                .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                    tooltip.add(Component.literal("Controller"));
                    tooltip.add(Component.literal("Click to open the 3D multiblock preview"));
                });

        final var itemInputs = recipe.getItemInputs();
        for (int i = 0; i < itemInputs.size() && i < 9; i++) {
            if (!itemInputs.get(i).isEmpty()) {
                final int col = i % 3;
                final int row = i / 3;
                final int finalI = i;

                final var visualStacks = java.util.Arrays.stream(UfoJeiPlugin.stackOf(itemInputs.get(i)).getItems())
                        .map(stack -> {
                            final var copy = stack.copy();
                            copy.setCount(1);
                            return copy;
                        }).toList();

                builder.addInputSlot(11 + (col * 18), 16 + (row * 18))
                        .addItemStacks(visualStacks)
                        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                            final long amount = itemInputs.get(finalI).getAmount();
                            tooltip.add(Component.literal("Amount Required: " + formatAmount(amount)));
                        });
            }
        }

        final var fluidInputs = recipe.getFluidInputs();
        for (int i = 0; i < fluidInputs.size() && i < 3; i++) {
            if (!fluidInputs.get(i).isEmpty()) {
                final int yPos = 16 + (i * 20);
                final int finalI = i;

                final var visualFluids = UfoJeiPlugin.stackOf(fluidInputs.get(i)).stream()
                        .map(stack -> new FluidStack(stack.getFluid(), 1000))
                        .toList();

                final var slot = builder.addInputSlot(71, yPos)
                        .setFluidRenderer(1_000_000, false, 11, 14);
                slot.addIngredients(NeoForgeTypes.FLUID_STACK, visualFluids)
                        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                            final long amount = fluidInputs.get(finalI).getAmount();
                            tooltip.add(Component.literal("Amount Required: " + formatAmount(amount) + " mB"));
                        });
            }
        }

        final var itemOutputs = recipe.getItemOutputs();
        for (int i = 0; i < itemOutputs.size() && i < 9; i++) {
            if (itemOutputs.get(i).what() instanceof final AEItemKey itemKey) {
                final int col = i % 3;
                final int row = i / 3;
                final int finalI = i;
                builder.addOutputSlot(127 + (col * 18), 16 + (row * 18))
                        .addItemStack(itemKey.toStack(1))
                        .addRichTooltipCallback((recipeSlotView, tooltip) ->
                                tooltip.add(Component.literal("Amount Produced: " + formatAmount(itemOutputs.get(finalI).amount()))));
            }
        }

        final var fluidOutputs = recipe.getFluidOutputs();
        for (int i = 0; i < fluidOutputs.size() && i < 6; i++) {
            if (fluidOutputs.get(i).what() instanceof final AEFluidKey fluidKey) {
                final int col = i % 3;
                final int row = i / 3;
                final int finalI = i;
                final int[] xOffsets = {127, 148, 169};
                final var slot = builder.addOutputSlot(xOffsets[col], 75 + (row * 20))
                        .setFluidRenderer(1_000_000, false, 11, 14);
                slot.addFluidStack(fluidKey.getFluid(), 1000)
                        .addRichTooltipCallback((recipeSlotView, tooltip) ->
                                tooltip.add(Component.literal("Amount Produced: " + formatAmount(fluidOutputs.get(finalI).amount()) + " mB")));
            }
        }
    }

    @Override
    public void draw(final StellarSimulationRecipe recipe, final IRecipeSlotsView recipeSlotsView, final GuiGraphicsExtractor gfx, final double mouseX, final double mouseY) {
        final Font font = Minecraft.getInstance().font;

        String simulationName = recipe.getSimulationName();
        if (simulationName == null || simulationName.isEmpty()) {
            simulationName = "Unknown Simulation";
        }
        drawScaledCenteredString(gfx, font, simulationName, WIDTH / 2, 4, 0x00FFFF, 1.0f);

        final int pWidth = 20;
        final int animWidth = (int) ((System.currentTimeMillis() / 40) % pWidth);
        gfx.fillGradient(94, 38, 94 + animWidth, 49, 0x558B5CF6, 0x556D28D9);

        drawScaledCenteredString(gfx, font, getFuelDisplayShortName(recipe), 29, 76, 0x00FFFF, 0.7f);
        drawScaledCenteredString(gfx, font, getCoolantDisplayTier(recipe), 75, 76, 0x00FFFF, 0.7f);
        drawScaledCenteredString(gfx, font, recipe.getFormattedTime(), 29, 88, 0xFFFFFF, 0.8f);
        drawScaledCenteredString(gfx, font, "Mk." + toRoman(recipe.getFieldTier()), 73, 88, 0xFFFFFF, 0.8f);
        drawScaledCenteredString(gfx, font, formatAmount(recipe.getTotalEnergy()) + " AE", 51, 100, 0xFFDF00, 0.7f);
    }

    private static void drawBackground(final GuiGraphicsExtractor gfx) {
        drawPanel(gfx, 0, 0, WIDTH, HEIGHT, 0xFFC8C8C8, 0xFFFFFFFF, 0xFF6A6A6A);
        drawPanel(gfx, 5, 12, 186, 123, 0xFFD6D6D6, 0xFFFFFFFF, 0xFF8B8B8B);

        gfx.fill(8, 15, 105, 116, 0xFF111111);
        gfx.fill(122, 15, 184, 116, 0xFF111111);

        drawSlot(gfx, CONTROLLER_X, CONTROLLER_Y);

        for (int i = 0; i < 9; i++) {
            final int col = i % 3;
            final int row = i / 3;
            drawSlot(gfx, 11 + (col * 18), 16 + (row * 18));
            drawSlot(gfx, 127 + (col * 18), 16 + (row * 18));
        }

        for (int i = 0; i < 3; i++) {
            drawTankSlot(gfx, 71, 16 + (i * 20));
        }

        for (int i = 0; i < 6; i++) {
            final int col = i % 3;
            final int row = i / 3;
            final int[] xOffsets = {127, 148, 169};
            drawTankSlot(gfx, xOffsets[col], 75 + (row * 20));
        }

        gfx.fill(94, 38, 114, 49, 0xFF171019);
        gfx.fill(95, 39, 113, 48, 0xFF080808);
    }

    private static void drawSlot(final GuiGraphicsExtractor gfx, final int x, final int y) {
        drawPanel(gfx, x - 1, y - 1, x + 17, y + 17, 0xFF121212, 0xFFBDBDBD, 0xFF414141);
    }

    private static void drawTankSlot(final GuiGraphicsExtractor gfx, final int x, final int y) {
        drawPanel(gfx, x - 1, y - 1, x + 12, y + 15, 0xFF121212, 0xFFBDBDBD, 0xFF414141);
    }

    private static void drawPanel(final GuiGraphicsExtractor gfx, final int left, final int top, final int right, final int bottom, final int fill, final int light, final int dark) {
        gfx.fill(left, top, right, bottom, dark);
        gfx.fill(left, top, right - 1, bottom - 1, light);
        gfx.fill(left + 1, top + 1, right - 1, bottom - 1, fill);
    }

    private void drawScaledCenteredString(final GuiGraphicsExtractor gfx, final Font font, final String text, final int x, final int y, final int color, final float scale) {
        gfx.pose().pushPose();
        gfx.pose().translate(x, y, 0);
        gfx.pose().scale(scale, scale, 1.0f);
        gfx.drawString(font, text, -font.width(text) / 2, 0, color, false);
        gfx.pose().popPose();
    }

    @Override
    public List<Component> getTooltipStrings(final StellarSimulationRecipe recipe, final IRecipeSlotsView recipeSlotsView, final double mouseX, final double mouseY) {
        final List<Component> tips = new ArrayList<>();

        if (mouseY >= 74 && mouseY <= 84 && mouseX >= 10 && mouseX <= 49) {
            if (!recipe.getFuelFluid().isEmpty() && recipe.getFuelAmount() > 0) {
                tips.add(Component.literal("Fuel Required: " + getFuelDisplayName(recipe)));
                tips.add(Component.literal("Amount: " + formatAmount(recipe.getFuelAmount()) + " mB"));
                tips.add(Component.literal("Extracted from ME storage on start."));
            } else {
                tips.add(Component.literal("No fuel liquid required."));
            }
            return tips;
        }

        if (mouseY >= 74 && mouseY <= 84 && mouseX >= 53 && mouseX <= 98) {
            if (recipe.getCoolantAmount() > 0) {
                tips.add(Component.literal("Coolant Required: " + getCoolantDisplayName(recipe)));
                tips.add(Component.literal("Tier: " + getCoolantDisplayTier(recipe)));
                tips.add(Component.literal("Amount: " + formatAmount(recipe.getCoolantAmount()) + " mB"));
                tips.add(Component.literal("Consumed during operation to control heat."));
            } else {
                tips.add(Component.literal("Cooling Level: " + recipe.getCoolingLevel() + "/3"));
                tips.add(Component.literal("Generic coolant from ME network."));
            }
            return tips;
        }

        if (mouseY >= 86 && mouseY <= 96 && mouseX >= 10 && mouseX <= 49) {
            tips.add(Component.literal("Duration: " + recipe.getFormattedTime() + " (" + recipe.getTime() + " ticks)"));
            return tips;
        }

        if (mouseY >= 86 && mouseY <= 96 && mouseX >= 53 && mouseX <= 92) {
            tips.add(Component.literal("Required Stellar Field Generator: Mk." + toRoman(recipe.getFieldTier())));
            return tips;
        }

        if (mouseY >= 98 && mouseY <= 108 && mouseX >= 32 && mouseX <= 71) {
            tips.add(Component.literal("Total AE Energy Required: " + String.format(Locale.ROOT, "%,d", recipe.getTotalEnergy()) + " AE"));
            tips.add(Component.literal("Charged passively from AE grid via Energy Hatch."));
            return tips;
        }

        if (mouseX >= CONTROLLER_X && mouseX <= CONTROLLER_X + 16 && mouseY >= CONTROLLER_Y && mouseY <= CONTROLLER_Y + 16) {
            tips.add(Component.literal("Controller"));
            tips.add(Component.literal("Click the controller to open Multiblock Info"));
            return tips;
        }

        if (mouseX >= 94 && mouseX <= 114 && mouseY >= 38 && mouseY <= 49) {
            return List.of(
                    Component.literal(recipe.getFormattedTime()),
                    Component.literal("(" + recipe.getTime() + " ticks)"),
                    Component.literal("Outputs directly into ME Network"));
        }

        return List.of();
    }

    public static String formatAmount(final long amount) {
        if (amount >= 1_000_000_000L) {
            final double value = amount / 1_000_000_000.0;
            return value == (long) value ? (long) value + "G" : String.format(Locale.ROOT, "%.1fG", value);
        }
        if (amount >= 1_000_000L) {
            final double value = amount / 1_000_000.0;
            return value == (long) value ? (long) value + "M" : String.format(Locale.ROOT, "%.1fM", value);
        }
        if (amount >= 1_000L) {
            final double value = amount / 1_000.0;
            return value == (long) value ? (long) value + "K" : String.format(Locale.ROOT, "%.1fK", value);
        }
        return String.valueOf(amount);
    }

    private static String formatFluidName(String path) {
        if (path.startsWith("source_")) {
            path = path.substring(7);
        }
        if (path.startsWith("flowing_")) {
            path = path.substring(8);
        }

        final String[] words = path.split("_");
        final StringBuilder builder = new StringBuilder();
        for (final String word : words) {
            if (!word.isEmpty()) {
                builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return builder.toString().trim();
    }

    private static String getFuelDisplayShortName(final StellarSimulationRecipe recipe) {
        if (recipe.getFuelFluid().isEmpty()) {
            return "None";
        }
        return abbreviateFluidName(getFuelDisplayName(recipe), 10);
    }

    private static String getFuelDisplayName(final StellarSimulationRecipe recipe) {
        if (recipe.getFuelFluid().isEmpty()) {
            return "None";
        }
        return getFluidDisplayName(Identifier.parse(recipe.getFuelFluid()));
    }

    private static String getCoolantDisplayTier(final StellarSimulationRecipe recipe) {
        final int tier = recipe.getCoolingLevel();
        return tier <= 0 ? "None" : "MK" + Math.min(3, tier);
    }

    private static String getCoolantDisplayName(final StellarSimulationRecipe recipe) {
        return switch (recipe.getCoolingLevel()) {
            case 1 -> getFluidDisplayName(Identifier.parse("ufo:source_gelid_cryotheum"));
            case 2 -> getFluidDisplayName(Identifier.parse("ufo:source_stable_coolant"));
            case 3 -> getFluidDisplayName(Identifier.parse("ufo:source_temporal_fluid"));
            default -> "None";
        };
    }

    private static String getFluidDisplayName(final Identifier fluidId) {
        final var fluid = BuiltInRegistries.FLUID.getOptional(fluidId).orElse(null);
        if (fluid == null) {
            return formatFluidName(fluidId.getPath());
        }

        final String hoverName = new FluidStack(fluid, 1).getHoverName().getString();
        return hoverName == null || hoverName.isBlank() ? formatFluidName(fluidId.getPath()) : hoverName;
    }

    private static String abbreviateFluidName(final String fullName, final int maxLength) {
        if (fullName == null || fullName.isBlank()) {
            return "None";
        }
        if (fullName.length() <= maxLength) {
            return fullName;
        }

        final String[] words = fullName.trim().split("\\s+");
        if (words.length > 1) {
            final StringBuilder initials = new StringBuilder();
            for (final String word : words) {
                if (!word.isEmpty()) {
                    initials.append(Character.toUpperCase(word.charAt(0)));
                }
            }
            if (!initials.isEmpty()) {
                return initials.toString();
            }
        }

        return fullName.substring(0, Math.max(1, maxLength - 1)).toUpperCase(Locale.ROOT) + ".";
    }

    private static String toRoman(final int tier) {
        return switch (tier) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            default -> String.valueOf(tier);
        };
    }
}
