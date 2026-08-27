package com.raishxn.ufo.network.packet;

import com.raishxn.ufo.UfoMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record CycleToolKeyPacket(boolean forward) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, "cycle_tool_key");

    public static final StreamCodec<FriendlyByteBuf, CycleToolKeyPacket> STREAM_CODEC =
            CustomPacketPayload.codec(CycleToolKeyPacket::write, CycleToolKeyPacket::new);

    public CycleToolKeyPacket(final FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

    public void write(final FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.forward);
    }

    public static final Type<CycleToolKeyPacket> TYPE = new Type<>(ID);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}