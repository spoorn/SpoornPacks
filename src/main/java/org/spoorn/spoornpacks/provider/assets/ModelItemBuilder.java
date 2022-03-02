package org.spoorn.spoornpacks.provider.assets;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.Type;
import org.spoorn.spoornpacks.util.JsonTUtil;

public class ModelItemBuilder implements ResourceProvider {

    private ObjectNode state = OBJECT_MAPPER.createObjectNode();

    private final String namespace;
    private final String name;
    private final Type<?> type;
    private final String defaultBlockPrefix;
    private final String defaultBlockWithTypePrefix;
    private final String templatePath;

    private final JsonTUtil jsonTUtil = new JsonTUtil();

    public ModelItemBuilder(String namespace, String name, Type<?> type, String templatePath) {
        this.namespace = namespace;
        this.name = name;
        this.type = type;
        this.defaultBlockPrefix = this.namespace + ":block/" + this.type.getPrefix() + this.name;
        // We use prefix + suffix here as ItemTypes defaults suffix to the name
        this.defaultBlockWithTypePrefix = this.defaultBlockPrefix + this.type.getSuffix();
        this.templatePath = templatePath;
    }

    @Override
    public ObjectNode getJson() {
        return this.state;
    }

    public ModelItemBuilder defaultLog() {
        return parent();
    }

    public ModelItemBuilder defaultWood() {
        return parent();
    }

    public ModelItemBuilder defaultPlanks() {
        return parent();
    }

    public ModelItemBuilder defaultLeaves() {
        return parent();
    }

    public ModelItemBuilder defaultSapling() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix      
        );
        return this;
    }

    public ModelItemBuilder defaultFence() {
        return parent(defaultBlockWithTypePrefix + "_inventory");
    }

    public ModelItemBuilder defaultFenceGate() {
        return parent();
    }

    public ModelItemBuilder defaultButton() {
        return parent(this.defaultBlockWithTypePrefix + "_inventory");
    }

    public ModelItemBuilder defaultSlab() {
        return parent();
    }

    public ModelItemBuilder defaultPressurePlate() {
        return parent();
    }

    public ModelItemBuilder defaultStairs() {
        return parent();
    }

    public ModelItemBuilder defaultTrapdoor() {
        return parent(this.defaultBlockWithTypePrefix + "_bottom");
    }

    public ModelItemBuilder defaultDoor() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.namespace + ":item/" + this.name + "_" + this.type.getName()
        );
        return this;
    }

    public ModelItemBuilder defaultCraftingTable() {
        return parent();
    }

    public ModelItemBuilder defaultBoat() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.namespace + ":item/" + this.name + "_" + this.type.getName()
        );
        return this;
    }

    public ModelItemBuilder defaultStrippedLog() {
        return parent();
    }

    public ModelItemBuilder defaultStrippedWood() {
        return parent();
    }
    
    public ModelItemBuilder defaultChest() {
        return chest(this.namespace + ":block/" + this.name + "_" + BlockType.PLANKS.getName());
    }

    public ModelItemBuilder chest(String particle) {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                particle
        );
        return this;
    }

    public ModelItemBuilder defaultBarrel() {
        return parent();
    }
    
    public ModelItemBuilder defaultSmallFlower() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockPrefix
        );
        return this;
    }

    public ModelItemBuilder defaultTallFlower() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockPrefix + "_top"
        );
        return this;
    }

    public ModelItemBuilder defaultChestMinecart() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.namespace + ":item/" + this.name + this.type.getSuffix()
        );
        return this;
    }

    public ModelItemBuilder defaultShulkerBox() {
        this.state = jsonTUtil.substituteToObjectNode(templatePath,
                this.defaultBlockWithTypePrefix
        );
        return this;
    }

    public ModelItemBuilder parent() {
        return parent(defaultBlockWithTypePrefix);
    }

    public ModelItemBuilder parent(String parent) {
        this.state.put("parent", parent);
        return this;
    }
}
