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
                case FENCE, FENCE_GATE, CRAFTING_TABLE, CHEST, BARREL -> {
                    register300(item);
                }
                default -> {
                }
            }
        }
    }

    private static void register300(ItemConvertible item) {
        FuelRegistry.INSTANCE.add(item, 300);
    }
}
