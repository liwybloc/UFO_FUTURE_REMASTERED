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

/**
 * Sent from the client when the player clicks the "Scan Structure" button
 * in the Stellar Nexus Controller screen. Forces an immediate rescan
 * on the server side.
 */
public record PacketScanStellarStructure(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketScanStellarStructure> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("ufo", "scan_stellar_structure"));

    public static final StreamCodec<FriendlyByteBuf, PacketScanStellarStructure> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketScanStellarStructure::pos,
            PacketScanStellarStructure::new
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
                controller.scanStructure(player.level(), player);
            }
        });
    }
}
