package com.raishxn.ufo.event;

import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.item.custom.HammerItem;
import com.raishxn.ufo.item.custom.IHasModeHUD;
import com.raishxn.ufo.item.custom.UfoEnergyHoeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public final class ClientForgeEvents {

    @SubscribeEvent
    public static void onRightClickBlock(final net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide() && event.getEntity().isShiftKeyDown() && event.getItemStack().isEmpty()) {
            final var state = event.getLevel().getBlockState(event.getPos());
            if (MultiblockControllerDefinitions.isSupportedController(state)) {
                final var facing = state.getValue(net.minecraft.world.level.block.DirectionalBlock.FACING);
                com.raishxn.ufo.client.GhostHologramRenderer.toggleHologram(event.getPos(), facing);

                if (com.raishxn.ufo.client.GhostHologramRenderer.isActive(event.getPos())) {
                    event.getEntity().sendOverlayMessage(Component.literal("Ghost multiblock hologram enabled.").withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
                } else {
                    event.getEntity().sendOverlayMessage(Component.literal("Ghost multiblock hologram disabled.").withStyle(net.minecraft.ChatFormatting.GRAY));
                }
                event.setCanceled(true);
                event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGui(final RenderGuiEvent.Post event) {
    }
}
