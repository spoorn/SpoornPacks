package org.spoorn.spoornpacks.mixin;

import static org.spoorn.spoornpacks.entity.SPEntities.CUSTOM_CHEST_BLOCK_ENTITY_CLASSES;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    
    private static final Logger log = LogManager.getLogger("TexturedRenderLayersMixin");
    
    @Inject(method = "getChestTextureId(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/enums/ChestType;Z)Lnet/minecraft/client/util/SpriteIdentifier;", 
            at = @At(value = "HEAD"), cancellable = true)
    private static void injectCustomChestTextures(BlockEntity blockEntity, ChestType type, boolean christmas, CallbackInfoReturnable<SpriteIdentifier> cir) {
        if (blockEntity == null) {
            return;
        }
        
        String namespace = null;
        String name = null;
        
        if (blockEntity instanceof SPChestBlockEntity spChestBlockEntity) {
            namespace = spChestBlockEntity.namespace;
            name = spChestBlockEntity.name;
        } else if (CUSTOM_CHEST_BLOCK_ENTITY_CLASSES.containsKey(blockEntity.getClass())) {
            Pair<String, String> pair = CUSTOM_CHEST_BLOCK_ENTITY_CLASSES.get(blockEntity.getClass());
            namespace = pair.getLeft();
            name = pair.getRight();
        }
        
        if (namespace != null && name != null) {
            cir.setReturnValue(SPTexturedRenderLayers.getChest(namespace, name, type));
        }
    }
    
    @Inject(method = "addDefaultTextures", at = @At(value = "TAIL"))
    private static void injectDefaultTextures(Consumer<SpriteIdentifier> adder, CallbackInfo ci) {
        for (Map<ChestType, SpriteIdentifier> spriteIdentifiers : SPTexturedRenderLayers.TEXTURED_RENDER_LAYERS.values()) {
            for (SpriteIdentifier spriteIdentifier : spriteIdentifiers.values()) {
                log.info("Injecting SpriteIdentifier={} into default textures", spriteIdentifier.getTextureId());
                adder.accept(spriteIdentifier);
            }
        }
        
        for (Map<Identifier, SpriteIdentifier> spriteIdentifiers : SPTexturedRenderLayers.STANDARD_TEXTURED_RENDER_LAYERS.values()) {
            for (SpriteIdentifier spriteIdentifier : spriteIdentifiers.values()) {
                log.info("Injecting SpriteIdentifier={} into default textures", spriteIdentifier.getTextureId());
                adder.accept(spriteIdentifier);
            }
        }
    }
}
