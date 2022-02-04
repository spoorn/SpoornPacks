package org.spoorn.spoornpacks.generator;

import lombok.extern.log4j.Log4j2;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.impl.DefaultResourceBuilder;
import org.spoorn.spoornpacks.impl.GeneratedResource;
import org.spoorn.spoornpacks.provider.assets.BlockStateBuilder;
import org.spoorn.spoornpacks.provider.assets.ModelBlockBuilder;
import org.spoorn.spoornpacks.provider.assets.ModelItemBuilder;
import org.spoorn.spoornpacks.provider.data.BlockLootTableBuilder;
import org.spoorn.spoornpacks.provider.data.TagsBuilder;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Log4j2
public class ResourceGenerator {

    private final FileGenerator fileGenerator;

    private final String id;

    public ResourceGenerator(String id) {
        this.id = id;
        this.fileGenerator = new FileGenerator(id);
    }

    public Resource generate(ResourceBuilder resourceBuilder) {
        if (!(resourceBuilder instanceof DefaultResourceBuilder)) {
            throw new UnsupportedOperationException("ResourceBuilder is unsupported!");
        }

        DefaultResourceBuilder drb = (DefaultResourceBuilder) resourceBuilder;

        // Blocks
        final Map<String, List<String>> blocks = drb.getBlocks();
        try {
            handleBlocks(drb.getNamespace(), blocks);
        } catch (IOException e) {
            log.error("Could not generate resources for blocks", e);
            throw new RuntimeException(e);
        }

        // Items
        final Map<String, List<String>> items = drb.getItems();
        try {
            handleItems(drb.getNamespace(), items);
        } catch (IOException e) {
            log.error("Could not generate resources for items", e);
            throw new RuntimeException(e);
        }

        GeneratedResource gen = new GeneratedResource(drb.getNamespace());
        return gen;
    }

    private void handleBlocks(String namespace, Map<String, List<String>> blocks) throws IOException {
        TagsBuilder minecraftLogs = new TagsBuilder("blocks");
        for (Entry<String, List<String>> entry : blocks.entrySet()) {
            BlockType type = BlockType.fromString(entry.getKey());
            for (String name : entry.getValue()) {
                switch (type) {
                    case LOG:
                        fileGenerator.generateBlockStates(namespace, name, new BlockStateBuilder(namespace, name, type).defaultLog());
                        fileGenerator.generateModelBlock(namespace, name, new ModelBlockBuilder(namespace, name, type).defaultLog());
                        fileGenerator.generateLootTable(namespace, name, new BlockLootTableBuilder(namespace, name, type).defaultLog());
                        minecraftLogs.value(namespace, name, type);
                        break;
                    default:
                        throw new UnsupportedOperationException("BlockType=[" + type + "] is not supported");
                }
            }
        }

        fileGenerator.generateTags("minecraft", "logs", minecraftLogs);
        fileGenerator.generateTags("minecraft", "logs_that_burn", minecraftLogs);
    }

    private void handleItems(String namespace, Map<String, List<String>> items) throws IOException {
        TagsBuilder minecraftLogs = new TagsBuilder("items");
        for (Entry<String, List<String>> entry : items.entrySet()) {
            ItemType type = ItemType.fromString(entry.getKey());
            for (String name : entry.getValue()) {
                switch (type) {
                    case LOG:
                        fileGenerator.generateModelItem(namespace, name, new ModelItemBuilder(namespace, name, type).defaultLog());
                        minecraftLogs.value(namespace, name, type);
                        break;
                    default:
                        throw new UnsupportedOperationException("BlockType=[" + type + "] is not supported");
                }
            }
        }

        fileGenerator.generateTags("minecraft", "logs", minecraftLogs);
        fileGenerator.generateTags("minecraft", "logs_that_burn", minecraftLogs);
    }
}
