package org.spoorn.spoornpacks.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PressurePlateBlock.class)
public interface PressurePlateBlockAccessor {

    @Invoker("<init>")
    static PressurePlateBlock create(PressurePlateBlock.ActivationRule type, AbstractBlock.Settings settings, SoundEvent depressSound, SoundEvent pressSound) {
        throw new Error("Mixin did not apply!");
    }
}
