package org.spoorn.spoornpacks.generator;

import lombok.extern.log4j.Log4j2;
import net.minecraft.util.Identifier;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.impl.DefaultResourceBuilder;
import org.spoorn.spoornpacks.impl.GeneratedResource;
import org.spoorn.spoornpacks.provider.assets.BlockStateBuilder;
import org.spoorn.spoornpacks.provider.assets.ModelBlockBuilder;
import org.spoorn.spoornpacks.provider.assets.ModelItemBuilder;
import org.spoorn.spoornpacks.provider.data.BlockLootTableBuilder;
import org.spoorn.spoornpacks.provider.data.RecipeBuilder;
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

    private final String modid;
    private final BlocksRegistry blocksRegistry;
    private final ItemsRegistry itemsRegistry;

    public ResourceGenerator(String modid) {
        this.modid = modid;
        this.fileGenerator = new FileGenerator(modid);
        this.blocksRegistry = new BlocksRegistry(modid);
        this.itemsRegistry = new ItemsRegistry(modid);
    }

    public Resource generate(ResourceBuilder resourceBuilder) {
        log.info("Generating resources for {}", modid);
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
                String filename = name + "_" + type.getName();
                switch (type) {
                    case LOG:
                        fileGenerator.generateBlockStates(namespace, filename, new BlockStateBuilder(namespace, name, type).defaultLog());
                        fileGenerator.generateModelBlock(namespace, filename, new ModelBlockBuilder(namespace, name, type).defaultLog());
                        fileGenerator.generateLootTable(namespace, filename, new BlockLootTableBuilder(namespace, name, type).defaultLog());
                        minecraftLogs.value(namespace, name, type);
                        blocksRegistry.registerLog(filename);
                        break;
                    case WOOD:
                        fileGenerator.generateBlockStates(namespace, filename, new BlockStateBuilder(namespace, name, type).defaultWood());
                        fileGenerator.generateModelBlock(namespace, filename, new ModelBlockBuilder(namespace, name, type).defaultWood());
                        fileGenerator.generateLootTable(namespace, filename, new BlockLootTableBuilder(namespace, name, type).defaultWood());
                        minecraftLogs.value(namespace, name, type);
                        blocksRegistry.registerWood(filename);
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
                String filename = name + "_" + type.getName();
                switch (type) {
                    case LOG:
                        fileGenerator.generateModelItem(namespace, filename, new ModelItemBuilder(namespace, name, type).defaultLog());
                        minecraftLogs.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                        break;
                    case WOOD:
                        fileGenerator.generateModelItem(namespace, filename, new ModelItemBuilder(namespace, name, type).defaultWood());
                        fileGenerator.generateRecipe(namespace, filename, new RecipeBuilder(namespace, name, type.getName()).defaultWood());
                        minecraftLogs.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
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
