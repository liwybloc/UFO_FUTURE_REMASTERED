package com.raishxn.ufo.network.packet;

import com.raishxn.ufo.block.entity.StellarNexusControllerBE;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketToggleStellarAutoStart(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketToggleStellarAutoStart> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("ufo", "toggle_stellar_auto_start"));

    public static final StreamCodec<FriendlyByteBuf, PacketToggleStellarAutoStart> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketToggleStellarAutoStart::pos,
            PacketToggleStellarAutoStart::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> {
            final Player player = context.player();
            if (!player.level().isLoaded(this.pos)) {
                return;
            }
            if (player.level().getBlockEntity(this.pos) instanceof final StellarNexusControllerBE controller) {
                controller.toggleAutoStart();
            }
        });
    }
}
