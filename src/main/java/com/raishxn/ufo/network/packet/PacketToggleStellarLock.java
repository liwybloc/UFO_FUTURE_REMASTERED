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

public record PacketToggleStellarLock(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketToggleStellarLock> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("ufo", "toggle_stellar_lock"));

    public static final StreamCodec<FriendlyByteBuf, PacketToggleStellarLock> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketToggleStellarLock::pos,
            PacketToggleStellarLock::new
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
                controller.toggleSimulationLock();
            }
        });
    }
}
