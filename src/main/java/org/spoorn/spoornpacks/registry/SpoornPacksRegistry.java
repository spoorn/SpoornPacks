package org.spoorn.spoornpacks.registry;

import org.spoorn.spoornpacks.generator.ResourceGenerator;

import java.util.ArrayList;
import java.util.List;

public class SpoornPacksRegistry {

    public static final List<String> RESOURCES = new ArrayList<>();

    public static ResourceGenerator registerResource(String id) {
        return registerResource(id, true);
    }
    
    public static ResourceGenerator registerResource(String id, boolean overwrite) {
        RESOURCES.add(id);
        return new ResourceGenerator(id, overwrite);
    }
}
