package org.spoorn.spoornpacks.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackProfile;

@Environment(EnvType.CLIENT)
public class SPResourcePackProfile extends ResourcePackProfile {

    public SPResourcePackProfile(ResourcePackProfile copy) {
        super(copy.getName(), copy.isAlwaysEnabled(), copy::createResourcePack, copy.getDisplayName(), copy.getDescription(), copy.getCompatibility(), copy.getInitialPosition(), copy.isPinned(), copy.getSource());
    }
}
