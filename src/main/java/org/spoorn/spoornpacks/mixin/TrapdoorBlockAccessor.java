package org.spoorn.spoornpacks.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TrapdoorBlock.class)
public interface TrapdoorBlockAccessor {

    @Invoker("<init>")
    static TrapdoorBlock create(AbstractBlock.Settings settings, SoundEvent closeSound, SoundEvent openSound) {
        throw new Error("Mixin did not apply!");
    }
}
