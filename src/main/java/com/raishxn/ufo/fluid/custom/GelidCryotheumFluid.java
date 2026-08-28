package com.raishxn.ufo.fluid.custom;

import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.fluid.ModFluidTypes;
import com.raishxn.ufo.fluid.ModFluids;
import com.raishxn.ufo.item.ModItems;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class GelidCryotheumFluid extends BaseFlowingFluid {
    protected GelidCryotheumFluid() {
        super(new Properties(
                ModFluidTypes.GELID_CRYOTHEUM_TYPE,
                ModFluids.SOURCE_GELID_CRYOTHEUM,
                ModFluids.FLOWING_GELID_CRYOTHEUM)
                .bucket(ModItems.GELID_CRYOTHEUM_BUCKET)
                .block(ModBlocks.GELID_CRYOTHEUM_BLOCK));
    }

    public static final class Source extends GelidCryotheumFluid {
        public Source() {
            super();
        }

        public int getAmount(final net.minecraft.world.level.material.FluidState state) {
            return 8;
        }

        public boolean isSource(final net.minecraft.world.level.material.FluidState state) {
            return true;
        }
    }

    public static final class Flowing extends GelidCryotheumFluid {
        public Flowing() {
            super();
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(final net.minecraft.world.level.block.state.StateDefinition.Builder<Fluid, net.minecraft.world.level.material.FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(final net.minecraft.world.level.material.FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(final net.minecraft.world.level.material.FluidState state) {
            return false;
        }
    }
}