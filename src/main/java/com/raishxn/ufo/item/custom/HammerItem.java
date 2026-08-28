package com.raishxn.ufo.item.custom;

import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.util.EnergyToolHelper; // Importante: Importar o Helper
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class HammerItem extends Item implements IEnergyTool, IHasModeHUD, IHasCycleableModes {

    private static final String TAG_RANGE = "range";
    private static final int[] RANGES = {0, 1, 2, 3}; // 0=1x1, 1=3x3, 2=5x5, 3=7x7

    public HammerItem(final ToolMaterial material, final Properties properties) {
        super(properties.tool(material, BlockTags.MINEABLE_WITH_PICKAXE, 7.0F, -3.4F, 0.0F));
    }


    @Override
    public Component getName(final ItemStack stack) {
        return IEnergyTool.super.getName(stack);
    }

    @Override
    public boolean isBarVisible(final ItemStack pStack) {
        return EnergyToolHelper.isBarVisible(pStack);
    }

    @Override
    public int getBarWidth(final ItemStack pStack) {
        return EnergyToolHelper.getBarWidth(pStack);
    }

    @Override
    public int getBarColor(final ItemStack pStack) {
        return EnergyToolHelper.getBarColor(pStack);
    }
    public void appendHoverText(final ItemStack pStack, final TooltipContext pContext, final List<Component> pTooltipComponents, final TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(getModeHudComponent(pStack));

    }

    @Override
    public int getEnergyPerUse() {
        return 50; // Custo por bloco
    }


    @Override
    public boolean mineBlock(final ItemStack pStack, final Level pLevel, final BlockState pState, final BlockPos pPos, final LivingEntity pEntityLiving) {
        if (!pLevel.isClientSide() && pEntityLiving instanceof final ServerPlayer player) {
            final int range = getRange(pStack);

            if (range == 0) {
                if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
                    consumeEnergy(pStack);
                }
                return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
            }

            final List<BlockPos> blocksToBreak = getBlocksToBeDestroyed(range, pPos, player);
            final boolean autoSmelt = pStack.getOrDefault(ModDataComponents.AUTO_SMELT.get(), false);

            if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
                consumeEnergy(pStack);
            }

            for (final BlockPos targetPos : blocksToBreak) {
                if (targetPos.equals(pPos)) continue;

                final BlockState targetState = pLevel.getBlockState(targetPos);

                if (!targetState.isAir() && pStack.isCorrectToolForDrops(targetState)) {
                    if (consumeEnergy(pStack)) {
                        if (autoSmelt) {
                            smeltAndSpawn(pLevel, targetPos, targetState, pStack);
                            pLevel.destroyBlock(targetPos, false, player);
                        } else {
                            pLevel.destroyBlock(targetPos, true, player);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return super.mineBlock(pStack, pLevel, pState, pPos, pEntityLiving);
    }


    public static List<BlockPos> getBlocksToBeDestroyed(final int range, final BlockPos initalBlockPos, final Player player) {
        final List<BlockPos> positions = new ArrayList<>();
        final BlockHitResult traceResult = player.level().clip(new ClipContext(player.getEyePosition(1f),
                (player.getEyePosition(1f).add(player.getViewVector(1f).scale(6f))),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

        if(traceResult.getType() == HitResult.Type.MISS) {
            return positions;
        }

        final Direction face = traceResult.getDirection();
        if(face == Direction.DOWN || face == Direction.UP) {
            for(int x = -range; x <= range; x++) for(int y = -range; y <= range; y++) positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY(), initalBlockPos.getZ() + y));
        } else if(face == Direction.NORTH || face == Direction.SOUTH) {
            for(int x = -range; x <= range; x++) for(int y = -range; y <= range; y++) positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY() + y, initalBlockPos.getZ()));
        } else if(face == Direction.EAST || face == Direction.WEST) {
            for(int x = -range; x <= range; x++) for(int y = -range; y <= range; y++) positions.add(new BlockPos(initalBlockPos.getX(), initalBlockPos.getY() + y, initalBlockPos.getZ() + x));
        }
        return positions;
    }

    private void smeltAndSpawn(final Level level, final BlockPos pos, final BlockState state, final ItemStack tool) {
        if (level.isClientSide()) return;
        final List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, pos, level.getBlockEntity(pos), null, tool);

        for (final ItemStack drop : drops) {
            final Optional<net.minecraft.world.item.crafting.RecipeHolder<SmeltingRecipe>> recipe = ((ServerLevel) level).recipeAccess()
                    .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(drop), level);

            if (recipe.isPresent()) {
                final ItemStack result = recipe.get().value().assemble(new SingleRecipeInput(drop)).copy();
                result.setCount(drop.getCount());
                spawnItem(level, pos, result);
            } else {
                spawnItem(level, pos, drop);
            }
        }
    }

    private void spawnItem(final Level level, final BlockPos pos, final ItemStack stack) {
        final ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        level.addFreshEntity(entity);
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

        player.sendOverlayMessage(Component.translatable("tooltip.ufo.mode_changed", newRange));
    }

    public static int getRange(final ItemStack stack) {
        final CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.copyTag().contains(TAG_RANGE)) {
            return customData.copyTag().getInt(TAG_RANGE).orElse(RANGES[0]);
        }
        return RANGES[0];
    }

    @Override
    public Component getModeHudComponent(final ItemStack stack) {
        final int range = getRange(stack);
        final int dimension = (range == 0) ? 1 : (range * 2) + 1;
        final String areaText = dimension + "x" + dimension;
        final ChatFormatting color;
        switch (range) {
            case 1: color = ChatFormatting.GREEN; break;
            case 2: color = ChatFormatting.YELLOW; break;
            case 3: color = ChatFormatting.RED; break;
            default: color = ChatFormatting.WHITE; break;
        }
        final Component coloredArea = Component.literal(areaText).withStyle(color);
        return Component.translatable("tooltip.ufo.area_mode", coloredArea);
    }
}
