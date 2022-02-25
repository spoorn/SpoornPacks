package org.spoorn.spoornpacks.impl;

import lombok.Getter;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemGroup;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;
import org.spoorn.spoornpacks.type.ResourceType;

import java.util.*;

public class DefaultResourceBuilder implements ResourceBuilder {

    @Getter
    private final String namespace;
    private final String defaultName;
    @Getter
    private final ItemGroup itemGroup;

    // We make blocks a tree map so we can conveniently process Planks before Stairs.
    // This is because stair blocks depend on the plank blocks.  See StairBlock's constructor
    @Getter
    private final TreeMap<String, List<String>> blocks = new TreeMap<>();
    @Getter
    private final Map<String, List<String>> items = new HashMap<>();
    @Getter
    private final Map<String, String> leavesToSaplingOverrides = new HashMap<>();
    @Getter
    private final Map<String, ConfiguredFeature<? extends FeatureConfig, ?>> saplingConfiguredFeatures = new HashMap<>();
    @Getter
    private final Map<String, Map<ResourceType, ResourceProvider>> customResourceProviders = new HashMap<>();
    @Getter
    private final Map<Pair<BlockType, String>, Pair<Block, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity>>> customBlocksWithEntity = new HashMap<>();
    @Getter
    private final Map<String, Pair<StatusEffect, Integer>> flowerConfigs = new HashMap<>();

    private final Set<String> blockIds = new HashSet<>();
    private final Set<String> itemIds = new HashSet<>();

    public DefaultResourceBuilder(String namespace, String defaultName, ItemGroup itemGroup) {
        validateNamespace(namespace);
        this.namespace = namespace;
        this.defaultName = defaultName;
        this.itemGroup = itemGroup;
    }

    @Override
    public synchronized ResourceBuilder addBlocks(BlockType... types) {
        for (BlockType type : types) {
            addBlock(type);
        }
        return this;
    }

    @Override
    public synchronized ResourceBuilder addItems(ItemType... types) {
        for (ItemType type : types) {
            addItem(type);
        }
        return this;
    }

    @Override
    public synchronized ResourceBuilder addBlock(BlockType type) {
        if (type == BlockType.SAPLING) {
            throw new IllegalArgumentException("BlockType=SAPLING should be added via ResourceBuilder#addSapling");
        } else if (type == BlockType.SMALL_FLOWER) {
            throw new IllegalArgumentException("BlockType=SMALL_FLOWER should be added via ResourceBuilder#addSmallFlower");
        }
        return addBlock(type, this.defaultName);
    }

    @Override
    public synchronized ResourceBuilder addBlock(BlockType type, String name) {
        registerBlock(type, name);
        return this;
    }

    @Override
    public synchronized ResourceBuilder addItem(ItemType type) {
        return addItem(type, this.defaultName);
    }

    @Override
    public synchronized ResourceBuilder addItem(ItemType type, String name) {
        if (type == ItemType.SMALL_FLOWER) {
            throw new IllegalArgumentException("ItemType=SMALL_FLOWER should be added via ResourceBuilder#addSmallFlower");
        }
        registerItem(type, name);
        return this;
    }

    @Override
    public ResourceBuilder addBlock(BlockType type, String name, Block block, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> blockEntity) {
        if (block == null || blockEntity == null) {
            throw new IllegalArgumentException("Block and BlockEntity cannot be NULL");
        }
        if (type != BlockType.CHEST && type != BlockType.BARREL) {
            throw new IllegalArgumentException("BlockType=" + type + " is not supported for this addBlock operation");
        }
        addBlock(type, name);
        this.customBlocksWithEntity.put(Pair.of(type, name), Pair.of(block, blockEntity));
        return this;
    }

    @Override
    public synchronized ResourceBuilder addLeavesWithSaplingOverride(String saplingName) {
        return addLeavesWithSaplingOverride(this.defaultName, saplingName);
    }

    @Override
    public synchronized ResourceBuilder addLeavesWithSaplingOverride(String name, String saplingName) {
        registerBlock(BlockType.LEAVES, name);
        this.leavesToSaplingOverrides.put(name, saplingName);
        return this;
    }

    @Override
    public synchronized ResourceBuilder addSapling(ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature) {
        return addSapling(this.defaultName, configuredFeature);
    }

    @Override
    public synchronized ResourceBuilder addSapling(String name, ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature) {
        registerBlock(BlockType.SAPLING, name);
        this.saplingConfiguredFeatures.put(name, configuredFeature);
        return this;
    }

    @Override
    public ResourceBuilder addSmallFlower(StatusEffect suspiciousStewEffect, int effectDuration) {
        return addSmallFlower(this.defaultName, suspiciousStewEffect, effectDuration);
    }

    @Override
    public ResourceBuilder addSmallFlower(String name, StatusEffect suspiciousStewEffect, int effectDuration) {
        registerBlock(BlockType.SMALL_FLOWER, name);
        registerItem(ItemType.SMALL_FLOWER, name);
        this.flowerConfigs.put(name, Pair.of(suspiciousStewEffect, effectDuration));
        return this;
    }

    @Override
    public synchronized ResourceBuilder addCustomResourceProvider(String name, ResourceType resourceType, ResourceProvider resourceProvider) {
        if (resourceProvider == null) {
            throw new IllegalArgumentException("ResourceProvider cannot be null");
        } else {
            this.customResourceProviders.computeIfAbsent(name, m -> new HashMap<>()).put(resourceType, resourceProvider);
        }
        return this;
    }

    private synchronized void registerBlock(BlockType type, String name) {
        validateName(name);
        String updatedName = name;
        String typeName = type.getName();
        updatedName += "_" + typeName;
        validateUniqueBlock(updatedName);

        if (!this.blocks.containsKey(typeName)) {
            List<String> blockList = new ArrayList<>();
            blockList.add(name);
            this.blocks.put(typeName, blockList);
        } else {
            this.blocks.get(typeName).add(name);
        }
    }

    private synchronized void registerItem(ItemType type, String name) {
        validateName(name);
        String updatedName = name;
        String typeName = type.getName();
        updatedName += "_" + typeName;
        validateUniqueItem(updatedName);

        if (!this.items.containsKey(typeName)) {
            List<String> blockList = new ArrayList<>();
            blockList.add(name);
            this.items.put(typeName, blockList);
        } else {
            this.items.get(typeName).add(name);
        }
    }

    private void validateUniqueBlock(String name) {
        if (this.blockIds.contains(name)) {
            throw new IllegalArgumentException("Block with name=[" + name + "] was already added!");
        } else {
            this.blockIds.add(name);
        }
    }

    private void validateUniqueItem(String name) {
        if (this.itemIds.contains(name)) {
            throw new IllegalArgumentException("Item with name=[" + name + "] was already added!");
        } else {
            this.itemIds.add(name);
        }
    }

    private void validateNamespace(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            throw new IllegalArgumentException("namespace cannot be empty");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be empty");
        }
    }
}
