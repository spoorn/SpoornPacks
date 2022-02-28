package org.spoorn.spoornpacks.type;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum VehicleType implements Type<VehicleType> {
    
    CHEST_MINECART("chest_minecart", "", "_chest_minecart");

    private final String name;

    // For use cases where there is a prefix and a suffix to the name, such as "stripped_<name>_log"
    private final String prefix;
    private final String suffix;

    VehicleType(String name) {
        // Default to setting the name as the suffix as most items do that
        this(name, "", "_" + name);
    }

    VehicleType(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
    }
    public static VehicleType fromString(String name) {
        VehicleType[] vehicleTypes = values();
        for (int i = 0; i < vehicleTypes.length; i++) {
            if (vehicleTypes[i].getName().equals(name)) {
                return vehicleTypes[i];
            }
        }
        throw new IllegalArgumentException("name=[" + name + "] is not a valid BlockType");
    }
}
