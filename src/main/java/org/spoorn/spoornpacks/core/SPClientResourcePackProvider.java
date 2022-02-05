package org.spoorn.spoornpacks.core;

import lombok.extern.log4j.Log4j2;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;
import org.spoorn.spoornpacks.SpoornPacks;
import org.spoorn.spoornpacks.registry.SpoornPacksRegistry;

import java.util.function.Consumer;

@Log4j2
public class SPClientResourcePackProvider implements ResourcePackProvider {

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
        for (String id : SpoornPacksRegistry.RESOURCES) {
            String name = "spoornpacks:" + id;
            ResourcePackProfile profile = ResourcePackProfile.of(
                    name,
                    true,
                    () -> new SPResourcePack(ResourceType.CLIENT_RESOURCES),
                    factory,
                    ResourcePackProfile.InsertionPosition.BOTTOM,
                    SpoornPacks.RESOURCE_PACK_SOURCE
            );

            log.info("Registering client-side SpoornPacks ResourcePackProfile={}", profile);

            profileAdder.accept(
                new SPResourcePackProfile(profile)
            );
        }
    }
}
