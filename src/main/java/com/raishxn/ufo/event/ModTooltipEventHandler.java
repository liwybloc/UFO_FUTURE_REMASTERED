package com.raishxn.ufo.event;

import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.block.custom.MegaCoProcessorBlockItem;
import com.raishxn.ufo.item.ModItems;
import com.raishxn.ufo.item.custom.MegaCraftingStorageBlockItem;
import com.raishxn.ufo.util.NumberFormattingUtil; // <-- IMPORTAR A NOVA CLASSE
import com.raishxn.ufo.event.ModKeyBindings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = "ufo", value = Dist.CLIENT)
public class ModTooltipEventHandler {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (MultiblockControllerDefinitions.getPreviewEntries().stream()
                .anyMatch(entry -> stack.is(entry.iconStack().getItem()))) {
            event.getToolTip().add(Component.translatable(
                    "ufo.tutorial.tooltip.hold",
                    ModKeyBindings.OPEN_UFO_TUTORIAL.getTranslatedKeyMessage()
            ).withStyle(ChatFormatting.DARK_GRAY));
        }

        if (stack.getItem() instanceof MegaCraftingStorageBlockItem item) {
            var tier = item.getTier();
            long capacity = tier.getStorageBytes();
            MutableComponent capacityLine = Component.translatable(
                    "tooltip.ufo.capacity",
                    NumberFormattingUtil.formatBytes(capacity) + "B"
            );
            capacityLine.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            event.getToolTip().add(capacityLine);
            if (Screen.hasShiftDown()) {
                MutableComponent exactCapacityLine = Component.translatable(
                        "tooltip.ufo.capacity_exact",
                        NumberFormattingUtil.formatNumberWithCommas(capacity) + " Bytes"
                );
                exactCapacityLine.setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY));
                event.getToolTip().add(exactCapacityLine);
            } else {
                event.getToolTip().add(Component.translatable("tooltip.ufo.press_shift").withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        else if (stack.getItem() instanceof MegaCoProcessorBlockItem item) {
            var tier = item.getTier();
            String formattedThreads = tier.getDisplayName();
            MutableComponent threadsLine = Component.translatable("tooltip.ufo.accelerator_threads", formattedThreads);
            threadsLine.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            event.getToolTip().add(threadsLine);
        }
        else if (stack.is(ModBlocks.QUANTUM_ENERGY_CELL.get().asItem())) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            boolean chargedPreview = customData != null
                    && customData.copyTag().getBoolean("ufoQuantumEnergyCellChargedPreview");
            event.getToolTip().add(Component.literal(chargedPreview
                    ? "Creative preview: Charged"
                    : "Creative preview: Discharged").withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(Component.translatable("tooltip.ufo.stored_energy_infinite").withStyle(ChatFormatting.GRAY));
        }
        else if (stack.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get().asItem())) {
            event.getToolTip().add(Component.literal("Universal field tier: MK1").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.literal("Nexus charge: 500K AE/t; Nexus fields must all match.").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (stack.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get().asItem())) {
            event.getToolTip().add(Component.literal("Universal field tier: MK2").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.literal("Over-tier universal recipes run faster and cheaper.").withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(Component.literal("Nexus charge: 1M AE/t; Nexus fields must all match.").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (stack.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get().asItem())) {
            event.getToolTip().add(Component.literal("Universal field tier: MK3").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.literal("Required for Stable Coolant in the Quantum Cryoforge.").withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(Component.literal("Nexus charge: 2M AE/t; Nexus fields must all match.").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (stack.is(MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get().asItem())) {
            event.getToolTip().add(Component.literal("Stable Coolant requires machine tier MK3.").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.literal("Use MK3 field generators in every field position.").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (isAeHatch(stack)) {
            event.getToolTip().add(Component.literal("AE2 grid hatch: connect ME cable to any side.").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.literal("Items, fluids and AE are read from ME storage, not sided pipes.").withStyle(ChatFormatting.DARK_GRAY));
        }
        else if (stack.is(ModItems.STABLE_COOLANT_BUCKET.get())) {
            event.getToolTip().add(Component.literal("Stable Coolant: 50 HU/mB, up to 10 mB/tick.").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.literal("Crafted in the Quantum Cryoforge at machine tier MK3.").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static boolean isAeHatch(ItemStack stack) {
        return stack.is(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get().asItem())
                || stack.is(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get().asItem())
                || stack.is(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get().asItem())
                || stack.is(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get().asItem());
    }
}
