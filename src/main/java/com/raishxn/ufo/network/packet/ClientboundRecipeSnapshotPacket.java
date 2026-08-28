package com.raishxn.ufo.network.packet;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.recipe.ClientRecipeCache;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record ClientboundRecipeSnapshotPacket(List<RecipeHolder<?>> recipes) implements CustomPacketPayload {
    public static final Type<ClientboundRecipeSnapshotPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, "recipe_snapshot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRecipeSnapshotPacket> STREAM_CODEC =
            StreamCodec.composite(
                    RecipeHolder.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    ClientboundRecipeSnapshotPacket::recipes,
                    ClientboundRecipeSnapshotPacket::new
            );

    public static void handle(final ClientboundRecipeSnapshotPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> ClientRecipeCache.replace(packet.recipes));
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
