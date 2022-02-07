package org.spoorn.spoornpacks.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spoorn.spoornpacks.entity.boat.SPBoatEntity;
import org.spoorn.spoornpacks.entity.boat.SPBoatRegistry;

import java.util.HashMap;
import java.util.Map;

public class SPEntities {

    private final Map<String, EntityType<SPBoatEntity>> boatEntities = new HashMap<>();
    
    public SPEntities() {
        
    }
    
    public EntityType<SPBoatEntity> registerBoatEntityType(String namespace, SPBoatRegistry spBoatRegistry) {
        if (boatEntities.containsKey(namespace)) {
            return boatEntities.get(namespace);
        } else {
            EntityType<SPBoatEntity> entityType = registerEntity(namespace, "boat",
                    EntityType.Builder.<SPBoatEntity>create((entType, world) -> new SPBoatEntity(spBoatRegistry, entType, world), SpawnGroup.MISC)
                            .setDimensions(1.375F, 0.5625F).build(namespace + ":boat"));
            boatEntities.put(namespace, entityType);
            return entityType;
        }
    }
    
    public EntityType<SPBoatEntity> getBoatEntityType(String namespace) {
        return this.boatEntities.get(namespace);
    }

    private static <E extends Entity, ET extends EntityType<E>> ET registerEntity(String namespace, String id, ET entityType) {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(namespace, id), entityType);
    }
}
