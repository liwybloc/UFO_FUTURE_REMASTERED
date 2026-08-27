package com.raishxn.ufo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinition;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.api.multiblock.MultiblockPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT)
public class GhostHologramRenderer {

    private static BlockPos activeControllerPos;
    private static net.minecraft.core.Direction activeFacing;
    private static final List<HologramBlock> cachedBlocks = new ArrayList<>();

    private record HologramBlock(BlockPos pos, BlockState state) {
    }

    public static void toggleHologram(final BlockPos pos, final net.minecraft.core.Direction facing) {
        if (activeControllerPos != null && activeControllerPos.equals(pos)) {
            activeControllerPos = null;
            activeFacing = null;
            cachedBlocks.clear();
            return;
        }

        activeControllerPos = pos;
        activeFacing = getPatternFacing(pos, facing);
        rebuildCache();
    }

    private static net.minecraft.core.Direction getPatternFacing(final BlockPos pos, final net.minecraft.core.Direction fallbackFacing) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return fallbackFacing;
        }

        final BlockEntity be = mc.level.getBlockEntity(pos);
        final BlockState state = mc.level.getBlockState(pos);
        return MultiblockControllerDefinitions.getPatternFacing(be, state);
    }

    public static boolean isActive(final BlockPos pos) {
        return activeControllerPos != null && activeControllerPos.equals(pos);
    }

    private static void rebuildCache() {
        cachedBlocks.clear();
        if (activeControllerPos == null || activeFacing == null) {
            return;
        }

        final Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        final Optional<MultiblockControllerDefinition> definitionOpt = getDefinition(mc.level.getBlockEntity(activeControllerPos));
        if (definitionOpt.isEmpty()) {
            return;
        }

        final MultiblockControllerDefinition definition = definitionOpt.get();
        final MultiblockPattern pattern = definition.pattern();
        final Map<Character, BlockState> defaults = definition.defaultCreativeStates();

        final char[][][] charPattern = pattern.getPattern();
        final int controllerCol = pattern.getControllerCol();
        final int controllerRow = pattern.getControllerRow();
        final int controllerLayer = pattern.getControllerLayer();

        for (int y = 0; y < charPattern.length; y++) {
            for (int z = 0; z < charPattern[y].length; z++) {
                for (int x = 0; x < charPattern[y][z].length; x++) {
                    final char c = charPattern[y][z][x];
                    if (c == ' ' || c == 'A' || c == pattern.getControllerChar()) {
                        continue;
                    }

                    final BlockState targetState = defaults.get(c);
                    if (targetState == null) {
                        continue;
                    }

                    final int offsetX = x - controllerCol;
                    final int offsetY = y - controllerLayer;
                    final int offsetZ = z - controllerRow;

                    final BlockPos worldPos = getRotatedPos(activeControllerPos, offsetX, offsetY, offsetZ, activeFacing);
                    cachedBlocks.add(new HologramBlock(worldPos, targetState));
                }
            }
        }
    }

    private static Optional<MultiblockControllerDefinition> getDefinition(final BlockEntity be) {
        return be == null ? Optional.empty() : MultiblockControllerDefinitions.getDefinition(be);
    }

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Post event) {
        final Minecraft mc = Minecraft.getInstance();
        if (activeControllerPos != null && (mc.level == null
                || !MultiblockControllerDefinitions.isSupportedController(mc.level.getBlockState(activeControllerPos)))) {
            activeControllerPos = null;
            activeFacing = null;
            cachedBlocks.clear();
        }
    }

    private static BlockPos getRotatedPos(final BlockPos center, final int localX, final int localY, final int localZ, final net.minecraft.core.Direction facing) {
        return switch (facing) {
            case SOUTH -> center.offset(-localX, localY, -localZ);
            case WEST -> center.offset(localZ, localY, -localX);
            case EAST -> center.offset(-localZ, localY, localX);
            case NORTH, UP, DOWN -> center.offset(localX, localY, localZ);
        };
    }
}
