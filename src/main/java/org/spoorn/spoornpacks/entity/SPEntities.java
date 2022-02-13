package org.spoorn.spoornpacks.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spoorn.spoornpacks.entity.boat.SPBoatEntity;
import org.spoorn.spoornpacks.entity.boat.SPBoatRegistry;
import org.spoorn.spoornpacks.entity.chest.SPChestBlockEntity;
import org.spoorn.spoornpacks.mixin.BlockEntityRendererFactoriesAccessor;
import org.spoorn.spoornpacks.util.ClientSideUtils;

import java.util.HashMap;
import java.util.Map;

public class SPEntities {

    private final Map<String, EntityType<SPBoatEntity>> boatEntities = new HashMap<>();
    private final Map<String, BlockEntityType<SPChestBlockEntity>> chestBlockEntities = new HashMap<>();
    
    public SPEntities() {
        
    }
    
    public EntityType<SPBoatEntity> registerBoatEntityType(String namespace, SPBoatRegistry spBoatRegistry) {
        if (boatEntities.containsKey(namespace)) {
            return boatEntities.get(namespace);
        } else {
            EntityType<SPBoatEntity> entityType = registerEntity(namespace, "boat",
                    EntityType.Builder.<SPBoatEntity>create((entType, world) -> new SPBoatEntity(spBoatRegistry, entType, world), SpawnGroup.MISC)
                            .setDimensions(1.375F, 0.5625F).build(namespace + ":boat"));
            boatEntities.put(namespace, entityType);
            return entityType;
        }
    }

    public BlockEntityType<SPChestBlockEntity> registerChestBlockEntityType(String namespace, String name, Block block) {
        if (chestBlockEntities.containsKey(namespace)) {
            return chestBlockEntities.get(namespace);
        } else {
            BlockEntityType<SPChestBlockEntity> entityType = registerBlockEntity(namespace, "chest",
                    BlockEntityType.Builder.<SPChestBlockEntity>create((blockPos, blockState) -> new SPChestBlockEntity(namespace, name, this, blockPos, blockState), block)
                            .build(null));
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                ClientSideUtils.registerBlockEntityRendererFactory(entityType, ChestBlockEntityRenderer::new);
            }
            chestBlockEntities.put(namespace, entityType);
            return entityType;
        }
    }
    
    public EntityType<SPBoatEntity> getBoatEntityType(String namespace) {
        return this.boatEntities.get(namespace);
    }

    public BlockEntityType<SPChestBlockEntity> getChestBlockEntityType(String namespace) {
        return this.chestBlockEntities.get(namespace);
    }

    private static <E extends Entity, ET extends EntityType<E>> ET registerEntity(String namespace, String id, ET entityType) {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(namespace, id), entityType);
    }

    private static <E extends BlockEntity, ET extends BlockEntityType<E>> ET registerBlockEntity(String namespace, String id, ET entityType) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(namespace, id), entityType);
    }
}
