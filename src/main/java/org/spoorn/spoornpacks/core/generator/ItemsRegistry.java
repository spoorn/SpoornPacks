package org.spoorn.spoornpacks.core.generator;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemsRegistry {

    private final String modid;

    public ItemsRegistry(String modid) {
        this.modid = modid;
    }

    // TODO: Add item groups
    public Item registerBlockItem(String id, Block block) {
        return Registry.register(Registry.ITEM, new Identifier(this.modid, id), new BlockItem(block, new FabricItemSettings().group(ItemGroup.MISC)));
    }

    public Item registerSaplingItem(String id, Block block) {
        Item item = Registry.register(Registry.ITEM, new Identifier(this.modid, id), new BlockItem(block, new FabricItemSettings().group(ItemGroup.MISC)));
        registerCompostable(item);
        return item;
    }

    // Update vanilla's composter block
    private void registerCompostable(Item item) {
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item.asItem(), 0.3f);
    }
}
