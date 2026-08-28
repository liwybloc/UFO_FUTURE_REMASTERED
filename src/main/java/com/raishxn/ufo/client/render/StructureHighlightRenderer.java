package com.raishxn.ufo.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.raishxn.ufo.UfoMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = UfoMod.MOD_ID, value = Dist.CLIENT)
public final class StructureHighlightRenderer {

    private static final Map<BlockPos, Long> HIGHLIGHTS = new ConcurrentHashMap<>();

    public static void highlight(final BlockPos pos, final long durationMs) {
        HIGHLIGHTS.put(pos, System.currentTimeMillis() + durationMs);
    }

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Post event) {
        final long now = System.currentTimeMillis();
        HIGHLIGHTS.entrySet().removeIf(entry -> entry.getValue() <= now);
    }
}
