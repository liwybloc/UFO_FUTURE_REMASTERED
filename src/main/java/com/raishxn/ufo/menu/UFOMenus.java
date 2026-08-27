package com.raishxn.ufo.menu;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.block.entity.DimensionalMatterAssemblerBlockEntity;

import net.minecraft.world.inventory.MenuType;
import net.pedroksl.ae2addonlib.registry.MenuRegistry;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;

import java.util.function.Supplier;

public class UFOMenus extends MenuRegistry {

    public static final UFOMenus INSTANCE = new UFOMenus();

    UFOMenus() {
        super(UfoMod.MOD_ID);
    }

    public static final Supplier<MenuType<DimensionalMatterAssemblerMenu>> DIMENSIONAL_MATTER_ASSEMBLER =
            create("dimensional_matter_assembler", DimensionalMatterAssemblerMenu::new, DimensionalMatterAssemblerBlockEntity.class);

    protected static <M extends AEBaseMenu, H> Supplier<MenuType<M>> create(
            final String id, final MenuTypeBuilder.MenuFactory<M, H> factory, final Class<H> host) {
        return create(UfoMod.MOD_ID, id, factory, host);
    }
}
