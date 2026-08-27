package com.raishxn.ufo.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public final class EntityDamageHelper {
    private EntityDamageHelper() {
    }

    public static void hurt(final Entity entity, final DamageSource source, final float amount) {
        if (entity.level() instanceof final ServerLevel level) {
            entity.hurtServer(level, source, amount);
        }
    }
}
