package com.raishxn.ufo.item.custom.cell;

import appeng.api.config.FuzzyMode;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.AEConfig;
import appeng.core.localization.PlayerMessages;
import appeng.items.contents.CellConfig;
import appeng.items.storage.StorageCellTooltipComponent;
import appeng.items.storage.StorageTier;
import appeng.recipes.game.StorageCellDisassemblyRecipe;
import appeng.util.ConfigInventory;
import appeng.util.InteractionUtil;
import appeng.util.Platform;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** BigInteger版元件物品的承载类，仅用于创造元件 */
public class AEBigIntegerCellItem extends Item implements IAEBigIntegerCell, ICellWorkbenchItem
{
    private final double idleDrain;
    private final StorageTier tier;
    private final AEKeyType keyType;

    public AEBigIntegerCellItem(final Item.Properties pProperties, final double idleDrain, final AEKeyType keyType, final StorageTier tier)
    {
        super(pProperties);
        this.idleDrain = idleDrain;
        this.keyType = keyType;
        this.tier = tier;
    }

    public void appendHoverText(@NotNull final ItemStack stack,
                                @NotNull final Item.TooltipContext context,
                                @NotNull final List<Component> lines,
                                @NotNull final TooltipFlag tooltipFlag)
    {
        if (Platform.isClient())
        {
            final BigInteger used = IAEBigIntegerCell.getUsedBytes(stack);
            final long maxBytes = getMaxBytes(stack);
            lines.add(AEUniversalTooltips.bytesUsed(used, maxBytes != Long.MAX_VALUE ? maxBytes : -1));
            
            final long typesUsed = IAEBigIntegerCell.getUsedTypes(stack);
            final long maxTypes = getMaxTypes(stack);
            lines.add(AEUniversalTooltips.typesUsed(typesUsed, maxTypes != Integer.MAX_VALUE ? maxTypes : -1));
        }
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull final ItemStack stack)
    {
        final boolean showUpg = AEConfig.instance().isTooltipShowCellUpgrades();
        final boolean showCnt = AEConfig.instance().isTooltipShowCellContent();

        List<ItemStack> upgrades = Collections.emptyList();
        if (showUpg) {
            final List<ItemStack> tmp = new ArrayList<>();
            getUpgrades(stack).forEach(tmp::add);
            upgrades = tmp;
        }

        List<GenericStack> content = Collections.emptyList();
        boolean hasMore = false;
        if (showCnt) {
            final List<GenericStack> show = IAEBigIntegerCell.getTooltipShowStacks(stack);
            if (!show.isEmpty()) {
                final int limit = 5;
                if (show.size() > limit) {
                    content = new ArrayList<>(show.subList(0, limit));
                    hasMore = true;
                } else {
                    content = new ArrayList<>(show);
                }
            }
        }

        return Optional.of(new StorageCellTooltipComponent(upgrades, content, hasMore, true));
    }

    @Override
    public double getIdleDrain()
    {
        return idleDrain;
    }

    @Override
    public StorageTier getTier() { return this.tier; }

    @Override
    public AEKeyType getKeyType() { return this.keyType; }

    @Override
    public IUpgradeInventory getUpgrades(final ItemStack is)
    {
        return UpgradeInventories.forItem(is, 2);
    }

    @Override
    public ConfigInventory getConfigInventory(final ItemStack is)
    {
        return CellConfig.create(is);
    }

    @Override
    public FuzzyMode getFuzzyMode(final ItemStack is)
    {
        return is.getOrDefault(AEComponents.STORAGE_CELL_FUZZY_MODE, FuzzyMode.IGNORE_ALL);
    }

    @Override
    public void setFuzzyMode(final ItemStack is, final FuzzyMode fzMode)
    {
        is.set(AEComponents.STORAGE_CELL_FUZZY_MODE, fzMode);
    }

    @Override
    public long getMaxBytes(final ItemStack stack) {
        if (this.tier == null) return Long.MAX_VALUE;
        if (this.tier.bytes() == Integer.MAX_VALUE) return Long.MAX_VALUE;
        return this.tier.bytes();
    }

    @Override
    public int getMaxTypes(final ItemStack stack) {
        if (this.tier == null || this.tier.bytes() == Integer.MAX_VALUE) return Integer.MAX_VALUE;
        final int bytes = this.tier.bytes();
        if (bytes >= 750_000_000) return 8192;   // Core (750M)
        if (bytes >= 250_000_000) return 4096;   // Nexus (250M)
        if (bytes >= 100_000_000) return 2048;   // Beacon (100M)
        return 1024;                              // Echo (40M) and below
    }

    @Override
    public int getBytesPerType(final ItemStack stack) {
        if (this.tier == null || this.tier.bytes() == Integer.MAX_VALUE) return 0;
        return 0; // We define overhead as 0 for all our custom cells so items cost 1 byte exactly.
    }

    @Override
    public @NotNull InteractionResult use(@NotNull final Level level, @NotNull final Player player, @NotNull final InteractionHand hand) {
        this.disassembleDrive(player.getItemInHand(hand), level, player);
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    private boolean disassembleDrive(final ItemStack stack, final Level level, final Player player) {
        if (!InteractionUtil.isInAlternateUseMode(player)) {
            return false;
        }

        if (!(level instanceof final net.minecraft.server.level.ServerLevel serverLevel)) {
            return false;
        }
        final var disassembledStacks = StorageCellDisassemblyRecipe.getDisassemblyResult(serverLevel, stack.getItem());
        if (disassembledStacks.isEmpty()) {
            return false;
        }

        final var playerInventory = player.getInventory();
        if (playerInventory.getSelectedItem() != stack) {
            return false;
        }

        final var inv = StorageCells.getCellInventory(stack, null);
        if (inv != null && !inv.getAvailableStacks().isEmpty()) {
            player.sendOverlayMessage(PlayerMessages.OnlyEmptyCellsCanBeDisassembled.text());
            return false;
        }

        playerInventory.setItem(playerInventory.getSelectedSlot(), ItemStack.EMPTY);

        for (final var disassembledStack : disassembledStacks) {
            playerInventory.placeItemBackInInventory(disassembledStack.copy());
        }

        getUpgrades(stack).forEach(playerInventory::placeItemBackInInventory);

        return true;
    }

    @Override
    public @NotNull InteractionResult onItemUseFirst(@NotNull final ItemStack stack, final UseOnContext context)
    {
        return this.disassembleDrive(stack, context.getLevel(), context.getPlayer())
                ? (context.getLevel().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER)
                : InteractionResult.PASS;
    }
}
