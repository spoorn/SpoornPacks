package org.spoorn.spoornpacks.util;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.spoorn.spoornpacks.client.render.SPRenderLayers;
import org.spoorn.spoornpacks.client.render.SPTexturedRenderLayers;
import org.spoorn.spoornpacks.entity.boat.SPBoatEntity;
import org.spoorn.spoornpacks.entity.boat.SPBoatEntityRenderer;
import org.spoorn.spoornpacks.entity.boat.SPBoatRegistry;
import org.spoorn.spoornpacks.mixin.BlockEntityRendererFactoriesAccessor;
import org.spoorn.spoornpacks.mixin.EntityRenderersAccessor;
import org.spoorn.spoornpacks.type.BlockType;

/**
 * This utility houses any client-side features only.  By having this in a separate utility class outside the core
 * classes such as {@link org.spoorn.spoornpacks.core.generator.ResourceGenerator}, this indirection seems to prevent
 * crashes on server-only due to the @Environment(EnvType.CLIENT) annotations on certain classes.
 * 
 * Any caller of this ClientSideUtils will need to check that this is actually on the client side.
 * 
 * This is a pretty nasty hack, but it works so hah
 */
public class ClientSideUtils {
    
    public static void registerBoatEntity(EntityType<SPBoatEntity> boatEntityType, SPBoatRegistry spBoatRegistry) {
        EntityRenderersAccessor.registerEntityRenderer(boatEntityType, (ctx) -> new SPBoatEntityRenderer(spBoatRegistry, ctx));
    }
    
    public static void registerRenderLayer(BlockType type, Block block) {
        SPRenderLayers.registerRenderLayer(type, block);
    }
    
    public static void registerChestTexturedRenderLayer(String namespace, String name) {
        SPTexturedRenderLayers.registerChest(namespace, name);
    }

    public static void registerShulkerTexturedRenderLayer(String namespace, String name) {
        SPTexturedRenderLayers.registerShulkerBox(namespace, name);
    }
    
    public static <T extends Entity> void registerEntityRendererFactory(EntityType<? extends T> entityType, EntityRendererFactory<T> factory) {
        EntityRenderersAccessor.registerEntityRenderer(entityType, factory);
    }
    
    public static <T extends AbstractMinecartEntity> void registerMinecartEntityRendererFactory(EntityType<? extends AbstractMinecartEntity> entityType) {
        registerEntityRendererFactory(entityType, ctx -> new MinecartEntityRenderer<>(ctx, EntityModelLayers.CHEST_MINECART));
    }
    
    public static <T extends BlockEntity> void registerBlockEntityRendererFactory(BlockEntityType<? extends T> entityType, BlockEntityRendererFactory<T> factory) {
        BlockEntityRendererFactoriesAccessor.register(entityType, factory);
    }
}
