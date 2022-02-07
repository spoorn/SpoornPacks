package org.spoorn.spoornpacks.core.generator;

import lombok.extern.log4j.Log4j2;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
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
import java.util.*;
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
    private static final String POTTED_PREFIX = "potted_";

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
        log.info("Generating resources for {}", this.modid);
        if (!(resourceBuilder instanceof DefaultResourceBuilder)) {
            throw new UnsupportedOperationException("ResourceBuilder is unsupported!");
        }

        DefaultResourceBuilder drb = (DefaultResourceBuilder) resourceBuilder;

        // Blocks
        final TreeMap<String, List<String>> blocks = drb.getBlocks();
        
        try {
            handleBlocks(drb.getNamespace(), blocks, drb.getLeavesToSaplingOverrides(), drb.getSaplingConfiguredFeatures());
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
        log.info("Done generating some resources for {}!", this.modid);
        return gen;
    }

    private void handleBlocks(String namespace, TreeMap<String, List<String>> blocks, Map<String, String> leavesToSaplingOverrides,
                              Map<String, ConfiguredFeature<? extends FeatureConfig, ?>> saplingConfiguredFeatures) throws IOException {
        TagsBuilder minecraftLogs = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftPlanks = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftLeaves = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftSaplings = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftWoodenFences = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftFenceGates = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftFlowerPots = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftWoodenButtons = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftWoodenSlabs = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftWoodenPressurePlates = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftWoodenStairs = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftWoodenTrapdoors = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftWoodenDoors = new TagsBuilder(BLOCKS);
        TagsBuilder hoeMineable = new TagsBuilder(BLOCKS + "/mineable");
        TagsBuilder axeMineable = new TagsBuilder(BLOCKS + "/mineable");
        Map<String, List<String>> customLogs = new HashMap<>();

        // We make blocks a tree map so we can conveniently process Planks before Stairs.
        // This is because stair blocks depend on the plank blocks.  See StairBlock's constructor
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
                    case LEAVES -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultLeaves());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultLeaves());
                        if (leavesToSaplingOverrides.containsKey(name)) {
                            fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type)
                                    .leavesWithSapling(leavesToSaplingOverrides.get(name)));
                        } else {
                            fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultLeaves());
                        }
                        minecraftLeaves.value(namespace, name, type);
                        hoeMineable.value(namespace, name, type);
                        blocksRegistry.registerLeaves(filename);
                    }
                    case SAPLING -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultSapling());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultSapling());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultSapling());
                        minecraftSaplings.value(namespace, name, type);
                        blocksRegistry.registerSapling(filename, saplingConfiguredFeatures.get(name));
                        
                        // Sapling in flower pot
                        fileGenerator.generateBlockStates(namespace, POTTED_PREFIX + filename, newBlockStateBuilder(namespace, POTTED_PREFIX + name, type).defaultSapling());
                        // Model for sapling in a flower pot uses the regular sapling in the models/block and loot_table/blocks files, but uses potted_sapling.jsonT template file
                        fileGenerator.generateModelBlock(namespace, POTTED_PREFIX + filename, newModelBlockBuilder(namespace, name, type, POTTED_PREFIX + type.getName()).defaultPottedSapling());
                        fileGenerator.generateLootTable(namespace, POTTED_PREFIX + filename, newBlockLootTableBuilder(namespace, name, type, POTTED_PREFIX + type.getName()).defaultPottedSapling());
                        minecraftFlowerPots.value(namespace, POTTED_PREFIX + name, type);
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
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultFenceGate());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultFenceGate());
                        fileGenerator.generateModelBlock(namespace, filename + "_open", newModelBlockBuilder(namespace, name, type, type.getName() + "_open").defaultFenceGateOpen());
                        fileGenerator.generateModelBlock(namespace, filename + "_wall", newModelBlockBuilder(namespace, name, type, type.getName() + "_wall").defaultFenceGateWall());
                        fileGenerator.generateModelBlock(namespace, filename + "_wall_open", newModelBlockBuilder(namespace, name, type, type.getName() + "_wall_open").defaultFenceGateWallOpen());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultFenceGate());
                        minecraftFenceGates.value(namespace, name, type);
                        blocksRegistry.registerFenceGate(filename);
                    }
                    case BUTTON -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultButton());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultButton());
                        fileGenerator.generateModelBlock(namespace, filename + "_inventory", newModelBlockBuilder(namespace, name, type, type.getName() + "_inventory").defaultButtonInventory());
                        fileGenerator.generateModelBlock(namespace, filename + "_pressed", newModelBlockBuilder(namespace, name, type, type.getName() + "_pressed").defaultButtonPressed());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultButton());
                        minecraftWoodenButtons.value(namespace, name, type);
                        blocksRegistry.registerButton(filename);
                    }
                    case SLAB -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultSlab());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultSlab());
                        fileGenerator.generateModelBlock(namespace, filename + "_top", newModelBlockBuilder(namespace, name, type, type.getName() + "_top").defaultSlabTop());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultSlab());
                        minecraftWoodenSlabs.value(namespace, name, type);
                        blocksRegistry.registerSlab(filename);
                    }
                    case PRESSURE_PLATE -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultPressurePlate());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultPressurePlate());
                        fileGenerator.generateModelBlock(namespace, filename + "_down", newModelBlockBuilder(namespace, name, type, type.getName() + "_down").defaultPressurePlateDown());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultPressurePlate());
                        minecraftWoodenPressurePlates.value(namespace, name, type);
                        blocksRegistry.registerPressurePlate(filename);
                    }
                    case STAIRS -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultStairs());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultStairs());
                        fileGenerator.generateModelBlock(namespace, filename + "_inner", newModelBlockBuilder(namespace, name, type, type.getName() + "_inner").defaultStairsInner());
                        fileGenerator.generateModelBlock(namespace, filename + "_outer", newModelBlockBuilder(namespace, name, type, type.getName() + "_outer").defaultStairsOuter());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultStairs());
                        minecraftWoodenStairs.value(namespace, name, type);
                        blocksRegistry.registerStairs(filename, blocksRegistry.register.get(new Identifier(namespace, name + "_" + BlockType.PLANKS.getName())));
                    }
                    case TRAPDOOR -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultTrapdoor());
                        fileGenerator.generateModelBlock(namespace, filename + "_bottom", newModelBlockBuilder(namespace, name, type, type.getName() + "_bottom").defaultTrapdoorBottom());
                        fileGenerator.generateModelBlock(namespace, filename + "_open", newModelBlockBuilder(namespace, name, type, type.getName() + "_open").defaultTrapdoorOpen());
                        fileGenerator.generateModelBlock(namespace, filename + "_top", newModelBlockBuilder(namespace, name, type, type.getName() + "_top").defaultTrapdoorTop());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultTrapdoor());
                        minecraftWoodenTrapdoors.value(namespace, name, type);
                        blocksRegistry.registerTrapdoor(filename);
                    }
                    case DOOR -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultDoor());
                        fileGenerator.generateModelBlock(namespace, filename + "_bottom", newModelBlockBuilder(namespace, name, type, type.getName() + "_bottom").defaultDoorPart());
                        fileGenerator.generateModelBlock(namespace, filename + "_bottom_hinge", newModelBlockBuilder(namespace, name, type, type.getName() + "_bottom_hinge").defaultDoorPart());
                        fileGenerator.generateModelBlock(namespace, filename + "_top", newModelBlockBuilder(namespace, name, type, type.getName() + "_top").defaultDoorPart());
                        fileGenerator.generateModelBlock(namespace, filename + "_top_hinge", newModelBlockBuilder(namespace, name, type, type.getName() + "_top_hinge").defaultDoorPart());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultDoor());
                        minecraftWoodenDoors.value(namespace, name, type);
                        blocksRegistry.registerDoor(filename);
                    }
                    case CRAFTING_TABLE -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultCraftingTable());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultCraftingTable());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultCraftingTable());
                        axeMineable.value(namespace, name, type);
                        blocksRegistry.registerCraftingTable(filename);
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
        fileGenerator.generateTags(MINECRAFT, "leaves", minecraftLeaves);
        fileGenerator.generateTags(MINECRAFT, "saplings", minecraftSaplings);
        fileGenerator.generateTags(MINECRAFT, "flower_pots", minecraftFlowerPots);
        fileGenerator.generateTags(MINECRAFT, "wooden_buttons", minecraftWoodenButtons);
        fileGenerator.generateTags(MINECRAFT, "wooden_slabs", minecraftWoodenSlabs);
        fileGenerator.generateTags(MINECRAFT, "wooden_pressure_plates", minecraftWoodenPressurePlates);
        fileGenerator.generateTags(MINECRAFT, "wooden_stairs", minecraftWoodenStairs);
        fileGenerator.generateTags(MINECRAFT, "wooden_trapdoors", minecraftWoodenTrapdoors);
        fileGenerator.generateTags(MINECRAFT, "wooden_doors", minecraftWoodenDoors);
        fileGenerator.generateTags(MINECRAFT, "hoe", hoeMineable);
        fileGenerator.generateTags(MINECRAFT, "axe", axeMineable);

        // Custom logs are needed for some recipes, such as Planks recipes, which can be from any log of the same name
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
        TagsBuilder minecraftLeaves = new TagsBuilder(ITEMS);
        TagsBuilder minecraftSaplings = new TagsBuilder(ITEMS);
        TagsBuilder minecraftWoodenFences = new TagsBuilder(ITEMS);
        TagsBuilder minecraftFenceGates = new TagsBuilder(ITEMS);
        TagsBuilder minecraftWoodenButtons = new TagsBuilder(ITEMS);
        TagsBuilder minecraftWoodenSlabs = new TagsBuilder(ITEMS);
        TagsBuilder minecraftWoodenPressurePlates = new TagsBuilder(ITEMS);
        TagsBuilder minecraftWoodenStairs = new TagsBuilder(ITEMS);
        TagsBuilder minecraftWoodenTrapdoors = new TagsBuilder(ITEMS);
        TagsBuilder minecraftWoodenDoors = new TagsBuilder(ITEMS);
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
                    case LEAVES -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultLeaves());
                        minecraftLeaves.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    case SAPLING -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultSapling());
                        minecraftSaplings.value(namespace, name, type);
                        itemsRegistry.registerSaplingItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    case FENCE -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultFence());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultFence());
                        minecraftWoodenFences.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    } 
                    case FENCE_GATE -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultFenceGate());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultFenceGate());
                        minecraftFenceGates.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    case BUTTON -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultButton());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultButton());
                        // TODO: allow specifying if button is wooden or not
                        minecraftWoodenButtons.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    case SLAB -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultSlab());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultSlab());
                        minecraftWoodenSlabs.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    case PRESSURE_PLATE -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultPressurePlate());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultPressurePlate());
                        minecraftWoodenPressurePlates.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    case STAIRS -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultStairs());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultStairs());
                        minecraftWoodenStairs.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    case TRAPDOOR -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultTrapdoor());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultTrapdoor());
                        minecraftWoodenTrapdoors.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    case DOOR -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultDoor());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultDoor());
                        minecraftWoodenDoors.value(namespace, name, type);
                        itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)));
                    }
                    case CRAFTING_TABLE -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultCraftingTable());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultCraftingTable());
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
        fileGenerator.generateTags(MINECRAFT, "leaves", minecraftLeaves);
        fileGenerator.generateTags(MINECRAFT, "saplings", minecraftSaplings);
        fileGenerator.generateTags(MINECRAFT, "wooden_buttons", minecraftWoodenButtons);
        fileGenerator.generateTags(MINECRAFT, "wooden_slabs", minecraftWoodenSlabs);
        fileGenerator.generateTags(MINECRAFT, "wooden_pressure_plates", minecraftWoodenPressurePlates);
        fileGenerator.generateTags(MINECRAFT, "wooden_stairs", minecraftWoodenStairs);
        fileGenerator.generateTags(MINECRAFT, "wooden_trapdoors", minecraftWoodenTrapdoors);
        fileGenerator.generateTags(MINECRAFT, "wooden_doors", minecraftWoodenDoors);

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
        return newBlockLootTableBuilder(namespace, name, type, type.getName());
    }

    private BlockLootTableBuilder newBlockLootTableBuilder(String namespace, String name, BlockType type, String templateFileName) {
        return new BlockLootTableBuilder(namespace, name, type, BLOCK_LOOTTABLES_TEMPLATE_PATH + templateFileName + JSONT_SUFFIX);
    }

    private RecipeBuilder newRecipeBuilder(String namespace, String name, ItemType type) {
        return new RecipeBuilder(namespace, name, type.getName(), RECIPES_TEMPLATE_PATH + type.getName() + JSONT_SUFFIX);
    }
}
