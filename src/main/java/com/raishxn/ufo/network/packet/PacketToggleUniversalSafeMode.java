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

public record PacketToggleUniversalSafeMode(BlockPos pos) implements CustomPacketPayload {
    public static final Type<PacketToggleUniversalSafeMode> TYPE = new Type<>(Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, "toggle_universal_safe_mode"));

    public static final StreamCodec<ByteBuf, PacketToggleUniversalSafeMode> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketToggleUniversalSafeMode::pos,
            PacketToggleUniversalSafeMode::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final PacketToggleUniversalSafeMode packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(packet.pos()) instanceof final IUniversalMultiblockController controller) {
                controller.toggleSafeMode();
            }
        });
    }
}
