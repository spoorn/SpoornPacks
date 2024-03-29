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

    public SPGroupResourcePack spGroupResourcePack;

    public SPClientResourcePackProvider() {
        this.spGroupResourcePack = new SPGroupResourcePack(ResourceType.CLIENT_RESOURCES);
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        ResourcePackProfile profile = ResourcePackProfile.create(
                SpoornPacks.MODID,
                SpoornPacks.RESOURCE_PACK_SOURCE_TEXT,
                true,
                factory -> spGroupResourcePack,
                ResourceType.CLIENT_RESOURCES,
                ResourcePackProfile.InsertionPosition.BOTTOM,
                SpoornPacks.RESOURCE_PACK_SOURCE
        );

        log.info("Registering client-side SpoornPacks ResourcePackProfile={}", profile);

        profileAdder.accept(profile);
    }

    public void addSubResourcePacks() {
        this.spGroupResourcePack.clear();  // Clear resource pack first
        for (String id : SpoornPacksRegistry.RESOURCES) {
            this.spGroupResourcePack.addSubResourcePack(id);
        }
    }
}