package org.spoorn.spoornpacks.provider.assets;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.jsont.JsonT;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.util.JsonTUtil;

import java.io.IOException;

public class ModelBlockBuilder implements ResourceProvider {

    private ObjectNode state = OBJECT_MAPPER.createObjectNode();

    private final String namespace;
    private final String name;
    private final BlockType type;
    private final String defaultBlockPrefix;
    private final String defaultBlockWithTypePrefix;
    private final String templatePath;

    private final JsonTUtil jsonTUtil = new JsonTUtil();

    public ModelBlockBuilder(String namespace, String name, BlockType type, String templatePath) {
        this.namespace = namespace;
        this.name = name;
        this.type = type;
        this.defaultBlockPrefix = this.namespace + ":block/" + this.name;
        this.defaultBlockWithTypePrefix = defaultBlockPrefix + "_" + this.type.getName();
        this.templatePath = templatePath;
    }

    @Override
    public ObjectNode getJson() {
        return this.state;
    }

    public ModelBlockBuilder defaultLog() {
        parentCubeColumn();
        endWithSuffix("_" + type.getName() + "_top");
        sideWithTypeSuffix();
        return this;
    }

    public ModelBlockBuilder defaultWood() {
        parentCubeColumn();
        endWithSuffix("_" + BlockType.LOG.getName());
        sideWithSuffix("_" + BlockType.LOG.getName());
        return this;
    }

    public ModelBlockBuilder defaultPlanks() {
        parentCubeAll();
        all();
        particle();
        return this;
    }
    
    public ModelBlockBuilder defaultLeaves() {
        parentCubeAll();
        all();
        particle();
        return this;
    }
    
    public ModelBlockBuilder defaultFenceGate() {
        this.state = jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultBlockPrefix + "_" + BlockType.PLANKS.getName()
        );
        return this;
    }

    public ModelBlockBuilder defaultFenceGateOpen() {
        this.state = jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultBlockPrefix + "_" + BlockType.PLANKS.getName()
        );
        return this;
    }

    public ModelBlockBuilder defaultFenceGateWall() {
        this.state = jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultBlockPrefix + "_" + BlockType.PLANKS.getName()
        );
        return this;
    }

    public ModelBlockBuilder defaultFenceGateWallOpen() {
        this.state = jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultBlockPrefix + "_" + BlockType.PLANKS.getName()
        );
        return this;
    }

    public ModelBlockBuilder defaultFenceInventory() {
        parent("minecraft:block/fence_inventory");
        texture(defaultBlockPrefix + "_" + BlockType.PLANKS.getName());
        return this;
    }
    
    public ModelBlockBuilder defaultFencePost() {
        parent("minecraft:block/fence_post");
        texture(defaultBlockPrefix + "_" + BlockType.PLANKS.getName());
        return this;
    }

    public ModelBlockBuilder defaultFenceSide() {
        parent("minecraft:block/fence_side");
        texture(defaultBlockPrefix + "_" + BlockType.PLANKS.getName());
        return this;
    }

    public ModelBlockBuilder parentCubeColumn() {
        parent("minecraft:block/cube_column");
        return this;
    }

    public ModelBlockBuilder parentCubeAll() {
        parent("minecraft:block/cube_all");
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

    public ModelBlockBuilder endWithTypeSuffix() {
        return end(this.defaultBlockWithTypePrefix);
    }

    public ModelBlockBuilder endWithSuffix(String suffix) {
        return end(this.defaultBlockPrefix + suffix);
    }

    public ModelBlockBuilder end(String end) {
        this.state.with("textures")
                .put("end", end);
        return this;
    }

    public ModelBlockBuilder sideWithTypeSuffix() {
        return side(this.defaultBlockWithTypePrefix);
    }

    public ModelBlockBuilder sideWithSuffix(String suffix) {
        return side(this.defaultBlockPrefix + suffix);
    }

    public ModelBlockBuilder side(String side) {
        this.state.with("textures")
                .put("side", side);
        return this;
    }

    public ModelBlockBuilder all() {
        this.state.with("textures").put("all", this.defaultBlockWithTypePrefix);
        return this;
    }

    public ModelBlockBuilder particle() {
        this.state.with("textures").put("particle", this.defaultBlockWithTypePrefix);
        return this;
    }

    public ModelBlockBuilder texture(String texture) {
        this.state.with("textures").put("texture", texture);
        return this;
    }
}
