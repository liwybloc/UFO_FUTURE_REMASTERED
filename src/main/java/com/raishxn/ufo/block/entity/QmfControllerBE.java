package com.raishxn.ufo.block.entity;

import com.raishxn.ufo.api.multiblock.MultiblockPattern;
import com.raishxn.ufo.block.entity.pattern.QmfPatternFactory;
import com.raishxn.ufo.block.entity.processing.MultiblockProcessingRecipe;
import com.raishxn.ufo.init.ModBlockEntities;
import com.raishxn.ufo.init.ModRecipes;
import com.raishxn.ufo.recipe.UniversalMultiblockMachineKind;
import com.raishxn.ufo.screen.QmfControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class QmfControllerBE extends AbstractParallelMultiblockControllerBE {

    private static MultiblockPattern PATTERN;

    public QmfControllerBE(final BlockPos pos, final BlockState state) {
        super(ModBlockEntities.QMF_CONTROLLER.get(), pos, state);
    }

    @Override
    protected MultiblockPattern getControllerPattern() {
        if (PATTERN == null) {
            PATTERN = QmfPatternFactory.getPattern();
        }
        return PATTERN;
    }

    @Override
    protected String getControllerTranslationKey() {
        return "block.ufo.quantum_matter_fabricator_controller";
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int id, final Inventory playerInventory, final Player player) {
        return new QmfControllerMenu(id, playerInventory, this);
    }

    @Override
    protected List<MultiblockProcessingRecipe> getAvailableRecipes() {
        if (!(this.level instanceof final ServerLevel serverLevel)) {
            return List.of();
        }

        final List<MultiblockProcessingRecipe> recipes = new ArrayList<>();
        for (final RecipeHolder<?> holder : serverLevel.recipeAccess().recipeMap().byType(ModRecipes.QMF_TYPE.get())) {
            recipes.add(MultiblockProcessingRecipe.fromQmf(holder.id().identifier(), (com.raishxn.ufo.recipe.QMFRecipe) holder.value()));
        }
        for (final RecipeHolder<?> holder : serverLevel.recipeAccess().recipeMap().byType(ModRecipes.DMA_RECIPE_TYPE.get())) {
            recipes.add(MultiblockProcessingRecipe.fromDma(holder.id().identifier(), (com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe) holder.value()));
        }
        for (final RecipeHolder<?> holder : serverLevel.recipeAccess().recipeMap().byType(ModRecipes.UNIVERSAL_MULTIBLOCK_TYPE.get())) {
            final var recipe = (com.raishxn.ufo.recipe.UniversalMultiblockRecipe) holder.value();
            if (recipe.machine() == UniversalMultiblockMachineKind.QMF) {
                recipes.add(MultiblockProcessingRecipe.fromUniversal(holder.id().identifier(), recipe));
            }
        }
        return recipes;
    }
}
