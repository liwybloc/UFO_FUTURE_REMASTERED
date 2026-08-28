package com.raishxn.ufo.screen;

import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.block.entity.EntropicConvergenceEngineBE;
import com.raishxn.ufo.init.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public final class EntropicConvergenceEngineMenu extends AbstractUniversalMultiblockControllerMenu<EntropicConvergenceEngineBE> {
    public EntropicConvergenceEngineMenu(final int id, final Inventory inv, final FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public EntropicConvergenceEngineMenu(final int id, final Inventory inv, final BlockEntity entity) {
        super(ModMenus.ENTROPIC_CONVERGENCE_ENGINE_MENU.get(), id, inv, (EntropicConvergenceEngineBE) entity,
                ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos()));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull final Player playerIn, final int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull final Player player) {
        return stillValid(this.levelAccess, player, getValidBlock());
    }

    @Override
    protected Block getValidBlock() {
        return MultiblockBlocks.ENTROPIC_CONVERGENCE_CASING.get();
    }
}
