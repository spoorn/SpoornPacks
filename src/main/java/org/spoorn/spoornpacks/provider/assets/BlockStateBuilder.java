package org.spoorn.spoornpacks.provider.assets;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.provider.assets.BlockStateParts.Apply;
import org.spoorn.spoornpacks.provider.assets.BlockStateParts.Multipart;
import org.spoorn.spoornpacks.provider.assets.BlockStateParts.When;
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
        this.defaultBlockPrefix = this.namespace + ":block/" + this.name;
        this.defaultBlockWithTypePrefix = this.defaultBlockPrefix + "_" + this.type.getName();
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
        addMultipart(Multipart.builder()
                .apply(Apply.builder()
                        .model(defaultBlockWithTypePrefix + "_post")
                        .build())
                .build());
        addMultipart(Multipart.builder()
                .when(When.builder()
                        .north("true")
                        .build())
                .apply(Apply.builder()
                        .model(defaultBlockWithTypePrefix + "_side")
                        .uvlock(true)
                        .build())
                .build());
        addMultipart(Multipart.builder()
                .when(When.builder()
                        .east("true")
                        .build())
                .apply(Apply.builder()
                        .model(defaultBlockWithTypePrefix + "_side")
                        .y(90)
                        .uvlock(true)
                        .build())
                .build());
        addMultipart(Multipart.builder()
                .when(When.builder()
                        .south("true")
                        .build())
                .apply(Apply.builder()
                        .model(defaultBlockWithTypePrefix + "_side")
                        .y(180)
                        .uvlock(true)
                        .build())
                .build());
        addMultipart(Multipart.builder()
                .when(When.builder()
                        .west("true")
                        .build())
                .apply(Apply.builder()
                        .model(defaultBlockWithTypePrefix + "_side")
                        .y(270)
                        .uvlock(true)
                        .build())
                .build());
        return this;
    }
    
    public BlockStateBuilder defaultFenceGate() {
        /*facingInWallOpen("east", false, false, this.defaultBlockWithTypePrefix);
        facingInWallOpen("east", false, true, this.defaultBlockWithTypePrefix + "_open");
        facingInWallOpen("east", false, false, this.defaultBlockWithTypePrefix);
        facingInWallOpen("east", false, false, this.defaultBlockWithTypePrefix);*/
        this.state = jsonTUtil.substitute(templatePath, ObjectNode.class, 
                defaultBlockWithTypePrefix,
                defaultBlockWithTypePrefix + "_open",
                defaultBlockWithTypePrefix + "_wall",
                defaultBlockWithTypePrefix + "_wall_open"
        );
        return this;
    }
    
    public BlockStateBuilder defaultButton() {
        this.state = jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultBlockWithTypePrefix,
                this.defaultBlockWithTypePrefix + "_pressed"
        );
        return this;
    }

    public BlockStateBuilder defaultSlab() {
        this.state = jsonTUtil.substitute(templatePath, ObjectNode.class,
                this.defaultBlockWithTypePrefix,
                this.defaultBlockPrefix + "_" + BlockType.PLANKS.getName(),
                this.defaultBlockWithTypePrefix + "_top"
        );
        return this;
    }

    public BlockStateBuilder variants() {
        this.state.putObject("variants");
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
        this.state.with("variants").with("").put("model", defaultBlockWithTypePrefix);
        return this;
    }

    public BlockStateBuilder addMultipart(Multipart multipart) {
        this.state.withArray("multipart").addPOJO(multipart);
        return this;
    }

    public BlockStateBuilder facingInWallOpen(String facing, boolean inWall, boolean open, String model) {
        ObjectNode nestedNode = this.state.with("variants")
                .with("facing=" + facing + ",in_wall=" + inWall + ",open=" + open);
        nestedNode.put("uvlock", true);
        if ("east".equals(facing)) {
            nestedNode.put("y", 270);
        } else if ("north".equals(facing)) {
            nestedNode.put("y", 180);
        } else if ("west".equals(facing)) {
            nestedNode.put("y", 90);
        } else if (!"south".equals(facing)) {
            throw new IllegalArgumentException("facing=" + facing + " is not supported for blockstates/ model");
        }

        nestedNode.put("model", model);
        return this;
    }
}
