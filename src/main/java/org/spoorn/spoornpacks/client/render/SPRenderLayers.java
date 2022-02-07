package org.spoorn.spoornpacks.client.render;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import org.spoorn.spoornpacks.mixin.RenderLayersAccessor;
import org.spoorn.spoornpacks.type.BlockType;

/**
 * CLIENT SIDE ONLY!
 */
@Log4j2
@Environment(EnvType.CLIENT)
public class SPRenderLayers {

    /**
     * Adds our blocks to Vanilla's RenderLayers maps.
     */
    public static void registerRenderLayer(BlockType type, Block block) {
        if (block != null) {
            switch (type) {
                case SAPLING, DOOR, TRAPDOOR -> {
                    RenderLayersAccessor.getBlocks().put(block, RenderLayer.getCutout());
                }
            }
        }
    }
}
