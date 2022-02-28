package org.spoorn.spoornpacks.entity;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.tuple.Pair;
import org.spoorn.spoornpacks.api.entity.vehicle.SPMinecartEntityFactory;
import org.spoorn.spoornpacks.entity.boat.SPBoatEntity;
import org.spoorn.spoornpacks.entity.boat.SPBoatRegistry;
import org.spoorn.spoornpacks.entity.chest.SPChestBlockEntity;
import org.spoorn.spoornpacks.type.VehicleType;
import org.spoorn.spoornpacks.util.ClientSideUtils;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class SPEntities {
    
    public static final Map<Class<? extends ChestBlockEntity>, Pair<String, String>> CUSTOM_CHEST_BLOCK_ENTITY_CLASSES = new HashMap<>();

    private final Map<String, EntityType<SPBoatEntity>> boatEntities = new HashMap<>();
    private final Map<String, EntityType<? extends AbstractMinecartEntity>> customMinecartEntityTypes = new HashMap<>();
    private final Map<String, BlockEntityType<SPChestBlockEntity>> chestBlockEntities = new HashMap<>();
    private final Map<String, BlockEntityType<? extends ChestBlockEntity>> customChestBlockEntityTypes = new HashMap<>();
    private final Map<String, BlockEntityType<? extends BarrelBlockEntity>> customBarrelBlockEntityTypes = new HashMap<>();
    
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

    public <T extends AbstractMinecartEntity> EntityType<? extends AbstractMinecartEntity> registerCustomMinecart(
            String namespace, String name, VehicleType type, SPMinecartEntityFactory factory) {
        if (customMinecartEntityTypes.containsKey(namespace)) {
            return customMinecartEntityTypes.get(namespace);
        } else {
            // Builder configurations copied from vanilla
            EntityType<? extends AbstractMinecartEntity> entityType = registerEntity(namespace, name + type.getSuffix(),
                    FabricEntityTypeBuilder.<AbstractMinecartEntity>create().entityFactory(factory::create).dimensions(EntityDimensions.changing(0.98f, 0.7f)).trackRangeBlocks(8).build());
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                ClientSideUtils.registerEntityRendererFactory(entityType, ctx -> new MinecartEntityRenderer<>(ctx, EntityModelLayers.CHEST_MINECART));
            }
            customMinecartEntityTypes.put(namespace, entityType);
            return entityType;
        }
    }

    public <T extends BarrelBlockEntity> BlockEntityType<? extends BarrelBlockEntity> registerCustomBarrelBlockEntityType(String namespace, String name, Block block,
            FabricBlockEntityTypeBuilder.Factory<? extends T> blockEntityFactory) {
        if (customBarrelBlockEntityTypes.containsKey(namespace)) {
            return customBarrelBlockEntityTypes.get(namespace);
        } else {
            BlockEntityType<? extends BarrelBlockEntity> entityType = registerBlockEntity(namespace, name + "_barrel",
                    FabricBlockEntityTypeBuilder.create(blockEntityFactory, block).build());
            customBarrelBlockEntityTypes.put(namespace, entityType);
            return entityType;
        }
    }
    
    public <T extends ChestBlockEntity> BlockEntityType<? extends ChestBlockEntity> registerCustomChestBlockEntityType(String namespace, String name, Block block, 
            FabricBlockEntityTypeBuilder.Factory<? extends T> blockEntityFactory) {
        // Create a dummy BlockEntity to fetch the class
        Class<? extends ChestBlockEntity> customChestBlockEntityClass = blockEntityFactory.create(BlockPos.ORIGIN, Blocks.OAK_LOG.getDefaultState()).getClass();
        if (CUSTOM_CHEST_BLOCK_ENTITY_CLASSES.containsKey(customChestBlockEntityClass)) {
            throw new IllegalArgumentException("Already registered custom chest block entity " + customChestBlockEntityClass.getName());
        }
        CUSTOM_CHEST_BLOCK_ENTITY_CLASSES.put(customChestBlockEntityClass, Pair.of(namespace, name));
        if (customChestBlockEntityTypes.containsKey(namespace)) {
            return customChestBlockEntityTypes.get(namespace);
        } else {
            BlockEntityType<? extends ChestBlockEntity> entityType = registerBlockEntity(namespace, name + "_chest",
                    FabricBlockEntityTypeBuilder.create(blockEntityFactory, block).build());
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                ClientSideUtils.registerBlockEntityRendererFactory(entityType, ChestBlockEntityRenderer::new);
            }
            customChestBlockEntityTypes.put(namespace, entityType);
            return entityType;
        }
    }

    public BlockEntityType<SPChestBlockEntity> registerChestBlockEntityType(String namespace, String name, Block block) {
        if (chestBlockEntities.containsKey(namespace)) {
            return chestBlockEntities.get(namespace);
        } else {
            BlockEntityType<SPChestBlockEntity> entityType = registerBlockEntity(namespace, name + "_chest",
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
        log.info("Registering EntityType with namespace={}, name={}", namespace, id);
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(namespace, id), entityType);
    }

    private static <E extends BlockEntity, ET extends BlockEntityType<E>> ET registerBlockEntity(String namespace, String id, ET entityType) {
        log.info("Registering BlockEntityType with namespace={}, name={}", namespace, id);
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(namespace, id), entityType);
    }
}
