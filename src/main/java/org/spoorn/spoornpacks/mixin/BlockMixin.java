package org.spoorn.spoornpacks.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.spoornpacks.block.SPLeafPileBlock;

@Mixin(Block.class)
public class BlockMixin {
    
    @Inject(method = "cannotConnect", at = @At(value = "HEAD"))
    private static void cannotConnectLeafPiles(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof SPLeafPileBlock) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
