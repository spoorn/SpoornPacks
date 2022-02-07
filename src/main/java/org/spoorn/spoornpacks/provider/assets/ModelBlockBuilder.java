package org.spoorn.spoornpacks.provider.assets;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.util.JsonTUtil;

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

    public ModelBlockBuilder defaultSapling() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
            this.defaultBlockWithTypePrefix        
        );
        return this;
    }
    
    public ModelBlockBuilder defaultPottedSapling() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix
        );
        return this;
    }
    
    public ModelBlockBuilder defaultFenceGate() {
        return substitutePlanks();
    }

    public ModelBlockBuilder defaultFenceGateOpen() {
        return substitutePlanks();
    }

    public ModelBlockBuilder defaultFenceGateWall() {
        return substitutePlanks();
    }

    public ModelBlockBuilder defaultFenceGateWallOpen() {
        return substitutePlanks();
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

    public ModelBlockBuilder defaultTrapdoorBottom() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix
        );
        return this;
    }

    public ModelBlockBuilder defaultTrapdoorOpen() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix
        );
        return this;
    }

    public ModelBlockBuilder defaultTrapdoorTop() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix
        );
        return this;
    }

    // This is the same for door_bottom/bottom_hinge/top/top_hinge
    public ModelBlockBuilder defaultDoorPart() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix + "_top",
                this.defaultBlockWithTypePrefix + "_bottom"
        );
        return this;
    }
    
    public ModelBlockBuilder defaultCraftingTable() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix + "_front",
                this.defaultBlockWithTypePrefix + "_side",
                this.defaultBlockWithTypePrefix + "_top",
                this.defaultBlockPrefix + "_" + BlockType.PLANKS.getName()
        );
        return this;
    }

    public ModelBlockBuilder defaultButton() {
        return substitutePlanks();
    }

    public ModelBlockBuilder defaultButtonInventory() {
        return substitutePlanks();
    }

    public ModelBlockBuilder defaultButtonPressed() {
        return substitutePlanks();
    }

    public ModelBlockBuilder defaultSlab() {
        return substitutePlanks();
    }

    public ModelBlockBuilder defaultSlabTop() {
        return substitutePlanks();
    }

    public ModelBlockBuilder defaultPressurePlate() {
        return substitutePlanks();
    }

    public ModelBlockBuilder defaultPressurePlateDown() {
        return substitutePlanks();
    }

    public ModelBlockBuilder defaultStairs() {
        return substitutePlanks();
    }

    public ModelBlockBuilder defaultStairsInner() {
        return substitutePlanks();
    }

    public ModelBlockBuilder defaultStairsOuter() {
        return substitutePlanks();
    }
    
    private ModelBlockBuilder substitutePlanks() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockPrefix + "_" + BlockType.PLANKS.getName()
        );
        return this;
    }

    public ModelBlockBuilder parentCubeColumn() {
        return parent("minecraft:block/cube_column");
    }

    public ModelBlockBuilder parentCubeAll() {
        return parent("minecraft:block/cube_all");
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
