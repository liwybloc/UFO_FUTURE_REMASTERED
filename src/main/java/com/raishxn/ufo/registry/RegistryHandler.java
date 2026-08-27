package com.raishxn.ufo.registry;

import com.glodblock.github.glodium.Glodium; // Supondo que você tenha esta classe de uma lib
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RegistryHandler {
    protected final String id;
    protected final List<Pair<String, Block>> blocks = new ArrayList<>();
    protected final List<Pair<String, Item>> items = new ArrayList<>();
    protected final List<Pair<String, BlockEntityType<?>>> tiles = new ArrayList<>();
    protected final List<Pair<String, DataComponentType<?>>> components = new ArrayList<>();
    protected final Object2ReferenceMap<String, Function<Block, Item>> itemBlocks = new Object2ReferenceOpenHashMap<>();

    public RegistryHandler(final String modid) {
        this.id = modid;
    }

    public void block(final String name, final Block block) {
        this.blocks.add(Pair.of(name, block));
    }

    public void block(final String name, final Block block, final Function<Block, Item> itemWrapper) {
        this.blocks.add(Pair.of(name, block));
        this.itemBlocks.put(name, itemWrapper);
    }

    public void item(final String name, final Item item) {
        this.items.add(Pair.of(name, item));
    }

    public void tile(final String name, final BlockEntityType<?> type) {
        this.tiles.add(Pair.of(name, type));
    }

    public void comp(final String name, final DataComponentType<?> component) {
        this.components.add(Pair.of(name, component));
    }

    @MustBeInvokedByOverriders
    public void runRegister() {
        this.onRegisterBlocks();
        this.onRegisterItems();
        this.onRegisterTileEntities();
        this.onRegisterComponents();
    }

    protected void onRegisterBlocks() {
        this.blocks.forEach((e) -> Registry.register(BuiltInRegistries.BLOCK, Glodium.id(this.id, e.getLeft()), e.getRight()));
    }

    protected void onRegisterItems() {
        for(final Pair<String, Block> e : this.blocks) {
            final Function<Block, Item> itemFunc = this.itemBlocks.getOrDefault(e.getLeft(), (block) -> new BlockItem(block, new Item.Properties()));
            Registry.register(BuiltInRegistries.ITEM, Glodium.id(this.id, e.getLeft()), itemFunc.apply(e.getRight()));
        }

        this.items.forEach((e) -> Registry.register(BuiltInRegistries.ITEM, Glodium.id(this.id, e.getLeft()), e.getRight()));
    }

    protected void onRegisterTileEntities() {
        this.tiles.forEach((e) -> Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Glodium.id(this.id, e.getLeft()), e.getRight()));
    }

    protected void onRegisterComponents() {
        this.components.forEach((e) -> Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Glodium.id(this.id, e.getLeft()), e.getRight()));
    }
}