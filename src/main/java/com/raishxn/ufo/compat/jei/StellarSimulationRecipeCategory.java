package com.raishxn.ufo.compat.jei;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.recipe.StellarSimulationRecipe;
import java.util.Arrays;
import java.util.Locale;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

public final class StellarSimulationRecipeCategory
        implements IRecipeCategory<StellarSimulationRecipe> {

    public static final IRecipeType<StellarSimulationRecipe> RECIPE_TYPE =
            IRecipeType.create(
                    UfoMod.MOD_ID,
                    "stellar_simulation",
                    StellarSimulationRecipe.class
            );

    private static final Identifier BACKGROUND =
            UfoMod.id("textures/guis/stellar_nexus_jei.png");

    private static final int WIDTH = 191;
    private static final int HEIGHT = 128;

    private static final int CONTROLLER_X = 170;
    private static final int CONTROLLER_Y = 4;

    private static final int ITEM_INPUT_X = 11;
    private static final int ITEM_INPUT_Y = 16;

    private static final int FLUID_INPUT_X = 71;
    private static final int FLUID_INPUT_Y = 16;

    private static final int ITEM_OUTPUT_X = 127;
    private static final int ITEM_OUTPUT_Y = 16;

    private static final int FLUID_OUTPUT_Y = 75;

    private static final int SLOT_SPACING = 18;
    private static final int FLUID_SLOT_SPACING = 20;
    private static final int FLUID_RENDER_CAPACITY = 1_000_000;

    private final IDrawable icon;
    private final IDrawable background;

    public StellarSimulationRecipeCategory(final IJeiHelpers helpers) {
        final var guiHelper = helpers.getGuiHelper();

        this.icon = guiHelper.createDrawableItemStack(
                MultiblockBlocks.STELLAR_NEXUS_CONTROLLER
                        .get()
                        .asItem()
                        .getDefaultInstance()
        );

        this.background = guiHelper.createDrawable(
                BACKGROUND,
                0,
                0,
                WIDTH,
                HEIGHT
        );
    }

    @Override
    public @NonNull IRecipeType<StellarSimulationRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NonNull Component getTitle() {
        return Component.translatable("jei.ufo.stellar_simulation");
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
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
    public void setRecipe(
            final IRecipeLayoutBuilder builder,
            final StellarSimulationRecipe recipe,
            final @NonNull IFocusGroup focuses
    ) {
        builder.addInputSlot(CONTROLLER_X, CONTROLLER_Y)
                .add(
                        MultiblockBlocks.STELLAR_NEXUS_CONTROLLER
                                .get()
                                .asItem()
                                .getDefaultInstance()
                )
                .addRichTooltipCallback(
                        (recipeSlotView, tooltip) -> {
                            tooltip.add(Component.literal("Controller"));
                            tooltip.add(
                                    Component.literal(
                                            "Click to open the 3D multiblock preview"
                                    )
                            );
                        }
                );

        final var itemInputs = recipe.getItemInputs();

        for (int i = 0; i < Math.min(itemInputs.size(), 9); i++) {
            final var input = itemInputs.get(i);

            if (input.isEmpty()) {
                continue;
            }

            final int column = i % 3;
            final int row = i / 3;
            final long amount = input.getAmount();

            final var ingredient = UfoJeiPlugin.stackOf(input);
            final var displayedIngredient = ingredient.stream()
                    .map(stack -> stack.copyWithCount(1))
                    .toList();

            builder.addInputSlot(
                            ITEM_INPUT_X + column * SLOT_SPACING,
                            ITEM_INPUT_Y + row * SLOT_SPACING
                    )
                    .addItemStacks(displayedIngredient)
                    .setOverlay(new AmountOverlay(amount), 0, 0)
                    .addRichTooltipCallback(
                            (_, tooltip) ->
                                    tooltip.add(
                                            Component.literal(
                                                    "Required: " + formatExactAmount(amount) + "x"
                                            )
                                    )
                    );
        }

        final var fluidInputs = recipe.getFluidInputs();

        for (int i = 0; i < Math.min(fluidInputs.size(), 3); i++) {
            final var input = fluidInputs.get(i);

            if (input.isEmpty()) {
                continue;
            }

            final long amount = input.getAmount();
            final int y = FLUID_INPUT_Y + i * FLUID_SLOT_SPACING;

            final var visualFluids = UfoJeiPlugin.stackOf(input)
                    .stream()
                    .map(stack ->
                            new FluidStack(
                                    stack.getFluid(),
                                    safeInt(amount)
                            )
                    )
                    .toList();

            builder.addInputSlot(FLUID_INPUT_X, y)
                    .setFluidRenderer(
                            FLUID_RENDER_CAPACITY,
                            false,
                            11,
                            14
                    )
                    .addIngredients(
                            NeoForgeTypes.FLUID_STACK,
                            visualFluids
                    )
                    .addRichTooltipCallback(
                            (recipeSlotView, tooltip) ->
                                    tooltip.add(
                                            Component.literal(
                                                    "Required: " + amount + " mB"
                                            )
                                    )
                    );
        }

        final var itemOutputs = recipe.getItemOutputs();

        for (int i = 0; i < Math.min(itemOutputs.size(), 9); i++) {
            final var output = itemOutputs.get(i);

            if (!(output.what() instanceof final AEItemKey itemKey)) {
                continue;
            }

            final int column = i % 3;
            final int row = i / 3;
            final long amount = output.amount();

            builder.addOutputSlot(
                            ITEM_OUTPUT_X + column * SLOT_SPACING,
                            ITEM_OUTPUT_Y + row * SLOT_SPACING
                    )
                    .add(itemKey.toStack(1))
                    .setOverlay(new AmountOverlay(amount), 0, 0)
                    .addRichTooltipCallback(
                            (recipeSlotView, tooltip) ->
                                    tooltip.add(
                                            Component.literal(
                                                    "Amount Produced: "
                                                            + formatExactAmount(amount)
                                            )
                                    )
                    );
        }

        final var fluidOutputs = recipe.getFluidOutputs();

        for (int i = 0; i < Math.min(fluidOutputs.size(), 6); i++) {
            final var output = fluidOutputs.get(i);

            if (!(output.what() instanceof final AEFluidKey fluidKey)) {
                continue;
            }

            final int column = i % 3;
            final int row = i / 3;
            final int x = 127 + column * 21;
            final int y = FLUID_OUTPUT_Y + row * FLUID_SLOT_SPACING;
            final long amount = output.amount();

            builder.addOutputSlot(x, y)
                    .setFluidRenderer(
                            FLUID_RENDER_CAPACITY,
                            false,
                            11,
                            14
                    )
                    .add(
                            NeoForgeTypes.FLUID_STACK,
                            new FluidStack(fluidKey.getFluid(), safeInt(amount))
                    )
                    .addRichTooltipCallback(
                            (recipeSlotView, tooltip) ->
                                    tooltip.add(
                                            Component.literal(
                                                    "Amount Produced: "
                                                            + formatAmount(amount)
                                                            + " mB"
                                            )
                                    )
                    );
        }
    }

    @Override
    public void draw(
            final StellarSimulationRecipe recipe,
            final @NonNull IRecipeSlotsView recipeSlotsView,
            final @NonNull GuiGraphicsExtractor gfx,
            final double mouseX,
            final double mouseY
    ) {
        this.background.draw(gfx, 0, 0);

        final Font font = Minecraft.getInstance().font;

        final String simulationName =
                recipe.getSimulationName() == null
                        || recipe.getSimulationName().isBlank()
                        ? "Unknown Simulation"
                        : recipe.getSimulationName();

        drawScaledCenteredString(
                gfx,
                font,
                simulationName,
                WIDTH / 2,
                4,
                0xFF00FFFF,
                1.0F
        );

        final int progressWidth = 20;
        final int animatedWidth =
                (int) ((System.currentTimeMillis() / 40L) % progressWidth);

        gfx.fillGradient(
                94,
                38,
                94 + animatedWidth,
                49,
                0x558B5CF6,
                0x556D28D9
        );

        drawScaledCenteredString(
                gfx,
                font,
                getFuelDisplayShortName(recipe),
                29,
                76,
                0xFF00FFFF,
                0.7F
        );

        drawScaledCenteredString(
                gfx,
                font,
                getCoolantDisplayTier(recipe),
                75,
                76,
                0xFF00FFFF,
                0.7F
        );

        drawScaledCenteredString(
                gfx,
                font,
                recipe.getFormattedTime(),
                29,
                88,
                0xFFFFFFFF,
                0.8F
        );

        drawScaledCenteredString(
                gfx,
                font,
                "Mk." + toRoman(recipe.getFieldTier()),
                73,
                88,
                0xFFFFFFFF,
                0.8F
        );

        drawScaledCenteredString(
                gfx,
                font,
                formatAmount(recipe.getTotalEnergy()) + " AE",
                51,
                100,
                0xFFFFDF00,
                0.7F
        );
    }

    @Override
    public void getTooltip(
            final @NonNull ITooltipBuilder tooltip,
            final StellarSimulationRecipe recipe,
            final @NonNull IRecipeSlotsView recipeSlotsView,
            final double mouseX,
            final double mouseY
    ) {
        if (isInside(mouseX, mouseY, 10, 74, 39, 10)) {
            if (!recipe.getFuelFluid().isEmpty() && recipe.getFuelAmount() > 0) {
                tooltip.add(
                        Component.literal(
                                "Fuel Required: "
                                        + getFuelDisplayName(recipe)
                        )
                );

                tooltip.add(
                        Component.literal(
                                "Amount: "
                                        + formatAmount(recipe.getFuelAmount())
                                        + " mB"
                        )
                );

                tooltip.add(
                        Component.literal(
                                "Extracted from ME storage on start."
                        )
                );
            } else {
                tooltip.add(
                        Component.literal(
                                "No fuel liquid required."
                        )
                );
            }

            return;
        }

        if (isInside(mouseX, mouseY, 53, 74, 45, 10)) {
            if (recipe.getCoolantAmount() > 0) {
                tooltip.add(
                        Component.literal(
                                "Coolant Required: "
                                        + getCoolantDisplayName(recipe)
                        )
                );

                tooltip.add(
                        Component.literal(
                                "Tier: "
                                        + getCoolantDisplayTier(recipe)
                        )
                );

                tooltip.add(
                        Component.literal(
                                "Amount: "
                                        + formatAmount(recipe.getCoolantAmount())
                                        + " mB"
                        )
                );

                tooltip.add(
                        Component.literal(
                                "Consumed during operation to control heat."
                        )
                );
            } else {
                tooltip.add(
                        Component.literal(
                                "Cooling Level: "
                                        + recipe.getCoolingLevel()
                                        + "/3"
                        )
                );

                tooltip.add(
                        Component.literal(
                                "Generic coolant from ME network."
                        )
                );
            }

            return;
        }

        if (isInside(mouseX, mouseY, 10, 86, 39, 10)) {
            tooltip.add(
                    Component.literal(
                            "Duration: "
                                    + recipe.getFormattedTime()
                                    + " ("
                                    + recipe.getTime()
                                    + " ticks)"
                    )
            );

            return;
        }

        if (isInside(mouseX, mouseY, 53, 86, 39, 10)) {
            tooltip.add(
                    Component.literal(
                            "Required Stellar Field Generator: Mk."
                                    + toRoman(recipe.getFieldTier())
                    )
            );

            return;
        }

        if (isInside(mouseX, mouseY, 32, 98, 39, 10)) {
            tooltip.add(
                    Component.literal(
                            "Total AE Energy Required: "
                                    + String.format(
                                    Locale.ROOT,
                                    "%,d",
                                    recipe.getTotalEnergy()
                            )
                                    + " AE"
                    )
            );

            tooltip.add(
                    Component.literal(
                            "Charged passively from AE grid via Energy Hatch."
                    )
            );

            return;
        }

        if (isInside(
                mouseX,
                mouseY,
                CONTROLLER_X,
                CONTROLLER_Y,
                16,
                16
        )) {
            tooltip.add(Component.literal("Controller"));

            tooltip.add(
                    Component.literal(
                            "Click the controller to open Multiblock Info"
                    )
            );

            return;
        }

        if (isInside(mouseX, mouseY, 94, 38, 20, 11)) {
            tooltip.add(
                    Component.literal(
                            recipe.getFormattedTime()
                    )
            );

            tooltip.add(
                    Component.literal(
                            "(" + recipe.getTime() + " ticks)"
                    )
            );

            tooltip.add(
                    Component.literal(
                            "Outputs directly into ME Network"
                    )
            );
        }
    }

    private static void drawScaledCenteredString(
            final GuiGraphicsExtractor gfx,
            final Font font,
            final String text,
            final int x,
            final int y,
            final int color,
            final float scale
    ) {
        gfx.pose().pushMatrix();
        gfx.pose().translate(x, y);
        gfx.pose().scale(scale, scale);

        gfx.text(
                font,
                text,
                -font.width(text) / 2,
                0,
                color,
                false
        );

        gfx.pose().popMatrix();
    }

    private static boolean isInside(
            final double mouseX,
            final double mouseY,
            final int x,
            final int y,
            final int width,
            final int height
    ) {
        return mouseX >= x
                && mouseX < x + width
                && mouseY >= y
                && mouseY < y + height;
    }

    private static int safeInt(final long amount) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, amount));
    }

    public static String formatAmount(final long amount) {
        if (amount >= 1_000_000_000L) {
            return amount / 1_000_000_000L + "B";
        }

        if (amount >= 1_000_000L) {
            return amount / 1_000_000L + "M";
        }

        if (amount >= 1_000L) {
            return amount / 1_000L + "K";
        }

        return Long.toString(amount);
    }

    private static String formatExactAmount(final long amount) {
        return String.format(Locale.ROOT, "%,d", amount);
    }

    private record AmountOverlay(long amount) implements IDrawable {
        @Override
        public int getWidth() {
            return 16;
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public void draw(final GuiGraphicsExtractor gfx, final int xOffset, final int yOffset) {
            final Font font = Minecraft.getInstance().font;
            final String text = formatAmount(this.amount);
            final float scale = 0.65F;

            gfx.pose().pushMatrix();
            gfx.pose().translate(xOffset + 16, yOffset + 18);
            gfx.pose().scale(scale, scale);
            gfx.text(font, text, -font.width(text), -font.lineHeight, 0xFFFFFFFF, true);
            gfx.pose().popMatrix();
        }
    }

    private static String formatFluidName(final String path) {
        String normalizedPath = path;

        if (normalizedPath.startsWith("source_")) {
            normalizedPath = normalizedPath.substring(7);
        }

        if (normalizedPath.startsWith("flowing_")) {
            normalizedPath = normalizedPath.substring(8);
        }

        final String[] words = normalizedPath.split("_");
        final StringBuilder builder = new StringBuilder();

        for (final String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            builder.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(' ');
        }

        return builder.toString().trim();
    }

    private static String getFuelDisplayShortName(
            final StellarSimulationRecipe recipe
    ) {
        if (recipe.getFuelFluid().isEmpty()) {
            return "None";
        }

        return abbreviateFluidName(
                getFuelDisplayName(recipe),
                10
        );
    }

    private static String getFuelDisplayName(
            final StellarSimulationRecipe recipe
    ) {
        if (recipe.getFuelFluid().isEmpty()) {
            return "None";
        }

        return getFluidDisplayName(
                Identifier.parse(recipe.getFuelFluid())
        );
    }

    private static String getCoolantDisplayTier(
            final StellarSimulationRecipe recipe
    ) {
        final int tier = recipe.getCoolingLevel();

        return tier <= 0
                ? "None"
                : "MK" + Math.min(3, tier);
    }

    private static String getCoolantDisplayName(
            final StellarSimulationRecipe recipe
    ) {
        return switch (recipe.getCoolingLevel()) {
            case 1 ->
                    getFluidDisplayName(
                            Identifier.parse(
                                    "ufo:source_gelid_cryotheum"
                            )
                    );

            case 2 ->
                    getFluidDisplayName(
                            Identifier.parse(
                                    "ufo:source_stable_coolant"
                            )
                    );

            case 3 ->
                    getFluidDisplayName(
                            Identifier.parse(
                                    "ufo:source_temporal_fluid"
                            )
                    );

            default -> "None";
        };
    }

    private static String getFluidDisplayName(
            final Identifier fluidId
    ) {
        final var fluid = BuiltInRegistries.FLUID
                .getOptional(fluidId)
                .orElse(null);

        if (fluid == null) {
            return formatFluidName(fluidId.getPath());
        }

        final String hoverName =
                new FluidStack(fluid, 1)
                        .getHoverName()
                        .getString();

        return hoverName == null || hoverName.isBlank()
                ? formatFluidName(fluidId.getPath())
                : hoverName;
    }

    private static String abbreviateFluidName(
            final String fullName,
            final int maxLength
    ) {
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
                    initials.append(
                            Character.toUpperCase(word.charAt(0))
                    );
                }
            }

            if (!initials.isEmpty()) {
                return initials.toString();
            }
        }

        return fullName
                .substring(
                        0,
                        Math.max(1, maxLength - 1)
                )
                .toUpperCase(Locale.ROOT)
                + ".";
    }

    private static String toRoman(final int tier) {
        return switch (tier) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            default -> Integer.toString(tier);
        };
    }
}
