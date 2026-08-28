package com.raishxn.ufo.init;

import com.raishxn.ufo.UfoMod;
import appeng.menu.implementations.MenuTypeBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, UfoMod.MOD_ID);

    public static final Supplier<MenuType<com.raishxn.ufo.screen.StellarNexusControllerMenu>> STELLAR_NEXUS_CONTROLLER_MENU =
            MENUS.register("stellar_nexus_controller_menu",
                    () -> IMenuTypeExtension.create(com.raishxn.ufo.screen.StellarNexusControllerMenu::new));

    public static final Supplier<MenuType<com.raishxn.ufo.screen.QmfControllerMenu>> QMF_CONTROLLER_MENU =
            MENUS.register("qmf_controller_menu",
                    () -> IMenuTypeExtension.create(com.raishxn.ufo.screen.QmfControllerMenu::new));

    public static final Supplier<MenuType<com.raishxn.ufo.screen.QuantumSlicerControllerMenu>> QUANTUM_SLICER_CONTROLLER_MENU =
            MENUS.register("quantum_slicer_controller_menu",
                    () -> IMenuTypeExtension.create(com.raishxn.ufo.screen.QuantumSlicerControllerMenu::new));

    public static final Supplier<MenuType<com.raishxn.ufo.screen.QuantumProcessorAssemblerControllerMenu>> QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER_MENU =
            MENUS.register("quantum_processor_assembler_controller_menu",
                    () -> IMenuTypeExtension.create(com.raishxn.ufo.screen.QuantumProcessorAssemblerControllerMenu::new));

    public static final Supplier<MenuType<com.raishxn.ufo.screen.QuantumCryoforgeControllerMenu>> QUANTUM_CRYOFORGE_CONTROLLER_MENU =
            MENUS.register("quantum_cryoforge_controller_menu",
                    () -> IMenuTypeExtension.create(com.raishxn.ufo.screen.QuantumCryoforgeControllerMenu::new));

    public static final Supplier<MenuType<com.raishxn.ufo.screen.QuantumPatternHatchMenu>> QUANTUM_PATTERN_HATCH_MENU =
            MENUS.register("quantum_pattern_hatch_menu",
                    () -> MenuTypeBuilder
                            .create((id, inv, host) -> new com.raishxn.ufo.screen.QuantumPatternHatchMenu(id, inv, host),
                                    appeng.helpers.patternprovider.PatternProviderLogicHost.class)
                            .build(Identifier.fromNamespaceAndPath(UfoMod.MOD_ID, "quantum_pattern_hatch_menu")));

    public static final Supplier<MenuType<com.raishxn.ufo.screen.EntropicAssemblerMatrixMenu>> ENTROPIC_ASSEMBLER_MATRIX_MENU =
            MENUS.register("entropic_assembler_matrix_menu",
                    () -> IMenuTypeExtension.create(com.raishxn.ufo.screen.EntropicAssemblerMatrixMenu::new));

    public static final Supplier<MenuType<com.raishxn.ufo.screen.EntropicConvergenceEngineMenu>> ENTROPIC_CONVERGENCE_ENGINE_MENU =
            MENUS.register("entropic_convergence_engine_menu",
                    () -> IMenuTypeExtension.create(com.raishxn.ufo.screen.EntropicConvergenceEngineMenu::new));

    public static void register(final IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
