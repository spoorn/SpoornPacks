package org.spoorn.spoornpacks.impl;

import lombok.Getter;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.client.exception.DuplicateNameException;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;

import java.util.*;

public class DefaultResourceBuilder implements ResourceBuilder {

    @Getter
    private final String namespace;
    private final String defaultName;

    @Getter
    private final Map<String, List<String>> blocks = new HashMap<>();
    @Getter
    private final Map<String, List<String>> items = new HashMap<>();
    @Getter
    private final Map<String, String> leavesToSaplingOverrides = new HashMap<>();
    @Getter
    private final Map<String, ConfiguredFeature<? extends FeatureConfig, ?>> saplingConfiguredFeatures = new HashMap<>();

    private final Set<String> blockIds = new HashSet<>();
    private final Set<String> itemIds = new HashSet<>();

    public DefaultResourceBuilder(String namespace, String defaultName) {
        validateNamesapce(namespace);
        validateName(defaultName);
        this.namespace = namespace;
        this.defaultName = defaultName;
    }

    @Override
    public ResourceBuilder addBlock(BlockType type) throws DuplicateNameException {
        registerBlock(type, this.defaultName);
        return this;
    }

    @Override
    public ResourceBuilder addBlock(BlockType type, String name) throws DuplicateNameException {
        registerBlock(type, name);
        return this;
    }

    @Override
    public ResourceBuilder addItem(ItemType type) throws DuplicateNameException {
        registerItem(type, this.defaultName);
        return this;
    }

    @Override
    public ResourceBuilder addItem(ItemType type, String name) throws DuplicateNameException {
        registerItem(type, name);
        return this;
    }

    @Override
    public ResourceBuilder addLeavesWithSaplingOverride(String saplingName) throws DuplicateNameException {
        return addLeavesWithSaplingOverride(this.defaultName, saplingName);
    }

    @Override
    public ResourceBuilder addLeavesWithSaplingOverride(String name, String saplingName) throws DuplicateNameException {
        registerBlock(BlockType.LEAVES, name);
        this.leavesToSaplingOverrides.put(name, saplingName);
        return this;
    }

    @Override
    public ResourceBuilder addSapling(ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature) throws DuplicateNameException {
        return addSapling(this.defaultName, configuredFeature);
    }

    @Override
    public ResourceBuilder addSapling(String name, ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature) throws DuplicateNameException {
        registerBlock(BlockType.SAPLING, name);
        this.saplingConfiguredFeatures.put(name, configuredFeature);
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
        }
    }

    private void validateUniqueItem(String name) throws DuplicateNameException {
        if (this.itemIds.contains(name)) {
            throw new DuplicateNameException("Item with name=[" + name + "] was already added!");
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
