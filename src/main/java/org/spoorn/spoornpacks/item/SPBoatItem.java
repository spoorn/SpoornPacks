package org.spoorn.spoornpacks.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spoorn.spoornpacks.entity.SPEntities;
import org.spoorn.spoornpacks.entity.boat.SPBoatEntity;
import org.spoorn.spoornpacks.entity.boat.SPBoatRegistry;

import java.util.List;
import java.util.function.Predicate;

/**
 * Custom boats.
 *
 * TODO: Add Dispenser Behavior for custom boats.
 */
public class SPBoatItem extends Item {

    private static final Predicate<Entity> RIDERS = EntityPredicates.EXCEPT_SPECTATOR.and(Entity::canHit);
    private final SPBoatRegistry.BoatType type;
    private final SPEntities spEntities;
    private final SPBoatRegistry spBoatRegistry;

    public SPBoatItem(SPEntities spEntities, SPBoatRegistry spBoatRegistry, SPBoatRegistry.BoatType type, Item.Settings settings) {
        super(settings);
        this.type = type;
        this.spEntities = spEntities;
        this.spBoatRegistry = spBoatRegistry;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult hitResult = BoatItem.raycast(world, user, RaycastContext.FluidHandling.ANY);
        if (((HitResult)hitResult).getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(itemStack);
        }
        Vec3d vec3d = user.getRotationVec(1.0f);
        double d = 5.0;
        List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().stretch(vec3d.multiply(5.0)).expand(1.0), RIDERS);
        if (!list.isEmpty()) {
            Vec3d vec3d2 = user.getEyePos();
            for (Entity entity : list) {
                Box box = entity.getBoundingBox().expand(entity.getTargetingMargin());
                if (!box.contains(vec3d2)) continue;
                return TypedActionResult.pass(itemStack);
            }
        }
        if (((HitResult)hitResult).getType() == HitResult.Type.BLOCK) {
            SPBoatEntity boatEntity = new SPBoatEntity(this.spEntities, spBoatRegistry, this.type, world, hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);
            boatEntity.setSPBoatType(this.type);
            boatEntity.setYaw(user.getYaw());
            if (!world.isSpaceEmpty(boatEntity, boatEntity.getBoundingBox())) {
                return TypedActionResult.fail(itemStack);
            }
            if (!world.isClient) {
                world.spawnEntity(boatEntity);
                world.emitGameEvent((Entity)user, GameEvent.ENTITY_PLACE, new BlockPos(hitResult.getPos()));
                if (!user.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.pass(itemStack);
    }
}
