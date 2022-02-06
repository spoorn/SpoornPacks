package org.spoorn.spoornpacks.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.TrapdoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TrapdoorBlock.class)
public interface TrapdoorBlockAccessor {

    @Invoker("<init>")
    static TrapdoorBlock create(AbstractBlock.Settings settings) {
        throw new Error("Mixin did not apply!");
    }
}
