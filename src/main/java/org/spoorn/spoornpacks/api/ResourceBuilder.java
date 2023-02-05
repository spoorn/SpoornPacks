package org.spoorn.spoornpacks.api;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.spoorn.spoornpacks.api.entity.vehicle.SPMinecartEntityFactory;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;
import org.spoorn.spoornpacks.type.ResourceType;
import org.spoorn.spoornpacks.type.VehicleType;

/**
 * ResourceBuilder is used to define all the resources to generate.  This is the input to 
 * {@link org.spoorn.spoornpacks.core.generator.ResourceGenerator#generate(ResourceBuilder)}.
 * 
 * Most APIs will have a variant with a "name" parameter, and one without.  The variant without the "name" parameter
 * will use the defaultName set for this ResourceBuilder.
 * 
 * TODO: Allow specifying if block is wooden or not, for the minecraft tags
 */
public interface ResourceBuilder {
    
    // Default simple APIs
    
    ResourceBuilder addBlocks(BlockType... types);
    
    ResourceBuilder addItems(ItemType... types);

    ResourceBuilder addBlock(BlockType type);

    ResourceBuilder addBlock(BlockType type, String name);

    ResourceBuilder addItem(ItemType type);

    ResourceBuilder addItem(ItemType type, String name);


    // More customizable APIs

    /**
     * Allows providing a custom block for this block resource.
     * 
     * NOTE: Currently only supports BlockType CHEST, BARREL, and SHULKER_BOX
     * 
     * @param type BlockType
     * @param block Custom block to register into the Blocks registry
     * @param blockEntity Custom BlockEntity constructor factory for the custom Block
     * @param name Name of the resource
     */
    ResourceBuilder addBlock(BlockType type, String name, Block block, FabricBlockEntityTypeBuilder.Factory<? extends BlockEntity> blockEntity);
    
    // TODO: Support setting sapling namespace as well
    ResourceBuilder addLeavesWithSaplingOverride(String saplingName);
    ResourceBuilder addLeavesWithSaplingOverride(String name, String saplingName);

    ResourceBuilder addSapling(RegistryKey<ConfiguredFeature<?, ?>> configuredFeature);
    ResourceBuilder addSapling(String name, RegistryKey<ConfiguredFeature<?, ?>> configuredFeature);

    /**
     * Adds a small flower (e.g. Minecraft's poppy) block and item.  Tall flowers can be added via the default
     * {@link #addBlock(BlockType, String)} and {@link #addItem(ItemType, String)}.
     * 
     * @param suspiciousStewEffect Effect when combining the flower with a bowl to produce suspicious stew.
     *                             You can get these from {@link StatusEffect}.
     * @param effectDuration Effect duration in seconds
     */
    ResourceBuilder addSmallFlower(StatusEffect suspiciousStewEffect, int effectDuration);
    ResourceBuilder addSmallFlower(String name, StatusEffect suspiciousStewEffect, int effectDuration);

    /**
     * Add a minecart with a custom MinecartEntity factory.  This is meant for use cases where you have your own
     * {@link AbstractMinecartEntity} and want to register/generate resources for it.
     *
     * NOTE: Only supported minecartType at the moment is CHEST
     *
     * @param name Name of the minecart (excluding any type suffix such as "_chest_minecart")
     * @param factory An SPMinecartEntityFactory that contains factory methods for constructing the custom MinecartEntity.
     */
    ResourceBuilder addMinecart(VehicleType type, String name, SPMinecartEntityFactory factory);
    ResourceBuilder addMinecart(VehicleType type, SPMinecartEntityFactory factory);

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
