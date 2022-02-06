package org.spoorn.spoornpacks.provider.data;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.provider.data.LootTableParts.Pool;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.util.JsonTUtil;

import java.util.List;

public class BlockLootTableBuilder implements ResourceProvider {

    private ObjectNode state = OBJECT_MAPPER.createObjectNode();

    private final String namespace;
    private final String name;
    private final BlockType type;
    private final String defaultPrefix;
    private final String defaultPrefixWithType;
    private final String templatePath;

    private final JsonTUtil jsonTUtil = new JsonTUtil();

    public BlockLootTableBuilder(String namespace, String name, BlockType type, String templatePath) {
        this.namespace = namespace;
        this.name = name;
        this.type = type;
        this.defaultPrefix = this.namespace + ":" + this.name;
        this.defaultPrefixWithType = this.defaultPrefix + "_" + type.getName();
        this.templatePath = templatePath;
    }

    @Override
    public ObjectNode getJson() {
        return this.state;
    }

    public BlockLootTableBuilder defaultLog() {
        return defaultSurvivesExplosion();
    }

    public BlockLootTableBuilder defaultWood() {
        return defaultSurvivesExplosion();
    }

    public BlockLootTableBuilder defaultPlanks() {
        return defaultSurvivesExplosion();
    }
    
    public BlockLootTableBuilder defaultFence() {
        return defaultSurvivesExplosion();
    }

    public BlockLootTableBuilder defaultFenceGate() {
        return defaultSurvivesExplosion();
    }
    
    public BlockLootTableBuilder defaultLeaves() {
        this.state = this.jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultPrefixWithType,
                this.defaultPrefix + "_" + BlockType.SAPLING.getName()
        );
        return this;
    }

    public BlockLootTableBuilder leavesWithSapling(String saplingName) {
        this.state = this.jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultPrefixWithType,
                this.namespace + ":" + saplingName + "_" + BlockType.SAPLING.getName()
        );
        return this;
    }

    public BlockLootTableBuilder defaultSapling() {
        return defaultSurvivesExplosion();
    }
    
    public BlockLootTableBuilder defaultPottedSapling() {
        this.state = this.jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultPrefixWithType
        );
        return this;
    }

    public BlockLootTableBuilder defaultButton() {
        return defaultSurvivesExplosion();
    }

    public BlockLootTableBuilder defaultSlab() {
        this.state = this.jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultPrefixWithType
        );
        return this;
    }

    public BlockLootTableBuilder defaultPressurePlate() {
        return defaultSurvivesExplosion();
    }

    public BlockLootTableBuilder typeBlock() {
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

    private BlockLootTableBuilder defaultSurvivesExplosion() {
        typeBlock();
        Pool pool = Pool.builder()
                .rolls(1.0)
                .bonus_rolls(0.0)
                .entries(List.of(
                        LootTableParts.Entry.builder()
                                .type("minecraft:item")
                                .name(defaultPrefixWithType)
                                .build()
                )).conditions(List.of(
                        survivesExplosionCondition()
                )).build();
        addPool(pool);
        return this;
    }
}
