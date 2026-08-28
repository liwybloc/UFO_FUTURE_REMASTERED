package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class AetherContainmentCapsuleItem extends Item {
    public static final int CAPACITY = 4000;

    public AetherContainmentCapsuleItem(final Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public void appendHoverText(final ItemStack stack, final TooltipContext context, final List<Component> tooltipComponents, final TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.ufo.acc").withStyle(ChatFormatting.GRAY));
        final FluidHandlerItemStack handler = new HazardousFluidHandler(stack, CAPACITY);
        final FluidStack fluid = handler.getFluid();
        if (!fluid.isEmpty()) {
            tooltipComponents.add(Component.literal("Contains: ")
                    .append(fluid.getHoverName())
                    .append(" (" + fluid.getAmount() + "mB)")
                    .withStyle(ChatFormatting.AQUA));
        } else {
            tooltipComponents.add(Component.literal("Empty").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static final class HazardousFluidHandler extends FluidHandlerItemStack {
        public HazardousFluidHandler(@NotNull final ItemStack container, final int capacity) {
            super(ModDataComponents.FLUID_CONTENT, container, capacity);
        }
        @Override
        public boolean isFluidValid(final int tank, @NotNull final FluidStack stack) {
            return stack.is(ModTags.Fluids.HAZARDOUS);
        }
    }
}
