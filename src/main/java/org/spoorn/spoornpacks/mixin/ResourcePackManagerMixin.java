package org.spoorn.spoornpacks.mixin;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {

    private static final SPClientResourcePackProvider CLIENT_PROVIDER = new SPClientResourcePackProvider();
    private static final SPServerResourcePackProvider SERVER_PROVIDER = new SPServerResourcePackProvider();

    @Shadow private List<ResourcePackProfile> enabled;
    @Shadow private Map<String, ResourcePackProfile> profiles;
    @Shadow @Final private Set<ResourcePackProvider> providers;

    /**
     * Injects our custom resource pack provider.
     */
    @Redirect(method = "<init>(Lnet/minecraft/resource/ResourcePackProfile$Factory;[Lnet/minecraft/resource/ResourcePackProvider;)V",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableSet;copyOf([Ljava/lang/Object;)Lcom/google/common/collect/ImmutableSet;"))
    private <E> ImmutableSet<Object> injectSPResourcePack(E[] elements) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER
            && (Arrays.stream(elements).noneMatch(element -> element instanceof SPServerResourcePackProvider))) {
            return ImmutableSet.copyOf(ArrayUtils.add(elements, SERVER_PROVIDER));
        } else {
            boolean isForClient = Arrays.stream(elements).anyMatch(element -> element instanceof ClientBuiltinResourcePackProvider);
            if (isForClient && Arrays.stream(elements).noneMatch(element -> element instanceof SPClientResourcePackProvider)) {
                return ImmutableSet.copyOf(ArrayUtils.add(elements, CLIENT_PROVIDER));
            }
        }
        return ImmutableSet.copyOf(elements);
    }

    @Inject(method = "scanPacks", at = @At(value = "TAIL"))
    private void debugPrints(CallbackInfo ci) {
        System.out.println("### server? " + (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER));
        System.out.println("### providers: " + this.providers);
        System.out.println("### profiles: " + this.profiles);
        System.out.println("### packs: " + this.enabled);
    }
}
