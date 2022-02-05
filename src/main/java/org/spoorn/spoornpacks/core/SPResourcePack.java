package org.spoorn.spoornpacks.core;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import net.minecraft.SharedConstants;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Log4j2
@ToString
public class SPResourcePack extends AbstractFileResourcePack implements ResourcePack {

    private static final String NAME = "spoornpacks";
    private static final String MINECRAFT_NAMESPACE = "minecraft";
    private static final Pattern RESOURCE_PACK_PATH = Pattern.compile("[a-z0-9-_]+");
    private static final PackResourceMetadata DEFAULT_PACK_METADATA = new PackResourceMetadata(new TranslatableText("spoornpack.metadata.description"), ResourceType.CLIENT_RESOURCES.getPackVersion(SharedConstants.getGameVersion()));

    private final ResourceType resourceType;
    private final String id;
    private Set<String> namespaces;
    private final String separator;
    private final Path basePath;

    public SPResourcePack(String id, ResourceType resourceType, Path basePath, String separator) {
        super(null);
        this.id = id;
        this.resourceType = resourceType;
        this.basePath = basePath.resolve(id).resolve("resources").toAbsolutePath().normalize();
        this.separator = basePath.getFileSystem().getSeparator();
    }

    private Path getPath(String filename) {
        Path childPath = basePath.resolve(filename.replace("/", separator)).toAbsolutePath().normalize();

        if (childPath.startsWith(basePath) && Files.exists(childPath)) {
            return childPath;
        } else {
            return null;
        }
    }

    @Override
    protected boolean containsFile(String filename) {
        // Pack Metadata is always available
        if (PACK_METADATA_NAME.equals(filename)) {
            return true;
        }

        Path path = getPath(filename);
        return path != null && Files.isRegularFile(path);
    }

    @Override
    protected InputStream openFile(String fileName) throws IOException {
        Path path = getPath(fileName);

        if (path != null && Files.isRegularFile(path)) {
            return Files.newInputStream(path);
        }

        if (PACK_METADATA_NAME.equals(fileName)) {
            return new ByteArrayInputStream(OBJECT_MAPPER.writeValueAsBytes(DEFAULT_PACK_METADATA));
        }

        return InputStream.nullInputStream();
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        //System.out.println("### findresources " + namespace + "/" + prefix);
        List<Identifier> ids = new ArrayList<>();
        String path = prefix.replace("/", separator);

        Path namespacePath = getPath(type.getDirectory() + "/" + namespace);

        //System.out.println("### namespacepath: " + namespacePath);
        if (namespacePath != null) {
            Path searchPath = namespacePath.resolve(path).toAbsolutePath().normalize();
            //System.out.println("### searchpath: " + searchPath);

            if (Files.exists(searchPath)) {
                try {
                    Files.walk(searchPath, maxDepth)
                            .filter(Files::isRegularFile)
                            .filter((p) -> {
                                String filename = p.getFileName().toString();
                                //System.out.println("### filename: " + filename);
                                return !filename.endsWith(".mcmeta") && pathFilter.test(filename);
                            })
                            .map(namespacePath::relativize)
                            .map((p) -> p.toString().replace(separator, "/"))
                            .forEach((s) -> {
                                try {
                                    //System.out.println("### path: " + s);
                                    ids.add(new Identifier(namespace, s));
                                } catch (InvalidIdentifierException e) {
                                    log.error(e.getMessage(), e);
                                }
                            });
                } catch (IOException e) {
                    log.warn("findResources at " + path + " in namespace " + namespace + " failed!", e);
                }
            }
        }

        return ids;
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        if (this.namespaces == null) {
            Path file = getPath(type.getDirectory());

            if (!Files.isDirectory(file)) {
                return Collections.emptySet();
            }

            Set<String> namespaces = new HashSet<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(file, Files::isDirectory)) {
                for (Path path : stream) {
                    String s = path.getFileName().toString();
                    // s may contain trailing slashes, remove them
                    s = s.replace(separator, "");

                    if (RESOURCE_PACK_PATH.matcher(s).matches()) {
                        namespaces.add(s);
                    } else {
                        log.error("Invalid namespace format at {}", path);
                    }
                }
            } catch (IOException e) {
                log.error("Could not get namespaces", e);
            }

            // Allow minecraft tags from mods as well
            // TODO: Optimize.  This will cause all SPResourcePacks to be invoked for "minecraft" tags
            namespaces.add("minecraft");
            this.namespaces = namespaces;
        }

        return this.namespaces;
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
