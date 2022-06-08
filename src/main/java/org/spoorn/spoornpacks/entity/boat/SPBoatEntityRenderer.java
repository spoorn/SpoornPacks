package org.spoorn.spoornpacks.entity.boat;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class SPBoatEntityRenderer extends EntityRenderer<SPBoatEntity> {

    private final Map<SPBoatRegistry.BoatType, Pair<Identifier, BoatEntityModel>> texturesAndModels;

    public SPBoatEntityRenderer(SPBoatRegistry spBoatRegistry, EntityRendererFactory.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.8f;
        this.texturesAndModels = spBoatRegistry.getBoatRegistry().stream().collect(ImmutableMap.toImmutableMap(
                (type) -> type,
                (type) -> {
                    return Pair.of(new Identifier(type.getNamespace(), "textures/entity/boat/" + type.getName() + ".png"), new BoatEntityModel(ctx.getPart(createBoat(type)), false));
                }));
    }

    @Override
    public void render(SPBoatEntity boatEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        float k;
        matrixStack.push();
        matrixStack.translate(0.0, 0.375, 0.0);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f - f));
        float h = (float)boatEntity.getDamageWobbleTicks() - g;
        float j = boatEntity.getDamageWobbleStrength() - g;
        if (j < 0.0f) {
            j = 0.0f;
        }
        if (h > 0.0f) {
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.sin(h) * h * j / 10.0f * (float)boatEntity.getDamageWobbleSide()));
        }
        if (!MathHelper.approximatelyEquals(k = boatEntity.interpolateBubbleWobble(g), 0.0f)) {
            matrixStack.multiply(new Quaternion(new Vec3f(1.0f, 0.0f, 1.0f), boatEntity.interpolateBubbleWobble(g), true));
        }
        Pair<Identifier, BoatEntityModel> pair = this.texturesAndModels.get(boatEntity.getSPBoatType());
        Identifier identifier = pair.getFirst();
        BoatEntityModel boatEntityModel = pair.getSecond();
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
        boatEntityModel.setAngles(boatEntity, g, 0.0f, -0.1f, 0.0f, 0.0f);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(boatEntityModel.getLayer(identifier));
        boatEntityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        if (!boatEntity.isSubmergedInWater()) {
            VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getWaterMask());
            boatEntityModel.getWaterPatch().render(matrixStack, vertexConsumer2, i, OverlayTexture.DEFAULT_UV);
        }
        matrixStack.pop();
        super.render(boatEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public static EntityModelLayer createBoat(SPBoatRegistry.BoatType type) {
        return create(type.getNamespace(), "boat/" + type.getName(), "main");
    }

    private static EntityModelLayer create(String namespace, String name, String layer) {
        return new EntityModelLayer(new Identifier(namespace, name), layer);
    }

    @Override
    public Identifier getTexture(SPBoatEntity entity) {
        return this.texturesAndModels.get(entity.getSPBoatType()).getFirst();
    }
}
