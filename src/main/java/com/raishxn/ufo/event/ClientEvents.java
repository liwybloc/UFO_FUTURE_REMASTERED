package com.raishxn.ufo.event;

import com.raishxn.ufo.item.custom.IHasCycleableModes;
import com.raishxn.ufo.item.custom.IHasModeHUD;
import com.raishxn.ufo.item.custom.IEnergyTool;
import com.raishxn.ufo.network.ModPackets;
import com.raishxn.ufo.network.packet.CycleModeKeyPacket;
import com.raishxn.ufo.network.packet.CycleToolKeyPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class ClientEvents {

    @SubscribeEvent
    public void onKeyInput(final InputEvent.Key event) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (ModKeyBindings.CYCLE_TOOL_FORWARD.consumeClick()) {
            if (mc.player.getMainHandItem().getItem() instanceof IEnergyTool) {
                ModPackets.sendToServer(new CycleToolKeyPacket(true));
            }
        }

        if (ModKeyBindings.CYCLE_TOOL_BACKWARD.consumeClick()) {
            if (mc.player.getMainHandItem().getItem() instanceof IEnergyTool) {
                ModPackets.sendToServer(new CycleToolKeyPacket(false));
            }
        }

        if (ModKeyBindings.CYCLE_MODE.consumeClick()) {
            if (mc.player.getMainHandItem().getItem() instanceof IHasCycleableModes) {
                ModPackets.sendToServer(new CycleModeKeyPacket());
            }
        }
    }

    @SubscribeEvent
    public void onRenderHud(final RenderGuiLayerEvent.Post event) {
    }
}
