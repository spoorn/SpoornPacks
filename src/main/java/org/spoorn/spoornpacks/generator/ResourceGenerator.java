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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Log4j2
public class ResourceGenerator {
    
    private static final String TEMPLATES_RESOURCE_PATH = "/assets/spoornpacks/jsont/templates/";
    private static final String BLOCK_STATES_TEMPLATE_PATH = TEMPLATES_RESOURCE_PATH + "assets/blockstates/";
    private static final String MODELS_BLOCK_TEMPLATE_PATH = TEMPLATES_RESOURCE_PATH + "assets/models/block/";
    private static final String MODELS_ITEM_TEMPLATE_PATH = TEMPLATES_RESOURCE_PATH + "assets/models/item/";
    private static final String BLOCK_LOOTTABLES_TEMPLATE_PATH = TEMPLATES_RESOURCE_PATH + "data/loot_tables/blocks/";
    private static final String RECIPES_TEMPLATE_PATH = TEMPLATES_RESOURCE_PATH + "data/recipes/";

    private static final String BLOCKS = "blocks";
    private static final String ITEMS = "items";
    private static final String MINECRAFT = "minecraft";
    private static final String JSONT_SUFFIX = ".jsonT";

    private final FileGenerator fileGenerator;

    private final String modid;
    private final BlocksRegistry blocksRegistry;
    private final ItemsRegistry itemsRegistry;

