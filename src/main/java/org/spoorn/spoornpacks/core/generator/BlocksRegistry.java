package org.spoorn.spoornpacks.core.generator;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.spoorn.spoornpacks.block.SPChestBlock;
import org.spoorn.spoornpacks.block.SPCraftingTableBlock;
import org.spoorn.spoornpacks.block.SPSaplingBlock;
import org.spoorn.spoornpacks.block.sapling.SPSaplingGenerator;
import org.spoorn.spoornpacks.client.render.SPRenderLayers;
import org.spoorn.spoornpacks.entity.SPEntities;
import org.spoorn.spoornpacks.mixin.*;
import org.spoorn.spoornpacks.type.BlockType;

import java.util.HashMap;
import java.util.Map;

public class BlocksRegistry {
    

    private final String modid;
    
    public final Map<Identifier, Block> register = new HashMap<>();

    public BlocksRegistry(String modid) {
        this.modid = modid;
    }

    public Block registerLog(String id) {
        Block block = new PillarBlock(FabricBlockSettings.of(Material.WOOD).strength(2.0f).sounds(BlockSoundGroup.WOOD));
        return registerBlock(id, block);
    }

    public Block registerWood(String id) {
        Block block = new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD));
        return registerBlock(id, block);
    }

    public Block registerPlanks(String id) {
        Block block = new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS));
        return registerBlock(id, block);
    }

    // TODO: Allow creating leaves of different mapcolors
    public Block registerLeaves(String id) {
        /*Block block = new LeavesBlock(AbstractBlock.Settings.of(Material.LEAVES, color).strength(0.2f).ticksRandomly()
                .sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning(SPBlocks::canSpawnOnLeaves)
                .suffocates((state, world, pos) -> false).blockVision((state, world, pos) -> false));*/
        Block block = new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES));
        return registerBlock(id, block);
    }

    public Block registerSapling(String id, ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature) {
        Block block = new SPSaplingBlock(new SPSaplingGenerator(configuredFeature), FabricBlockSettings.copyOf(Blocks.OAK_SAPLING));
        return registerBlock(id, block);
    }

    public Block registerFlowerPot(String id, Block saplingBlock) {
        Block block = new FlowerPotBlock(saplingBlock, FabricBlockSettings.copyOf(Blocks.POTTED_OAK_SAPLING));
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            SPRenderLayers.registerRenderLayer(BlockType.SAPLING, block);
        }
        return registerBlock(id, block);
    }

    public Block registerFence(String id) {
        Block block = new FenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE));
        return registerBlock(id, block);
    }

    public Block registerFenceGate(String id) {
        Block block = new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE));
        return registerBlock(id, block);
    }

    public Block registerButton(String id) {
        Block block = WoodenButtonBlockAccessor.create(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON));
        return registerBlock(id, block);
    }

    public Block registerSlab(String id) {
        Block block = new SlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB));
        return registerBlock(id, block);
    }

    public Block registerPressurePlate(String id) {
        Block block = PressurePlateBlockAccessor.create(PressurePlateBlock.ActivationRule.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE));
        return registerBlock(id, block);
    }

    // TODO: Allow different types for the settings part of stairs block
    public Block registerStairs(String id, Block defaultBlock) {
        Block block = StairsBlockAccessor.create(defaultBlock.getDefaultState(), FabricBlockSettings.copyOf(defaultBlock));
        return registerBlock(id, block);
    }

    public Block registerTrapdoor(String id) {
        Block block = TrapdoorBlockAccessor.create(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE));
        return registerBlock(id, block);
    }

    public Block registerDoor(String id) {
        Block block = DoorBlockAccessor.create(FabricBlockSettings.copyOf(Blocks.OAK_DOOR));
        return registerBlock(id, block);
    }

    public Block registerCraftingTable(String id) {
        Block block = new SPCraftingTableBlock(FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE));
        return registerBlock(id, block);
    }
    
    public Block registerChest(String namespace, String name, String id, SPEntities spEntities) {
        // SPChestBlock has a reference to the block name, and actual identifier with _chest suffix
        Block block = new SPChestBlock(FabricBlockSettings.copyOf(Blocks.CHEST), namespace, name, spEntities);
        return registerBlock(id, block);
    }
    
    private Block registerBlock(String id, Block block) {
        Identifier identifier = new Identifier(this.modid, id);
        register.put(identifier, block);
        return Registry.register(Registry.BLOCK, identifier, block);
    }

    // Copied from vanilla
    public static Boolean canSpawnOnLeaves(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return type == EntityType.OCELOT || type == EntityType.PARROT;
    }
}
