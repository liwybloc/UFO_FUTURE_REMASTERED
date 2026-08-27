package com.raishxn.ufo.network.packet;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.item.custom.HammerItem;
import com.raishxn.ufo.item.custom.UfoEnergyPickaxeItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ToggleAutoSmeltPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ToggleAutoSmeltPacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, "toggle_auto_smelt"));

    public static final StreamCodec<ByteBuf, ToggleAutoSmeltPacket> STREAM_CODEC =
            StreamCodec.unit(new ToggleAutoSmeltPacket());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final ToggleAutoSmeltPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof final ServerPlayer player) {
                final ItemStack stack = player.getMainHandItem();

                if (stack.getItem() instanceof UfoEnergyPickaxeItem || stack.getItem() instanceof HammerItem) {
                    final boolean currentStatus = stack.getOrDefault(ModDataComponents.AUTO_SMELT.get(), false);
                    final boolean newStatus = !currentStatus;

                    stack.set(ModDataComponents.AUTO_SMELT.get(), newStatus);

                    final String statusText = newStatus ? "ON" : "OFF";
                    final ChatFormatting color = newStatus ? ChatFormatting.GREEN : ChatFormatting.RED;

                    player.sendSystemMessage(Component.literal("Auto-Smelt: " + statusText).withStyle(color));
                }
            }
        });
    }
}
