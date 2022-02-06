package org.spoorn.spoornpacks.provider.data;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.jsont.JsonT;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.provider.data.RecipeParts.Ingredient;
import org.spoorn.spoornpacks.provider.data.RecipeParts.Key;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.util.JsonTUtil;

import java.io.IOException;
import java.util.List;

public class RecipeBuilder implements ResourceProvider {

    private ObjectNode state = OBJECT_MAPPER.createObjectNode();

    private final String namespace;
    private final String name;
    private final String type;
    private final String defaultPrefix;
    private final String defaultPrefixWithType;
    private final String templatePath;

    private final JsonTUtil jsonTUtil = new JsonTUtil();

    public RecipeBuilder(String namespace, String name, String type, String templatePath) {
        this.namespace = namespace;
        this.name = name;
        this.type = type;
        this.defaultPrefix = this.namespace + ":" + this.name;
        this.defaultPrefixWithType = this.defaultPrefix + "_" + this.type;
        this.templatePath = templatePath;
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
                .item(this.defaultPrefix + "_" + BlockType.LOG.getName())
                .build());
        result(3);
        return this;
    }

    public RecipeBuilder defaultPlanks() {
        craftingShapelessType();
        group("planks");
        addIngredient(Ingredient.builder()
                .tag(this.defaultPrefix + "_" + BlockType.LOG.getName() + "s")
                .build());
        result(4);
        return this;
    }

    public RecipeBuilder defaultFence() {
        craftingShapedType();
        group("wooden_fence");
        pattern(List.of("W#W", "W#W"));
        key("W", Key.builder()
                .item(this.defaultPrefix + "_" + BlockType.PLANKS.getName())
                .build());
        key("#", Key.builder()
                .item("minecraft:stick")
                .build());
        result(3);
        return this;
    }
    
    public RecipeBuilder defaultFenceGate() {
        this.state = jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultPrefix + "_" + BlockType.PLANKS.getName(),
                this.defaultPrefixWithType
        );
        return this;
    }

    public RecipeBuilder defaultButton() {
        this.state = jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultPrefix + "_" + BlockType.PLANKS.getName(),
                this.defaultPrefixWithType
        );
        return this;
    }

    public RecipeBuilder defaultSlab() {
        this.state = jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultPrefix + "_" + BlockType.PLANKS.getName(),
                this.defaultPrefixWithType
        );
        return this;
    }

    public RecipeBuilder defaultPressurePlate() {
        this.state = jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultPrefix + "_" + BlockType.PLANKS.getName(),
                this.defaultPrefixWithType
        );
        return this;
    }

    public RecipeBuilder craftingShapedType() {
        return type("minecraft:crafting_shaped");
    }

    public RecipeBuilder craftingShapelessType() {
        return type("minecraft:crafting_shapeless");
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
                .put("item", defaultPrefix + "_" + this.type)
                .put("count", count);
        return this;
    }

    public RecipeBuilder addIngredient(Ingredient ingredient) {
        this.state.withArray("ingredients").addPOJO(ingredient);
        return this;
    }
}
