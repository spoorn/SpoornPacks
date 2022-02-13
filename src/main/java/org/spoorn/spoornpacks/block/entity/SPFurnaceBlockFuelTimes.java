package org.spoorn.spoornpacks.block.entity;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import org.spoorn.spoornpacks.type.ItemType;

/**
 * Furnace fuel times in ticks for various items.
 */
public class SPFurnaceBlockFuelTimes {

    public static void registerFurnaceBlockFuelTime(ItemType type, Item item) {
        if (item != null) {
            switch (type) {
                case FENCE, FENCE_GATE -> {
                    registerFence(item);
                }
                case CRAFTING_TABLE -> {
                    registerCraftingTable(item);
                }
                case CHEST -> {
                    registerChest(item);
                }
                default -> {
                }
            }
        }
    }

    private static void registerFence(ItemConvertible item) {
        FuelRegistry.INSTANCE.add(item, 300);
    }

    private static void registerCraftingTable(ItemConvertible item) {
        FuelRegistry.INSTANCE.add(item, 300);
    }

    private static void registerChest(ItemConvertible item) {
        FuelRegistry.INSTANCE.add(item, 300);
    }
}
