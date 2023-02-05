package org.spoorn.spoornpacks.core;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

@Log4j2
public class SPGroupResourcePack implements ResourcePack {

    private static final String NAME = "spoornpacks";
    private static final PackResourceMetadata DEFAULT_PACK_METADATA = new PackResourceMetadata(Text.translatable("spoornpack.metadata.description"), ResourceType.CLIENT_RESOURCES.getPackVersion(SharedConstants.getGameVersion()));

    private final ResourceType resourceType;
    private final Path basePath;

    // To check if packs conflict
    private final Set<String> addedPacks = new HashSet<>();
    // Map from namespace to list of sub SPResourcePacks that are registered to handle the namespace.
    // There can be multiple of them as mods can modify resources of other namespaces, such as the vanilla "minecraft" namespace
    private final Map<String, List<SPResourcePack>> subResourcePacks = new HashMap<>();

    public SPGroupResourcePack(ResourceType resourceType) {
        this.resourceType = resourceType;
        this.basePath = FabricLoader.getInstance().getGameDir().resolve("spoornpacks").toAbsolutePath().normalize();
    }

    public void addSubResourcePack(String id) {
        if (addedPacks.contains(id)) {
            throw new RuntimeException("Namespace [" + id + "] was already registered in SpoornPacks!");
        }

        SPResourcePack spResourcePack = new SPResourcePack(id, resourceType, basePath);

        for (String namespace : spResourcePack.getNamespaces(resourceType)) {
            this.subResourcePacks.computeIfAbsent(namespace, m -> new ArrayList<>()).add(spResourcePack);
        }

        addedPacks.add(id);
        log.info("Registered [{}] resource pack in SpoornPacks", id);
    }
    
    public void clear() {
        log.info("Clearing SPGroupResourcePack");
        this.addedPacks.clear();
        this.subResourcePacks.clear();
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... pathSegments) {
        String fileName = String.join("/", pathSegments);
        
        if (PACK_METADATA_NAME.equals(fileName)) {
            String description = "SpoornPacks resources";
            String pack = String.format("{\"pack\":{\"pack_format\":" + resourceType.getPackVersion(SharedConstants.getGameVersion()) + ",\"description\":\"%s\"}}", description);
            return () -> IOUtils.toInputStream(pack, StandardCharsets.UTF_8);
        }

        // TODO: handle pack.png filename

        return null;
    }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        List<SPResourcePack> subPacks = this.subResourcePacks.get(id.getNamespace());

        if (subPacks != null) {
            for (int i = subPacks.size() - 1; i >= 0; i--) {
                ResourcePack pack = subPacks.get(i);
                InputSupplier<InputStream> supplier = pack.open(type, id);

                if (supplier != null) {
                    return supplier;
                }
            }
        }

        //log.error("Could not open file for Identifier={}", id);
        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer visitor) {
        List<SPResourcePack> subPacks = this.subResourcePacks.get(namespace);

        if (subPacks == null) {
            return;
        }

        for (int i = subPacks.size() - 1; i >= 0; i--) {
            ResourcePack pack = subPacks.get(i);
            pack.findResources(type, namespace, prefix, visitor);
        }
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return this.subResourcePacks.keySet();
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        if (metaReader.getKey().equals("pack")) {
            return (T) DEFAULT_PACK_METADATA;
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void close() {
    }

    /**
     * For appending resources if multiple resource packs use the same namespace, such as "minecraft" tags used in multiple
     * SPResourcePacks.
     * 
     * This is copied from Fabric's {@link net.fabricmc.fabric.impl.resource.loader.GroupResourcePack}.
     */
    public void appendResources(ResourceType type, Identifier id, List<Resource> resources) throws IOException {
        List<SPResourcePack> packs = this.subResourcePacks.get(id.getNamespace());

        if (packs == null) {
            return;
        }

        Identifier metadataId = NamespaceResourceManager.getMetadataPath(id);

        for (SPResourcePack pack : packs) {
            InputSupplier<InputStream> supplier = pack.open(type, id);

            if (supplier != null) {
                InputSupplier<ResourceMetadata> metadataSupplier = () -> {
                    InputSupplier<InputStream> rawMetadataSupplier = pack.open(type, metadataId);
                    return rawMetadataSupplier != null ? NamespaceResourceManager.loadMetadata(rawMetadataSupplier) : ResourceMetadata.NONE;
                };

                resources.add(new Resource(pack, supplier, metadataSupplier));
            }
        }
    }
}
