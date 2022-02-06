package org.spoorn.spoornpacks.api;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.spoorn.spoornpacks.client.exception.DuplicateNameException;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;

public interface ResourceBuilder {

    ResourceBuilder addBlock(BlockType type) throws DuplicateNameException;

    ResourceBuilder addBlock(BlockType type, String name) throws DuplicateNameException;

    ResourceBuilder addItem(ItemType type) throws DuplicateNameException;

    ResourceBuilder addItem(ItemType type, String name) throws DuplicateNameException;
    
    
    // More customizable APIs

    // TODO: Support setting sapling namespace as well
    ResourceBuilder addLeavesWithSaplingOverride(BlockType type, String saplingName) throws DuplicateNameException;
    ResourceBuilder addLeavesWithSaplingOverride(BlockType type, String name, String saplingName) throws DuplicateNameException;

    ResourceBuilder addSapling(ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature) throws DuplicateNameException;
    ResourceBuilder addSapling(String name, ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature) throws DuplicateNameException;
}
