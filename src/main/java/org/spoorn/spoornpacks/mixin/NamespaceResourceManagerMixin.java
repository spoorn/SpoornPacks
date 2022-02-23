package org.spoorn.spoornpacks.mixin;

import net.fabricmc.fabric.mixin.resource.loader.NamespaceResourceManagerAccessor;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spoorn.spoornpacks.core.SPGroupResourcePack;

import java.io.IOException;
import java.util.List;

/**
 * This intercepts ResourcePack loading to add multiple SPResourcePacks that have resources for the same Identifier,
 * such as tags for "minecraft".
 */
@Mixin(NamespaceResourceManager.class)
public class NamespaceResourceManagerMixin {

    @Shadow @Final protected List<ResourcePack> packList;
    private final ThreadLocal<List<Resource>> fabric$getAllResources$resources = new ThreadLocal<>();

    /**
     * Capture local variable resources List in {@link NamespaceResourceManager#getAllResources}.
     */
    @Inject(method = "getAllResources", at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/resource/NamespaceResourceManager;getMetadataPath(Lnet/minecraft/util/Identifier;)Lnet/minecraft/util/Identifier;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void onGetAllResources(Identifier id, CallbackInfoReturnable<List<Resource>> cir, List<Resource> resources) {
        this.fabric$getAllResources$resources.set(resources);
    }

    /**
     * Append our resources to the captured resources Array in {@link NamespaceResourceManager#getAllResources}.
     * 
     * This injects instead of Redirects to not conflict with Fabric's Redirect in {@link net.fabricmc.fabric.mixin.resource.loader.NamespaceResourceManagerMixin}.
     */
    @Inject(method = "getAllResources", at = @At(value = "RETURN"))
    private void onResourceAdd(Identifier id, CallbackInfoReturnable<List<Resource>> cir) throws IOException {
        for (ResourcePack pack : this.packList) {
            if (pack instanceof SPGroupResourcePack) {
                ((SPGroupResourcePack) pack).appendResources((NamespaceResourceManagerAccessor) this, id, this.fabric$getAllResources$resources.get());
            }
        }
    }
}
