package com.raishxn.ufo.block.entity;

import com.raishxn.ufo.api.multiblock.MultiblockPattern;
import com.raishxn.ufo.block.entity.pattern.QuantumSlicerPatternFactory;
import com.raishxn.ufo.init.ModBlockEntities;
import com.raishxn.ufo.screen.QuantumSlicerControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class QuantumSlicerControllerBE extends AbstractParallelMultiblockControllerBE {

    private static MultiblockPattern PATTERN;

    public QuantumSlicerControllerBE(final BlockPos pos, final BlockState state) {
        super(ModBlockEntities.QUANTUM_SLICER_CONTROLLER_BE.get(), pos, state);
    }

    @Override
    protected MultiblockPattern getControllerPattern() {
        if (PATTERN == null) {
            PATTERN = QuantumSlicerPatternFactory.getPattern();
        }
        return PATTERN;
    }

    @Override
    protected String getControllerTranslationKey() {
        return "block.ufo.quantum_slicer_controller";
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int id, final Inventory playerInventory, final Player player) {
        return new QuantumSlicerControllerMenu(id, playerInventory, this);
    }

    @Override
    protected java.util.List<com.raishxn.ufo.block.entity.processing.MultiblockProcessingRecipe> getAvailableRecipes() {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return java.util.List.of();
        }
        final java.util.List<com.raishxn.ufo.block.entity.processing.MultiblockProcessingRecipe> recipes = new java.util.ArrayList<>();
        for (final var holder : serverLevel.recipeAccess().recipeMap().byType(com.raishxn.ufo.init.ModRecipes.UNIVERSAL_MULTIBLOCK_TYPE.get())) {
            final var recipe = holder.value();
            if (recipe.machine() == com.raishxn.ufo.recipe.UniversalMultiblockMachineKind.QUANTUM_SLICER) {
                recipes.add(com.raishxn.ufo.block.entity.processing.MultiblockProcessingRecipe.fromUniversal(holder.id().identifier(), recipe));
            }
        }
        return recipes;
    }
}