    public ResourceGenerator(String modid, boolean overwrite) {
        this.modid = modid;
        this.fileGenerator = new FileGenerator(modid, overwrite);
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
        TagsBuilder minecraftLogs = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftPlanks = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftWoodenFences = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftFenceGates = new TagsBuilder(BLOCKS);
        Map<String, List<String>> customLogs = new HashMap<>();

        for (Entry<String, List<String>> entry : blocks.entrySet()) {
            BlockType type = BlockType.fromString(entry.getKey());
            for (String name : entry.getValue()) {
                String filename = name + "_" + type.getName();
                switch (type) {
                    case LOG -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultLog());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultLog());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultLog());
                        minecraftLogs.value(namespace, name, type);
                        customLogs.computeIfAbsent(name, m -> new ArrayList<>()).add(namespace + ":" + filename);
                        blocksRegistry.registerLog(filename);
                    }
                    case WOOD -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultWood());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultWood());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultWood());
                        minecraftLogs.value(namespace, name, type);
                        customLogs.computeIfAbsent(name, m -> new ArrayList<>()).add(namespace + ":" + filename);
                        blocksRegistry.registerWood(filename);
                    }
                    case PLANKS -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultPlanks());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultPlanks());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultPlanks());
                        minecraftPlanks.value(namespace, name, type);
                        blocksRegistry.registerPlanks(filename);
                    }
                    case FENCE -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultFence());
                        fileGenerator.generateModelBlock(namespace, filename + "_inventory", newModelBlockBuilder(namespace, name, type).defaultFenceInventory());
                        fileGenerator.generateModelBlock(namespace, filename + "_post", newModelBlockBuilder(namespace, name, type).defaultFencePost());
                        fileGenerator.generateModelBlock(namespace, filename + "_side", newModelBlockBuilder(namespace, name, type).defaultFenceSide());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultFence());
                        minecraftWoodenFences.value(namespace, name, type);
                        blocksRegistry.registerFence(filename);
                    }
                    case FENCE_GATE -> {
                        fileGenerator.generateBlockStates(namespace, filename, new BlockStateBuilder(namespace, name, type, BLOCK_STATES_TEMPLATE_PATH + type.getName() + JSONT_SUFFIX).defaultFenceGate());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultFenceGate());
                        fileGenerator.generateModelBlock(namespace, filename + "_open", newModelBlockBuilder(namespace, name, type, type.getName() + "_open").defaultFenceGateOpen());
                        fileGenerator.generateModelBlock(namespace, filename + "_wall", newModelBlockBuilder(namespace, name, type, type.getName() + "_wall").defaultFenceGateWall());
                        fileGenerator.generateModelBlock(namespace, filename + "_wall_open", newModelBlockBuilder(namespace, name, type, type.getName() + "_wall_open").defaultFenceGateWallOpen());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultFenceGate());
                        minecraftFenceGates.value(namespace, name, type);
                        blocksRegistry.registerFenceGate(filename);
                    }
                    default -> throw new UnsupportedOperationException("BlockType=[" + type + "] is not supported");
                }
            }
        }

        fileGenerator.generateTags(MINECRAFT, "logs", minecraftLogs);
        fileGenerator.generateTags(MINECRAFT, "logs_that_burn", minecraftLogs);
        fileGenerator.generateTags(MINECRAFT, "planks", minecraftPlanks);
        fileGenerator.generateTags(MINECRAFT, "wooden_fences", minecraftWoodenFences);
        fileGenerator.generateTags(MINECRAFT, "fence_gates", minecraftFenceGates);

        for (Entry<String, List<String>> entry : customLogs.entrySet()) {
            TagsBuilder customLogTags = new TagsBuilder(BLOCKS);
            for (String value : entry.getValue()) {
                customLogTags.value(value);
            }
            fileGenerator.generateTags(namespace, entry.getKey() + "_logs", customLogTags);
        }
    }

    private void handleItems(String namespace, Map<String, List<String>> items) throws IOException {
        TagsBuilder minecraftLogs = new TagsBuilder(ITEMS);
        TagsBuilder minecraftPlanks = new TagsBuilder(ITEMS);
        TagsBuilder minecraftWoodenFences = new TagsBuilder(ITEMS);
        TagsBuilder minecraftFenceGates = new TagsBuilder(ITEMS);
        Map<String, List<String>> customLogs = new HashMap<>();

        for (Entry<String, List<String>> entry : items.entrySet()) {
            ItemType type = ItemType.fromString(entry.getKey());
            for (String name : entry.getValue()) {
                String filename = name + "_" + type.getName();
                switch (type) {
                    case LOG -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultLog());
                        minecraftLogs.value(namespace, name, type);
                        customLogs.computeIfAbsent(name, m -> new ArrayList<>()).add(namespace + ":" + filename);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    case WOOD -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultWood());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultWood());
                        minecraftLogs.value(namespace, name, type);
                        customLogs.computeIfAbsent(name, m -> new ArrayList<>()).add(namespace + ":" + filename);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    case PLANKS -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultPlanks());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultPlanks());
                        minecraftPlanks.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    case FENCE -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultFence());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultFence());
                        minecraftWoodenFences.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    } case FENCE_GATE -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultFenceGate());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultFenceGate());
                        minecraftFenceGates.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    default -> throw new UnsupportedOperationException("BlockType=[" + type + "] is not supported");
                }
            }
        }

        fileGenerator.generateTags(MINECRAFT, "logs", minecraftLogs);
        fileGenerator.generateTags(MINECRAFT, "logs_that_burn", minecraftLogs);
        fileGenerator.generateTags(MINECRAFT, "planks", minecraftPlanks);
        fileGenerator.generateTags(MINECRAFT, "wooden_fences", minecraftWoodenFences);
        fileGenerator.generateTags(MINECRAFT, "fence_gates", minecraftFenceGates);

        for (Entry<String, List<String>> entry : customLogs.entrySet()) {
            TagsBuilder customLogTags = new TagsBuilder(ITEMS);
            for (String value : entry.getValue()) {
                customLogTags.value(value);
            }
            fileGenerator.generateTags(namespace, entry.getKey() + "_logs", customLogTags);
        }
    }
    
    private BlockStateBuilder newBlockStateBuilder(String namespace, String name, BlockType type) {
        return new BlockStateBuilder(namespace, name, type, BLOCK_STATES_TEMPLATE_PATH + type.getName() + JSONT_SUFFIX);
    }
    
    private ModelBlockBuilder newModelBlockBuilder(String namespace, String name, BlockType type) {
        return newModelBlockBuilder(namespace, name, type, type.getName());
    }

    private ModelBlockBuilder newModelBlockBuilder(String namespace, String name, BlockType type, String templateFileName) {
        return new ModelBlockBuilder(namespace, name, type, MODELS_BLOCK_TEMPLATE_PATH + templateFileName + JSONT_SUFFIX);
    }

    private ModelItemBuilder newModelItemBuilder(String namespace, String name, ItemType type) {
        return new ModelItemBuilder(namespace, name, type, MODELS_ITEM_TEMPLATE_PATH + type.getName() + JSONT_SUFFIX);
    }

    private BlockLootTableBuilder newBlockLootTableBuilder(String namespace, String name, BlockType type) {
        return new BlockLootTableBuilder(namespace, name, type, BLOCK_LOOTTABLES_TEMPLATE_PATH + type.getName() + JSONT_SUFFIX);
    }

    private RecipeBuilder newRecipeBuilder(String namespace, String name, ItemType type) {
        return new RecipeBuilder(namespace, name, type.getName(), RECIPES_TEMPLATE_PATH + type.getName() + JSONT_SUFFIX);
    }
}
