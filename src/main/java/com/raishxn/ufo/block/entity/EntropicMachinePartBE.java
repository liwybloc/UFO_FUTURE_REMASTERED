package com.raishxn.ufo.block.entity;

import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import com.raishxn.ufo.api.multiblock.IMultiblockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EntropicMachinePartBE extends AENetworkedBlockEntity implements IMultiblockPart {
    @Nullable
    private BlockPos controllerPos;

    public EntropicMachinePartBE(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
        this.getMainNode()
                .setExposedOnSides(java.util.EnumSet.allOf(Direction.class))
                .setIdlePowerUsage(0);
    }

    @Override
    public void linkToController(final BlockPos controllerPos) {
        this.controllerPos = controllerPos.immutable();
        setChanged();
    }

    @Override
    public void unlinkFromController() {
        this.controllerPos = null;
        setChanged();
    }

    @Override
    public @Nullable BlockPos getControllerPos() {
        return this.controllerPos;
    }

    @Override
    public AECableType getCableConnectionType(final Direction dir) {
        return AECableType.DENSE_SMART;
    }

    @Override
    public void saveAdditional(@NotNull final ValueOutput output) {
        super.saveAdditional(output);
        if (this.controllerPos != null) {
            output.store("ControllerPos", BlockPos.CODEC, this.controllerPos);
        }
    }

    @Override
    protected void loadAdditional(@NotNull final ValueInput input) {
        super.loadAdditional(input);
        this.controllerPos = input.read("ControllerPos", BlockPos.CODEC).map(BlockPos::immutable).orElse(null);
    }
}
