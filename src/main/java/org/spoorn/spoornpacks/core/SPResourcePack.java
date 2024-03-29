package org.spoorn.spoornpacks.core;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import net.minecraft.SharedConstants;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.PathUtil;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Log4j2
@ToString
public class SPResourcePack extends AbstractFileResourcePack implements ResourcePack {

    private static final String NAME = "spoornpacks";
    private static final String MINECRAFT_NAMESPACE = "minecraft";
    private static final Pattern RESOURCE_PACK_PATH = Pattern.compile("[a-z0-9-_]+");
    private static final PackResourceMetadata DEFAULT_PACK_METADATA = new PackResourceMetadata(Text.translatable("spoornpack.metadata.description"), SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES));

    private final ResourceType resourceType;
    private final String id;
    private Set<String> namespaces;
    private final String separator;
    private final Path basePath;

    public SPResourcePack(String id, ResourceType resourceType, Path basePath) {
        super(NAME, true);
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

    private InputSupplier<InputStream> openFile(String fileName) {
        Path path = getPath(fileName);

        if (path != null && Files.isRegularFile(path)) {
            return () -> Files.newInputStream(path);
        }

        // This should never be reached as SPResourcePack is not directly added as a resource pack
        if (PACK_METADATA_NAME.equals(fileName)) {
            return () -> new ByteArrayInputStream(OBJECT_MAPPER.writeValueAsBytes(DEFAULT_PACK_METADATA));
        }

        return null;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        PathUtil.validatePath(segments);

        return this.openFile(String.join("/", segments));
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        final Path path = getPath(getFilename(type, id));
        return path == null ? null : InputSupplier.create(path);
    }

    private static String getFilename(ResourceType type, Identifier id) {
        return String.format(Locale.ROOT, "%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath());
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer visitor) {
        //System.out.println("### findresources " + namespace + "/" + prefix);
        String path = prefix.replace("/", separator);

        Path namespacePath = getPath(type.getDirectory() + "/" + namespace);

        //System.out.println("### namespacepath: " + namespacePath);
        if (namespacePath != null) {
            Path searchPath = namespacePath.resolve(path).toAbsolutePath().normalize();
            //System.out.println("### searchpath: " + searchPath);

            /**
             * Copied from {@link net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack#findResources(ResourceType, String, String, Predicate).} 
             */
            if (Files.exists(searchPath)) {
                try {
                    Files.walkFileTree(searchPath, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            String fileName = file.getFileName().toString();
                            if (fileName.endsWith(".mcmeta")) return FileVisitResult.CONTINUE;

                            try {
                                Identifier id = new Identifier(namespace, namespacePath.relativize(file).toString().replace(separator, "/"));
                                visitor.accept(id, InputSupplier.create(file));
                            } catch (InvalidIdentifierException e) {
                                log.error(e.getMessage());
                            }

                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    log.warn("findResources at " + path + " in namespace " + namespace + " failed!", e);
                }
            }
        }
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
            namespaces.add(MINECRAFT_NAMESPACE);
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
