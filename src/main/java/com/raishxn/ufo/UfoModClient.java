package com.raishxn.ufo;

import appeng.client.InitScreens;
import com.raishxn.ufo.client.gui.DimensionalMatterAssemblerScreen;
import com.raishxn.ufo.client.renderer.ApocalypseTypeARenderer;
import com.raishxn.ufo.client.tutorial.UfoTutorials;
import com.raishxn.ufo.event.ModKeyBindings;
import com.raishxn.ufo.event.ModTooltipEventHandler;
import com.raishxn.ufo.fluid.BaseFluidType;
import com.raishxn.ufo.fluid.ModFluidTypes;
import com.raishxn.ufo.fluid.ModFluids;
import com.raishxn.ufo.init.ModEntities;
import com.raishxn.ufo.init.ModMenus;
import com.raishxn.ufo.menu.UFOMenus;
import com.raishxn.ufo.screen.EntropicAssemblerMatrixScreen;
import com.raishxn.ufo.screen.EntropicConvergenceEngineScreen;
import com.raishxn.ufo.screen.QmfControllerScreen;
import com.raishxn.ufo.screen.QuantumCryoforgeControllerScreen;
import com.raishxn.ufo.screen.QuantumPatternHatchScreen;
import com.raishxn.ufo.screen.QuantumProcessorAssemblerControllerScreen;
import com.raishxn.ufo.screen.QuantumSlicerControllerScreen;
import com.raishxn.ufo.screen.StellarNexusControllerScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public final class UfoModClient {

    public UfoModClient(final IEventBus eventBus) {
        eventBus.addListener(this::onClientSetup);
        NeoForge.EVENT_BUS.register(ModTooltipEventHandler.class);
        eventBus.addListener(this::onRegisterKeyMappings);
        eventBus.addListener(this::registerScreens);
        eventBus.addListener(this::registerRenderers);
        eventBus.addListener(this::registerFluidModels);
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
        InitScreens.register(
                event,
                ModMenus.QMF_CONTROLLER_MENU.get(),
                QmfControllerScreen::new,
                "/screens/universal_multiblock_controller.json");
        InitScreens.register(
                event,
                ModMenus.QUANTUM_SLICER_CONTROLLER_MENU.get(),
                QuantumSlicerControllerScreen::new,
                "/screens/universal_multiblock_controller.json");
        InitScreens.register(
                event,
                ModMenus.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER_MENU.get(),
                QuantumProcessorAssemblerControllerScreen::new,
                "/screens/universal_multiblock_controller.json");
        InitScreens.register(
                event,
                ModMenus.QUANTUM_CRYOFORGE_CONTROLLER_MENU.get(),
                QuantumCryoforgeControllerScreen::new,
                "/screens/universal_multiblock_controller.json");
        InitScreens.register(
                event,
                ModMenus.ENTROPIC_ASSEMBLER_MATRIX_MENU.get(),
                EntropicAssemblerMatrixScreen::new,
                "/screens/universal_multiblock_controller.json");
        InitScreens.register(
                event,
                ModMenus.ENTROPIC_CONVERGENCE_ENGINE_MENU.get(),
                EntropicConvergenceEngineScreen::new,
                "/screens/universal_multiblock_controller.json");
        InitScreens.register(
                event,
                ModMenus.QUANTUM_PATTERN_HATCH_MENU.get(),
                QuantumPatternHatchScreen::new,
                "/screens/quantum_pattern_hatch.json");
        event.register(ModMenus.STELLAR_NEXUS_CONTROLLER_MENU.get(), StellarNexusControllerScreen::new);
    }

    private void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.APOCALYPSE_TYPE_A.get(), ApocalypseTypeARenderer::new);
    }

    private void registerFluidModels(final RegisterFluidModelsEvent event) {
        registerFluidModel(event, ModFluidTypes.PULSAR_FRAGMENT_FLUID_TYPE.get(), ModFluids.SOURCE_PULSAR_FRAGMENT_FLUID, ModFluids.FLOWING_PULSAR_FRAGMENT_FLUID);
        registerFluidModel(event, ModFluidTypes.NEUTRON_STAR_FRAGMENT_FLUID_TYPE.get(), ModFluids.SOURCE_NEUTRON_STAR_FRAGMENT_FLUID, ModFluids.FLOWING_NEUTRON_STAR_FRAGMENT_FLUID);
        registerFluidModel(event, ModFluidTypes.WHITE_DWARF_FRAGMENT_FLUID_TYPE.get(), ModFluids.SOURCE_WHITE_DWARF_FRAGMENT_FLUID, ModFluids.FLOWING_WHITE_DWARF_FRAGMENT_FLUID);
        registerFluidModel(event, ModFluidTypes.LIQUID_STARLIGHT_FLUID_TYPE.get(), ModFluids.SOURCE_LIQUID_STARLIGHT_FLUID, ModFluids.FLOWING_LIQUID_STARLIGHT_FLUID);
        registerFluidModel(event, ModFluidTypes.PRIMORDIAL_MATTER_FLUID_TYPE.get(), ModFluids.SOURCE_PRIMORDIAL_MATTER_FLUID, ModFluids.FLOWING_PRIMORDIAL_MATTER_FLUID);
        registerFluidModel(event, ModFluidTypes.RAW_STAR_MATTER_PLASMA_FLUID_TYPE.get(), ModFluids.SOURCE_RAW_STAR_MATTER_PLASMA_FLUID, ModFluids.FLOWING_RAW_STAR_MATTER_PLASMA_FLUID);
        registerFluidModel(event, ModFluidTypes.TRANSCENDING_MATTER_FLUID_TYPE.get(), ModFluids.SOURCE_TRANSCENDING_MATTER_FLUID, ModFluids.FLOWING_TRANSCENDING_MATTER_FLUID);
        registerFluidModel(event, ModFluidTypes.UU_MATTER_FLUID_TYPE.get(), ModFluids.SOURCE_UU_MATTER_FLUID, ModFluids.FLOWING_UU_MATTER_FLUID);
        registerFluidModel(event, ModFluidTypes.UU_AMPLIFIER_FLUID_TYPE.get(), ModFluids.SOURCE_UU_AMPLIFIER_FLUID, ModFluids.FLOWING_UU_AMPLIFIER_FLUID);
        registerFluidModel(event, ModFluidTypes.GELID_CRYOTHEUM_TYPE.get(), ModFluids.SOURCE_GELID_CRYOTHEUM, ModFluids.FLOWING_GELID_CRYOTHEUM);
        registerFluidModel(event, ModFluidTypes.STABLE_COOLANT_TYPE.get(), ModFluids.SOURCE_STABLE_COOLANT, ModFluids.FLOWING_STABLE_COOLANT);
        registerFluidModel(event, ModFluidTypes.TEMPORAL_FLUID_TYPE.get(), ModFluids.SOURCE_TEMPORAL_FLUID, ModFluids.FLOWING_TEMPORAL_FLUID);
        registerFluidModel(event, ModFluidTypes.SPATIAL_FLUID_TYPE.get(), ModFluids.SOURCE_SPATIAL_FLUID, ModFluids.FLOWING_SPATIAL_FLUID);
    }

    private static void registerFluidModel(
            final RegisterFluidModelsEvent event,
            final net.neoforged.neoforge.fluids.FluidType fluidType,
            final Supplier<? extends Fluid> source,
            final Supplier<? extends Fluid> flowing
    ) {
        final BaseFluidType type = (BaseFluidType) fluidType;
        final Material overlay = type.getOverlayTexture() == null ? null : new Material(type.getOverlayTexture());
        final FluidModel.Unbaked model = new FluidModel.Unbaked(
                new Material(type.getStillTexture()),
                new Material(type.getFlowingTexture()),
                overlay,
                (FluidTintSource) state -> type.getTintColor()
        );
        event.register(model, source, flowing);
    }
    private void onClientSetup(final FMLClientSetupEvent event) {
    }
}
