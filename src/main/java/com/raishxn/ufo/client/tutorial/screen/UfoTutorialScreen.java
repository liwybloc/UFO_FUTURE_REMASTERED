package com.raishxn.ufo.client.tutorial.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinition;
import com.raishxn.ufo.api.multiblock.MultiblockPattern;
import com.raishxn.ufo.api.tutorial.UfoTutorialEntry;
import com.raishxn.ufo.api.tutorial.UfoTutorialScene;
import com.raishxn.ufo.api.tutorial.UfoTutorialStep;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

public class UfoTutorialScreen extends Screen {
    private static final int SIDEBAR_WIDTH = 172;
    private static final int TIMELINE_HEIGHT = 44;
    private static final int TICKS_PER_BLOCK = 6;
    private static final int ALL_LAYERS = Integer.MIN_VALUE;
    private static final float DEFAULT_CAMERA_PITCH = 32.5F;
    private static final float DEFAULT_CAMERA_YAW = -45.0F;

    private final UfoTutorialEntry entry;
    private final UfoTutorialScene scene;
    private final List<RenderBlock> renderBlocks;
    private int stepIndex;
    private int layerOverride = ALL_LAYERS;
    private int lastSceneWidth;
    private int lastSceneHeight;
    private boolean playing = true;
    private int speedIndex = 0;
    private int buildTick;
    private float cameraPitch = DEFAULT_CAMERA_PITCH;
    private float cameraYaw = DEFAULT_CAMERA_YAW;
    private float cameraZoom = 1.0F;
    private boolean draggingScene;
    private double lastDragX;
    private double lastDragY;
    private BlockFilter blockFilter = BlockFilter.ALL;

    public UfoTutorialScreen(UfoTutorialEntry entry) {
        super(entry.title());
        this.entry = entry;
        this.scene = entry.scenes().getFirst();
        this.renderBlocks = buildRenderBlocks(entry);
    }

