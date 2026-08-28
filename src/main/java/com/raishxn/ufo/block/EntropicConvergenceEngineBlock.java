package com.raishxn.ufo.block;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.crafting.CraftingUnitType;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.raishxn.ufo.block.entity.EntropicConvergenceEngineBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class EntropicConvergenceEngineBlock extends AbstractCraftingUnitBlock<EntropicConvergenceEngineBE> {
    public EntropicConvergenceEngineBlock(final BlockBehaviour.Properties properties) {
        super(metalProps(properties), CraftingUnitType.UNIT);
    }

    @Override
    protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof final EntropicConvergenceEngineBE be) {
            if (!be.isGuiAssembled() || !be.isNetworkConnected()) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                MenuOpener.open(com.raishxn.ufo.init.ModMenus.ENTROPIC_CONVERGENCE_ENGINE_MENU.get(), player, MenuLocators.forBlockEntity(be));
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block blockIn, final Orientation orientation, final boolean isMoving) {
        final EntropicConvergenceEngineBE be = this.getBlockEntity(level, pos);
        if (be != null) {
            be.updateMultiBlock(pos);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean isMoving) {
        final EntropicConvergenceEngineBE be = this.getBlockEntity(level, pos);
        if (be != null) {
            be.breakCluster();
        }
        super.affectNeighborsAfterRemoval(state, level, pos, isMoving);
    }

    @Nullable
    @Override
    public EntropicConvergenceEngineBE newBlockEntity(final BlockPos pos, final BlockState state) {
        return new EntropicConvergenceEngineBE(com.raishxn.ufo.init.ModBlockEntities.ENTROPIC_CONVERGENCE_ENGINE_BE.get(), pos, state);
    }
}
