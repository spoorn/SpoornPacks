package org.spoorn.spoornpacks.api;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.spoorn.spoornpacks.exception.DuplicateNameException;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;
import org.spoorn.spoornpacks.type.ResourceType;

/**
 * 
 * TODO: Allow specifying if block is wooden or not, for the minecraft tags
 */
public interface ResourceBuilder {
    
    // Default simple APIs
    
    ResourceBuilder addBlocks(BlockType... types) throws DuplicateNameException;
    
    ResourceBuilder addItems(ItemType... types) throws DuplicateNameException;

    ResourceBuilder addBlock(BlockType type) throws DuplicateNameException;

    ResourceBuilder addBlock(BlockType type, String name) throws DuplicateNameException;

    ResourceBuilder addItem(ItemType type) throws DuplicateNameException;

    ResourceBuilder addItem(ItemType type, String name) throws DuplicateNameException;
    
    
    // More customizable APIs

    // TODO: Support setting sapling namespace as well
    ResourceBuilder addLeavesWithSaplingOverride(String saplingName) throws DuplicateNameException;
    ResourceBuilder addLeavesWithSaplingOverride(String name, String saplingName) throws DuplicateNameException;

    ResourceBuilder addSapling(ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature) throws DuplicateNameException;
    ResourceBuilder addSapling(String name, ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature) throws DuplicateNameException;

    /**
     * Adds a custom {@link ResourceProvider} for a resource's {@link ResourceType}.  Will NOT apply if the resource was
     * never added.
     * 
     * Note: Logs will not generate a recipe even if you specify a custom ResourceProvider, as logs typically do not have a recipe.
     * 
     * @param fullName Full name of the resouce (e.g. red_blossom_log, stripped_red_blossom_wood)
     * @param resourceType Resource Type
     * @param resourceProvider Custom ResourceProvider
     * @return ResourceBuilder with the added resource provider and block
     */
    ResourceBuilder addCustomResourceProvider(String fullName, ResourceType resourceType, ResourceProvider resourceProvider);
}
