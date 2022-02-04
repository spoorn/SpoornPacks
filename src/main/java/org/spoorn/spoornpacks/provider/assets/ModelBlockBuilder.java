package org.spoorn.spoornpacks.provider.assets;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.SpoornPack;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;

public class ModelBlockBuilder implements ResourceProvider {

    private final ObjectNode state = SpoornPack.OBJECT_MAPPER.createObjectNode();

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
        end();
        side();
        return this;
    }

    public ModelBlockBuilder parentCubeColumn() {
        parent("block/cube_column");
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

    public ModelBlockBuilder end() {
        String prefix = namespace + ":block/" + name + "_" + type.getName();
        this.state.with("textures")
                .put("end", prefix + "_top");
        return this;
    }

    public ModelBlockBuilder side() {
        String prefix = namespace + ":block/" + name;
        this.state.with("textures")
                .put("side", prefix + "_top");
        return this;
    }
}
