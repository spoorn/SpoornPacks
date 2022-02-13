package org.spoorn.spoornpacks.type;

import lombok.Getter;

@Getter
public enum ItemType {
    LOG("log"),
    STRIPPED_LOG("stripped_log", "stripped_", "_log"),
    WOOD("wood"),
    STRIPPED_WOOD("stripped_wood", "stripped_", "_wood"),
    PLANKS("planks"),
    LEAVES("leaves"),
    SAPLING("sapling"),
    FENCE("fence"),
    FENCE_GATE("fence_gate"),
    BUTTON("button"),
    SLAB("slab"),
    PRESSURE_PLATE("pressure_plate"),
    STAIRS("stairs"),
    TRAPDOOR("trapdoor"),
    DOOR("door"),
    CRAFTING_TABLE("crafting_table"),
    BOAT("boat"),
    CHEST("chest"),
    BARREL("barrel");

    private final String name;

    // For use cases where there is a prefix and a suffix to the name, such as "stripped_<name>_log"
    private String prefix;
    private String suffix;

    ItemType(String name) {
        // Default to setting the name as the suffix as most items use that as the suffix
        this(name, "", "_" + name);
    }

    ItemType(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public static ItemType fromString(String name) {
        ItemType[] itemTypes = values();
        for (int i = 0; i < itemTypes.length; i++) {
            if (itemTypes[i].getName().equals(name)) {
                return itemTypes[i];
            }
        }
        throw new IllegalArgumentException("name=[" + name + "] is not a valid ItemType");
    }
}
