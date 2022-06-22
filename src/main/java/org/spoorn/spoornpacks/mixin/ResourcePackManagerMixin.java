package org.spoorn.spoornpacks.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spoorn.spoornpacks.core.SPClientResourcePackProvider;
import org.spoorn.spoornpacks.core.SPServerResourcePackProvider;

import java.util.HashSet;
import java.util.Set;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
    
    @Shadow @Final @Mutable private Set<ResourcePackProvider> providers;

    /**
     * Injects our custom resource pack provider.
     */
    @Inject(method = "<init>(Lnet/minecraft/resource/ResourcePackProfile$Factory;[Lnet/minecraft/resource/ResourcePackProvider;)V", at = @At(value = "RETURN"))
    private <E> void injectSPResourcePack(ResourcePackProfile.Factory profileFactory, ResourcePackProvider[] resourcePackProviders, CallbackInfo ci) {
        providers = new HashSet<>(providers);
        
        boolean isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
        boolean providerPresent = false;

        for (ResourcePackProvider element : providers) {
            if (element instanceof SPClientResourcePackProvider) {
                isClient = true;
                providerPresent = true;
                break;
            }

            if (element instanceof SPServerResourcePackProvider) {
                providerPresent = true;
                break;
            }
        }

        if (!providerPresent) {
            if (isClient) {
                //System.out.println("### adding client");
                providers.add(new SPClientResourcePackProvider());
            } else {
                //System.out.println("### adding server");
                providers.add(new SPServerResourcePackProvider());
            }
        }
    }

    @Inject(method = "scanPacks", at = @At(value = "TAIL"))
    private void addSubResourcePacks(CallbackInfo ci) {
        //System.out.println("### server? " + (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER));
        //System.out.println("### providers: " + this.providers);
        //System.out.println("### profiles: " + this.profiles);
        //System.out.println("### packs: " + this.enabled);

        for (ResourcePackProvider provider : this.providers) {
            if (provider instanceof SPClientResourcePackProvider) {
                //System.out.println("### client present");
                ((SPClientResourcePackProvider) provider).addSubResourcePacks();
            }

            if (provider instanceof SPServerResourcePackProvider) {
                //System.out.println("### server present");
                ((SPServerResourcePackProvider) provider).addSubResourcePacks();
            }
        }
    }
}
