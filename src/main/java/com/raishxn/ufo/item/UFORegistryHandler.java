package com.raishxn.ufo.item;

import appeng.api.client.StorageCellModels;
import appeng.api.storage.StorageCells;
import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.item.custom.cell.AEBigIntegerCellHandler;
import com.raishxn.ufo.item.custom.cell.InfinityGenesisCellInventory;
import com.raishxn.ufo.item.custom.cell.InfinityCellInventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class UFORegistryHandler {

    public static final UFORegistryHandler INSTANCE = new UFORegistryHandler();

    private boolean initialized = false;

    public void onInit() {
        if (initialized) return;
        initialized = true;
        this.registerStorageHandler();
        this.registerUpgrades();
    }

    private void registerUpgrades() {
        final java.util.List<net.minecraft.world.item.Item> machineItems = java.util.List.of(
                com.raishxn.ufo.block.ModBlocks.DIMENSIONAL_MATTER_ASSEMBLER_BLOCK.get().asItem(),
                com.raishxn.ufo.block.MultiblockBlocks.QUANTUM_MATTER_FABRICATOR_CONTROLLER.get().asItem(),
                com.raishxn.ufo.block.MultiblockBlocks.QUANTUM_SLICER_CONTROLLER.get().asItem(),
                com.raishxn.ufo.block.MultiblockBlocks.QUANTUM_PROCESSOR_ASSEMBLER_CONTROLLER.get().asItem(),
                com.raishxn.ufo.block.MultiblockBlocks.QUANTUM_CRYOFORGE_CONTROLLER.get().asItem());

        for (final var machineItem : machineItems) {
            appeng.api.upgrades.Upgrades.add(ModItems.MATTERFLOW_CATALYST_T1.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.MATTERFLOW_CATALYST_T2.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.MATTERFLOW_CATALYST_T3.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.CHRONO_CATALYST_T1.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.CHRONO_CATALYST_T2.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.CHRONO_CATALYST_T3.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.OVERFLUX_CATALYST_T1.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.OVERFLUX_CATALYST_T2.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.OVERFLUX_CATALYST_T3.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.QUANTUM_CATALYST_T1.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.QUANTUM_CATALYST_T2.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.QUANTUM_CATALYST_T3.get(), machineItem, 4);
            appeng.api.upgrades.Upgrades.add(ModItems.DIMENSIONAL_CATALYST.get(), machineItem, 4);
        }
    }

    private void registerStorageHandler() {
        StorageCells.addCellHandler(InfinityCellInventory.HANDLER);
        StorageCells.addCellHandler(InfinityGenesisCellInventory.HANDLER);
        StorageCells.addCellHandler(AEBigIntegerCellHandler.INSTANCE);
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            registerStorageModels();
        }
    }

    private void registerStorageModels() {
        StorageCellModels.registerModel(ModCells.INFINITY_WATER_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_COBBLESTONE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_COBBLED_DEEPSLATE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_END_STONE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_NETHERRACK_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_LAVA_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_SAND_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_SKY_STONE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_OBSIDIAN_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_GRAVEL_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_OAK_LOG_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_GLASS_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_AMETHYST_SHARD_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));

        StorageCellModels.registerModel(ModCells.INFINITY_WHITE_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_ORANGE_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_MAGENTA_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_LIGHT_BLUE_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_YELLOW_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_LIME_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_PINK_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_GRAY_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_LIGHT_GRAY_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_CYAN_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_PURPLE_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_BLUE_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_BROWN_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_GREEN_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_RED_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_BLACK_DYE_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));
        StorageCellModels.registerModel(ModCells.INFINITY_GENESIS_CELL.get(), UfoMod.id("drive/cells/infinity_cell"));

        StorageCellModels.registerModel(ModCellItems.ITEM_CELL_40M.get(), UfoMod.id("drive/cells/white_dwarf_cell"));
        StorageCellModels.registerModel(ModCellItems.ITEM_CELL_100M.get(), UfoMod.id("drive/cells/white_dwarf_cell"));
        StorageCellModels.registerModel(ModCellItems.ITEM_CELL_250M.get(), UfoMod.id("drive/cells/white_dwarf_cell"));
        StorageCellModels.registerModel(ModCellItems.ITEM_CELL_750M.get(), UfoMod.id("drive/cells/white_dwarf_cell"));
        StorageCellModels.registerModel(ModCellItems.ITEM_CELL_SINGULARITY.get(), UfoMod.id("drive/cells/white_dwarf_cell"));

        StorageCellModels.registerModel(ModCellItems.FLUID_CELL_40M.get(), UfoMod.id("drive/cells/neutron_star_cell"));
        StorageCellModels.registerModel(ModCellItems.FLUID_CELL_100M.get(), UfoMod.id("drive/cells/neutron_star_cell"));
        StorageCellModels.registerModel(ModCellItems.FLUID_CELL_250M.get(), UfoMod.id("drive/cells/neutron_star_cell"));
        StorageCellModels.registerModel(ModCellItems.FLUID_CELL_750M.get(), UfoMod.id("drive/cells/neutron_star_cell"));
        StorageCellModels.registerModel(ModCellItems.FLUID_CELL_SINGULARITY.get(), UfoMod.id("drive/cells/neutron_star_cell"));
    }
}
