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
        this.defaultBlockPrefix = this.namespace + ":block/" + this.type.getPrefix() + this.name;
        this.defaultBlockWithTypePrefix = defaultBlockPrefix + this.type.getSuffix();
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
                this.defaultBlockWithTypePrefix + "_bottom",
                this.defaultBlockWithTypePrefix + "_top"
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

    // Same for log and log_horizontal
    public ModelBlockBuilder defaultStrippedLog() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix + "_top",
                this.defaultBlockWithTypePrefix
        );
        return this;
    }
    
    public ModelBlockBuilder defaultStrippedWood() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockPrefix + BlockType.LOG.getSuffix()
        );
        return this;
    }
    
    public ModelBlockBuilder defaultChest() {
        return substitutePlanks();
    }
    
    public ModelBlockBuilder chest(String particle) {
        return particle(particle);
    }

    public ModelBlockBuilder defaultBarrel() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix + "_top",
                this.defaultBlockWithTypePrefix + "_bottom",
                this.defaultBlockWithTypePrefix + "_side"
        );
        return this;
    }

    // defaultBlockWithTypePrefix should be namespace:name_barrel, excluding the _top suffix
    public ModelBlockBuilder defaultBarrelOpen() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix + "_top_open",
                this.defaultBlockWithTypePrefix + "_bottom",
                this.defaultBlockWithTypePrefix + "_side"
        );
        return this;
    }
    
    // Same for potted
    public ModelBlockBuilder defaultSmallFlower() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockPrefix
        );
        return this;
    }

    public ModelBlockBuilder defaultTallFlowerBottom() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockPrefix + "_bottom"
        );
        return this;
    }

    public ModelBlockBuilder defaultTallFlowerTop() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockPrefix + "_top"
        );
        return this;
    }
    
    public ModelBlockBuilder defaultShulkerBox() {
        return particle(this.defaultBlockWithTypePrefix);
    }

    public ModelBlockBuilder defaultLeafPile() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockPrefix + "_leaves"
        );
        return this;
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

    public ModelBlockBuilder particle(String particle) {
        this.state.with("textures").put("particle", particle);
        return this;
    }

    public ModelBlockBuilder texture(String texture) {
        this.state.with("textures").put("texture", texture);
        return this;
    }
}
