package org.spoorn.spoornpacks.provider.data;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;

public class TagsBuilder implements ResourceProvider {

    private final ObjectNode state = OBJECT_MAPPER.createObjectNode();

    // sub-directory under tags/
    @Getter
    private String type;
    private boolean empty = true;

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
    
    public boolean isEmpty() {
        return this.empty;
    }

    public TagsBuilder replace(boolean replace) {
        state.put("replace", replace);
        return this;
    }

    public TagsBuilder value(String namespace, String name, BlockType type) {
        this.empty = false;
        return value(namespace + ":" + name + "_" + type.getName());
    }

    public TagsBuilder value(String namespace, String name, ItemType type) {
        this.empty = false;
        return value(namespace + ":" + name + "_" + type.getName());
    }

    public TagsBuilder value(String value) {
        this.empty = false;
        this.state.withArray("values").add(value);
        return this;
    }
}
