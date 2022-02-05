package org.spoorn.spoornpacks.generator;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

import java.util.HashMap;
import java.util.Map;

public class BlocksRegistry {

    private final String modid;
    final Map<Identifier, Block> register = new HashMap<>();

    public BlocksRegistry(String modid) {
        this.modid = modid;
    }

    public Block registerLog(String id) {
        Block block = new PillarBlock(FabricBlockSettings.of(Material.WOOD).strength(2.0f).sounds(BlockSoundGroup.WOOD));
        Identifier identifier = new Identifier(this.modid, id);
        register.put(identifier, block);
        return Registry.register(Registry.BLOCK, identifier, block);
    }

    public Block registerWood(String id) {
        Block block = new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD));
        Identifier identifier = new Identifier(this.modid, id);
        register.put(identifier, block);
        return Registry.register(Registry.BLOCK, identifier, block);
    }

    public Block registerPlanks(String id) {
        Block block = new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS));
        Identifier identifier = new Identifier(this.modid, id);
        register.put(identifier, block);
        return Registry.register(Registry.BLOCK, identifier, block);
    }

    public Boolean canSpawnOnLeaves(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return type == EntityType.OCELOT || type == EntityType.PARROT;
    }
}
