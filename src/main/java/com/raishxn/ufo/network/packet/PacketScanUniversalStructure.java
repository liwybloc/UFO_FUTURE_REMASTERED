package com.raishxn.ufo.network.packet;

import com.raishxn.ufo.api.multiblock.IMultiblockController;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketScanUniversalStructure(BlockPos pos) implements CustomPacketPayload {

    public static final Type<PacketScanUniversalStructure> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("ufo", "scan_universal_structure"));

    public static final StreamCodec<FriendlyByteBuf, PacketScanUniversalStructure> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            PacketScanUniversalStructure::pos,
            PacketScanUniversalStructure::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> {
            final Player player = context.player();
            if (player != null && player.level().isLoaded(pos)
                    && player.level().getBlockEntity(pos) instanceof final IMultiblockController controller) {
                controller.scanStructure(player.level());
            }
        });
    }
}
