package com.raishxn.ufo.event;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.init.ModBlockEntities;
import com.raishxn.ufo.item.ModItems;
import com.raishxn.ufo.item.ModArmor;
import com.raishxn.ufo.item.ModTools;
import com.raishxn.ufo.item.custom.AetherContainmentCapsuleItem;
import com.raishxn.ufo.util.ConfigType;
import com.raishxn.ufo.util.IOMode;
import com.raishxn.ufo.util.UfoPersistentEnergyStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import appeng.blockentity.powersink.AEBasePoweredBlockEntity;

@EventBusSubscriber(modid = UfoMod.MOD_ID)
public final class ModCapabilityEvents {

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        registerItemCapabilities(event);

        event.registerBlockEntity(
                appeng.api.AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.DIMENSIONAL_MATTER_ASSEMBLER_BE.get(),
                (be, context) -> (appeng.api.networking.IInWorldGridNodeHost) be
        );
        event.registerBlockEntity(
                appeng.api.AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.QUANTUM_PATTERN_HATCH_BE.get(),
                (be, context) -> (appeng.api.networking.IInWorldGridNodeHost) be
        );
        event.registerBlockEntity(
                appeng.api.AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.ME_MASSIVE_OUTPUT_HATCH_BE.get(),
                (be, context) -> (appeng.api.networking.IInWorldGridNodeHost) be
        );
        event.registerBlockEntity(
                appeng.api.AECapabilities.CRAFTING_MACHINE,
                ModBlockEntities.QMF_CONTROLLER.get(),
                (be, context) -> be
        );
        event.registerBlockEntity(
                appeng.api.AECapabilities.CRAFTING_MACHINE,
                ModBlockEntities.QUANTUM_SLICER_CONTROLLER_BE.get(),
                (be, context) -> be
        );
        event.registerBlockEntity(
                appeng.api.AECapabilities.CRAFTING_MACHINE,
                ModBlockEntities.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER_BE.get(),
                (be, context) -> be
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntities.DIMENSIONAL_MATTER_ASSEMBLER_BE.get(),
                appeng.blockentity.AEBaseInvBlockEntity::getExposedItemHandler
        );
        event.registerBlockEntity(
                appeng.api.AECapabilities.GENERIC_INTERNAL_INV,
                ModBlockEntities.DIMENSIONAL_MATTER_ASSEMBLER_BE.get(),
                (be, context) -> be.getTank()
        );
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModBlockEntities.DIMENSIONAL_MATTER_ASSEMBLER_BE.get(),
                AEBasePoweredBlockEntity::getEnergyStorage
        );
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModBlockEntities.UFO_ENERGY_CELL_BE.get(),
                (be, context) -> be.getExposedEnergy()
        );
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModBlockEntities.QUANTUM_ENERGY_CELL_BE.get(),
                (be, context) -> be.getExposedEnergy()
        );

    }

    private static void registerItemCapabilities(final RegisterCapabilitiesEvent event) {

        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModTools.UFO_SWORD.get()
        );
        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModTools.UFO_PICKAXE.get()
        );
        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModTools.UFO_AXE.get()
        );
        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModTools.UFO_SHOVEL.get()
        );
        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModTools.UFO_HOE.get()
        );
        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModTools.UFO_STAFF.get()
        );


        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModTools.UFO_HAMMER.get()
        );

        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModTools.UFO_GREATSWORD.get()
        );

        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModTools.UFO_FISHING_ROD.get()
        );

        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModTools.UFO_BOW.get()
        );

        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModArmor.UFO_HELMET.get()
        );
        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModArmor.UFO_CHESTPLATE.get()
        );
        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModArmor.UFO_LEGGINGS.get()
        );
        event.registerItem(Capabilities.Energy.ITEM, (stack, context) ->
                        new UfoPersistentEnergyStorage(stack, ModDataComponents.ENERGY.get(), 1_000_000_000, 1000000, 0),
                ModArmor.UFO_BOOTS.get()
        );
    }
}
