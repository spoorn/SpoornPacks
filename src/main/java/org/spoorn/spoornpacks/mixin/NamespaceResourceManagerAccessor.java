package org.spoorn.spoornpacks.mixin;

import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Copy of {@link net.fabricmc.fabric.mixin.resource.loader.NamespaceResourceManagerAccessor}.
 * 
 * Copied to support Quilt.
 * 
 * TODO: Remove
 */
@Mixin(NamespaceResourceManager.class)
public interface NamespaceResourceManagerAccessor {
    
    @Accessor("type")
    ResourceType getType();

    @Invoker("getMetadataPath")
    static Identifier spoorn$accessor_getMetadataPath(Identifier id) {
        throw new UnsupportedOperationException("Invoker injection failed.");
    }
}
