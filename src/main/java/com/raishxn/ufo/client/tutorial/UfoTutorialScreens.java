package com.raishxn.ufo.client.tutorial;

import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.api.tutorial.UfoTutorialRegistry;
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
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.hitResult == null || minecraft.hitResult.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        final BlockEntity blockEntity = minecraft.level.getBlockEntity(((BlockHitResult) minecraft.hitResult).getBlockPos());
        return blockEntity != null && openFor(blockEntity);
    }

    public static boolean openFromCurrentContext() {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return false;
        }

        final ItemStack hoveredStack = getHoveredContainerStack(minecraft);
        if (!hoveredStack.isEmpty() && openFor(hoveredStack)) {
            return true;
        }

        final ItemStack jeiStack = getHoveredJeiStack();
        if (!jeiStack.isEmpty() && openFor(jeiStack)) {
            return true;
        }

        final ItemStack carriedStack = minecraft.player.containerMenu.getCarried();
        if (!carriedStack.isEmpty() && openFor(carriedStack)) {
            return true;
        }

        if (openFor(minecraft.player.getMainHandItem()) || openFor(minecraft.player.getOffhandItem())) {
            return true;
        }

        return openLookedAtController();
    }

    public static boolean openFor(final BlockEntity blockEntity) {
        return false;
    }

    public static boolean openFor(final ItemStack stack) {
        return false;
    }

    private static ItemStack getHoveredContainerStack(final Minecraft minecraft) {
        if (!(minecraft.screen instanceof final AbstractContainerScreen<?> containerScreen)) {
            return ItemStack.EMPTY;
        }

        try {
            final Field hoveredSlotField = AbstractContainerScreen.class.getDeclaredField("hoveredSlot");
            hoveredSlotField.setAccessible(true);
            final Object value = hoveredSlotField.get(containerScreen);
            if (value instanceof final Slot slot && slot.hasItem()) {
                return slot.getItem();
            }
        } catch (final ReflectiveOperationException | RuntimeException ignored) {
            return ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack getHoveredJeiStack() {
        try {
            final Class<?> pluginClass = Class.forName("com.raishxn.ufo.compat.jei.UfoJeiPlugin");
            final Method method = pluginClass.getDeclaredMethod("getHoveredItemStack");
            final Object value = method.invoke(null);
            if (value instanceof final ItemStack stack) {
                return stack;
            }
        } catch (final ReflectiveOperationException | LinkageError | RuntimeException ignored) {
            return ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }
}
