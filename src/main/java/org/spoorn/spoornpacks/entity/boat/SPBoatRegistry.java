package org.spoorn.spoornpacks.entity.boat;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.spoorn.spoornpacks.SpoornPacks;

import java.util.ArrayList;
import java.util.List;

public class SPBoatRegistry {
    
    // Global registry that EntityModelsMixin can access so the game can fetch and render all models of all boats.
    // See SPBoatEntityRenderer's constructor which involves a call to EntityModels
    public static final List<BoatType> GLOBAL_SP_BOAT_REGISTRY = new ArrayList<>();

    // Unique integer ordinal to BoatType.  This is to mimic enums as in the Type enum in BoatEntity
    @Getter
    private final List<BoatType> boatRegistry = new ArrayList<>();
    
    public SPBoatRegistry() {
        
    }
    
    public synchronized BoatType registerBoat(String namespace, String name) {
        BoatType boatType = new BoatType(namespace, name, this.boatRegistry.size());
        this.boatRegistry.add(boatType);
        GLOBAL_SP_BOAT_REGISTRY.add(boatType);
        return boatType;
    }
    
    public synchronized BoatType getBoat(int ordinal) {
        return this.boatRegistry.get(ordinal);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BoatType {

        private String namespace;
        private String name;
        private int ordinal;

        private BoatType(String namespace, String name, int ordinal) {
            this.namespace = namespace;
            this.name = name;
            this.ordinal = ordinal;
        }
        
        public String getCustomNbtData() {
            try {
                return SpoornPacks.OBJECT_MAPPER.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Could not get custom Nbt data for SpoornPacks BoatType", e);
            }
        }
        
        public String toString() {
            return this.name;
        }
    }
}
