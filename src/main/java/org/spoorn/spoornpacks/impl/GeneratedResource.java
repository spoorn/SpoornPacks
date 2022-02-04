package org.spoorn.spoornpacks.impl;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GeneratedResource implements Resource {

    @Getter
    private final String namespace;

    private final Map<String, Map<String, Block>> blocks = new HashMap<>();
    private final Map<String, Map<String, Item>> items = new HashMap<>();

    public GeneratedResource(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public Optional<Block> getBlock(BlockType type, String name) {
        return Optional.of(blocks)
                .map(outer -> outer.get(type.getName()))
                .map(inner -> inner.get(name));
    }

    @Override
    public Optional<Item> getItem(ItemType type, String name) {
        return Optional.of(items)
                .map(outer -> outer.get(type.getName()))
                .map(inner -> inner.get(name));
    }
}
