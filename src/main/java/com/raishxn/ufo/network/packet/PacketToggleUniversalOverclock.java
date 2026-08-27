package com.raishxn.ufo.network.packet;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.entity.IUniversalMultiblockController;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketToggleUniversalOverclock(BlockPos pos) implements CustomPacketPayload {
    public static final Type<PacketToggleUniversalOverclock> TYPE = new Type<>(Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, "toggle_universal_overclock"));

    public static final StreamCodec<ByteBuf, PacketToggleUniversalOverclock> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketToggleUniversalOverclock::pos,
            PacketToggleUniversalOverclock::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final PacketToggleUniversalOverclock packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(packet.pos()) instanceof final IUniversalMultiblockController controller) {
                controller.toggleOverclock();
            }
        });
    }
}
