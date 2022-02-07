package org.spoorn.spoornpacks.type;

import lombok.ToString;

@ToString
public enum BlockType {
    LOG("log"),
    WOOD("wood"),
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

    BlockType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
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
