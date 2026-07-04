package com.raishxn.ufo.client.tutorial;

import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.api.tutorial.UfoTutorialRegistry;
import com.raishxn.ufo.client.tutorial.screen.UfoTutorialScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class UfoTutorialScreens {
    private UfoTutorialScreens() {
    }

    public static boolean openLookedAtController() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.hitResult == null || minecraft.hitResult.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        BlockEntity blockEntity = minecraft.level.getBlockEntity(((BlockHitResult) minecraft.hitResult).getBlockPos());
        return blockEntity != null && openFor(blockEntity);
    }

    public static boolean openFromCurrentContext() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return false;
        }

        ItemStack hoveredStack = getHoveredContainerStack(minecraft);
        if (!hoveredStack.isEmpty() && openFor(hoveredStack)) {
            return true;
        }

        ItemStack jeiStack = getHoveredJeiStack();
        if (!jeiStack.isEmpty() && openFor(jeiStack)) {
            return true;
        }

        ItemStack carriedStack = minecraft.player.containerMenu.getCarried();
        if (!carriedStack.isEmpty() && openFor(carriedStack)) {
            return true;
        }

        if (openFor(minecraft.player.getMainHandItem()) || openFor(minecraft.player.getOffhandItem())) {
            return true;
        }

        return openLookedAtController();
    }

    public static boolean openFor(BlockEntity blockEntity) {
        return MultiblockControllerDefinitions.getDefinition(blockEntity)
                .flatMap(definition -> MultiblockControllerDefinitions.getPreviewEntries().stream()
                        .filter(entry -> entry.definition() == definition)
                        .findFirst())
                .flatMap(UfoTutorialRegistry::get)
                .map(entry -> {
                    Minecraft.getInstance().setScreen(new UfoTutorialScreen(entry));
                    return true;
                })
                .orElse(false);
    }

    public static boolean openFor(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) {
            return false;
        }

        return MultiblockControllerDefinitions.getPreviewEntries().stream()
                .filter(entry -> stack.is(entry.iconStack().getItem()))
                .findFirst()
                .flatMap(UfoTutorialRegistry::get)
                .map(entry -> {
                    Minecraft.getInstance().setScreen(new UfoTutorialScreen(entry));
                    return true;
                })
                .orElse(false);
    }

    private static ItemStack getHoveredContainerStack(Minecraft minecraft) {
        if (!(minecraft.screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return ItemStack.EMPTY;
        }

        try {
            Field hoveredSlotField = AbstractContainerScreen.class.getDeclaredField("hoveredSlot");
            hoveredSlotField.setAccessible(true);
            Object value = hoveredSlotField.get(containerScreen);
            if (value instanceof Slot slot && slot.hasItem()) {
                return slot.getItem();
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack getHoveredJeiStack() {
        try {
            Class<?> pluginClass = Class.forName("com.raishxn.ufo.compat.jei.UfoJeiPlugin");
            Method method = pluginClass.getDeclaredMethod("getHoveredItemStack");
            Object value = method.invoke(null);
            if (value instanceof ItemStack stack) {
                return stack;
            }
        } catch (ReflectiveOperationException | LinkageError | RuntimeException ignored) {
            return ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }
}
