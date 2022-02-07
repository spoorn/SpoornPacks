package org.spoorn.spoornpacks.impl;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;

import java.util.Map;
import java.util.Optional;

public class GeneratedResource implements Resource {

    @Getter
    private final String namespace;

    private final Map<BlockType, Map<String, Block>> blocks;
    private final Map<ItemType, Map<String, Item>> items;

    public GeneratedResource(String namespace, Map<BlockType, Map<String, Block>> blocks, Map<ItemType, Map<String, Item>> items) {
        this.namespace = namespace;
        this.blocks = blocks;
        this.items = items;
    }

    @Override
    public Optional<Block> getBlock(BlockType type, String name) {
        return Optional.of(blocks)
                .map(outer -> outer.get(type))
                .map(inner -> inner.get(name));
    }

    @Override
    public Optional<Item> getItem(ItemType type, String name) {
        return Optional.of(items)
                .map(outer -> outer.get(type))
                .map(inner -> inner.get(name));
    }
}
