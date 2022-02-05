package org.spoorn.spoornpacks.provider.data;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;

public class TagsBuilder implements ResourceProvider {

    private final ObjectNode state = OBJECT_MAPPER.createObjectNode();

    // "blocks" or "items"
    @Getter
    private String type;

    public TagsBuilder(String type) {
        this(false, type);
    }

    public TagsBuilder(boolean replace, String type) {
        replace(replace);
        this.type = type;
    }

    @Override
    public ObjectNode getJson() {
        return this.state;
    }

    public TagsBuilder replace(boolean replace) {
        state.put("replace", replace);
        return this;
    }

    public TagsBuilder value(String namespace, String name, BlockType type) {
        return value(namespace + ":" + name + "_" + type.getName());
    }

    public TagsBuilder value(String namespace, String name, ItemType type) {
        return value(namespace + ":" + name + "_" + type.getName());
    }

    public TagsBuilder value(String value) {
        this.state.withArray("values").add(value);
        return this;
    }
}
