package org.spoorn.spoornpacks.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.WoodenButtonBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WoodenButtonBlock.class)
public interface WoodenButtonBlockAccessor {

    @Invoker("<init>")
    static WoodenButtonBlock create(AbstractBlock.Settings settings) {
        throw new Error("Mixin did not apply!");
    }
}