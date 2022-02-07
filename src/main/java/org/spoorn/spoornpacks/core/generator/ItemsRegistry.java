package org.spoorn.spoornpacks.core.generator;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spoorn.spoornpacks.entity.SPEntities;
import org.spoorn.spoornpacks.entity.boat.SPBoatRegistry;
import org.spoorn.spoornpacks.item.SPBoatItem;

import java.util.ArrayList;
import java.util.List;

public class ItemsRegistry {
    
    private final String modid;

    public ItemsRegistry(String modid) {
        this.modid = modid;
    }

    // TODO: Add item groups
    public Item registerBlockItem(String id, Block block) {
        return registerItem(id, new BlockItem(block, new FabricItemSettings().group(ItemGroup.MISC)));
    }

    public Item registerSaplingItem(String id, Block block) {
        Item item = new BlockItem(block, new FabricItemSettings().group(ItemGroup.MISC));
        registerCompostable(item);
        return registerItem(id, item);
    }

    public Item registerBoatItem(String namespace, String id, SPBoatRegistry spBoatRegistry, SPBoatRegistry.BoatType boatType, SPEntities spEntities) {
        Item item = new SPBoatItem(spEntities, spBoatRegistry, boatType, new Item.Settings().maxCount(1).group(ItemGroup.MISC));
        return registerItem(id, item);
    }
    
    private Item registerItem(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(this.modid, id), item);
    }

    // Update vanilla's composter block
    private void registerCompostable(Item item) {
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item.asItem(), 0.3f);
    }
}
