package org.spoorn.spoornpacks.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.spoornpacks.client.render.SPTexturedRenderLayers;
import org.spoorn.spoornpacks.entity.chest.SPChestBlockEntity;

import java.util.Map;
import java.util.function.Consumer;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {
    
    @Inject(method = "getChestTexture(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/enums/ChestType;Z)Lnet/minecraft/client/util/SpriteIdentifier;", 
            at = @At(value = "HEAD"), cancellable = true)
    private static void injectCustomChestTextures(BlockEntity blockEntity, ChestType type, boolean christmas, CallbackInfoReturnable<SpriteIdentifier> cir) {
        if (blockEntity instanceof SPChestBlockEntity spChestBlockEntity) {
            cir.setReturnValue(SPTexturedRenderLayers.getChest(spChestBlockEntity.namespace, spChestBlockEntity.name, type));
        }
    }
    
    @Inject(method = "addDefaultTextures", at = @At(value = "TAIL"))
    private static void injectDefaultTextures(Consumer<SpriteIdentifier> adder, CallbackInfo ci) {
        for (Map<ChestType, SpriteIdentifier> spriteIdentifiers : SPTexturedRenderLayers.TEXTURED_RENDER_LAYERS.values()) {
            for (SpriteIdentifier spriteIdentifier : spriteIdentifiers.values()) {
                adder.accept(spriteIdentifier);
            }
        }
    }
}
