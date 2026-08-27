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

public record PacketChangeStellarRecipe(BlockPos pos, Identifier recipeId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketChangeStellarRecipe> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("ufo", "change_stellar_recipe"));

    public static final StreamCodec<FriendlyByteBuf, PacketChangeStellarRecipe> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketChangeStellarRecipe::pos,
            Identifier.STREAM_CODEC, PacketChangeStellarRecipe::recipeId,
            PacketChangeStellarRecipe::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> {
            final Player player = context.player();
            if (player != null && player.level().isLoaded(pos)) {
                if (player.level().getBlockEntity(pos) instanceof final StellarNexusControllerBE controller) {
                    controller.setActiveRecipe(recipeId);
                }
            }
        });
    }
}
