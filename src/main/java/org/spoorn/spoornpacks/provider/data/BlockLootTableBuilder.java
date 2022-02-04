package org.spoorn.spoornpacks.provider.data;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.provider.data.LootTableParts.Pool;
import org.spoorn.spoornpacks.type.BlockType;

import java.util.List;

public class BlockLootTableBuilder implements ResourceProvider {

    private final ObjectNode state = OBJECT_MAPPER.createObjectNode();

    private final String namespace;
    private final String name;
    private final BlockType type;

    public BlockLootTableBuilder(String namespace, String name, BlockType type) {
        this.namespace = namespace;
        this.name = name;
        this.type = type;
    }

    @Override
    public ObjectNode getJson() {
        return this.state;
    }

    public BlockLootTableBuilder defaultLog() {
        Pool pool = Pool.builder()
                .rolls(1)
                .entries(List.of(
                    LootTableParts.Entry.builder()
                            .type("minecraft:item")
                            .name(namespace + ":" + name + "_" + type.getName())
                            .build()
                )).conditions(List.of(
                        survivesExplosionCondition()
                )).build();
        addPool(pool);
        return this;
    }

    public BlockLootTableBuilder type() {
        this.state.put("type", "minecraft:block");
        return this;
    }

    public BlockLootTableBuilder pools() {
        this.state.putArray("pools");
        return this;
    }

    public BlockLootTableBuilder addPool(Pool pool) {
        this.state.withArray("pools").addPOJO(pool);
        return this;
    }

    private LootTableParts.Condition survivesExplosionCondition() {
        return LootTableParts.Condition.builder()
                .condition("minecraft:survives_explosion")
                .build();
    }
}
