package com.raishxn.ufo.init;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.entity.custom.ApocalypseTypeAEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, UfoMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ApocalypseTypeAEntity>> APOCALYPSE_TYPE_A =
            ENTITIES.register("apocalypse_type_a", () -> EntityType.Builder
                    .of(ApocalypseTypeAEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 4.2F)
                    .eyeHeight(3.8F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, UfoMod.id("apocalypse_type_a"))));

    private ModEntities() {
    }

    public static void register(final IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    public static void registerAttributes(final EntityAttributeCreationEvent event) {
        event.put(APOCALYPSE_TYPE_A.get(), ApocalypseTypeAEntity.createAttributes().build());
    }
}
