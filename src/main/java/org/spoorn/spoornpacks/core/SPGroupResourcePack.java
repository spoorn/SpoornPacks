package org.spoorn.spoornpacks.core;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceNotFoundException;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

@Log4j2
public class SPGroupResourcePack implements ResourcePack {

    private static final String NAME = "spoornpacks";
    private static final PackResourceMetadata DEFAULT_PACK_METADATA = new PackResourceMetadata(new TranslatableText("spoornpack.metadata.description"), ResourceType.CLIENT_RESOURCES.getPackVersion(SharedConstants.getGameVersion()));

    private final ResourceType resourceType;
    private final String separator;
    private final Path basePath;

    // To check if packs conflict
    private final Set<String> addedPacks = new HashSet<>();
    // Map from namespace to list of sub SPResourcePacks that are registered to handle the namespace.
    // There can be multiple of them as mods can modify resources of other namespaces, such as the vanilla "minecraft" namespace
    private final Map<String, List<SPResourcePack>> subResourcePacks = new HashMap<>();

    public SPGroupResourcePack(ResourceType resourceType) {
        this.resourceType = resourceType;
        this.basePath = FabricLoader.getInstance().getGameDir().resolve("spoornpacks").toAbsolutePath().normalize();
        this.separator = basePath.getFileSystem().getSeparator();
    }

    public void addSubResourcePack(String id) {
        if (addedPacks.contains(id)) {
            throw new RuntimeException("Namespace [" + id + "] was already registered in SpoornPacks!");
        }

        SPResourcePack spResourcePack = new SPResourcePack(id, resourceType, basePath, separator);

        for (String namespace : spResourcePack.getNamespaces(resourceType)) {
            if (this.subResourcePacks.containsKey(namespace)) {
                this.subResourcePacks.get(namespace).add(spResourcePack);
            } else {
                List<SPResourcePack> spResourcePacks = new ArrayList<>();
                spResourcePacks.add(spResourcePack);
                this.subResourcePacks.put(namespace, spResourcePacks);
            }
        }

        addedPacks.add(id);
        log.info("Registered [{}] resource pack in SpoornPacks", id);
    }

    @Nullable
    @Override
    public InputStream openRoot(String fileName) throws IOException {
        if (PACK_METADATA_NAME.equals(fileName)) {
            String description = "Spoornpacks resources";
            String pack = String.format("{\"pack\":{\"pack_format\":" + resourceType.getPackVersion(SharedConstants.getGameVersion()) + ",\"description\":\"%s\"}}", description);
            return IOUtils.toInputStream(pack, StandardCharsets.UTF_8);
        }

        // handle pack.png filename

        // ReloadableResourceManagerImpl gets away with FileNotFoundException.
        throw new FileNotFoundException("\"" + fileName + "\" in SpoornPacks group resource pack");
    }

    @Override
    public InputStream open(ResourceType type, Identifier id) throws IOException {
        List<SPResourcePack> subPacks = this.subResourcePacks.get(id.getNamespace());

        if (subPacks != null) {
            for (int i = subPacks.size() - 1; i >= 0; i--) {
                ResourcePack pack = subPacks.get(i);

                if (pack.contains(type, id)) {
                    return pack.open(type, id);
                }
            }
        }

        log.error("Could not open file for Identifier={}", id);
        throw new ResourceNotFoundException(null,
                String.format("%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath()));
    }

    @Override
    public boolean contains(ResourceType type, Identifier id) {
        List<SPResourcePack> subPacks = this.subResourcePacks.get(id.getNamespace());

        if (subPacks == null) {
            return false;
        }

        for (int i = subPacks.size() - 1; i >= 0; i--) {
            ResourcePack pack = subPacks.get(i);

            if (pack.contains(type, id)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        log.info("### findresources {}, {}", namespace, this.subResourcePacks);
        List<SPResourcePack> subPacks = this.subResourcePacks.get(namespace);

        if (subPacks == null) {
            return Collections.emptyList();
        }

        Set<Identifier> resources = new HashSet<>();

        for (int i = subPacks.size() - 1; i >= 0; i--) {
            ResourcePack pack = subPacks.get(i);
            Collection<Identifier> modResources = pack.findResources(type, namespace, prefix, maxDepth, pathFilter);

            resources.addAll(modResources);
        }

        return resources;
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
}
