package org.spoorn.spoornpacks.provider.assets;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.SpoornPack;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;

public class ModelItemBuilder implements ResourceProvider {

    private final ObjectNode state = SpoornPack.OBJECT_MAPPER.createObjectNode();

    private final String namespace;
    private final String name;
    private final BlockType type;

    public ModelItemBuilder(String namespace, String name, BlockType type) {
        this.namespace = namespace;
        this.name = name;
        this.type = type;
    }

    @Override
    public ObjectNode getJson() {
        return this.state;
    }

    public ModelItemBuilder defaultLog() {
        parentBlock();
        return this;
    }

    public ModelItemBuilder parentBlock() {
        parent(namespace + ":block/" + name + "_" + type.getName());
        return this;
    }

    public ModelItemBuilder parent(String parent) {
        this.state.put("parent", parent);
        return this;
    }
}
