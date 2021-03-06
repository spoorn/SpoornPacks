package org.spoorn.spoornpacks.core;

import lombok.extern.log4j.Log4j2;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;
import org.spoorn.spoornpacks.SpoornPacks;
import org.spoorn.spoornpacks.registry.SpoornPacksRegistry;

import java.util.function.Consumer;

@Log4j2
public class SPServerResourcePackProvider implements ResourcePackProvider {

    public SPGroupResourcePack spGroupResourcePack;

    public SPServerResourcePackProvider() {
        this.spGroupResourcePack = new SPGroupResourcePack(ResourceType.SERVER_DATA);
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
        ResourcePackProfile profile = ResourcePackProfile.of(
                SpoornPacks.MODID,
                true,
                () -> spGroupResourcePack,
                factory,
                ResourcePackProfile.InsertionPosition.BOTTOM,
                SpoornPacks.RESOURCE_PACK_SOURCE
        );

        log.info("Registering server-side SpoornPacks ResourcePackProfile={}", profile);

        profileAdder.accept(new SPResourcePackProfile(profile));
    }

    public void addSubResourcePacks() {
        this.spGroupResourcePack.clear();  // Clear resource pack first
        for (String id : SpoornPacksRegistry.RESOURCES) {
            this.spGroupResourcePack.addSubResourcePack(id);
        }
    }
}