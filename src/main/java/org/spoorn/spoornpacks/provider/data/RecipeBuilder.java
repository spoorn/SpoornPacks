package org.spoorn.spoornpacks.provider.data;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.provider.ResourceProvider;

public class RecipeBuilder implements ResourceProvider {

    private final ObjectNode state = OBJECT_MAPPER.createObjectNode();

    private final String namespace;
    private final String name;
    private final String type;

    public RecipeBuilder(String namespace, String name, String type) {
        this.namespace = namespace;
        this.name = name;
        this.type = type;
    }

    @Override
    public ObjectNode getJson() {
        return this.state;
    }
}