    @Override
    protected void init() {
        int sidebarX = this.width - SIDEBAR_WIDTH;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), button -> onClose())
                .bounds(14, 12, 78, 24)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("ufo.tutorial.layer_down"), button -> moveLayer(-1))
                .bounds(sidebarX + 12, 146, 70, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("ufo.tutorial.layer_up"), button -> moveLayer(1))
                .bounds(sidebarX + 90, 146, 70, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("ufo.tutorial.show_all"), button -> setBlockFilter(BlockFilter.ALL))
                .bounds(sidebarX + 12, 174, 70, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("ufo.tutorial.show_hatches"), button -> setBlockFilter(BlockFilter.HATCHES))
                .bounds(sidebarX + 90, 174, 70, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("ufo.tutorial.show_controller"), button -> setBlockFilter(BlockFilter.CONTROLLER))
                .bounds(sidebarX + 12, 198, 70, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("ufo.tutorial.center_camera"), button -> centerCamera())
                .bounds(sidebarX + 90, 198, 70, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> this.cameraYaw -= 22.5F)
                .bounds(sidebarX + 12, 222, 70, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> this.cameraYaw += 22.5F)
                .bounds(sidebarX + 90, 222, 70, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.ufo.previous"), button -> moveStep(-1))
                .bounds(sidebarX + 12, this.height - 32, 70, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.ufo.next"), button -> moveStep(1))
                .bounds(sidebarX + 90, this.height - 32, 70, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("ufo.tutorial.pause"), button -> {
                    this.playing = !this.playing;
                    button.setMessage(Component.translatable(this.playing ? "ufo.tutorial.pause" : "ufo.tutorial.play"));
                })
                .bounds(12, this.height - 34, 56, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal("1x"), button -> {
                    this.speedIndex = (this.speedIndex + 1) % TimelapseSpeed.values().length;
                    button.setMessage(Component.literal(TimelapseSpeed.values()[this.speedIndex].label()));
                })
                .bounds(76, this.height - 34, 48, 20)
                .build());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.playing) {
            this.buildTick += TimelapseSpeed.values()[this.speedIndex].ticksPerClientTick();
            int totalTicks = totalBuildTicks(currentStep());
            if (this.buildTick > totalTicks) {
                this.buildTick = totalTicks;
                this.playing = false;
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void renderTransparentBackground(GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        UfoTutorialStep step = currentStep();
        int sceneWidth = this.width - SIDEBAR_WIDTH;
        int sceneHeight = this.height - TIMELINE_HEIGHT;
        int sidebarX = sceneWidth;
        SceneLayout layout = createLayout(step, sceneWidth, sceneHeight);
        this.lastSceneWidth = sceneWidth;
        this.lastSceneHeight = sceneHeight;

        guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);
        guiGraphics.fill(sidebarX, 0, this.width, this.height, 0xFF090B17);
        guiGraphics.fill(sceneWidth - 1, 0, sceneWidth + 1, sceneHeight, 0xFF51D8FF);
        guiGraphics.fill(0, sceneHeight - 1, sceneWidth, sceneHeight + 1, 0xFF51D8FF);

        int visibleBuildBlocks = visibleBuildBlocks(step);
        renderScene(guiGraphics, sceneWidth, sceneHeight, step, layout, partialTick, visibleBuildBlocks);
        renderBottomCaption(guiGraphics, sceneWidth, sceneHeight, step);
        renderTimeline(guiGraphics, sceneWidth, sceneHeight, step, visibleBuildBlocks);
        renderSidebar(guiGraphics, sidebarX, step);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderHoveredBlockTooltip(guiGraphics, step, layout, mouseX, mouseY);
    }

    private void renderScene(GuiGraphics guiGraphics, int sceneWidth, int sceneHeight, UfoTutorialStep step,
                             SceneLayout layout, float partialTick, int visibleBuildBlocks) {
        List<RenderBlock> visibleBlocks = visibleBlocksForStep(step).stream()
                .limit(visibleBuildBlocks)
                .sorted(Comparator.comparingInt(RenderBlock::row).reversed()
                        .thenComparingInt(RenderBlock::col))
                .toList();

        renderBasePlate(guiGraphics, layout);
        if (visibleBlocks.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, Component.translatable("ufo.tutorial.no_visible_blocks"),
                    sceneWidth / 2, sceneHeight / 2, 0xFF8E99AA);
            return;
        }
        renderBlockModels(guiGraphics, visibleBlocks, step, layout, sceneWidth, sceneHeight, partialTick);
    }

    private void renderBlockModels(GuiGraphics guiGraphics, List<RenderBlock> visibleBlocks, UfoTutorialStep step,
                                   SceneLayout layout, int sceneWidth, int sceneHeight, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        PoseStack poseStack = guiGraphics.pose();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        float scale = layout.modelScale() * this.cameraZoom;
        float centerX = (layout.minCol() + layout.maxCol()) / 2.0F;
        float centerY = (layout.minLayer() + layout.maxLayer()) / 2.0F;
        float centerZ = (layout.minRow() + layout.maxRow()) / 2.0F;
        float pulse = (float) (Math.sin((minecraft.level == null ? 0 : minecraft.level.getGameTime() + partialTick) * 0.18F) * 0.5F + 0.5F);

        RenderSystem.enableDepthTest();
        poseStack.pushPose();
        poseStack.translate(sceneWidth / 2.0F, sceneHeight * 0.54F, 420.0F);
        poseStack.scale(scale, -scale, scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(this.cameraPitch));
        poseStack.mulPose(Axis.YP.rotationDegrees(this.cameraYaw));

        for (RenderBlock block : visibleBlocks) {
            boolean highlighted = step.highlightedSymbols().isEmpty() || step.highlightedSymbols().contains(block.symbol());
            poseStack.pushPose();
            poseStack.translate(block.col() - centerX, block.layer() - centerY, block.row() - centerZ);
            minecraft.getBlockRenderer().renderSingleBlock(block.state(), poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();

            if (highlighted) {
                poseStack.pushPose();
                poseStack.translate(block.col() - centerX - 0.015F, block.layer() - centerY - 0.015F, block.row() - centerZ - 0.015F);
                poseStack.scale(1.03F + pulse * 0.025F, 1.03F + pulse * 0.025F, 1.03F + pulse * 0.025F);
                minecraft.getBlockRenderer().renderSingleBlock(block.state(), poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
        poseStack.popPose();
        bufferSource.endBatch();
        RenderSystem.disableDepthTest();
    }

    private void renderBasePlate(GuiGraphics guiGraphics, SceneLayout layout) {
        int sceneWidth = this.width - SIDEBAR_WIDTH;
        int sceneHeight = this.height - TIMELINE_HEIGHT;
        int left = 24;
        int top = 36;
        int right = Math.max(left + 120, sceneWidth - 24);
        int bottom = Math.max(top + 90, sceneHeight - 72);
        guiGraphics.fill(left, top, right, bottom, 0xFF11151C);
        for (int y = top; y < bottom; y += 10) {
            guiGraphics.fill(left, y, right, y + 1, 0xFF28303A);
        }
        for (int x = left; x < right; x += 10) {
            guiGraphics.fill(x, top, x + 1, bottom, 0xFF28303A);
        }
        guiGraphics.fill(left, top, right, top + 1, 0xFF3A4452);
        guiGraphics.fill(left, bottom - 1, right, bottom, 0xFF3A4452);
        guiGraphics.fill(left, top, left + 1, bottom, 0xFF3A4452);
        guiGraphics.fill(right - 1, top, right, bottom, 0xFF3A4452);
    }

    private void renderSidebar(GuiGraphics guiGraphics, int x, UfoTutorialStep step) {
        guiGraphics.drawString(this.font, this.entry.title(), x + 12, 44, 0xFF76F4E8, false);
        guiGraphics.fill(x + 8, 60, this.width, 62, 0xFF3D4052);
        guiGraphics.drawString(this.font, Component.literal("Show:"), x + 12, 78, 0xFF8A90A4, false);
        guiGraphics.drawString(this.font, currentBlockFilterText(), x + 12, 98, 0xFFF0F3FF, false);
        guiGraphics.drawString(this.font, currentVisibleLayerText(step), x + 12, 112, 0xFFF0F3FF, false);
        guiGraphics.fill(x + 8, 128, this.width, 130, 0xFF3D4052);

        guiGraphics.drawString(this.font, step.title(), x + 12, 264, 0xFFF5F7FA, false);
        int lineY = 282;
        for (var line : this.font.split(step.text(), 144)) {
            guiGraphics.drawString(this.font, line, x + 12, lineY, 0xFFD4DCEC, false);
            lineY += this.font.lineHeight + 2;
        }

        renderMaterials(guiGraphics, x + 12, 374, step);
    }

    private void renderMaterials(GuiGraphics guiGraphics, int x, int y, UfoTutorialStep step) {
        List<MaterialLine> materials = collectMaterials(step);
        guiGraphics.drawString(this.font, Component.translatable("ufo.tutorial.materials"), x, y, 0xFFF5F7FA, false);
        if (materials.isEmpty()) {
            guiGraphics.drawString(this.font, Component.literal("-"), x, y + 14, 0xFF8E99AA, false);
            return;
        }

        int shown = Math.min(5, materials.size());
        for (int i = 0; i < shown; i++) {
            MaterialLine material = materials.get(i);
            int rowY = y + 14 + i * 18;
            guiGraphics.renderItem(material.stack(), x, rowY - 4);
            String text = material.count() + "x " + material.stack().getHoverName().getString();
            guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(text, 92), x + 20, rowY, 0xFFD4DCEC, false);
        }
        if (materials.size() > shown) {
            guiGraphics.drawString(this.font, Component.literal("+" + (materials.size() - shown)), x + 102, y + 14 + shown * 18, 0xFFAFC7FF, false);
        }
    }

    private void renderBottomCaption(GuiGraphics guiGraphics, int sceneWidth, int sceneHeight, UfoTutorialStep step) {
        String text = step.text().getString();
        int textWidth = Math.min(sceneWidth - 120, this.font.width(text) + 20);
        int x = (sceneWidth - textWidth) / 2;
        int y = sceneHeight - 44;
        guiGraphics.fill(x, y, x + textWidth, y + 18, 0xAA000000);
        guiGraphics.drawString(this.font, this.font.plainSubstrByWidth(text, textWidth - 12), x + 6, y + 5, 0xFFFFFFFF, false);
    }

    private void renderTimeline(GuiGraphics guiGraphics, int sceneWidth, int sceneHeight, UfoTutorialStep step, int visibleBuildBlocks) {
        int y = sceneHeight + 18;
        int startX = 140;
        int endX = sceneWidth - 90;
        int totalTicks = totalBuildTicks(step);
        double progress = totalTicks <= 0 ? 1.0D : Math.min(1.0D, this.buildTick / (double) totalTicks);
        int progressX = startX + (int) ((endX - startX) * progress);
        guiGraphics.drawString(this.font, this.playing ? "||" : ">", 14, y - 7, 0xFF76F4E8, false);
        guiGraphics.drawString(this.font, TimelapseSpeed.values()[this.speedIndex].label(), 78, y - 7, 0xFFD4DCEC, false);
        guiGraphics.fill(startX, y, endX, y + 5, 0xFF172335);
        guiGraphics.fill(startX, y, progressX, y + 5, 0xFF51D8FF);
        int count = this.scene.steps().size();
        for (int i = 0; i < count; i++) {
            int x = count == 1 ? startX : startX + (endX - startX) * i / (count - 1);
            guiGraphics.fill(x - 2, y - 5, x + 2, y + 9, i == this.stepIndex ? 0xFF51D8FF : 0xFFE5EDF8);
        }
        guiGraphics.fill(progressX - 4, y - 8, progressX + 4, y + 13, 0xFF51D8FF);
        guiGraphics.drawString(this.font,
                Component.literal(formatTime(Math.min(this.buildTick, totalTicks)) + "/" + formatTime(totalTicks)),
                endX + 16, y - 7, 0xFFAFC7FF, false);
        guiGraphics.drawString(this.font,
                Component.literal(visibleBuildBlocks + "/" + visibleBlocksForStep(step).size()),
                endX + 16, y + 8, 0xFF8E99AA, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
            moveStep(1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
            moveStep(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W) {
            moveLayer(1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S) {
            moveLayer(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_HOME) {
            this.stepIndex = 0;
            this.layerOverride = ALL_LAYERS;
            this.buildTick = 0;
            this.playing = true;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_END) {
            this.stepIndex = this.scene.steps().size() - 1;
            this.layerOverride = ALL_LAYERS;
            this.buildTick = 0;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_SPACE) {
            this.playing = !this.playing;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_R) {
            centerCamera();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_Q) {
            this.cameraYaw -= 15.0F;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_E) {
            this.cameraYaw += 15.0F;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX >= this.lastSceneWidth) {
            moveLayer(scrollY > 0 ? 1 : -1);
            return true;
        }
        if (mouseX < this.lastSceneWidth && mouseY < this.lastSceneHeight) {
            this.cameraZoom = Math.max(0.35F, Math.min(2.4F, this.cameraZoom + (float) scrollY * 0.08F));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseY >= this.lastSceneHeight && mouseX < this.lastSceneWidth) {
            int count = this.scene.steps().size();
            int startX = 140;
            int endX = this.lastSceneWidth - 90;
            if (mouseX >= startX && mouseX <= endX && count > 1) {
                if (Screen.hasShiftDown()) {
                    double stepProgress = (mouseX - startX) / Math.max(1.0D, endX - startX);
                    this.stepIndex = Math.max(0, Math.min(count - 1, (int) Math.round(stepProgress * (count - 1))));
                    this.layerOverride = ALL_LAYERS;
                    this.buildTick = 0;
                } else {
                    double progress = (mouseX - startX) / Math.max(1.0D, endX - startX);
                    this.buildTick = Math.max(0, Math.min(totalBuildTicks(currentStep()), (int) Math.round(progress * totalBuildTicks(currentStep()))));
                }
                return true;
            }
        }
        if (button == 0 && mouseX < this.lastSceneWidth && mouseY < this.lastSceneHeight) {
            this.draggingScene = true;
            this.lastDragX = mouseX;
            this.lastDragY = mouseY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.draggingScene) {
            this.cameraYaw += (float) (mouseX - this.lastDragX) * 0.35F;
            this.cameraPitch = Math.max(15.0F, Math.min(70.0F, this.cameraPitch + (float) (mouseY - this.lastDragY) * 0.25F));
            this.lastDragX = mouseX;
            this.lastDragY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.draggingScene) {
            this.draggingScene = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void renderHoveredBlockTooltip(GuiGraphics guiGraphics, UfoTutorialStep step, SceneLayout layout, int mouseX, int mouseY) {
        RenderBlock hovered = getHoveredBlock(step, layout, mouseX, mouseY);
        if (hovered != null) {
            RenderSystem.disableDepthTest();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 1000.0F);
            guiGraphics.renderTooltip(this.font, hovered.tooltip().stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
            guiGraphics.pose().popPose();
        }
    }

    private RenderBlock getHoveredBlock(UfoTutorialStep step, SceneLayout layout, int mouseX, int mouseY) {
        List<RenderBlock> hoverableBlocks = visibleBlocksForStep(step).stream()
                .limit(visibleBuildBlocks(step))
                .sorted(Comparator.comparingInt(RenderBlock::row)
                        .thenComparingInt(RenderBlock::layer).reversed())
                .toList();
        float pickRadius = Math.max(8.0F, layout.modelScale() * this.cameraZoom * 0.65F);
        for (RenderBlock block : hoverableBlocks) {
            ProjectedPoint point = projectBlock(block, layout);
            if (Math.abs(mouseX - point.x()) <= pickRadius && Math.abs(mouseY - point.y()) <= pickRadius) {
                return block;
            }
        }
        return null;
    }

    private ProjectedPoint projectBlock(RenderBlock block, SceneLayout layout) {
        float scale = layout.modelScale() * this.cameraZoom;
        float x = block.col() - (layout.minCol() + layout.maxCol()) / 2.0F;
        float y = block.layer() - (layout.minLayer() + layout.maxLayer()) / 2.0F;
        float z = block.row() - (layout.minRow() + layout.maxRow()) / 2.0F;
        double pitch = Math.toRadians(this.cameraPitch);
        double yaw = Math.toRadians(this.cameraYaw);
        float y1 = (float) (y * Math.cos(pitch) - z * Math.sin(pitch));
        float z1 = (float) (y * Math.sin(pitch) + z * Math.cos(pitch));
        float x2 = (float) (x * Math.cos(yaw) + z1 * Math.sin(yaw));
        float y2 = y1;
        return new ProjectedPoint(this.lastSceneWidth / 2.0F + x2 * scale, this.lastSceneHeight * 0.54F - y2 * scale);
    }

    private UfoTutorialStep currentStep() {
        return this.scene.steps().get(this.stepIndex);
    }

    private void moveStep(int delta) {
        this.stepIndex = Math.max(0, Math.min(this.scene.steps().size() - 1, this.stepIndex + delta));
        this.layerOverride = ALL_LAYERS;
        this.buildTick = 0;
        this.playing = true;
    }

    private void moveLayer(int delta) {
        int minLayer = this.renderBlocks.stream().mapToInt(RenderBlock::layer).min().orElse(0);
        int maxLayer = this.renderBlocks.stream().mapToInt(RenderBlock::layer).max().orElse(0);
        if (this.layerOverride == ALL_LAYERS) {
            this.layerOverride = findNextLayer(delta > 0 ? minLayer - 1 : maxLayer + 1, delta, minLayer, maxLayer);
            clampBuildTick();
            return;
        }
        int nextLayer = findNextLayer(this.layerOverride, delta, minLayer, maxLayer);
        if (nextLayer < minLayer || nextLayer > maxLayer) {
            this.layerOverride = ALL_LAYERS;
        } else {
            this.layerOverride = nextLayer;
        }
        clampBuildTick();
    }

    private int findNextLayer(int currentLayer, int delta, int minLayer, int maxLayer) {
        int candidate = currentLayer + delta;
        while (candidate >= minLayer && candidate <= maxLayer) {
            if (hasVisibleBlocksInLayer(candidate)) {
                return candidate;
            }
            candidate += delta;
        }
        return -1;
    }

    private boolean hasVisibleBlocksInLayer(int layer) {
        return this.renderBlocks.stream()
                .anyMatch(block -> block.layer() == layer && isFilterVisible(block));
    }

    private void setBlockFilter(BlockFilter blockFilter) {
        this.blockFilter = blockFilter;
        this.layerOverride = ALL_LAYERS;
        this.buildTick = 0;
        this.playing = true;
    }

    private void centerCamera() {
        this.cameraPitch = DEFAULT_CAMERA_PITCH;
        this.cameraYaw = DEFAULT_CAMERA_YAW;
        this.cameraZoom = 1.0F;
    }

    private void clampBuildTick() {
        this.buildTick = Math.max(0, Math.min(this.buildTick, totalBuildTicks(currentStep())));
        if (this.buildTick >= totalBuildTicks(currentStep())) {
            this.playing = false;
        }
    }

    private Component currentVisibleLayerText(UfoTutorialStep step) {
        int layer = activeLayer(step);
        return layer != ALL_LAYERS
                ? Component.translatable("ufo.tutorial.visible_layer", layer)
                : Component.translatable("ufo.tutorial.visible_layer_all");
    }

    private Component currentBlockFilterText() {
        return Component.translatable(this.blockFilter.translationKey());
    }

    private SceneLayout createLayout(UfoTutorialStep step, int sceneWidth, int sceneHeight) {
        int minCol = 0;
        int minRow = 0;
        int minLayer = 0;
        int maxCol = 0;
        int maxRow = 0;
        int maxLayer = 0;
        boolean found = false;
        for (RenderBlock block : this.renderBlocks) {
            if (!isVisible(block, step) || !isFilterVisible(block)) {
                continue;
            }
            if (!found) {
                minCol = maxCol = block.col();
                minRow = maxRow = block.row();
                minLayer = maxLayer = block.layer();
                found = true;
                continue;
            }
            minCol = Math.min(minCol, block.col());
            minRow = Math.min(minRow, block.row());
            minLayer = Math.min(minLayer, block.layer());
            maxCol = Math.max(maxCol, block.col());
            maxRow = Math.max(maxRow, block.row());
            maxLayer = Math.max(maxLayer, block.layer());
        }

        int colSpan = maxCol - minCol + 1;
        int rowSpan = maxRow - minRow + 1;
        int layerSpan = maxLayer - minLayer + 1;
        int offsetX = layerSpan > 10 ? 1 : 5;
        int offsetY = layerSpan > 10 ? 1 : 6;
        int cellByWidth = Math.max(3, (sceneWidth - 120 - layerSpan * offsetX) / Math.max(1, colSpan));
        int cellByHeight = Math.max(3, (sceneHeight - 140 - layerSpan * offsetY) / Math.max(1, rowSpan));
        int cellSize = Math.max(3, Math.min(24, Math.min(cellByWidth, cellByHeight)));
        int usedWidth = colSpan * cellSize + layerSpan * offsetX;
        int usedHeight = rowSpan * cellSize + layerSpan * offsetY;
        float modelScale = Math.max(4.0F, Math.min(22.0F, Math.min(
                (sceneWidth - 160.0F) / Math.max(1.0F, colSpan + rowSpan + 2.0F),
                (sceneHeight - 170.0F) / Math.max(1.0F, layerSpan + Math.max(colSpan, rowSpan) * 0.6F + 2.0F))));
        return new SceneLayout(
                cellSize,
                offsetX,
                offsetY,
                Math.max(30, (sceneWidth - usedWidth) / 2),
                Math.max(60, (sceneHeight - usedHeight) / 2),
                minCol,
                minRow,
                minLayer,
                maxCol,
                maxRow,
                maxLayer,
                modelScale);
    }

    private boolean isVisible(RenderBlock block, UfoTutorialStep step) {
        int layer = activeLayer(step);
        return layer == ALL_LAYERS || block.layer() == layer;
    }

    private boolean isFilterVisible(RenderBlock block) {
        return switch (this.blockFilter) {
            case ALL -> true;
            case HATCHES -> block.role() == BlockRole.HATCH;
            case CONTROLLER -> block.role() == BlockRole.CONTROLLER;
            case HIGHLIGHTED -> currentStep().highlightedSymbols().isEmpty() || currentStep().highlightedSymbols().contains(block.symbol());
        };
    }

    private int activeLayer(UfoTutorialStep step) {
        if (this.layerOverride != ALL_LAYERS) {
            return this.layerOverride;
        }
        OptionalInt visibleLayer = step.visibleLayer();
        return visibleLayer.isPresent()
                ? visibleLayer.getAsInt() - this.entry.previewEntry().definition().pattern().getControllerLayer()
                : ALL_LAYERS;
    }

    private List<MaterialLine> collectMaterials(UfoTutorialStep step) {
        Map<String, MaterialLine> materials = new LinkedHashMap<>();
        for (RenderBlock block : this.renderBlocks) {
            if (!isVisible(block, step) || !isFilterVisible(block)) {
                continue;
            }
            if (!step.highlightedSymbols().isEmpty() && !step.highlightedSymbols().contains(block.symbol())) {
                continue;
            }
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(block.stack().getItem());
            materials.compute(String.valueOf(key), (ignored, existing) -> existing == null
                    ? new MaterialLine(block.stack().copyWithCount(1), 1)
                    : new MaterialLine(existing.stack(), existing.count() + 1));
        }
        return materials.values().stream()
                .sorted(Comparator.comparingInt(MaterialLine::count).reversed())
                .toList();
    }

    private List<RenderBlock> visibleBlocksForStep(UfoTutorialStep step) {
        return this.renderBlocks.stream()
                .filter(block -> isVisible(block, step))
                .filter(this::isFilterVisible)
                .sorted(Comparator.comparingInt(RenderBlock::buildIndex))
                .toList();
    }

    private int visibleBuildBlocks(UfoTutorialStep step) {
        List<RenderBlock> blocks = visibleBlocksForStep(step);
        if (blocks.isEmpty()) {
            return 0;
        }
        return Math.max(1, Math.min(blocks.size(), this.buildTick / TICKS_PER_BLOCK + 1));
    }

    private int totalBuildTicks(UfoTutorialStep step) {
        return Math.max(TICKS_PER_BLOCK, visibleBlocksForStep(step).size() * TICKS_PER_BLOCK);
    }

    private static String formatTime(int ticks) {
        int totalTenths = Math.max(0, ticks) / 2;
        return (totalTenths / 10) + "." + (totalTenths % 10) + "s";
    }

    private static List<RenderBlock> buildRenderBlocks(UfoTutorialEntry entry) {
        MultiblockControllerDefinition definition = entry.previewEntry().definition();
        MultiblockPattern pattern = definition.pattern();
        char[][][] chars = pattern.getPattern();
        BlockState controllerState = resolveControllerState(entry.previewEntry().iconStack());
        List<RenderBlock> blocks = new ArrayList<>();

        for (int layer = 0; layer < chars.length; layer++) {
            for (int row = 0; row < chars[layer].length; row++) {
                for (int col = 0; col < chars[layer][row].length; col++) {
                    char symbol = chars[layer][row][col];
                    BlockState state = symbol == pattern.getControllerChar()
                            ? controllerState
                            : definition.defaultCreativeStates().get(symbol);
                    if (state == null || state.isAir()) {
                        continue;
                    }
                    ItemStack stack = new ItemStack(state.getBlock());
                    if (!stack.isEmpty()) {
                        BlockRole role = classifyBlock(pattern, symbol, state);
                        int relativeLayer = layer - pattern.getControllerLayer();
                        int relativeRow = pattern.getControllerRow() - row;
                        int relativeCol = col - pattern.getControllerCol();
                        blocks.add(new RenderBlock(relativeLayer, relativeRow, relativeCol, symbol, role, state, stack, colorFor(symbol), 0, buildTooltip(pattern, symbol, state, role)));
                    }
                }
            }
        }
        List<RenderBlock> ordered = blocks.stream()
                .sorted(Comparator.comparingInt(RenderBlock::layer)
                        .thenComparingInt(RenderBlock::row)
                        .thenComparingInt(RenderBlock::col))
                .toList();
        List<RenderBlock> indexed = new ArrayList<>();
        for (int i = 0; i < ordered.size(); i++) {
            RenderBlock block = ordered.get(i);
            indexed.add(new RenderBlock(block.layer(), block.row(), block.col(), block.symbol(), block.role(), block.state(), block.stack(), block.color(), i, block.tooltip()));
        }
        return indexed;
    }

    private static List<Component> buildTooltip(MultiblockPattern pattern, char symbol, BlockState state, BlockRole role) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return List.of(
                pattern.getLegendName(symbol),
                Component.literal(symbol + " - " + key),
                Component.translatable(functionTooltipKey(role, key.getPath())));
    }

    private static BlockRole classifyBlock(MultiblockPattern pattern, char symbol, BlockState state) {
        if (symbol == pattern.getControllerChar()) {
            return BlockRole.CONTROLLER;
        }
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        if (path.contains("hatch") || path.contains("input") || path.contains("output") || path.contains("fuel") || path.contains("energy") || path.contains("pattern")) {
            return BlockRole.HATCH;
        }
        return BlockRole.STRUCTURE;
    }

    private static String functionTooltipKey(BlockRole role, String blockPath) {
        if (role == BlockRole.CONTROLLER) {
            return "ufo.tutorial.role.controller";
        }
        if (role != BlockRole.HATCH) {
            return "ufo.tutorial.role.structure";
        }
        if (blockPath.contains("input")) {
            return "ufo.tutorial.role.hatch.input";
        }
        if (blockPath.contains("output")) {
            return "ufo.tutorial.role.hatch.output";
        }
        if (blockPath.contains("fluid")) {
            return "ufo.tutorial.role.hatch.fluid";
        }
        if (blockPath.contains("energy")) {
            return "ufo.tutorial.role.hatch.energy";
        }
        if (blockPath.contains("fuel")) {
            return "ufo.tutorial.role.hatch.fuel";
        }
        if (blockPath.contains("pattern")) {
            return "ufo.tutorial.role.hatch.pattern";
        }
        return "ufo.tutorial.role.hatch";
    }

    private static BlockState resolveControllerState(ItemStack iconStack) {
        if (iconStack.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock().defaultBlockState();
        }
        return Blocks.IRON_BLOCK.defaultBlockState();
    }

    private static int colorFor(char symbol) {
        int hue = Math.abs(symbol * 1103515245);
        int r = 80 + hue % 130;
        int g = 80 + (hue / 17) % 130;
        int b = 80 + (hue / 37) % 130;
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record RenderBlock(int layer, int row, int col, char symbol, BlockRole role, BlockState state, ItemStack stack, int color, int buildIndex, List<Component> tooltip) {
    }

    private record SceneLayout(int cellSize, int layerOffsetX, int layerOffsetY, int leftPad, int topPad,
                               int minCol, int minRow, int minLayer, int maxCol, int maxRow, int maxLayer,
                               float modelScale) {
    }

    private record MaterialLine(ItemStack stack, int count) {
    }

    private record ProjectedPoint(float x, float y) {
    }

    private enum TimelapseSpeed {
        X1("1x", 1),
        X2("2x", 2),
        X4("4x", 4),
        X8("8x", 8);

        private final String label;
        private final int ticksPerClientTick;

        TimelapseSpeed(String label, int ticksPerClientTick) {
            this.label = label;
            this.ticksPerClientTick = ticksPerClientTick;
        }

        private String label() {
            return label;
        }

        private int ticksPerClientTick() {
            return ticksPerClientTick;
        }
    }

    private enum BlockFilter {
        ALL("ufo.tutorial.filter_all"),
        HATCHES("ufo.tutorial.filter_hatches"),
        CONTROLLER("ufo.tutorial.filter_controller"),
        HIGHLIGHTED("ufo.tutorial.filter_highlighted");

        private final String translationKey;

        BlockFilter(String translationKey) {
            this.translationKey = translationKey;
        }

        private String translationKey() {
            return translationKey;
        }
    }

    private enum BlockRole {
        STRUCTURE,
        HATCH,
        CONTROLLER
    }
}
