package org.spoorn.spoornpacks.provider.assets;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;

public class ModelBlockBuilder implements ResourceProvider {

    private final ObjectNode state = OBJECT_MAPPER.createObjectNode();

    private final String namespace;
    private final String name;
    private final BlockType type;

    public ModelBlockBuilder(String namespace, String name, BlockType type) {
        this.namespace = namespace;
        this.name = name;
        this.type = type;
    }

    @Override
    public ObjectNode getJson() {
        return this.state;
    }

    public ModelBlockBuilder defaultLog() {
        parentCubeColumn();
        endWithSuffix("_" + type.getName() + "_top");
        sideWithTypeSuffix();
        return this;
    }

    public ModelBlockBuilder defaultWood() {
        parentCubeColumn();
        endWithSuffix("_" + BlockType.LOG.getName());
        sideWithSuffix("_" + BlockType.LOG.getName());
        return this;
    }

    public ModelBlockBuilder parentCubeColumn() {
        parent("minecraft:block/cube_column");
        return this;
    }

    public ModelBlockBuilder parent(String parent) {
        this.state.put("parent", parent);
        return this;
    }

    public ModelBlockBuilder textures() {
        this.state.putObject("textures");
        return this;
    }

    public ModelBlockBuilder endWithTypeSuffix() {
        return end(this.namespace + ":block/" + this.name + "_" + this.type.getName());
    }

    public ModelBlockBuilder endWithSuffix(String suffix) {
        return end(this.namespace + ":block/" + this.name + suffix);
    }

    public ModelBlockBuilder end(String end) {
        this.state.with("textures")
                .put("end", end);
        return this;
    }

    public ModelBlockBuilder sideWithTypeSuffix() {
        return side(this.namespace + ":block/" + this.name + "_" + this.type.getName());
    }

    public ModelBlockBuilder sideWithSuffix(String suffix) {
        return side(this.namespace + ":block/" + this.name + suffix);
    }

    public ModelBlockBuilder side(String side) {
        this.state.with("textures")
                .put("side", side);
        return this;
    }
}
