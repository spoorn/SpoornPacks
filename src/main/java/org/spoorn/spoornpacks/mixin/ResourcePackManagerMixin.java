package org.spoorn.spoornpacks.mixin;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spoorn.spoornpacks.core.SPClientResourcePackProvider;
import org.spoorn.spoornpacks.core.SPServerResourcePackProvider;

import java.util.Set;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
    
    @Shadow @Final private Set<ResourcePackProvider> providers;

    /**
     * Injects our custom resource pack provider.
     */
    @Redirect(method = "<init>(Lnet/minecraft/resource/ResourcePackProfile$Factory;[Lnet/minecraft/resource/ResourcePackProvider;)V",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableSet;copyOf([Ljava/lang/Object;)Lcom/google/common/collect/ImmutableSet;"))
    private <E> ImmutableSet<Object> injectSPResourcePack(E[] elements) {
        //System.out.println("### injectSPResourcePack");
        boolean isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
        boolean providerPresent = false;

        for (int i = 0; i < elements.length; i++) {
            E element = elements[i];

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
                return ImmutableSet.copyOf(ArrayUtils.add(elements, new SPClientResourcePackProvider()));
            } else {
                //System.out.println("### adding server");
                return ImmutableSet.copyOf(ArrayUtils.add(elements, new SPServerResourcePackProvider()));
            }
        }

        return ImmutableSet.copyOf(elements);
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
