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

public abstract class PrimordialMatterFluid extends BaseFlowingFluid {
    public static final Properties PROPERTIES = new Properties(
            ModFluidTypes.PRIMORDIAL_MATTER_FLUID_TYPE,
            ModFluids.SOURCE_PRIMORDIAL_MATTER_FLUID,
            ModFluids.FLOWING_PRIMORDIAL_MATTER_FLUID
    )
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(ModBlocks.PRIMORDIAL_MATTER_FLUID_BLOCK)
            .bucket(ModItems.PRIMORDIAL_MATTER_BUCKET);
    protected PrimordialMatterFluid() {
        super(PROPERTIES);
    }
    public Fluid getSource() {
        return ModFluids.SOURCE_PRIMORDIAL_MATTER_FLUID.get();
    }
    public Fluid getFlowing() {
        return ModFluids.FLOWING_PRIMORDIAL_MATTER_FLUID.get();
    }
    public Item getBucket() {
        return ModItems.PRIMORDIAL_MATTER_BUCKET.get();
    }
    protected boolean canConvertToSource(final Level level) {
        return false;
    }
    public static class Source extends PrimordialMatterFluid {
        public Source() { super(); }
        public int getAmount(final FluidState state) {
            return 8; // Bloco cheio
        }
        public boolean isSource(final FluidState state) {
            return true;
        }
    }
    public static class Flowing extends PrimordialMatterFluid {
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
