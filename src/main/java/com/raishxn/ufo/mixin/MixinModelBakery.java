package com.raishxn.ufo.mixin;

import com.raishxn.ufo.UfoMod;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelBakery.class)
public abstract class MixinModelBakery {
    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void loadModelHook(final Identifier id, final CallbackInfoReturnable<UnbakedModel> cir) {
        final UnbakedModel model = ufo$getUnbakedModel(id);
        if (model != null) {
            cir.setReturnValue(model);
        }
    }

    @Unique
    private UnbakedModel ufo$getUnbakedModel(final Identifier variant) {
        if (!variant.getNamespace().equals(UfoMod.MOD_ID)) return null;
        return AccessorBuiltInModelHooks.getBuiltInModels().get(variant);
    }
}