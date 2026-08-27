package com.raishxn.ufo;

import appeng.client.InitScreens;
import com.raishxn.ufo.client.gui.DimensionalMatterAssemblerScreen;
import com.raishxn.ufo.client.renderer.ApocalypseTypeARenderer;
import com.raishxn.ufo.client.tutorial.UfoTutorials;
import com.raishxn.ufo.event.ModKeyBindings;
import com.raishxn.ufo.event.ModTooltipEventHandler;
import com.raishxn.ufo.init.ModEntities;
import com.raishxn.ufo.menu.UFOMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

public class UfoModClient {

    public UfoModClient(final IEventBus eventBus) {
        eventBus.addListener(this::onClientSetup);
        NeoForge.EVENT_BUS.register(ModTooltipEventHandler.class);
        eventBus.addListener(this::onRegisterKeyMappings);
        eventBus.addListener(this::registerScreens);
        eventBus.addListener(this::registerRenderers);
    }

    private void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(ModKeyBindings.CYCLE_TOOL_FORWARD);
        event.register(ModKeyBindings.CYCLE_TOOL_BACKWARD);
        event.register(ModKeyBindings.CYCLE_MODE);
        event.register(ModKeyBindings.TOGGLE_AUTO_SMELT);
        event.register(ModKeyBindings.OPEN_UFO_TUTORIAL);
    }
    private void registerScreens(final RegisterMenuScreensEvent event) {
        InitScreens.register(
                event,
                UFOMenus.DIMENSIONAL_MATTER_ASSEMBLER.get(),
                DimensionalMatterAssemblerScreen::new,
                "/screens/dimensional_matter_assembler.json");
    }

    private void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.APOCALYPSE_TYPE_A.get(), ApocalypseTypeARenderer::new);
    }
    private void onClientSetup(final FMLClientSetupEvent event) {
    }
}
