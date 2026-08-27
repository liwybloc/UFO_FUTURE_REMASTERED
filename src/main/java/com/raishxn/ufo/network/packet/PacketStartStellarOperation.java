package com.raishxn.ufo.network.packet;

import com.raishxn.ufo.block.entity.StellarNexusControllerBE;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PacketStartStellarOperation(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketStartStellarOperation> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("ufo", "start_stellar_operation"));

    public static final StreamCodec<FriendlyByteBuf, PacketStartStellarOperation> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketStartStellarOperation::pos,
            PacketStartStellarOperation::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> {
            final Player player = context.player();
            if (player != null && player.level().isLoaded(pos)) {
                if (player.level().getBlockEntity(pos) instanceof final StellarNexusControllerBE controller) {
                    final List<Component> errors = controller.startOperation();
                    if (!errors.isEmpty()) {
                        player.sendSystemMessage(Component.literal("§c§l[STELLAR NEXUS] §eCannot start simulation:"));
                        for (final Component error : errors) {
                            player.sendSystemMessage(Component.literal("  ").append(error));
                        }
                    } else {
                        player.sendOverlayMessage(Component.literal("§a§l[STELLAR NEXUS] §fSimulation started successfully!"));
                    }
                }
            }
        });
    }
}
