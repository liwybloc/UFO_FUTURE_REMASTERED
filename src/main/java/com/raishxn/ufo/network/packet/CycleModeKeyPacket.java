package com.raishxn.ufo.network.packet;

import com.raishxn.ufo.UfoMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record CycleModeKeyPacket() implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, "cycle_mode_key");

    public static final StreamCodec<FriendlyByteBuf, CycleModeKeyPacket> STREAM_CODEC =
            CustomPacketPayload.codec(CycleModeKeyPacket::write, CycleModeKeyPacket::new);

    public CycleModeKeyPacket(final FriendlyByteBuf buffer) {
        this();
    }

    public void write(final FriendlyByteBuf buffer) {
    }

    public static final Type<CycleModeKeyPacket> TYPE = new Type<>(ID);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
