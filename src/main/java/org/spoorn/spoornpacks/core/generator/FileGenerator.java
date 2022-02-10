package org.spoorn.spoornpacks.core.generator;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import static org.spoorn.spoornpacks.SpoornPacks.PRETTY_PRINTER;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.provider.assets.BlockStateBuilder;
import org.spoorn.spoornpacks.provider.assets.ModelBlockBuilder;
import org.spoorn.spoornpacks.provider.assets.ModelItemBuilder;
import org.spoorn.spoornpacks.provider.data.BlockLootTableBuilder;
import org.spoorn.spoornpacks.provider.data.RecipeBuilder;
import org.spoorn.spoornpacks.provider.data.TagsBuilder;
import org.spoorn.spoornpacks.type.ResourceType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class FileGenerator {

    public static final String SPOORNPACKS_PREFIX = "spoornpacks/";
    private static final String ASSETS_PREFIX = "/resources/assets/";
    private static final String DATA_PREFIX = "/resources/data/";
    private static final String JSON_SUFFIX = ".json";

    private final String id;
    private final Map<String, Map<ResourceType, ResourceProvider>> customResourceProviders;

    FileGenerator(String id, boolean overwrite, Map<String, Map<ResourceType, ResourceProvider>> customResourceProviders) {
        this.id = id;
        if (overwrite) {
            try {
                FileUtils.deleteDirectory(new File(SPOORNPACKS_PREFIX + this.id));
            } catch (IOException e) {
                String errorMessage = "Mod Id=[" + this.id + "] set overwrite to true for their sub-spoornpack, but could not delete" +
                        "files at " + SPOORNPACKS_PREFIX + this.id;
                log.error(errorMessage, e);
                throw new RuntimeException(errorMessage, e);
            }
        }
        this.customResourceProviders = customResourceProviders;
    }

    public boolean generateBlockStates(String namespace, String filename, BlockStateBuilder builder) throws IOException {
        try {
            Path file = Paths.get(SPOORNPACKS_PREFIX + this.id + ASSETS_PREFIX + namespace + "/blockstates/" + filename + JSON_SUFFIX);
            
            Optional<ResourceProvider> customResourceProvider = getCustomResourceProvider(filename, ResourceType.BLOCKSTATE);
            if (customResourceProvider.isPresent()) {
                return writeFile(file, customResourceProvider.get());
            } else {
                return writeFile(file, builder);
            }
        } catch (Exception e) {
            log.error("Could not create blockstates/ file for namespace=" + namespace + ", name=" + filename, e);
            throw e;
        }
    }

    public boolean generateModelBlock(String namespace,  String filename, ModelBlockBuilder builder) throws IOException {
        try {
            Path file = Paths.get(SPOORNPACKS_PREFIX + this.id + ASSETS_PREFIX + namespace + "/models/block/" + filename + JSON_SUFFIX);
            Optional<ResourceProvider> customResourceProvider = getCustomResourceProvider(filename, ResourceType.BLOCK_MODEL);
            if (customResourceProvider.isPresent()) {
                return writeFile(file, customResourceProvider.get());
            } else {
                return writeFile(file, builder);
            }
        } catch (Exception e) {
            log.error("Could not create models/block file for namespace=" + namespace + ", name=" + filename, e);
            throw e;
        }
    }

    public boolean generateModelItem(String namespace, String filename, ModelItemBuilder builder) throws IOException {
        try {
            Path file = Paths.get(SPOORNPACKS_PREFIX + this.id + ASSETS_PREFIX + namespace + "/models/item/" + filename + JSON_SUFFIX);
            Optional<ResourceProvider> customResourceProvider = getCustomResourceProvider(filename, ResourceType.ITEM_MODEL);
            if (customResourceProvider.isPresent()) {
                return writeFile(file, customResourceProvider.get());
            } else {
                return writeFile(file, builder);
            }
        } catch (Exception e) {
            log.error("Could not create models/item file for namespace=" + namespace + ", name=" + filename, e);
            throw e;
        }
    }

    public boolean generateLootTable(String namespace, String filename, BlockLootTableBuilder builder) throws IOException {
        try {
            Path file = Paths.get(SPOORNPACKS_PREFIX + this.id + DATA_PREFIX + namespace + "/loot_tables/blocks/" + filename + JSON_SUFFIX);
            Optional<ResourceProvider> customResourceProvider = getCustomResourceProvider(filename, ResourceType.BLOCK_LOOT_TABLE);
            if (customResourceProvider.isPresent()) {
                return writeFile(file, customResourceProvider.get());
            } else {
                return writeFile(file, builder);
            }
        } catch (Exception e) {
            log.error("Could not create loot_tables/blocks file for namespace=" + namespace + ", name=" + filename, e);
            throw e;
        }
    }

    public boolean generateRecipe(String namespace, String filename, RecipeBuilder builder) throws IOException  {
        try {
            Path file = Paths.get(SPOORNPACKS_PREFIX + this.id + DATA_PREFIX + namespace + "/recipes/" + filename + JSON_SUFFIX);
            Optional<ResourceProvider> customResourceProvider = getCustomResourceProvider(filename, ResourceType.RECIPE);
            if (customResourceProvider.isPresent()) {
                return writeFile(file, customResourceProvider.get());
            } else {
                return writeFile(file, builder);
            }
        } catch (Exception e) {
            log.error("Could not create recipes/ file for namespace=" + namespace + ", name=" + filename, e);
            throw e;
        }
    }

    public boolean generateTags(String namespace, String filename, TagsBuilder builder) throws IOException {
        try {
            if (builder.isEmpty()) {
                return false;
            }
            Path file = Paths.get(SPOORNPACKS_PREFIX + this.id + DATA_PREFIX + namespace + "/tags/" + builder.getType() + "/" + filename + JSON_SUFFIX);
            return writeFile(file, builder);
        } catch (Exception e) {
            log.error("Could not create tags/ file for namespace=" + namespace + ", name=" + filename, e);
            throw e;
        }
    }

    private boolean writeFile(Path file, ResourceProvider resourceProvider) throws IOException {
        Files.createDirectories(file.getParent());

        // If file already exists, don't modify it
        if (Files.exists(file)) {
            log.warn("File already exists at, not overwriting at {}", file.toString());
            return false;
        }

        Files.writeString(file, OBJECT_MAPPER.writer(PRETTY_PRINTER).writeValueAsString(resourceProvider.getJson()), StandardCharsets.UTF_8);
        return true;
    }
    
    private Optional<ResourceProvider> getCustomResourceProvider(String filename, ResourceType resourceType) {
        Optional<ResourceProvider> resourceProvider = Optional.ofNullable(this.customResourceProviders.get(filename))
                    .map(m -> m.get(resourceType));
        
        if (resourceProvider.isPresent()) {
            log.info("Using custom ResourceProvider={} for resourceType={}, fullName={}", resourceProvider.get(), resourceType, filename);
        }
        return resourceProvider;
    }
}
