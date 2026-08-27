package com.raishxn.ufo;


import com.mojang.logging.LogUtils;
import com.raishxn.ufo.block.ModBlocks;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.client.tutorial.UfoTutorialScreens;
import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.event.ModKeyBindings;
import com.raishxn.ufo.init.ModBlockEntities;
import com.raishxn.ufo.init.ModEntities;
import com.raishxn.ufo.init.ModMenus;
import com.raishxn.ufo.init.ModRecipes;
import com.raishxn.ufo.init.ModSounds;
import com.raishxn.ufo.item.ModCellItems;
import com.raishxn.ufo.item.ModCreativeModeTabs;
import com.raishxn.ufo.item.ModItems;
import com.raishxn.ufo.item.UFORegistryHandler;
import com.raishxn.ufo.network.ModPackets;
import com.raishxn.ufo.network.packet.CycleModeKeyPacket;
import com.raishxn.ufo.network.packet.CycleToolKeyPacket;
import com.raishxn.ufo.network.packet.ToggleAutoSmeltPacket;
import com.raishxn.ufo.util.LazyInits;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

@Mod(UfoMod.MOD_ID)
public class UfoMod {
    public static final String MOD_ID = "ufo";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final int TUTORIAL_HOLD_TICKS = 12;
    private int tutorialHoldTicks;
    private boolean tutorialHoldOpened;
    public static Identifier id(final String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public UfoMod(final IEventBus modEventBus, final ModContainer modContainer) {
        if (net.neoforged.fml.loading.FMLEnvironment.getDist() == net.neoforged.api.distmarker.Dist.CLIENT) {
            new UfoModClient(modEventBus);
        }
        ModDataComponents.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        com.raishxn.ufo.fluid.ModFluidTypes.register(modEventBus);
        com.raishxn.ufo.fluid.ModFluids.register(modEventBus);
        MultiblockBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCellItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMenus.register(modEventBus);
        com.raishxn.ufo.menu.UFOMenus.INSTANCE.register(modEventBus);
        ModSounds.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, UFOConfig.SPEC);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::registerPackets);
        modEventBus.addListener(ModEntities::registerAttributes);
        ModBlocks.INSTANCE.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
    }

    private void registerPackets(final RegisterPayloadHandlersEvent event) {
        ModPackets.register(event);
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            UFORegistryHandler.INSTANCE.onInit();
            java.util.Objects.requireNonNull(com.raishxn.ufo.menu.UFOSlotSemantics.MACHINE_OUTPUT_2);
            LazyInits.initCommon();
        });
    }
    private void loadComplete(final FMLLoadCompleteEvent event) {
        event.enqueueWork(LazyInits::initFinal);
    }

    @SubscribeEvent
    public void onKeyInput(final InputEvent.Key event) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (ModKeyBindings.CYCLE_TOOL_FORWARD.consumeClick()) {
            ModPackets.sendToServer(new CycleToolKeyPacket(true));
            LOGGER.info("[UFO Mod] Cycle Tool Forward key pressed!");
        }

        if (ModKeyBindings.CYCLE_TOOL_BACKWARD.consumeClick()) {
            ModPackets.sendToServer(new CycleToolKeyPacket(false));
            LOGGER.info("[UFO Mod] Cycle Tool Backward key pressed!");
        }

        if (ModKeyBindings.CYCLE_MODE.consumeClick()) {
            ModPackets.sendToServer(new CycleModeKeyPacket());
            LOGGER.info("[UFO Mod] Cycle Mode key pressed!");
        }
        if (ModKeyBindings.TOGGLE_AUTO_SMELT.consumeClick()) {
            ModPackets.sendToServer(new ToggleAutoSmeltPacket());
        }
    }

    @SubscribeEvent
    public void onClientTick(final ClientTickEvent.Post event) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen == null) {
            resetTutorialHold();
            return;
        }

        if (!ModKeyBindings.OPEN_UFO_TUTORIAL.isDown()) {
            resetTutorialHold();
            return;
        }

        this.tutorialHoldTicks++;
        if (!this.tutorialHoldOpened && this.tutorialHoldTicks >= TUTORIAL_HOLD_TICKS) {
            this.tutorialHoldOpened = UfoTutorialScreens.openFromCurrentContext();
        }
    }

    private void resetTutorialHold() {
        this.tutorialHoldTicks = 0;
        this.tutorialHoldOpened = false;
    }
    private void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(ModKeyBindings.CYCLE_TOOL_FORWARD);
        event.register(ModKeyBindings.CYCLE_TOOL_BACKWARD);
        event.register(ModKeyBindings.CYCLE_MODE);
    }

    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterKeys(final RegisterKeyMappingsEvent event) {
            event.register(ModKeyBindings.CYCLE_TOOL_FORWARD);
            event.register(ModKeyBindings.CYCLE_TOOL_BACKWARD);
            event.register(ModKeyBindings.CYCLE_MODE);
            event.register(ModKeyBindings.TOGGLE_AUTO_SMELT);
        }
    }

    public static Identifier makeId(final String id) {
        return Identifier.fromNamespaceAndPath(MOD_ID, id);
    }
}
