package org.spoorn.spoornpacks.core.generator;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.block.SPFlammables;
import org.spoorn.spoornpacks.block.entity.SPFurnaceBlockFuelTimes;
import org.spoorn.spoornpacks.entity.SPEntities;
import org.spoorn.spoornpacks.entity.boat.SPBoatEntity;
import org.spoorn.spoornpacks.entity.boat.SPBoatRegistry;
import org.spoorn.spoornpacks.impl.DefaultResourceBuilder;
import org.spoorn.spoornpacks.impl.GeneratedResource;
import org.spoorn.spoornpacks.item.SPAxeItemModifier;
import org.spoorn.spoornpacks.provider.assets.BlockStateBuilder;
import org.spoorn.spoornpacks.provider.assets.ModelBlockBuilder;
import org.spoorn.spoornpacks.provider.assets.ModelItemBuilder;
import org.spoorn.spoornpacks.provider.data.BlockLootTableBuilder;
import org.spoorn.spoornpacks.provider.data.RecipeBuilder;
import org.spoorn.spoornpacks.provider.data.TagsBuilder;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;
import org.spoorn.spoornpacks.util.ClientSideUtils;

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

    private final String modid;
    private boolean overwrite;
    private final BlocksRegistry blocksRegistry;
    private final ItemsRegistry itemsRegistry;
    private final SPBoatRegistry spBoatRegistry = new SPBoatRegistry();
    private final SPEntities spEntities = new SPEntities();

    public ResourceGenerator(String modid, boolean overwrite) {
        this.modid = modid;
        this.overwrite = overwrite;
        this.blocksRegistry = new BlocksRegistry(modid);
        this.itemsRegistry = new ItemsRegistry(modid);
    }

    public synchronized Resource generate(ResourceBuilder resourceBuilder) {
        log.info("Generating resources for {}", this.modid);
        if (!(resourceBuilder instanceof DefaultResourceBuilder)) {
            throw new UnsupportedOperationException("ResourceBuilder is unsupported!");
        }

        DefaultResourceBuilder drb = (DefaultResourceBuilder) resourceBuilder;

        // Blocks
        final Map<BlockType, Map<String, Block>> generatedBlocks = new HashMap<>();
        final Map<ItemType, Map<String, Item>> generatedItems = new HashMap<>();
        FileGenerator fileGenerator = new FileGenerator(modid, this.overwrite, drb.getCustomResourceProviders());
        this.overwrite = false;  // Only overwrite on the first generate for a single ResourceGenerator instance

        try {
            handleBlocks(generatedBlocks, drb, fileGenerator);
        } catch (IOException e) {
            log.error("Could not generate resources for blocks", e);
            throw new RuntimeException(e);
        }

        // Items
        final Map<String, List<String>> items = drb.getItems();
        try {
            handleItems(generatedItems, drb.getNamespace(), items, drb.getItemGroup(), fileGenerator);
        } catch (IOException e) {
            log.error("Could not generate resources for items", e);
            throw new RuntimeException(e);
        }

        GeneratedResource gen = new GeneratedResource(drb.getNamespace(), generatedBlocks, generatedItems);
        log.info("Done generating some resources for {}!", this.modid);
        return gen;
    }

    private void handleBlocks(Map<BlockType, Map<String, Block>> generatedBlocks, DefaultResourceBuilder drb, 
                              FileGenerator fileGenerator) throws IOException {
        String namespace = drb.getNamespace();
        TreeMap<String, List<String>> blocks = drb.getBlocks();
        Map<String, String> leavesToSaplingOverrides = drb.getLeavesToSaplingOverrides();
        Map<String, ConfiguredFeature<? extends FeatureConfig, ?>> saplingConfiguredFeatures = drb.getSaplingConfiguredFeatures();
        Map<Pair<BlockType, String>, Pair<Block, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity>>> customBlocksWithEntity
                = drb.getCustomBlocksWithEntity();
        Map<String, Pair<StatusEffect, Integer>> flowerConfigs = drb.getFlowerConfigs();
        
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
        TagsBuilder minecraftFeaturesCannotReplace = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftLavaPoolStoneCannotReplace = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftGuardedByPiglins = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftSmallFlowers = new TagsBuilder(BLOCKS);
        TagsBuilder minecraftTallFlowers = new TagsBuilder(BLOCKS);
        TagsBuilder hoeMineable = new TagsBuilder(BLOCKS + "/mineable");
        TagsBuilder axeMineable = new TagsBuilder(BLOCKS + "/mineable");
        Map<String, List<String>> customLogs = new HashMap<>();
        List<Pair<String, Block>> strippedBlocks = new ArrayList<>();

        // We make blocks a tree map so we can conveniently process Planks before Stairs.
        // This is because stair blocks depend on the plank blocks.  See StairBlock's constructor
        for (Entry<String, List<String>> entry : blocks.entrySet()) {
            BlockType type = BlockType.fromString(entry.getKey());
            for (String name : entry.getValue()) {
                String filename = type.getPrefix() + name + type.getSuffix();
                Block block = null;
                
                Pair<Block, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity>> customBlockWithEntity =
                        customBlocksWithEntity.get(Pair.of(type, name));
                if (customBlockWithEntity != null) {
                    block = customBlockWithEntity.getLeft();
                    blocksRegistry.registerBlockIfAbsent(filename, block);
                }
                switch (type) {
                    case LOG -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultLog());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultLog());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultLog());
                        minecraftLogs.value(namespace, name, type);
                        customLogs.computeIfAbsent(name, m -> new ArrayList<>()).add(namespace + ":" + filename);
                        if (block == null)  block = blocksRegistry.registerLog(filename);
                    }
                    case WOOD -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultWood());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultWood());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultWood());
                        minecraftLogs.value(namespace, name, type);
                        customLogs.computeIfAbsent(name, m -> new ArrayList<>()).add(namespace + ":" + filename);
                        if (block == null)  block = blocksRegistry.registerWood(filename);
                    }
                    case PLANKS -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultPlanks());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultPlanks());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultPlanks());
                        minecraftPlanks.value(namespace, name, type);
                        if (block == null)  block = blocksRegistry.registerPlanks(filename);
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
                        if (block == null)  block = blocksRegistry.registerLeaves(filename);
                    }
                    case SAPLING -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultSapling());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultSapling());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultSapling());
                        minecraftSaplings.value(namespace, name, type);
                        if (block == null)  block = blocksRegistry.registerSapling(filename, saplingConfiguredFeatures.get(name));
                        
                        // Sapling in flower pot
                        fileGenerator.generateBlockStates(namespace, POTTED_PREFIX + filename, newBlockStateBuilder(namespace, POTTED_PREFIX + name, type).defaultSapling());
                        // Model for sapling in a flower pot uses the regular sapling in the models/block and loot_table/blocks files, but uses potted_sapling.jsonT template file
                        fileGenerator.generateModelBlock(namespace, POTTED_PREFIX + filename, newModelBlockBuilder(namespace, name, type, POTTED_PREFIX + type.getName()).defaultPottedSapling());
                        fileGenerator.generateLootTable(namespace, POTTED_PREFIX + filename, newBlockLootTableBuilder(namespace, name, type, POTTED_PREFIX + type.getName()).defaultPottedSapling());
                        minecraftFlowerPots.value(namespace, POTTED_PREFIX + name, type);
                        Block flowerBlock = blocksRegistry.registerFlowerPot(POTTED_PREFIX + filename, block);
                        generatedBlocks.computeIfAbsent(type, m -> new HashMap<>()).put(POTTED_PREFIX + name, flowerBlock);
                    }
                    case FENCE -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultFence());
                        fileGenerator.generateModelBlock(namespace, filename + "_inventory", newModelBlockBuilder(namespace, name, type).defaultFenceInventory());
                        fileGenerator.generateModelBlock(namespace, filename + "_post", newModelBlockBuilder(namespace, name, type).defaultFencePost());
                        fileGenerator.generateModelBlock(namespace, filename + "_side", newModelBlockBuilder(namespace, name, type).defaultFenceSide());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultFence());
                        minecraftWoodenFences.value(namespace, name, type);
                        if (block == null)  block = blocksRegistry.registerFence(filename);
                    }
                    case FENCE_GATE -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultFenceGate());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultFenceGate());
                        fileGenerator.generateModelBlock(namespace, filename + "_open", newModelBlockBuilder(namespace, name, type, type.getName() + "_open").defaultFenceGateOpen());
                        fileGenerator.generateModelBlock(namespace, filename + "_wall", newModelBlockBuilder(namespace, name, type, type.getName() + "_wall").defaultFenceGateWall());
                        fileGenerator.generateModelBlock(namespace, filename + "_wall_open", newModelBlockBuilder(namespace, name, type, type.getName() + "_wall_open").defaultFenceGateWallOpen());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultFenceGate());
                        minecraftFenceGates.value(namespace, name, type);
                        if (block == null)  block = blocksRegistry.registerFenceGate(filename);
                    }
                    case BUTTON -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultButton());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultButton());
                        fileGenerator.generateModelBlock(namespace, filename + "_inventory", newModelBlockBuilder(namespace, name, type, type.getName() + "_inventory").defaultButtonInventory());
                        fileGenerator.generateModelBlock(namespace, filename + "_pressed", newModelBlockBuilder(namespace, name, type, type.getName() + "_pressed").defaultButtonPressed());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultButton());
                        minecraftWoodenButtons.value(namespace, name, type);
                        if (block == null)  block = blocksRegistry.registerButton(filename);
                    }
                    case SLAB -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultSlab());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultSlab());
                        fileGenerator.generateModelBlock(namespace, filename + "_top", newModelBlockBuilder(namespace, name, type, type.getName() + "_top").defaultSlabTop());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultSlab());
                        minecraftWoodenSlabs.value(namespace, name, type);
                        if (block == null)  block = blocksRegistry.registerSlab(filename);
                    }
                    case PRESSURE_PLATE -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultPressurePlate());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultPressurePlate());
                        fileGenerator.generateModelBlock(namespace, filename + "_down", newModelBlockBuilder(namespace, name, type, type.getName() + "_down").defaultPressurePlateDown());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultPressurePlate());
                        minecraftWoodenPressurePlates.value(namespace, name, type);
                        if (block == null)  block = blocksRegistry.registerPressurePlate(filename);
                    }
                    case STAIRS -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultStairs());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultStairs());
                        fileGenerator.generateModelBlock(namespace, filename + "_inner", newModelBlockBuilder(namespace, name, type, type.getName() + "_inner").defaultStairsInner());
                        fileGenerator.generateModelBlock(namespace, filename + "_outer", newModelBlockBuilder(namespace, name, type, type.getName() + "_outer").defaultStairsOuter());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultStairs());
                        minecraftWoodenStairs.value(namespace, name, type);
                        if (block == null)  block = blocksRegistry.registerStairs(filename, blocksRegistry.register.get(new Identifier(namespace, name + "_" + BlockType.PLANKS.getName())));
                    }
                    case TRAPDOOR -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultTrapdoor());
                        fileGenerator.generateModelBlock(namespace, filename + "_bottom", newModelBlockBuilder(namespace, name, type, type.getName() + "_bottom").defaultTrapdoorBottom());
                        fileGenerator.generateModelBlock(namespace, filename + "_open", newModelBlockBuilder(namespace, name, type, type.getName() + "_open").defaultTrapdoorOpen());
                        fileGenerator.generateModelBlock(namespace, filename + "_top", newModelBlockBuilder(namespace, name, type, type.getName() + "_top").defaultTrapdoorTop());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultTrapdoor());
                        minecraftWoodenTrapdoors.value(namespace, name, type);
                        if (block == null)  block = blocksRegistry.registerTrapdoor(filename);
                    }
                    case DOOR -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultDoor());
                        fileGenerator.generateModelBlock(namespace, filename + "_bottom", newModelBlockBuilder(namespace, name, type, type.getName() + "_bottom").defaultDoorPart());
                        fileGenerator.generateModelBlock(namespace, filename + "_bottom_hinge", newModelBlockBuilder(namespace, name, type, type.getName() + "_bottom_hinge").defaultDoorPart());
                        fileGenerator.generateModelBlock(namespace, filename + "_top", newModelBlockBuilder(namespace, name, type, type.getName() + "_top").defaultDoorPart());
                        fileGenerator.generateModelBlock(namespace, filename + "_top_hinge", newModelBlockBuilder(namespace, name, type, type.getName() + "_top_hinge").defaultDoorPart());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultDoor());
                        minecraftWoodenDoors.value(namespace, name, type);
                        if (block == null)  block = blocksRegistry.registerDoor(filename);
                    }
                    case CRAFTING_TABLE -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultCraftingTable());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultCraftingTable());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultCraftingTable());
                        axeMineable.value(namespace, name, type);
                        if (block == null)  block = blocksRegistry.registerCraftingTable(filename);
                    }
                    case STRIPPED_LOG -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultStrippedLog());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultStrippedLog());
                        fileGenerator.generateModelBlock(namespace, filename + "_horizontal", newModelBlockBuilder(namespace, name, type, type.getName() + "_horizontal").defaultStrippedLog());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultStrippedLog());
                        minecraftLogs.value(namespace + ":" + filename);
                        customLogs.computeIfAbsent(name, m -> new ArrayList<>()).add(namespace + ":" + filename);
                        if (block == null)  block = blocksRegistry.registerLog(filename);
                        strippedBlocks.add(Pair.of(name + type.getSuffix(), block));
                    }
                    case STRIPPED_WOOD -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultStrippedWood());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultStrippedWood());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultStrippedWood());
                        minecraftLogs.value(namespace + ":" + filename);
                        customLogs.computeIfAbsent(name, m -> new ArrayList<>()).add(namespace + ":" + filename);
                        if (block == null)  block = blocksRegistry.registerLog(filename);
                        strippedBlocks.add(Pair.of(name + type.getSuffix(), block));
                    }
                    case CHEST -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultChest());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultChest());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultChest());
                        if (block == null)  block = blocksRegistry.registerChest(namespace, name, filename, this.spEntities);
                        if (customBlockWithEntity == null) {
                            this.spEntities.registerChestBlockEntityType(namespace, name, block);
                        } else {
                            this.spEntities.registerCustomChestBlockEntityType(namespace, name, block, 
                                    (FabricBlockEntityTypeBuilder.Factory<? extends ChestBlockEntity>) customBlockWithEntity.getRight());
                        }
                        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                            ClientSideUtils.registerTexturedRenderLayer(namespace, name);
                        }
                        axeMineable.value(namespace, name, type);
                        minecraftFeaturesCannotReplace.value(namespace, name, type);
                        minecraftLavaPoolStoneCannotReplace.value(namespace, name, type);
                        minecraftGuardedByPiglins.value(namespace, name, type);
                    }
                    case BARREL -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultBarrel());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultBarrel());
                        fileGenerator.generateModelBlock(namespace, filename + "_open", newModelBlockBuilder(namespace, name, type).defaultBarrelOpen());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultBarrel());
                        if (block == null)  block = blocksRegistry.registerBarrel(filename);
                        if (customBlockWithEntity != null) {
                            this.spEntities.registerCustomBarrelBlockEntityType(namespace, name, block,
                                    (FabricBlockEntityTypeBuilder.Factory<? extends BarrelBlockEntity>) customBlockWithEntity.getRight());
                        }
                        axeMineable.value(namespace, name, type);
                        minecraftGuardedByPiglins.value(namespace, name, type);
                    }
                    case SMALL_FLOWER -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultSmallFlower());
                        fileGenerator.generateModelBlock(namespace, filename, newModelBlockBuilder(namespace, name, type).defaultSmallFlower());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultSmallFlower());
                        // Potted small flower
                        fileGenerator.generateBlockStates(namespace, POTTED_PREFIX + filename, newBlockStateBuilder(namespace, POTTED_PREFIX + name, type, POTTED_PREFIX + type.getName()).defaultSmallFlower());
                        fileGenerator.generateModelBlock(namespace, POTTED_PREFIX + filename, newModelBlockBuilder(namespace, name, type, POTTED_PREFIX + type.getName()).defaultSmallFlower());
                        fileGenerator.generateLootTable(namespace, POTTED_PREFIX + filename, newBlockLootTableBuilder(namespace, name, type, POTTED_PREFIX + type.getName()).defaultSmallFlower());
                        Pair<StatusEffect, Integer> flowerConfig = flowerConfigs.get(name);
                        if (block == null)  block = blocksRegistry.registerSmallFlower(filename, flowerConfig.getLeft(), flowerConfig.getRight());
                        minecraftSmallFlowers.value(namespace + ":" + name);
                        Block pottedBlock = blocksRegistry.registerFlowerPot(POTTED_PREFIX + filename, block);
                        generatedBlocks.computeIfAbsent(type, m -> new HashMap<>()).put(POTTED_PREFIX + name, pottedBlock);
                    }
                    case TALL_FLOWER -> {
                        fileGenerator.generateBlockStates(namespace, filename, newBlockStateBuilder(namespace, name, type).defaultTallFlower());
                        fileGenerator.generateModelBlock(namespace, filename + "_bottom", newModelBlockBuilder(namespace, name, type, type.getName() + "_bottom").defaultTallFlowerBottom());
                        fileGenerator.generateModelBlock(namespace, filename + "_top", newModelBlockBuilder(namespace, name, type, type.getName() + "_top").defaultTallFlowerTop());
                        fileGenerator.generateLootTable(namespace, filename, newBlockLootTableBuilder(namespace, name, type).defaultTallFlower());
                        if (block == null)  block = blocksRegistry.registerTallFlower(filename);
                        minecraftTallFlowers.value(namespace + ":" + name);
                    }
                    default -> throw new UnsupportedOperationException("BlockType=[" + type + "] is not supported");
                }

                // Flammables, Render Layers, etc.
                SPFlammables.registerFlammable(type, block);
                if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                    ClientSideUtils.registerRenderLayer(type, block);
                }
                generatedBlocks.computeIfAbsent(type, m -> new HashMap<>()).put(name, block);
            }
        }

        // Do this outside the loops so unstripped blocks are available in the blocksRegistry
        SPAxeItemModifier.registerStrippedLog(namespace, strippedBlocks, blocksRegistry);

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
        fileGenerator.generateTags(MINECRAFT, "features_cannot_replace", minecraftFeaturesCannotReplace);
        fileGenerator.generateTags(MINECRAFT, "lava_pool_stone_cannot_replace", minecraftLavaPoolStoneCannotReplace);
        fileGenerator.generateTags(MINECRAFT, "guarded_by_piglins", minecraftGuardedByPiglins);
        fileGenerator.generateTags(MINECRAFT, "small_flowers", minecraftSmallFlowers);
        fileGenerator.generateTags(MINECRAFT, "tall_flowers", minecraftTallFlowers);
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

    private void handleItems(Map<ItemType, Map<String, Item>> generatedItems, String namespace, 
                             Map<String, List<String>> items, ItemGroup itemGroup, FileGenerator fileGenerator) throws IOException {
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
        TagsBuilder minecraftBoats = new TagsBuilder(ITEMS);
        TagsBuilder minecraftSmallFlowers = new TagsBuilder(ITEMS);
        TagsBuilder minecraftTallFlowers = new TagsBuilder(ITEMS);
        Map<String, List<String>> customLogs = new HashMap<>();

        for (Entry<String, List<String>> entry : items.entrySet()) {
            ItemType type = ItemType.fromString(entry.getKey());
            for (String name : entry.getValue()) {
                String filename = type.getPrefix() + name + type.getSuffix();
                Item item;
                switch (type) {
                    case LOG -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultLog());
                        minecraftLogs.value(namespace, name, type);
                        customLogs.computeIfAbsent(name, m -> new ArrayList<>()).add(namespace + ":" + filename);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case WOOD -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultWood());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultWood());
                        minecraftLogs.value(namespace, name, type);
                        customLogs.computeIfAbsent(name, m -> new ArrayList<>()).add(namespace + ":" + filename);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case PLANKS -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultPlanks());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultPlanks());
                        minecraftPlanks.value(namespace, name, type);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case LEAVES -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultLeaves());
                        minecraftLeaves.value(namespace, name, type);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case SAPLING -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultSapling());
                        minecraftSaplings.value(namespace, name, type);
                        item = itemsRegistry.registerSaplingItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case FENCE -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultFence());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultFence());
                        minecraftWoodenFences.value(namespace, name, type);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    } 
                    case FENCE_GATE -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultFenceGate());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultFenceGate());
                        minecraftFenceGates.value(namespace, name, type);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case BUTTON -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultButton());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultButton());
                        // TODO: allow specifying if button is wooden or not
                        minecraftWoodenButtons.value(namespace, name, type);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case SLAB -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultSlab());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultSlab());
                        minecraftWoodenSlabs.value(namespace, name, type);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case PRESSURE_PLATE -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultPressurePlate());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultPressurePlate());
                        minecraftWoodenPressurePlates.value(namespace, name, type);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case STAIRS -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultStairs());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultStairs());
                        minecraftWoodenStairs.value(namespace, name, type);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case TRAPDOOR -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultTrapdoor());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultTrapdoor());
                        minecraftWoodenTrapdoors.value(namespace, name, type);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case DOOR -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultDoor());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultDoor());
                        minecraftWoodenDoors.value(namespace, name, type);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case CRAFTING_TABLE -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultCraftingTable());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultCraftingTable());
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case BOAT -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultBoat());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultBoat());
                        minecraftBoats.value(namespace, name, type);
                        SPBoatRegistry.BoatType boatType = this.spBoatRegistry.registerBoat(namespace, name);
                        item = itemsRegistry.registerBoatItem(filename, this.spBoatRegistry, boatType, this.spEntities, itemGroup);
                        EntityType<SPBoatEntity> boatEntityType = this.spEntities.registerBoatEntityType(namespace, this.spBoatRegistry);
                        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                            ClientSideUtils.registerBoatEntity(boatEntityType, spBoatRegistry);
                        }
                    }
                    case STRIPPED_LOG -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultStrippedLog());
                        minecraftLogs.value(namespace + ":" + filename);
                        customLogs.computeIfAbsent(name, m -> new ArrayList<>()).add(namespace + ":" + filename);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case STRIPPED_WOOD -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultStrippedWood());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultStrippedWood());
                        minecraftLogs.value(namespace + ":" + filename);
                        customLogs.computeIfAbsent(name, m -> new ArrayList<>()).add(namespace + ":" + filename);
                        item = itemsRegistry.registerBlockItem(filename, blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case CHEST -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultChest());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultChest());
                        item = itemsRegistry.registerBlockItem(filename,  blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case BARREL -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultBarrel());
                        fileGenerator.generateRecipe(namespace, filename, newRecipeBuilder(namespace, name, type).defaultBarrel());
                        item = itemsRegistry.registerBlockItem(filename,  blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                    }
                    case SMALL_FLOWER -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultSmallFlower());
                        item = itemsRegistry.registerBlockItem(filename,  blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                        minecraftSmallFlowers.value(namespace + ":" + name);
                    }
                    case TALL_FLOWER -> {
                        fileGenerator.generateModelItem(namespace, filename, newModelItemBuilder(namespace, name, type).defaultTallFlower());
                        item = itemsRegistry.registerBlockItem(filename,  blocksRegistry.register.get(new Identifier(namespace, filename)), itemGroup);
                        minecraftTallFlowers.value(namespace + ":" + name);
                    }
                    default -> throw new UnsupportedOperationException("BlockType=[" + type + "] is not supported");
                }

                // Register furnace fuel times for items
                SPFurnaceBlockFuelTimes.registerFurnaceBlockFuelTime(type, item);
                generatedItems.computeIfAbsent(type, m -> new HashMap<>()).put(name, item);
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
        fileGenerator.generateTags(MINECRAFT, "boats", minecraftBoats);
        fileGenerator.generateTags(MINECRAFT, "small_flowers", minecraftSmallFlowers);
        fileGenerator.generateTags(MINECRAFT, "tall_flowers", minecraftTallFlowers);

        for (Entry<String, List<String>> entry : customLogs.entrySet()) {
            TagsBuilder customLogTags = new TagsBuilder(ITEMS);
            for (String value : entry.getValue()) {
                customLogTags.value(value);
            }
            fileGenerator.generateTags(namespace, entry.getKey() + "_logs", customLogTags);
        }
    }
    
    public static BlockStateBuilder newBlockStateBuilder(String namespace, String name, BlockType type) {
        return newBlockStateBuilder(namespace, name, type, type.getName());
    }

    public static BlockStateBuilder newBlockStateBuilder(String namespace, String name, BlockType type, String templateFileName) {
        return new BlockStateBuilder(namespace, name, type, BLOCK_STATES_TEMPLATE_PATH + templateFileName + JSONT_SUFFIX);
    }
    
    public static ModelBlockBuilder newModelBlockBuilder(String namespace, String name, BlockType type) {
        return newModelBlockBuilder(namespace, name, type, type.getName());
    }

    private static ModelBlockBuilder newModelBlockBuilder(String namespace, String name, BlockType type, String templateFileName) {
        return new ModelBlockBuilder(namespace, name, type, MODELS_BLOCK_TEMPLATE_PATH + templateFileName + JSONT_SUFFIX);
    }

    public static ModelItemBuilder newModelItemBuilder(String namespace, String name, ItemType type) {
        return new ModelItemBuilder(namespace, name, type, MODELS_ITEM_TEMPLATE_PATH + type.getName() + JSONT_SUFFIX);
    }

    public static BlockLootTableBuilder newBlockLootTableBuilder(String namespace, String name, BlockType type) {
        return newBlockLootTableBuilder(namespace, name, type, type.getName());
    }

    private static BlockLootTableBuilder newBlockLootTableBuilder(String namespace, String name, BlockType type, String templateFileName) {
        return new BlockLootTableBuilder(namespace, name, type, BLOCK_LOOTTABLES_TEMPLATE_PATH + templateFileName + JSONT_SUFFIX);
    }

    public static RecipeBuilder newRecipeBuilder(String namespace, String name, ItemType type) {
        return new RecipeBuilder(namespace, name, type, RECIPES_TEMPLATE_PATH + type.getName() + JSONT_SUFFIX);
    }
}
