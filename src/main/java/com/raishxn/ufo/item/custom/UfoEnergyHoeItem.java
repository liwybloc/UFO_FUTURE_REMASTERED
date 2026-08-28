package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.util.EnergyToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class UfoEnergyHoeItem extends HoeItem implements IEnergyTool, IHasModeHUD, IHasCycleableModes {

    private static final String TAG_RANGE = "range";
    private static final int[] RANGES = {0, 1, 2, 3, 5}; // 0=1x1, 1=3x3...

    public UfoEnergyHoeItem(final ToolMaterial material, final Properties properties) {
        super(material, 0.0F, -3.0F, properties);
    }

    @Override
    public InteractionResult useOn(final UseOnContext pContext) {
        final Level level = pContext.getLevel();
        final BlockPos originPos = pContext.getClickedPos();
        final Player player = pContext.getPlayer();
        final ItemStack stack = pContext.getItemInHand();
        final int range = getRange(stack);

        if (player == null || level.isClientSide()) {
            return InteractionResult.PASS;
        }

        boolean actionPerformed = false;
        for (final BlockPos pos : getPositions(originPos, range)) {
            final BlockState blockState = level.getBlockState(pos);

            final BlockState modifiedState = level.getBlockState(pos).getToolModifiedState(
                    pContext,
                    net.neoforged.neoforge.common.ItemAbilities.HOE_TILL,
                    false
            );

            if (modifiedState != null) {
                if (consumeEnergy(stack)) {
                    level.setBlock(pos, modifiedState, 11);
                    actionPerformed = true;
                } else {
                    break;
                }
            }
        }

        if (actionPerformed) {
            level.playSound(player, originPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }


    @Override
    public void cycleMode(final ItemStack stack, final Player player) {
        final int currentRange = getRange(stack);
        int currentIndex = 0;
        for (int i = 0; i < RANGES.length; i++) { if (RANGES[i] == currentRange) { currentIndex = i; break; } }
        final int nextIndex = (currentIndex + 1) % RANGES.length;
        final int newRange = RANGES[nextIndex];

        final CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt(TAG_RANGE, newRange);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static int getRange(final ItemStack stack) {
        final CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) return data.copyTag().getInt(TAG_RANGE).orElse(RANGES[0]);
        return RANGES[0];
    }

    public static List<BlockPos> getPositions(final BlockPos origin, final int range) {
        return StreamSupport.stream(
                        BlockPos.betweenClosed(origin.offset(-range, 0, -range), origin.offset(range, 0, range)).spliterator(), false)
                .map(BlockPos::immutable)
                .collect(Collectors.toList());
    }


    @Override
    public Component getModeHudComponent(final ItemStack stack) {
        final int range = getRange(stack);
        final int dimension = (range == 0) ? 1 : (range * 2) + 1;
        final String areaText = dimension + "x" + dimension;

        final ChatFormatting color;
        switch (range) {
            case 1:  // 3x3
                color = ChatFormatting.GREEN;
                break;
            case 2:  // 5x5
                color = ChatFormatting.YELLOW;
                break;
            case 3:  // 7x7
                color = ChatFormatting.RED;
                break;
            case 5:  // 11x11
                color = ChatFormatting.AQUA; // Uma cor especial para o maior range
                break;
            default: // 1x1 (range 0)
                color = ChatFormatting.WHITE;
                break;
        }

        final Component coloredArea = Component.literal(areaText).withStyle(color);

        return Component.translatable("tooltip.ufo.area_mode", coloredArea);
    }

    @Override
    public Component getName(final ItemStack stack) { return IEnergyTool.super.getName(stack); }
    public void appendHoverText(final ItemStack pStack, final Item.TooltipContext pContext, final List<Component> pTooltipComponents, final TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(getModeHudComponent(pStack));
    }

    @Override public int getEnergyPerUse() { return 50; }
    @Override public boolean isBarVisible(final ItemStack pStack) { return EnergyToolHelper.isBarVisible(pStack); }
    @Override public int getBarWidth(final ItemStack pStack) { return EnergyToolHelper.getBarWidth(pStack); }
    @Override public int getBarColor(final ItemStack pStack) { return EnergyToolHelper.getBarColor(pStack); }
}
