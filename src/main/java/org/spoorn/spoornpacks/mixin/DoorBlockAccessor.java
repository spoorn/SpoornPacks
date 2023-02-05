package org.spoorn.spoornpacks.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DoorBlock.class)
public interface DoorBlockAccessor {

    @Invoker("<init>")
    static DoorBlock create(AbstractBlock.Settings settings, SoundEvent closeSound, SoundEvent openSound) {
        throw new Error("Mixin did not apply!");
    }
}
