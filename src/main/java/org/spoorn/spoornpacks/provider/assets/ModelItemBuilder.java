package org.spoorn.spoornpacks.provider.assets;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.jsont.JsonT;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.ItemType;

public class ModelItemBuilder implements ResourceProvider {

    private final ObjectNode state = OBJECT_MAPPER.createObjectNode();

    private final String namespace;
    private final String name;
    private final ItemType type;
    private final String defaultBlockWithTypePrefix;
    private final String templatePath;

    private final JsonT jsonT = new JsonT();

    public ModelItemBuilder(String namespace, String name, ItemType type, String templatePath) {
        this.namespace = namespace;
        this.name = name;
        this.type = type;
        this.defaultBlockWithTypePrefix = this.namespace + ":block/" + this.name + "_" + this.type.getName();
        this.templatePath = templatePath;
    }

    @Override
    public ObjectNode getJson() {
        return this.state;
    }

    public ModelItemBuilder defaultLog() {
        parent();
        return this;
    }

    public ModelItemBuilder defaultWood() {
        parent();
        return this;
    }

    public ModelItemBuilder defaultPlanks() {
        parent();
        return this;
    }

    public ModelItemBuilder defaultFence() {
        parent(defaultBlockWithTypePrefix + "_inventory");
        return this;
    }

    public ModelItemBuilder parent() {
        parent(defaultBlockWithTypePrefix);
        return this;
    }

    public ModelItemBuilder parent(String parent) {
        this.state.put("parent", parent);
        return this;
    }
}
