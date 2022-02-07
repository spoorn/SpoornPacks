package org.spoorn.spoornpacks.mixin;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderers.class)
public interface EntityRenderersAccessor {
    
    @Invoker("register")
    static <T extends Entity> void registerEntityRenderer(EntityType<? extends T> type, EntityRendererFactory<T> factory) {
        throw new Error("Mixin did not apply!");
    }
}
