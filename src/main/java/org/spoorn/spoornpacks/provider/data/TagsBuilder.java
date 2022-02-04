package org.spoorn.spoornpacks.provider.data;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.spoorn.spoornpacks.SpoornPack;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;

public class TagsBuilder implements ResourceProvider {

    private final ObjectNode state = SpoornPack.OBJECT_MAPPER.createObjectNode();

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
        state.withArray("values").add(namespace + ":" + name + "_" + type.getName());
        return this;
    }

    public TagsBuilder value(String namespace, String name, ItemType type) {
        state.withArray("values").add(namespace + ":" + name + "_" + type.getName());
        return this;
    }
}
