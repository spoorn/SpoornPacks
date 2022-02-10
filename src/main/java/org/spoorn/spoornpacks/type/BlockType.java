package org.spoorn.spoornpacks.type;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum BlockType {
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
    CRAFTING_TABLE("crafting_table");

    private final String name;
    
    // For use cases where there is a prefix and a suffix to the name, such as "stripped_<name>_log"
    private final String prefix;
    private final String suffix;

    BlockType(String name) {
        // Default to setting the name as the suffix as most items do that
        this(name, "", "_" + name);
    }
    
    BlockType(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
    }
    public static BlockType fromString(String name) {
        BlockType[] blockTypes = values();
        for (int i = 0; i < blockTypes.length; i++) {
            if (blockTypes[i].getName().equals(name)) {
                return blockTypes[i];
            }
        }
        throw new IllegalArgumentException("name=[" + name + "] is not a valid BlockType");
    }
}
