package org.spoorn.spoornpacks.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import org.spoorn.spoornpacks.client.render.SPRenderLayers;
import org.spoorn.spoornpacks.client.render.SPTexturedRenderLayers;
import org.spoorn.spoornpacks.entity.boat.SPBoatEntity;
import org.spoorn.spoornpacks.entity.boat.SPBoatEntityRenderer;
import org.spoorn.spoornpacks.entity.boat.SPBoatRegistry;
import org.spoorn.spoornpacks.mixin.EntityRenderersAccessor;
import org.spoorn.spoornpacks.type.BlockType;

/**
 * This utility houses any client-side features only.  By having this in a separate utility class outside the core
 * classes such as {@link org.spoorn.spoornpacks.core.generator.ResourceGenerator}, this indirection seems to prevent
 * crashes on server-only due to the @Environment(EnvType.CLIENT) annotations on certain classes.
 * 
 * This is a pretty nasty hack, but it works so hah
 */
public class ClientSideUtils {
    
    public static void registerBoatEntity(EntityType<SPBoatEntity> boatEntityType, SPBoatRegistry spBoatRegistry) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            EntityRenderersAccessor.registerEntityRenderer(boatEntityType, (ctx) -> new SPBoatEntityRenderer(spBoatRegistry, ctx));
        }
    }
    
    public static void registerRenderLayer(BlockType type, Block block) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            SPRenderLayers.registerRenderLayer(type, block);
        }
    }
    
    public static void registerTexturedRenderLayer(String namespace, String name) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            SPTexturedRenderLayers.registerChest(namespace, name);
        }
    }
}
