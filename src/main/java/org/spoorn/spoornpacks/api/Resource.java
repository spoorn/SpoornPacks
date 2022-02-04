package org.spoorn.spoornpacks.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;

import java.util.Optional;

public interface Resource {

    String getNamespace();

    Optional<Block> getBlock(BlockType type, String name);

    Optional<Item> getItem(ItemType type, String name);
}
