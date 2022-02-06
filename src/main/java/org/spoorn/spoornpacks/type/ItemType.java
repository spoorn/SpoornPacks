package org.spoorn.spoornpacks.type;

public enum ItemType {
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
    TRAPDOOR("trapdoor");

    private final String name;

    ItemType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
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
