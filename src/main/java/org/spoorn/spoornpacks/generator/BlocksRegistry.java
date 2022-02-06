package org.spoorn.spoornpacks.generator;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.spoorn.spoornpacks.block.SPSaplingBlock;
import org.spoorn.spoornpacks.block.sapling.SPSaplingGenerator;
import org.spoorn.spoornpacks.mixin.PressurePlateBlockAccessor;
import org.spoorn.spoornpacks.mixin.WoodenButtonBlockAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlocksRegistry {

    public static final List<Block> SAPLINGS = new ArrayList<>();
    public static final List<Block> POTTED_BLOCKS = new ArrayList<>();
    public static final List<Block> DOOR_BLOCKS = new ArrayList<>();
    public static final List<Block> FENCES = new ArrayList<>();
    public static final List<Block> FENCE_GATES = new ArrayList<>();
    public static final List<Block> TRAPDOORS = new ArrayList<>();

    private final String modid;
    final Map<Identifier, Block> register = new HashMap<>();

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
        SAPLINGS.add(block);
        registerFlowerPot("potted_" + id, block);
        return registerBlock(id, block);
    }

    public void registerFlowerPot(String id, Block saplingBlock) {
        Block block = new FlowerPotBlock(saplingBlock, FabricBlockSettings.copyOf(Blocks.POTTED_OAK_SAPLING));
        POTTED_BLOCKS.add(registerBlock(id, block));
    }

    public Block registerFence(String id) {
        Block block = new FenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE));
        FENCES.add(block);
        return registerBlock(id, block);
    }

    public Block registerFenceGate(String id) {
        Block block = new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE));
        FENCE_GATES.add(block);
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
