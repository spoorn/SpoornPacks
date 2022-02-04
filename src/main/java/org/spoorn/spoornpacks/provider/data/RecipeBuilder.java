package org.spoorn.spoornpacks.provider.data;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.provider.data.RecipeParts.Key;
import org.spoorn.spoornpacks.type.BlockType;

import java.util.List;

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

    public RecipeBuilder defaultWood() {
        craftingShapedType();
        group("bark");
        pattern(List.of("##", "##"));
        key("#", Key.builder()
                .item(this.namespace + ":" + this.name + "_" + BlockType.LOG.getName())
                .build());
        result(3);
        return this;
    }

    public RecipeBuilder craftingShapedType() {
        return type("minecraft:crafting_shaped");
    }

    public RecipeBuilder type(String type) {
        this.state.put("type", type);
        return this;
    }

    public RecipeBuilder group(String name) {
        this.state.put("group", name);
        return this;
    }

    public RecipeBuilder pattern(List<String> patterns) {
        ArrayNode arrayNode = this.state.putArray("pattern");
        for (String s : patterns) {
            arrayNode.add(s);
        }
        return this;
    }

    public RecipeBuilder key(String key, Key value) {
        this.state.with("key").putPOJO(key, value);
        return this;
    }

    public RecipeBuilder result(int count) {
        this.state.with("result")
                .put("item", this.namespace + ":" + this.name + "_" + this.type)
                .put("count", count);
        return this;
    }
}
