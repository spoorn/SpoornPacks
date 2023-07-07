package org.spoorn.spoornpacks.core.generator;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.spoorn.spoornpacks.block.SPChestBlock;
import org.spoorn.spoornpacks.block.SPCraftingTableBlock;
import org.spoorn.spoornpacks.block.SPLeafPileBlock;
import org.spoorn.spoornpacks.block.SPSaplingBlock;
import org.spoorn.spoornpacks.block.sapling.SPSaplingGenerator;
import org.spoorn.spoornpacks.entity.SPEntities;
import org.spoorn.spoornpacks.mixin.*;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.util.ClientSideUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlocksRegistry {
    
    public static Set<ChestBlock> CUSTOM_CHESTS = new HashSet<>();
    public static Set<ShulkerBoxBlock> CUSTOM_SHULKER_BOXES = new HashSet<>();

    private final String modid;
    
    public final Map<Identifier, Block> register = new HashMap<>();

    public BlocksRegistry(String modid) {
        this.modid = modid;
    }

    public Block registerLog(String id) {
        Block block = new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG));
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

    public Block registerSapling(String id, RegistryKey<ConfiguredFeature<?, ?>> configuredFeature) {
        Block block = new SPSaplingBlock(new SPSaplingGenerator(configuredFeature), FabricBlockSettings.copyOf(Blocks.OAK_SAPLING));
        return registerBlock(id, block);
    }

    public Block registerFlowerPot(String id, Block flowerBlock) {
        Block block;
        boolean isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
        if (flowerBlock instanceof SaplingBlock) {
            block = new FlowerPotBlock(flowerBlock, FabricBlockSettings.copyOf(Blocks.POTTED_OAK_SAPLING));
            if (isClient) {
                ClientSideUtils.registerRenderLayer(BlockType.SAPLING, block);
            }
        } else if (flowerBlock instanceof FlowerBlock) {
            block = new FlowerPotBlock(flowerBlock, FabricBlockSettings.copyOf(Blocks.POTTED_POPPY));
            if (isClient) {
                ClientSideUtils.registerRenderLayer(BlockType.SMALL_FLOWER, block);
            }
        } else {
            throw new UnsupportedOperationException("Block " + flowerBlock + " for FlowerPotBlock is not supported");
        }
        return registerBlock(id, block);
    }

    public Block registerFence(String id) {
        Block block = new FenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE));
        return registerBlock(id, block);
    }

    public Block registerFenceGate(String id) {
        Block block = new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE), WoodType.OAK);
        return registerBlock(id, block);
    }

    public Block registerButton(String id) {
        Block block = new ButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON), BlockSetType.OAK, 30, true);
        return registerBlock(id, block);
    }

    public Block registerSlab(String id) {
        Block block = new SlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB));
        return registerBlock(id, block);
    }

    public Block registerPressurePlate(String id) {
        Block block = new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE), BlockSetType.OAK);
        return registerBlock(id, block);
    }

    // TODO: Allow different types for the settings part of stairs block
    public Block registerStairs(String id, Block defaultBlock) {
        Block block = new StairsBlock(defaultBlock.getDefaultState(), FabricBlockSettings.copyOf(defaultBlock));
        return registerBlock(id, block);
    }

    public Block registerTrapdoor(String id) {
        Block block = new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE), BlockSetType.OAK);
        return registerBlock(id, block);
    }

    public Block registerDoor(String id) {
        Block block = new DoorBlock(FabricBlockSettings.copyOf(Blocks.OAK_DOOR), BlockSetType.OAK);
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

    public Block registerBarrel(String id) {
        Block block = new BarrelBlock(FabricBlockSettings.copyOf(Blocks.BARREL));
        return registerBlock(id, block);
    }
    
    public Block registerSmallFlower(String id, StatusEffect suspiciousStewEffect, int effectDuration) {
        Block block = new FlowerBlock(suspiciousStewEffect, effectDuration, FabricBlockSettings.copyOf(Blocks.POPPY));
        return registerBlock(id, block);
    }
    
    public Block registerTallFlower(String id) {
        Block block = new TallFlowerBlock(FabricBlockSettings.copyOf(Blocks.LILAC));
        return registerBlock(id, block);
    }
    
    public Block registerShulkerBox(String id) {
        Block block = new ShulkerBoxBlock(null, FabricBlockSettings.copyOf(Blocks.SHULKER_BOX));
        return registerBlock(id, block);
    }

    public Block registerLeafPileBlock(String id) {
        Block block = new SPLeafPileBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES));
        return registerBlock(id, block);
    }

    public Block registerBlockIfAbsent(String id, Block block) {
        Identifier identifier = new Identifier(this.modid, id);
        register.put(identifier, block);
        
        if (block instanceof ChestBlock) {
            CUSTOM_CHESTS.add((ChestBlock) block);
        }
        
        if (block instanceof ShulkerBoxBlock) {
            CUSTOM_SHULKER_BOXES.add((ShulkerBoxBlock) block);
        }
        
        if (!Registries.BLOCK.containsId(identifier)) {
            return Registry.register(Registries.BLOCK, identifier, block);
        } else {
            return block;
        }
    }
    
    private Block registerBlock(String id, Block block) {
        Identifier identifier = new Identifier(this.modid, id);
        register.put(identifier, block);
        return Registry.register(Registries.BLOCK, identifier, block);
    }
}
