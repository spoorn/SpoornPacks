package org.spoorn.spoornpacks.impl;

import lombok.Getter;
import net.minecraft.item.ItemGroup;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.exception.DuplicateNameException;
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

    private final Set<String> blockIds = new HashSet<>();
    private final Set<String> itemIds = new HashSet<>();

    public DefaultResourceBuilder(String namespace, String defaultName, ItemGroup itemGroup) {
        validateNamesapce(namespace);
        validateName(defaultName);
        this.namespace = namespace;
        this.defaultName = defaultName;
        this.itemGroup = itemGroup;
    }

    @Override
    public synchronized ResourceBuilder addBlocks(BlockType... types) throws DuplicateNameException {
        for (BlockType type : types) {
            addBlock(type);
        }
        return this;
    }

    @Override
    public synchronized ResourceBuilder addItems(ItemType... types) throws DuplicateNameException {
        for (ItemType type : types) {
            addItem(type);
        }
        return this;
    }

    @Override
    public synchronized ResourceBuilder addBlock(BlockType type) throws DuplicateNameException {
        if (type == BlockType.SAPLING) {
            throw new IllegalArgumentException("BlockType=SAPLING should be added via #addSapling");
        }
        return addBlock(type, this.defaultName);
    }

    @Override
    public synchronized ResourceBuilder addBlock(BlockType type, String name) throws DuplicateNameException {
        registerBlock(type, name);
        return this;
    }

    @Override
    public synchronized ResourceBuilder addItem(ItemType type) throws DuplicateNameException {
        return addItem(type, this.defaultName);
    }

    @Override
    public synchronized ResourceBuilder addItem(ItemType type, String name) throws DuplicateNameException {
        registerItem(type, name);
        return this;
    }

    @Override
    public synchronized ResourceBuilder addLeavesWithSaplingOverride(String saplingName) throws DuplicateNameException {
        return addLeavesWithSaplingOverride(this.defaultName, saplingName);
    }

    @Override
    public synchronized ResourceBuilder addLeavesWithSaplingOverride(String name, String saplingName) throws DuplicateNameException {
        registerBlock(BlockType.LEAVES, name);
        this.leavesToSaplingOverrides.put(name, saplingName);
        return this;
    }

    @Override
    public synchronized ResourceBuilder addSapling(ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature) throws DuplicateNameException {
        return addSapling(this.defaultName, configuredFeature);
    }

    @Override
    public synchronized ResourceBuilder addSapling(String name, ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature) throws DuplicateNameException {
        registerBlock(BlockType.SAPLING, name);
        this.saplingConfiguredFeatures.put(name, configuredFeature);
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

    private void registerBlock(BlockType type, String name) throws DuplicateNameException {
        validateName(name);
        String updatedName = name;
        String typeName = type.getName();
        updatedName += "_" + typeName;
        validateUniqueBlock(updatedName);
        /*switch (type) {
            case LOG, WOOD, PLANKS, FENCE, FENCE_GATE -> {
                updatedName += "_" + typeName;
                validateUniqueBlock(updatedName);
            }
            default -> throw new IllegalArgumentException("BlockType=[" + type + "] is not valid");
        }*/

        if (!this.blocks.containsKey(typeName)) {
            List<String> blockList = new ArrayList<>();
            blockList.add(name);
            this.blocks.put(typeName, blockList);
        } else {
            this.blocks.get(typeName).add(name);
        }
    }

    private void registerItem(ItemType type, String name) throws DuplicateNameException {
        validateName(name);
        String updatedName = name;
        String typeName = type.getName();
        updatedName += "_" + typeName;
        validateUniqueItem(updatedName);
        /*switch (type) {
            case LOG, WOOD, PLANKS, FENCE, FENCE_GATE -> {
                updatedName += "_" + typeName;
                validateUniqueItem(updatedName);
            }
            default -> throw new IllegalArgumentException("ItemType=[" + type + "] is not valid");
        }*/

        if (!this.items.containsKey(typeName)) {
            List<String> blockList = new ArrayList<>();
            blockList.add(name);
            this.items.put(typeName, blockList);
        } else {
            this.items.get(typeName).add(name);
        }
    }

    private void validateUniqueBlock(String name) throws DuplicateNameException {
        if (this.blockIds.contains(name)) {
            throw new DuplicateNameException("Block with name=[" + name + "] was already added!");
        } else {
            this.blockIds.add(name);
        }
    }

    private void validateUniqueItem(String name) throws DuplicateNameException {
        if (this.itemIds.contains(name)) {
            throw new DuplicateNameException("Item with name=[" + name + "] was already added!");
        } else {
            this.itemIds.add(name);
        }
    }

    private void validateNamesapce(String namespace) {
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
