package com.raishxn.ufo.mixin;

import appeng.hooks.BuiltInModelHooks;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = BuiltInModelHooks.class, remap = false)
public interface AccessorBuiltInModelHooks {
    @Accessor("builtInModels")
    static Map<Identifier, UnbakedModel> getBuiltInModels() {
        throw new AssertionError();
    }
}