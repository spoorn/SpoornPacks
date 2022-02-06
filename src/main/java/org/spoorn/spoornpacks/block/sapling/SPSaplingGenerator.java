package org.spoorn.spoornpacks.block.sapling;

import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SPSaplingGenerator extends SaplingGenerator {

    private final ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature;

    public SPSaplingGenerator(ConfiguredFeature<? extends FeatureConfig, ?> configuredFeature) {
        this.configuredFeature = configuredFeature;
    }

    @Nullable
    @Override
    protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
        return configuredFeature;
    }
}
