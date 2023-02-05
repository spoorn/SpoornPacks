package org.spoorn.spoornpacks.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ButtonBlock;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ButtonBlock.class)
public interface ButtonBlockAccessor {

    @Invoker("<init>")
    static ButtonBlock create(AbstractBlock.Settings settings, int pressTicks, boolean wooden, SoundEvent clickOffSound, SoundEvent clickOnSound) {
        throw new Error("Mixin did not apply!");
    }
}