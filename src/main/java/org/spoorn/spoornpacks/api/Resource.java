package org.spoorn.spoornpacks.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;
import org.spoorn.spoornpacks.type.VehicleType;

import java.util.Optional;

/**
 * A representation of Resources generated from SpoornPacks.  Allows access to blocks and items generated and registered
 * based on the {@link ResourceBuilder} used to generate this Resource.
 */
public interface Resource {

    String getNamespace();

    Optional<Block> getBlock(BlockType type, String name);

    Optional<Item> getItem(ItemType type, String name);
    
    Optional<Item> getVehicleItem(VehicleType type, String name);
}
