package org.spoorn.spoornpacks.impl;

import lombok.Getter;
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

    private void registerBlock(BlockType type, String name) throws DuplicateNameException {
        validateName(name);
        String updatedName = name;
        String typeName = type.getName();
        switch (type) {
            case LOG, WOOD, PLANKS, FENCE -> {
                updatedName += "_" + typeName;
                validateUniqueBlock(updatedName);
            }
            default -> throw new IllegalArgumentException("BlockType=[" + type + "] is not valid");
        }

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
        switch (type) {
            case LOG, WOOD, PLANKS, FENCE -> {
                updatedName += "_" + typeName;
                validateUniqueItem(updatedName);
            }
            default -> throw new IllegalArgumentException("BlockType=[" + type + "] is not valid");
        }

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
