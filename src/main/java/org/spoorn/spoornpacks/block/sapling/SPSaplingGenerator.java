package org.spoorn.spoornpacks.block.sapling;

import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.Random;

public class SPSaplingGenerator extends SaplingGenerator {

    private final RegistryEntry<? extends ConfiguredFeature<?, ?>> configuredFeature;

    public SPSaplingGenerator(RegistryEntry<? extends ConfiguredFeature<?, ?>> configuredFeature) {
        this.configuredFeature = configuredFeature;
    }

    @Override
    protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
        return configuredFeature;
    }
}
