package com.raishxn.ufo.block.entity;

import com.raishxn.ufo.api.multiblock.MultiblockPattern;
import com.raishxn.ufo.block.entity.pattern.QuantumCryoforgePatternFactory;
import com.raishxn.ufo.block.entity.processing.MultiblockProcessingRecipe;
import com.raishxn.ufo.init.ModBlockEntities;
import com.raishxn.ufo.init.ModRecipes;
import com.raishxn.ufo.recipe.UniversalMultiblockMachineKind;
import com.raishxn.ufo.screen.QuantumCryoforgeControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class QuantumCryoforgeControllerBE extends AbstractParallelMultiblockControllerBE {

    private static MultiblockPattern PATTERN;

    public QuantumCryoforgeControllerBE(final BlockPos pos, final BlockState state) {
        super(ModBlockEntities.QUANTUM_CRYOFORGE_CONTROLLER_BE.get(), pos, state);
    }

    @Override
    protected MultiblockPattern getControllerPattern() {
        if (PATTERN == null) {
            PATTERN = QuantumCryoforgePatternFactory.getPattern();
        }
        return PATTERN;
    }

    @Override
    protected String getControllerTranslationKey() {
        return "block.ufo.quantum_cryoforge_controller";
    }

    @Override
    protected double getHeatGenerationMultiplier() {
        return 0.5D;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int id, final Inventory playerInventory, final Player player) {
        return new QuantumCryoforgeControllerMenu(id, playerInventory, this);
    }

    @Override
    protected List<MultiblockProcessingRecipe> getAvailableRecipes() {
        if (!(this.level instanceof final ServerLevel serverLevel)) {
            return List.of();
        }

        final List<MultiblockProcessingRecipe> recipes = new ArrayList<>();
        for (final var holder : serverLevel.recipeAccess().recipeMap().byType(ModRecipes.UNIVERSAL_MULTIBLOCK_TYPE.get())) {
            final var recipe = holder.value();
            if (recipe.machine() == UniversalMultiblockMachineKind.QUANTUM_CRYOFORGE) {
                recipes.add(MultiblockProcessingRecipe.fromUniversal(holder.id().identifier(), recipe));
            }
        }
        return recipes;
    }
}
