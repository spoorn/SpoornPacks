package org.spoorn.spoornpacks.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Add items such as saplings to the cutout render layers map in vanilla minecraft.
 */
@Mixin(RenderLayers.class)
public interface RenderLayersAccessor {

    @Accessor("BLOCKS")
    static Map<Block, RenderLayer> getBlocks() {
        throw new RuntimeException("[SpoornPink] Could not get BLOCKS from RenderLayers");
    }
}
