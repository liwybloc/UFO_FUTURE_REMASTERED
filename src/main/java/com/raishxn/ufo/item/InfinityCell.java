package com.raishxn.ufo.item;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.items.AEBaseItem;
import appeng.items.storage.StorageCellTooltipComponent;
import com.raishxn.ufo.util.ColorHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class InfinityCell extends AEBaseItem {

    private final Supplier<AEKey> type;
    private final ChatFormatting[] nameFormatting;

    public InfinityCell(final Item.Properties properties, @NotNull final Supplier<AEKey> type, final ChatFormatting... nameFormatting) {
        super(properties.stacksTo(1));
        this.type = type;
        this.nameFormatting = nameFormatting;
    }

    public AEKey getRecord() {
        return this.type.get();
    }

    @Override
    public @NotNull Component getName(@NotNull final ItemStack is) {
        if (getRecord() == null) {
            return Component.translatable("item.ufo.infinity_cell_invalid");
        }

        final String text = Component.translatable("item.ufo.infinity_cell_name", getRecord().getDisplayName()).getString();
        return ColorHelper.getSolidColoredText(text, this.nameFormatting);
    }
    public void appendHoverText(@NotNull final ItemStack is, final Item.@NotNull TooltipContext ctx, @NotNull final List<Component> lines, @NotNull final TooltipFlag adv) {
        if (getRecord() != null) {
            lines.add(Component.translatable("item.ufo.infinity_cell_tooltip").withStyle(ChatFormatting.GREEN));
        }
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull final ItemStack stack) {
        if (getRecord() == null) {
            return Optional.empty();
        }
        final AEKey record = getRecord();
        final var content = Collections.singletonList(new GenericStack(record, getAsIntMax(record)));
        return Optional.of(new StorageCellTooltipComponent(List.of(), content, false, true));
    }

    public static long getAsIntMax(final AEKey key) {
        if (key == null) return 0;
        return (long) Integer.MAX_VALUE * key.getAmountPerUnit();
    }
}
