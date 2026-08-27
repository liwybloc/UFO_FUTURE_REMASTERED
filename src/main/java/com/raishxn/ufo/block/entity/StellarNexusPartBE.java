package com.raishxn.ufo.block.entity;

import com.raishxn.ufo.api.multiblock.IMultiblockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Block Entity for any Stellar Nexus structural part (casing, hatch, generator, etc.).
 * <p>
 * Implements {@link IMultiblockPart} to track its link to a controller.
 * This is a lightweight BE — it stores only the controller position.
 */
public class StellarNexusPartBE extends BlockEntity implements IMultiblockPart {

    @Nullable
    private BlockPos controllerPos = null;

    public StellarNexusPartBE(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }


    @Override
    public void linkToController(final BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        this.setChanged();
    }

    @Override
    public void unlinkFromController() {
        this.controllerPos = null;
        this.setChanged();
    }

    @Nullable
    @Override
    public BlockPos getControllerPos() {
        return this.controllerPos;
    }


    @Override
    protected void saveAdditional(@NotNull final ValueOutput output) {
        super.saveAdditional(output);
        if (this.controllerPos != null) {
            output.store("controllerPos", BlockPos.CODEC, this.controllerPos);
        }
    }

    @Override
    protected void loadAdditional(@NotNull final ValueInput input) {
        super.loadAdditional(input);
        this.controllerPos = input.read("controllerPos", BlockPos.CODEC).map(BlockPos::immutable).orElse(null);
    }
}
