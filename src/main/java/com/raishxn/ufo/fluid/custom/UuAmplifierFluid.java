package com.raishxn.ufo.fluid.custom;

import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.fluid.ModFluidTypes;
import com.raishxn.ufo.fluid.ModFluids;
import com.raishxn.ufo.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class UuAmplifierFluid extends BaseFlowingFluid {
    public static final Properties PROPERTIES = new Properties(
            ModFluidTypes.UU_AMPLIFIER_FLUID_TYPE,
            ModFluids.SOURCE_UU_AMPLIFIER_FLUID,
            ModFluids.FLOWING_UU_AMPLIFIER_FLUID
    )
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(ModBlocks.UU_AMPLIFIER_FLUID_BLOCK)
            .bucket(ModItems.UU_AMPLIFIER_BUCKET);
    protected UuAmplifierFluid() {
        super(PROPERTIES);
    }
    public Fluid getSource() {
        return ModFluids.SOURCE_UU_AMPLIFIER_FLUID.get();
    }
    public Fluid getFlowing() {
        return ModFluids.FLOWING_UU_AMPLIFIER_FLUID.get();
    }
    public Item getBucket() {
        return ModItems.UU_AMPLIFIER_BUCKET.get();
    }
    protected boolean canConvertToSource(final Level level) {
        return false;
    }
    public static class Source extends UuAmplifierFluid {
        public Source() { super(); }
        public int getAmount(final FluidState state) {
            return 8; // Bloco cheio
        }
        public boolean isSource(final FluidState state) {
            return true;
        }
    }
    public static class Flowing extends UuAmplifierFluid {
        public Flowing() { super(); }
        protected void createFluidStateDefinition(final StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        public int getAmount(final FluidState state) {
            return state.getValue(LEVEL);
        }
        public boolean isSource(final FluidState state) {
            return false;
        }
    }
}
