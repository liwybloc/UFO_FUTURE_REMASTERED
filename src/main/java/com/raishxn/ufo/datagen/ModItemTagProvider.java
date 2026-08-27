package com.raishxn.ufo.datagen;

import com.raishxn.ufo.UfoMod;
import com.raishxn.ufo.item.ModItems;
import com.raishxn.ufo.item.ModArmor;
import com.raishxn.ufo.item.ModTools;
import com.raishxn.ufo.util.ModTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.TagConventionLogWarning;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider,
                              final CompletableFuture<TagLookup<Block>> blockTags, @Nullable final ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, UfoMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        tag(ModTags.Items.TRANSFORMABLE_ITEMS)
                .add(ModItems.BISMUTH.get())
                .add(Items.COAL)
                .add(Items.STICK)
                .add(Items.COMPASS);
        tag(ItemTags.SWORDS)
                .add(ModTools.UFO_SWORD.get())
                .add(ModTools.UFO_GREATSWORD.get());
        tag(ItemTags.PICKAXES)
                .add(ModTools.UFO_PICKAXE.get());
        tag(ItemTags.SHOVELS)
                .add(ModTools.UFO_SHOVEL.get());
        tag(ItemTags.AXES)
                .add(ModTools.UFO_AXE.get());
        tag(ItemTags.HOES)
                .add(ModTools.UFO_HOE.get());
        tag(ItemTags.FISHING_ENCHANTABLE)
                .add(ModTools.UFO_FISHING_ROD.get());
        tag(ItemTags.BOW_ENCHANTABLE)
                .add(ModTools.UFO_BOW.get());
        tag(ModTags.Items.STAFF)
                .add(ModTools.UFO_STAFF.get());
        tag(ItemTags.HEAD_ARMOR)
                .add(ModArmor.UFO_HELMET.get());
        tag(ItemTags.CHEST_ARMOR)
                .add(ModArmor.UFO_CHESTPLATE.get());
        tag(ItemTags.LEG_ARMOR)
                .add(ModArmor.UFO_LEGGINGS.get());
        tag(ItemTags.FOOT_ARMOR)
                .add(ModArmor.UFO_BOOTS.get());


        tag(ModTags.Items.HAZARDOUS).add(
                ModItems.QUANTUM_ANOMALY.get(),
                ModItems.NUCLEAR_STAR.get(),
                ModItems.UNSTABLE_WHITE_HOLE_MATTER.get(),
                ModItems.RAW_STAR_MATTER_PLASMA_BUCKET.get(),
                ModItems.UU_MATTER_BUCKET.get(),
                ModItems.CHARGED_ENRICHED_NEUTRONIUM_SPHERE.get()
        );
        tag(ModTags.Items.COOLANTS).add(
                ModItems.GELID_CRYOTHEUM_BUCKET.get(),
                ModItems.TEMPORAL_FLUID_BUCKET.get(),
                ModItems.SPATIAL_FLUID_BUCKET.get()
        );
        tag(ModTags.Items.CONTAINMENT_DEVICE).add(
                ModItems.AETHER_CONTAINMENT_CAPSULE.get(),
                ModItems.SAFE_CONTAINMENT_MATTER.get()
        );
        tag(ModTags.Items.COOLANT_MATERIAL).add(
                ModItems.DUST_BLIZZ.get(),
                ModItems.DUST_CRYOTHEUM.get()
        );
        tag(ModTags.Items.COMPONENT).add(
                ModItems.OBSIDIAN_MATRIX.get()
        );
        tag(ModTags.Items.MATTER_STAGE_1).add(
                ModItems.PROTO_MATTER.get()
        );
        tag(ModTags.Items.MATTER_STAGE_2).add(
                ModItems.CORPOREAL_MATTER.get()
        );
        tag(ModTags.Items.MATTER_STAGE_FINAL).add(
                ModItems.DARK_MATTER.get(),
                ModItems.PULSAR_MATTER.get(),
                ModItems.WHITE_DWARF_MATTER.get(),
                ModItems.NEUTRON_STAR_MATTER.get()
        );
        tag(ModTags.Items.ADVANCED_COMPONENT).add(
                ModItems.ENRICHED_NEUTRONIUM_SPHERE.get(),
                ModItems.NEUTRONIUM_SPHERE.get()
        );
        tag(ModTags.Items.COMPRESSED).add(
                ModItems.SCRAP_BOX.get()
        );
        tag(ModTags.Items.RECYCLABLE).add(
                ModItems.SCRAP.get()
        );
        tag(ModTags.Items.DIM_RESIDUE).add(
                ModItems.SCAR.get()
        );
        tag(ModTags.Items.CATALYST).add(
        ModItems.CHRONO_CATALYST_T1.get(),
        ModItems.CHRONO_CATALYST_T2.get(),
        ModItems.CHRONO_CATALYST_T3.get(),
        ModItems.MATTERFLOW_CATALYST_T1.get(),
        ModItems.MATTERFLOW_CATALYST_T2.get(),
        ModItems.MATTERFLOW_CATALYST_T3.get(),
        ModItems.OVERFLUX_CATALYST_T1.get(),
        ModItems.OVERFLUX_CATALYST_T2.get(),
        ModItems.OVERFLUX_CATALYST_T3.get(),
        ModItems.QUANTUM_CATALYST_T1.get(),
        ModItems.QUANTUM_CATALYST_T2.get(),
        ModItems.QUANTUM_CATALYST_T3.get(),
        ModItems.DIMENSIONAL_CATALYST.get()
        );

        tag(ItemTags.create(Identifier.parse("ae2:p2p_attunements/fe_p2p_tunnel")))
                .add(
                        com.raishxn.ufo.block.ModBlocks.UFO_ENERGY_CELL.get().asItem(),
                        com.raishxn.ufo.block.ModBlocks.QUANTUM_ENERGY_CELL.get().asItem()
                );
    }
}

