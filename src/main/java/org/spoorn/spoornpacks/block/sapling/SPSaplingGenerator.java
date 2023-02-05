package org.spoorn.spoornpacks.block.sapling;

import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class SPSaplingGenerator extends SaplingGenerator {

    private final RegistryKey<ConfiguredFeature<?, ?>> configuredFeature;

    public SPSaplingGenerator(RegistryKey<ConfiguredFeature<?, ?>> configuredFeature) {
        this.configuredFeature = configuredFeature;
    }

    @Override
    protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
        return configuredFeature;
    }
}
