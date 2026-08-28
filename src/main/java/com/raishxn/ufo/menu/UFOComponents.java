package com.raishxn.ufo.menu;

import appeng.api.stacks.GenericStack;
import com.mojang.serialization.Codec;
import com.raishxn.ufo.UfoMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.pedroksl.ae2addonlib.registry.ComponentRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public final class UFOComponents extends ComponentRegistry {

    public static final UFOComponents INSTANCE = new UFOComponents();
    UFOComponents() {
        super(UfoMod.MOD_ID);
    }

    public static final DataComponentType<List<Integer>> EXPORTED_ALLOWED_SIDES =
            register("allowed_output_sides", builder -> builder.persistent(Codec.list(Codec.of(Codec.INT, Codec.INT)))
                    .networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list())));


    protected static <T> DataComponentType<T> register(final String name, final Consumer<DataComponentType.Builder<T>> customizer) {
        return register(UfoMod.MOD_ID, name, customizer);
    }
}
