package com.raishxn.ufo.mixin;

import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftingCPUCluster.class, priority = 3000, remap = false)
public abstract class MixinCraftingCPUCluster {
    private static final int UFO_MAX_SAFE_COPROCESSORS = Integer.MAX_VALUE - 1;

    @Shadow private int accelerator;

    /**
     * Preserve AE2's original method shape so other add-ons can still inject
     * into addBlockEntity, but bypass the hard per-block 16 thread exception
     * for UFO's larger crafting units.
     */
    @ModifyConstant(method = "addBlockEntity", constant = @Constant(intValue = 16))
    private int ufo$allowLargeCoProcessors(final int original) {
        return Integer.MAX_VALUE;
    }

    @Inject(method = "addBlockEntity", at = @At("TAIL"))
    private void ufo$clampThreadTotal(final CraftingBlockEntity te, final CallbackInfo ci) {
        if (this.accelerator < 0 || this.accelerator == Integer.MAX_VALUE) {
            this.accelerator = UFO_MAX_SAFE_COPROCESSORS;
        }
    }
}
