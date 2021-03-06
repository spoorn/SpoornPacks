package org.spoorn.spoornpacks.core.generator;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spoorn.spoornpacks.api.entity.vehicle.SPMinecartEntityFactory;
import org.spoorn.spoornpacks.entity.SPEntities;
import org.spoorn.spoornpacks.entity.boat.SPBoatRegistry;
import org.spoorn.spoornpacks.item.SPBoatItem;
import org.spoorn.spoornpacks.item.SPMinecartItem;

public class ItemsRegistry {
    
    private final String modid;

    public ItemsRegistry(String modid) {
        this.modid = modid;
    }

    public Item registerBlockItem(String id, Block block, ItemGroup itemGroup) {
        Item item = registerItem(id, new BlockItem(block, new FabricItemSettings().group(itemGroup)));
        if (block instanceof FlowerBlock || block instanceof TallFlowerBlock) {
            registerCompostable(item);
        }
        return item;
    }

    public Item registerSaplingItem(String id, Block block, ItemGroup itemGroup) {
        Item item = new BlockItem(block, new FabricItemSettings().group(itemGroup));
        registerCompostable(item);
        return registerItem(id, item);
    }

    public Item registerBoatItem(String id, SPBoatRegistry spBoatRegistry, SPBoatRegistry.BoatType boatType, SPEntities spEntities, ItemGroup itemGroup) {
        Item item = new SPBoatItem(spEntities, spBoatRegistry, boatType, new Item.Settings().maxCount(1).group(itemGroup));
        return registerItem(id, item);
    }
    
    public Item registerChestMinecart(String id, ItemGroup itemGroup, SPMinecartEntityFactory factory) {
        Item item = new SPMinecartItem(factory, new Item.Settings().maxCount(1).group(itemGroup));
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
