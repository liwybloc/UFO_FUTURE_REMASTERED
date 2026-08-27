package com.raishxn.ufo.network.packet;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.entity.StellarNexusControllerBE;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketToggleStellarOverclock(BlockPos pos) implements CustomPacketPayload {
    public static final Type<PacketToggleStellarOverclock> TYPE = new Type<>(Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, "toggle_stellar_overclock"));

    public static final StreamCodec<ByteBuf, PacketToggleStellarOverclock> STREAM_CODEC = StreamCodec.composite(
            net.minecraft.core.BlockPos.STREAM_CODEC, PacketToggleStellarOverclock::pos,
            PacketToggleStellarOverclock::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final PacketToggleStellarOverclock packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(packet.pos()) instanceof final StellarNexusControllerBE controller) {
                controller.toggleOverclock();
            }
        });
    }
}
