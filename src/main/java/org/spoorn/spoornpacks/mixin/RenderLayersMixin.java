package org.spoorn.spoornpacks.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.spoornpacks.block.SPLeafPileBlock;

@Mixin(RenderLayers.class)
public class RenderLayersMixin {

    @Shadow private static boolean fancyGraphicsOrBetter;

    @Inject(method = "getBlockLayer", at = @At(value = "HEAD"), cancellable = true)
    private static void getLeafPileBlockLayer(BlockState state, CallbackInfoReturnable<RenderLayer> cir) {
        if (state.getBlock() instanceof SPLeafPileBlock) {
            cir.setReturnValue(fancyGraphicsOrBetter ? RenderLayer.getCutoutMipped() : RenderLayer.getSolid());
            cir.cancel();
        }
    }

    @Inject(method = "getMovingBlockLayer", at = @At(value = "HEAD"), cancellable = true)
    private static void getMovingLeafPileBlockLayer(BlockState state, CallbackInfoReturnable<RenderLayer> cir) {
        if (state.getBlock() instanceof SPLeafPileBlock) {
            cir.setReturnValue(fancyGraphicsOrBetter ? RenderLayer.getCutoutMipped() : RenderLayer.getSolid());
            cir.cancel();
        }
    }
}
