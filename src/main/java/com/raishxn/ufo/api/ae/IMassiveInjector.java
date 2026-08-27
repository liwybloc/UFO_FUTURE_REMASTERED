package com.raishxn.ufo.api.ae;

import appeng.api.stacks.AEKey;
import net.minecraft.world.level.Level;

/**
 * Defines a block entity capable of injecting massive quantities of keyed resources
 * directly into an AE2 ME network.
 * <p>
 * Implementations use the ME network storage service and express transferred amounts
 * as {@code long} values.
 */
public interface IMassiveInjector {

    /**
     * Injects a large quantity of a keyed resource (item or fluid) into the
     * connected AE2 network.
     *
     * @param what   the AE2 key representing the item / fluid to inject
     * @param amount the quantity to inject (may be millions)
     * @param level  the server level
     * @return the amount that was actually accepted by the network
     */
    long injectIntoNetwork(final AEKey what, final long amount, final Level level);

    /**
     * Determines whether this injector can currently access a powered ME network.
     *
     * @return {@code true} when the connected ME network is powered and online
     */
    boolean isNetworkReady();
}
