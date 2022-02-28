package org.spoorn.spoornpacks.impl;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;
import org.spoorn.spoornpacks.type.VehicleType;

import java.util.Map;
import java.util.Optional;

@Log4j2
public class GeneratedResource implements Resource {

    @Getter
    private final String namespace;

    private final Map<BlockType, Map<String, Block>> blocks;
    private final Map<ItemType, Map<String, Item>> items;
    private final Map<VehicleType, Map<String, Item>> vehicleItems;

    public GeneratedResource(String namespace, Map<BlockType, Map<String, Block>> blocks, Map<ItemType, Map<String, Item>> items, 
                             Map<VehicleType, Map<String, Item>> vehicleItems) {
        this.namespace = namespace;
        this.blocks = blocks;
        this.items = items;
        this.vehicleItems = vehicleItems;
    }

    @Override
    public Optional<Block> getBlock(BlockType type, String name) {
        Optional<Block> block = Optional.of(blocks)
                .map(outer -> outer.get(type))
                .map(inner -> inner.get(name));
        if (block.isEmpty() && name.endsWith(type.getSuffix())) {
            log.warn("getBlock called with name={}.  You should exclude suffixes for the name", name);
        }
        return block;
    }

    @Override
    public Optional<Item> getItem(ItemType type, String name) {
        Optional<Item> item = Optional.of(items)
                .map(outer -> outer.get(type))
                .map(inner -> inner.get(name));
        if (item.isEmpty() && name.endsWith(type.getSuffix())) {
            log.warn("getItem called with name={}.  You should exclude suffixes for the name", name);
        }
        return item;
    }

    @Override
    public Optional<Item> getVehicleItem(VehicleType type, String name) {
        Optional<Item> item = Optional.of(vehicleItems)
                .map(outer -> outer.get(type))
                .map(inner -> inner.get(name));
        if (item.isEmpty() && name.endsWith(type.getSuffix())) {
            log.warn("getVehicleItem called with name={}.  You should exclude suffixes for the name", name);
        }
        return item;
    }
}
