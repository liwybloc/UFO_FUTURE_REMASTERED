package com.raishxn.ufo.block;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import com.raishxn.ufo.block.entity.DimensionalMatterAssemblerBlockEntity;
import com.raishxn.ufo.menu.UFOMenus;
import com.raishxn.ufo.init.ModMenus;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;

public final class DimensionalMatterAssemblerBlock extends AEBaseEntityBlock<DimensionalMatterAssemblerBlockEntity> {

    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    public DimensionalMatterAssemblerBlock(final BlockBehaviour.Properties properties) {
        super(properties
                .strength(1.5f, 4.0f)
                .sound(SoundType.METAL)
                .noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(WORKING, false));
    }

    @Override
    protected InteractionResult useWithoutItem(
            final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof final DimensionalMatterAssemblerBlockEntity be) {
            if (!level.isClientSide()) {
                MenuOpener.open(UFOMenus.DIMENSIONAL_MATTER_ASSEMBLER.get(), player, MenuLocators.forBlockEntity(be));
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useItemOn(
            final ItemStack heldItem,
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Player player,
            final InteractionHand hand,
            final BlockHitResult hit) {
        if (heldItem.getItem() instanceof final BucketItem bucket) {
            final var didSomething = useBucket(player, level, pos, heldItem, hand);
            if (didSomething) {
                return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
        }
        return super.useItemOn(heldItem, state, level, pos, player, hand, hit);
    }

    public boolean useBucket(final Player player, final Level level, final BlockPos pos, final ItemStack stack, final InteractionHand hand) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WORKING);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.horizontalFacing();
    }
}
