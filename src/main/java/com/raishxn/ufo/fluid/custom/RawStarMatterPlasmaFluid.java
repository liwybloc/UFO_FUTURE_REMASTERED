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

public abstract class RawStarMatterPlasmaFluid extends BaseFlowingFluid {
    public static final Properties PROPERTIES = new Properties(
            ModFluidTypes.RAW_STAR_MATTER_PLASMA_FLUID_TYPE,
            ModFluids.SOURCE_RAW_STAR_MATTER_PLASMA_FLUID,
            ModFluids.FLOWING_RAW_STAR_MATTER_PLASMA_FLUID
    )
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(ModBlocks.RAW_STAR_MATTER_PLASMA_FLUID_BLOCK)
            .bucket(ModItems.RAW_STAR_MATTER_PLASMA_BUCKET);
    protected RawStarMatterPlasmaFluid() {
        super(PROPERTIES);
    }
    public Fluid getSource() {
        return ModFluids.SOURCE_RAW_STAR_MATTER_PLASMA_FLUID.get();
    }
    public Fluid getFlowing() {
        return ModFluids.FLOWING_RAW_STAR_MATTER_PLASMA_FLUID.get();
    }
    public Item getBucket() {
        return ModItems.RAW_STAR_MATTER_PLASMA_BUCKET.get();
    }
    protected boolean canConvertToSource(final Level level) {
        return false;
    }
    public static class Source extends RawStarMatterPlasmaFluid {
        public Source() { super(); }
        public int getAmount(final FluidState state) {
            return 8; // Bloco cheio
        }
        public boolean isSource(final FluidState state) {
            return true;
        }
    }
    public static class Flowing extends RawStarMatterPlasmaFluid {
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
