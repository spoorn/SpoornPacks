package org.spoorn.spoornpacks.provider.assets;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.ItemType;

public class ModelItemBuilder implements ResourceProvider {

    private final ObjectNode state = OBJECT_MAPPER.createObjectNode();

    private final String namespace;
    private final String name;
    private final ItemType type;

    public ModelItemBuilder(String namespace, String name, ItemType type) {
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
