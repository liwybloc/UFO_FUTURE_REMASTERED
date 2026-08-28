package com.raishxn.ufo.init;

import com.raishxn.ufo.UfoMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, UfoMod.MOD_ID);

    public static final Supplier<SoundEvent> DMA_WORK = registerSound("block.dma.work");
    public static final Supplier<SoundEvent> DMA_ALARM = registerSound("block.dma.alarm");


    private static Supplier<SoundEvent> registerSound(final String name) {
        final Identifier id = UfoMod.id(name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(final IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
