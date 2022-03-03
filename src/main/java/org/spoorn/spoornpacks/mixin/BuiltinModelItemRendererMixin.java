package org.spoorn.spoornpacks.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spoorn.spoornpacks.block.SPChestBlock;
import org.spoorn.spoornpacks.core.generator.BlocksRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders Items with a built-in model, such as chests.
 */
@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {

    private static final Map<Block, BlockEntity> blockEntityCache = new HashMap<>();
    
    @Shadow @Final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void renderBuiltInModels(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            BlockEntity blockEntity = null;
            Block block = ((BlockItem) item).getBlock();
            
            // TODO: Generalize this to any block from SpoornPacks with interface
            if (block instanceof SPChestBlock || BlocksRegistry.CUSTOM_CHESTS.contains(block) || BlocksRegistry.CUSTOM_SHULKER_BOXES.contains(block)) {
                if (blockEntityCache.containsKey(block)) {
                    blockEntity = blockEntityCache.get(block);
                } else {
                    blockEntity = ((BlockEntityProvider) block).createBlockEntity(BlockPos.ORIGIN, block.getDefaultState());
                    blockEntityCache.put(block, blockEntity);
                }
            }
            
            if (blockEntity != null) {
                this.blockEntityRenderDispatcher.renderEntity(blockEntity, matrices, vertexConsumers, light, overlay);
                ci.cancel();
            }
        }
    }
}
