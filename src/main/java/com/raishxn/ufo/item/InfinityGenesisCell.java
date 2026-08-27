package com.raishxn.ufo.item;

import appeng.api.stacks.GenericStack;
import appeng.items.AEBaseItem;
import appeng.items.storage.StorageCellTooltipComponent;
import com.raishxn.ufo.datagen.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class InfinityGenesisCell extends AEBaseItem {

    public InfinityGenesisCell(final Item.Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.EPIC).fireResistant());
    }
    public void appendHoverText(@NotNull final ItemStack stack, final Item.@NotNull TooltipContext context,
                                @NotNull final List<Component> lines, @NotNull final TooltipFlag advancedTooltips) {
        final List<GenericStack> keys = stack.getOrDefault(ModDataComponents.INFINITY_GENESIS_CELL_KEYS.get(), List.of());
        lines.add(Component.translatable("item.ufo.infinity_genesis_cell.tooltip").withStyle(ChatFormatting.LIGHT_PURPLE));
        lines.add(Component.translatable("item.ufo.infinity_genesis_cell.types", keys.size()).withStyle(ChatFormatting.GRAY));
        if (!keys.isEmpty()) {
            lines.add(Component.translatable("item.ufo.infinity_genesis_cell.learned").withStyle(ChatFormatting.DARK_AQUA));
            int shown = 0;
            for (final var key : keys) {
                lines.add(Component.literal(" - ").append(key.what().getDisplayName()));
                if (++shown >= 8) {
                    if (keys.size() > shown) {
                        lines.add(Component.translatable("item.ufo.infinity_genesis_cell.more", keys.size() - shown)
                                .withStyle(ChatFormatting.DARK_GRAY));
                    }
                    break;
                }
            }
        }
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull final ItemStack stack) {
        final List<GenericStack> keys = stack.getOrDefault(ModDataComponents.INFINITY_GENESIS_CELL_KEYS.get(), List.of());
        final var content = keys.stream()
                .limit(5)
                .map(entry -> new GenericStack(entry.what(), InfinityCell.getAsIntMax(entry.what())))
                .toList();
        return Optional.of(new StorageCellTooltipComponent(List.of(), content, keys.size() > 5, true));
    }
}
