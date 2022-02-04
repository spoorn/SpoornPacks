package org.spoorn.spoornpacks.provider.assets;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spoorn.spoornpacks.SpoornPack;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;

public class BlockStateBuilder implements ResourceProvider {

    private final ObjectNode state = SpoornPack.OBJECT_MAPPER.createObjectNode();

    private final String namespace;
    private final String name;
    private final BlockType type;

    public BlockStateBuilder(String namespace, String name, BlockType type) {
        this.namespace = namespace;
        this.name = name;
        this.type = type;
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

    public BlockStateBuilder variants() {
        this.state.putObject("variants");
        return this;
    }

    public BlockStateBuilder yaxis() {
        this.state.with("variants").with("axis=y")
                .put("model", this.namespace + ":block/" + this.name + "_" + this.type.getName());
        return this;
    }

    public BlockStateBuilder zaxis() {
        this.state.with("variants").with("axis=z")
                .put("model", this.namespace + ":block/" + this.name + "_" + this.type.getName())
                .put("x", 90);
        return this;
    }

    public BlockStateBuilder xaxis() {
        this.state.with("variants").with("axis=x")
                .put("model", this.namespace + ":block/" + this.name + "_" + this.type.getName())
                .put("x", 90)
                .put("y", 90);
        return this;
    }
}
