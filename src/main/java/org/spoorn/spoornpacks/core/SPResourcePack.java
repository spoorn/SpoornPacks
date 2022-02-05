package org.spoorn.spoornpacks.core;

import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class SPResourcePack implements ResourcePack {

    private static final String NAME = "spoornpacks";
    private static final PackResourceMetadata DEFAULT_PACK_METADATA = new PackResourceMetadata(new TranslatableText("spoornpack.metadata.description"), ResourceType.CLIENT_RESOURCES.getPackVersion(SharedConstants.getGameVersion()));

    private static final InputStream tst = InputStream.nullInputStream();

    private final ResourceType resourceType;

    public SPResourcePack(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Nullable
    @Override
    public InputStream openRoot(String fileName) throws IOException {
        return tst;
    }

    @Override
    public InputStream open(ResourceType type, Identifier id) throws IOException {
        return tst;
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        return new ArrayList<>();
    }

    @Override
    public boolean contains(ResourceType type, Identifier id) {
        return false;
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return new HashSet<>();
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        return (T) DEFAULT_PACK_METADATA;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void close() {
        try {
            tst.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
