package org.spoorn.spoornpacks.core;

import net.minecraft.resource.ResourcePackProfile;

public class SPResourcePackProfile extends ResourcePackProfile {

    public SPResourcePackProfile(ResourcePackProfile copy) {
        super(copy.getName(), copy.isAlwaysEnabled(), copy::createResourcePack, copy.getDisplayName(), copy.getDescription(), copy.getCompatibility(), copy.getInitialPosition(), copy.isPinned(), copy.getSource());
    }
}
