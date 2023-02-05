package org.spoorn.spoornpacks.impl;

import lombok.Getter;
import lombok.NonNull;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.apache.commons.lang3.tuple.Pair;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.api.entity.vehicle.SPMinecartEntityFactory;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;
import org.spoorn.spoornpacks.type.ResourceType;
import org.spoorn.spoornpacks.type.VehicleType;

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
    private final Map<String, List<String>> vehicles = new HashMap<>();
    @Getter
    private final Map<String, String> leavesToSaplingOverrides = new HashMap<>();
    @Getter
    private final Map<String, RegistryKey<ConfiguredFeature<?, ?>>> saplingConfiguredFeatures = new HashMap<>();
    @Getter
    private final Map<String, Map<ResourceType, ResourceProvider>> customResourceProviders = new HashMap<>();
    @Getter
    private final Map<Pair<BlockType, String>, Pair<Block, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity>>> customBlocksWithEntity = new HashMap<>();
    @Getter
    private final Map<String, Pair<StatusEffect, Integer>> flowerConfigs = new HashMap<>();
    @Getter
    private final Map<Pair<VehicleType, String>, SPMinecartEntityFactory> minecartConfigs = new HashMap<>();

    private final Set<String> blockIds = new HashSet<>();
    private final Set<String> itemIds = new HashSet<>();
    private final Set<String> vehicleIds = new HashSet<>();

    public DefaultResourceBuilder(String namespace, String defaultName, ItemGroup itemGroup) {
        validateNamespace(namespace);
        this.namespace = namespace;
        this.defaultName = defaultName;
        if (itemGroup == null) {
            this.itemGroup = ItemGroups.TOOLS;
        } else {
            this.itemGroup = itemGroup;
        }
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
        } else if (type == BlockType.SHULKER_BOX) {
            throw new UnsupportedOperationException("BlockType=SHULKER_BOX is not yet supported for the default addBlock.  " +
                    "Please use the addBlock() with a custom Block and BlockEntity Factory");
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
        if (type != BlockType.CHEST && type != BlockType.BARREL && type != BlockType.SHULKER_BOX) {
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
    public synchronized ResourceBuilder addSapling(RegistryKey<ConfiguredFeature<?, ?>> configuredFeature) {
        return addSapling(this.defaultName, configuredFeature);
    }

    @Override
    public synchronized ResourceBuilder addSapling(String name, RegistryKey<ConfiguredFeature<?, ?>> configuredFeature) {
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
    public ResourceBuilder addMinecart(VehicleType type, String name, @NonNull SPMinecartEntityFactory factory) {
        if (type != VehicleType.CHEST_MINECART) {
            throw new UnsupportedOperationException("VehicleType=" + type + " is not supported.  Only CHEST_MINECART is supported!");
        }
        if (factory.getVanillaMinecartEntityType() != AbstractMinecartEntity.Type.CHEST) {
            throw new UnsupportedOperationException("AbstractMinecartEntity type=" + factory.getVanillaMinecartEntityType() + " is not supported.  Only CHEST type is supported!");
        }
        registerVehicle(type, name);
        this.minecartConfigs.put(Pair.of(type, name), factory);
        return this;
    }

    @Override
    public ResourceBuilder addMinecart(VehicleType type, @NonNull SPMinecartEntityFactory factory) {
        return addMinecart(type, this.defaultName, factory);
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
        String typeName = type.getName();
        String updatedName = type.getPrefix() + name + type.getSuffix();
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
        String typeName = type.getName();
        String updatedName = type.getPrefix() + name + type.getSuffix();
        validateUniqueItem(updatedName);

        if (!this.items.containsKey(typeName)) {
            List<String> itemList = new ArrayList<>();
            itemList.add(name);
            this.items.put(typeName, itemList);
        } else {
            this.items.get(typeName).add(name);
        }
    }

    private synchronized void registerVehicle(VehicleType type, String name) {
        validateName(name);
        String typeName = type.getName();
        String updatedName = type.getPrefix() + name + type.getSuffix();
        validateUniqueItem(updatedName);

        if (!this.vehicles.containsKey(typeName)) {
            List<String> vehicleList = new ArrayList<>();
            vehicleList.add(name);
            this.vehicles.put(typeName, vehicleList);
        } else {
            this.vehicles.get(typeName).add(name);
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

    private void validateUniqueVehicle(String name) {
        if (this.vehicleIds.contains(name)) {
            throw new IllegalArgumentException("Vehicle with name=[" + name + "] was already added!");
        } else {
            this.vehicleIds.add(name);
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
