package org.spoorn.spoornpacks.provider.assets;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.util.JsonTUtil;

public class BlockStateBuilder implements ResourceProvider {

    private ObjectNode state = OBJECT_MAPPER.createObjectNode();

    private final String namespace;
    private final String name;
    private final BlockType type;
    private final String defaultBlockPrefix;
    private final String defaultBlockWithTypePrefix;
    private final String templatePath;

    private final JsonTUtil jsonTUtil = new JsonTUtil();

    public BlockStateBuilder(String namespace, String name, BlockType type, String templatePath) {
        this.namespace = namespace;
        this.name = name;
        this.type = type;
        this.defaultBlockPrefix = this.namespace + ":block/" + this.type.getPrefix() + this.name;
        this.defaultBlockWithTypePrefix = this.defaultBlockPrefix + this.type.getSuffix();
        this.templatePath = templatePath;
    }

    @Override
    public ObjectNode getJson() {
        return this.state;
    }

    public BlockStateBuilder defaultLog() {
        yaxis();
        zaxis();
        xaxis();
        return this;
    }

    public BlockStateBuilder defaultWood() {
        yaxis();
        zaxis();
        xaxis();
        return this;
    }

    public BlockStateBuilder defaultPlanks() {
        return emptyModel();
    }

    public BlockStateBuilder defaultLeaves() {
        return emptyModel();
    }

    public BlockStateBuilder defaultSapling() {
        return emptyModel();
    }

    public BlockStateBuilder defaultFence() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                defaultBlockWithTypePrefix + "_post",
                defaultBlockWithTypePrefix + "_side"
        );
        return this;
    }
    
    public BlockStateBuilder defaultFenceGate() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath, 
                defaultBlockWithTypePrefix,
                defaultBlockWithTypePrefix + "_open",
                defaultBlockWithTypePrefix + "_wall",
                defaultBlockWithTypePrefix + "_wall_open"
        );
        return this;
    }
    
    public BlockStateBuilder defaultButton() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix,
                this.defaultBlockWithTypePrefix + "_pressed"
        );
        return this;
    }

    public BlockStateBuilder defaultSlab() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix,
                this.defaultBlockPrefix + "_" + BlockType.PLANKS.getName(),
                this.defaultBlockWithTypePrefix + "_top"
        );
        return this;
    }

    public BlockStateBuilder defaultPressurePlate() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix,
                this.defaultBlockWithTypePrefix + "_down"
        );
        return this;
    }

    public BlockStateBuilder defaultStairs() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix + "_inner",
                this.defaultBlockWithTypePrefix + "_outer",
                this.defaultBlockWithTypePrefix
        );
        return this;
    }

    public BlockStateBuilder defaultTrapdoor() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix + "_bottom",
                this.defaultBlockWithTypePrefix + "_open",
                this.defaultBlockWithTypePrefix + "_top"
        );
        return this;
    }

    public BlockStateBuilder defaultDoor() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix + "_bottom_left",
                this.defaultBlockWithTypePrefix + "_bottom_left_open",
                this.defaultBlockWithTypePrefix + "_bottom_right",
                this.defaultBlockWithTypePrefix + "_bottom_right_open",
                this.defaultBlockWithTypePrefix + "_top_left",
                this.defaultBlockWithTypePrefix + "_top_left_open",
                this.defaultBlockWithTypePrefix + "_top_right",
                this.defaultBlockWithTypePrefix + "_top_right_open"
        );
        return this;
    }

    public BlockStateBuilder defaultStrippedLog() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix + "_horizontal",
                this.defaultBlockWithTypePrefix
        );
        return this;
    }

    public BlockStateBuilder defaultStrippedWood() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix
        );
        return this;
    }

    public BlockStateBuilder defaultCraftingTable() {
        return emptyModel();
    }
    
    public BlockStateBuilder defaultChest() {
        return emptyModel();
    }
    
    public BlockStateBuilder defaultBarrel() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix,
                this.defaultBlockWithTypePrefix + "_open"
        );
        return this;
    }
    
    // Same for potted small flower
    public BlockStateBuilder defaultSmallFlower() {
        return emptyModel(this.defaultBlockPrefix);
    }

    public BlockStateBuilder defaultTallFlower() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix + "_bottom",
                this.defaultBlockWithTypePrefix + "_top"
        );
        return this;
    }

    public BlockStateBuilder defaultShulkerBox() {
        return emptyModel(this.defaultBlockWithTypePrefix);
    }
    
    public BlockStateBuilder defaultLeafPile() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix
        );
        return this;
    }

    public BlockStateBuilder yaxis() {
        this.state.with("variants").with("axis=y")
                .put("model", defaultBlockWithTypePrefix);
        return this;
    }

    public BlockStateBuilder zaxis() {
        this.state.with("variants").with("axis=z")
                .put("model", defaultBlockWithTypePrefix)
                .put("x", 90);
        return this;
    }

    public BlockStateBuilder xaxis() {
        this.state.with("variants").with("axis=x")
                .put("model", defaultBlockWithTypePrefix)
                .put("x", 90)
                .put("y", 90);
        return this;
    }

    public BlockStateBuilder emptyModel() {
        return emptyModel(defaultBlockWithTypePrefix);
    }

    public BlockStateBuilder emptyModel(String model) {
        this.state.with("variants").with("").put("model", model);
        return this;
    }
}
