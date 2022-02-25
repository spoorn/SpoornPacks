package org.spoorn.spoornpacks.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spoorn.spoornpacks.mixin.FireBlockAccessor;
import org.spoorn.spoornpacks.type.BlockType;

public class SPFlammables {

    public static FireBlockAccessor FIRE_BLOCK = (FireBlockAccessor) Blocks.FIRE;
    
    public static void registerFlammable(BlockType type, Block block) {
        if (block != null) {
            switch (type) {
                case LOG -> {
                    registerLog(block);
                }
                case WOOD -> {
                    registerWood(block);
                }
                case PLANKS -> {
                    registerPlanks(block);
                }
                case SLAB -> {
                    registerSlab(block);
                }
                case STAIRS -> {
                    registerStairs(block);
                }
                case FENCE -> {
                    registerFence(block);
                }
                case FENCE_GATE -> {
                    registerFenceGate(block);
                }
                case LEAVES -> {
                    registerLeaves(block);
                }
                case SMALL_FLOWER, TALL_FLOWER -> {
                    registerSmallFlower(block);
                }
                default -> {}
            }
        }
    }

    private static void registerLog(Block block) {
        registerFlammableBlock(block, 5, 5);
    }

    private static void registerWood(Block block) {
        registerFlammableBlock(block, 5, 5);
    }

    private static void registerPlanks(Block block) {
        registerFlammableBlock(block, 5, 20);
    }

    private static void registerSlab(Block block) {
        registerFlammableBlock(block, 5, 20);
    }

    private static void registerStairs(Block block) {
        registerFlammableBlock(block, 5, 20);
    }

    private static void registerFence(Block block) {
        registerFlammableBlock(block, 5, 20);
    }

    private static void registerFenceGate(Block block) {
        registerFlammableBlock(block, 5, 20);
    }

    private static void registerLeaves(Block block) {
        registerFlammableBlock(block, 30, 60);
    }

    private static void registerSmallFlower(Block block) {
        registerFlammableBlock(block, 60, 100);
    }

    private static void registerFlammableBlock(Block block, int burnChance, int spreadChance) {
        FIRE_BLOCK.registerSPFlammable(block, burnChance, spreadChance);
    }
}
