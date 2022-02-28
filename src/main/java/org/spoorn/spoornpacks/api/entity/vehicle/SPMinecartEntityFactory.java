package org.spoorn.spoornpacks.api.entity.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.World;
import org.spoorn.spoornpacks.type.VehicleType;

/**
 * Implement this interface if you want to add a Minecart Entity via 
 * {@link org.spoorn.spoornpacks.api.ResourceBuilder#addMinecart(VehicleType, SPMinecartEntityFactory)}
 * or any of the similar variants.
 */
public interface SPMinecartEntityFactory {

    AbstractMinecartEntity.Type getVanillaMinecartEntityType();

    /**
     * Constructor factory for creating the MinecartEntity object.  This is required for creating the EntityType.
     *
     * The typical implementation of this would just instantiate your MinecartEntity object by invoking the constructor.
     *
     * Example constructor implementations:
     *      See {@link net.minecraft.entity.vehicle.ChestMinecartEntity#ChestMinecartEntity(EntityType, World)}
     */
    AbstractMinecartEntity create(EntityType<? extends Entity> entityType, World world);

    /**
     * Factory constructor for creating a custom MinecartEntity object.  This is needed to replace entity spawning
     * when using a custom SPMinecartItem.
     * 
     * The typical implementation of this would just instantiate your MinecartEntity object by invoking the constructor.
     * 
     * Example constructor implementations: 
     *      See {@link AbstractMinecartEntity#create(World, double, double, double, AbstractMinecartEntity.Type)} for examples.
     *      Or See {@link net.minecraft.entity.vehicle.ChestMinecartEntity#ChestMinecartEntity(World, double, double, double)}
     */
    AbstractMinecartEntity create(World world, double x, double y, double z);
}
