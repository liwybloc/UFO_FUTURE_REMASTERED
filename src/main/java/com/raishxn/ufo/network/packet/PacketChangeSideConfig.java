package com.raishxn.ufo.network.packet;

import com.raishxn.ufo.UfoMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketChangeSideConfig(BlockPos pos, Direction side, int typeOrdinal, int nextModeOrdinal) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketChangeSideConfig> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, "change_side_config"));

    public static final StreamCodec<ByteBuf, PacketChangeSideConfig> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketChangeSideConfig::pos,
            Direction.STREAM_CODEC, PacketChangeSideConfig::side,
            ByteBufCodecs.INT, PacketChangeSideConfig::typeOrdinal,
            ByteBufCodecs.INT, PacketChangeSideConfig::nextModeOrdinal,
            PacketChangeSideConfig::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final PacketChangeSideConfig packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof final ServerPlayer player)
                    || player.blockPosition().distSqr(packet.pos()) > 64.0) {
                return;
            }
        });
    }
}
