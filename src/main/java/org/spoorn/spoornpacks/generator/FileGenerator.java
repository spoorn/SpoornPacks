package org.spoorn.spoornpacks.generator;

import static org.spoorn.spoornpacks.SpoornPacks.OBJECT_MAPPER;
import static org.spoorn.spoornpacks.SpoornPacks.PRETTY_PRINTER;
import lombok.extern.log4j.Log4j2;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.provider.assets.BlockStateBuilder;
import org.spoorn.spoornpacks.provider.assets.ModelBlockBuilder;
import org.spoorn.spoornpacks.provider.assets.ModelItemBuilder;
import org.spoorn.spoornpacks.provider.data.BlockLootTableBuilder;
import org.spoorn.spoornpacks.provider.data.RecipeBuilder;
import org.spoorn.spoornpacks.provider.data.TagsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public class FileGenerator {

    public static final String SPOORNPACKS_PREFIX = "spoornpacks/";
    private static final String ASSETS_PREFIX = "/resources/assets/";
    private static final String DATA_PREFIX = "/resources/data/";
    private static final String JSON_SUFFIX = ".json";

    private final String id;

    // TODO: Allow force rewrites
    FileGenerator(String id) {
        this.id = id;
    }

    public boolean generateBlockStates(String namespace, String name, BlockStateBuilder builder) throws IOException {
        try {
            Path file = Paths.get(SPOORNPACKS_PREFIX + this.id + ASSETS_PREFIX + namespace + "/blockstates/" + name + JSON_SUFFIX);
            return writeFile(file, builder);
        } catch (Exception e) {
            log.error("Could not create blockstates/ file for namespace=" + namespace + ", name=" + name, e);
            throw e;
        }
    }

    public boolean generateModelBlock(String namespace, String name, ModelBlockBuilder builder) throws IOException {
        try {
            Path file = Paths.get(SPOORNPACKS_PREFIX + this.id + ASSETS_PREFIX + namespace + "/models/block/" + name + JSON_SUFFIX);
            return writeFile(file, builder);
        } catch (Exception e) {
            log.error("Could not create models/block file for namespace=" + namespace + ", name=" + name, e);
            throw e;
        }
    }

    public boolean generateModelItem(String namespace, String name, ModelItemBuilder builder) throws IOException {
        try {
            Path file = Paths.get(SPOORNPACKS_PREFIX + this.id + ASSETS_PREFIX + namespace + "/models/item/" + name + JSON_SUFFIX);
            return writeFile(file, builder);
        } catch (Exception e) {
            log.error("Could not create models/item file for namespace=" + namespace + ", name=" + name, e);
            throw e;
        }
    }

    public boolean generateLootTable(String namespace, String name, BlockLootTableBuilder builder) throws IOException {
        try {
            Path file = Paths.get(SPOORNPACKS_PREFIX + this.id + DATA_PREFIX + namespace + "/loot_tables/blocks/" + name + JSON_SUFFIX);
            return writeFile(file, builder);
        } catch (Exception e) {
            log.error("Could not create loot_tables/blocks file for namespace=" + namespace + ", name=" + name, e);
            throw e;
        }
    }

    public boolean generateRecipe(String namespace, String name, RecipeBuilder builder) throws IOException  {
        try {
            Path file = Paths.get(SPOORNPACKS_PREFIX + this.id + DATA_PREFIX + namespace + "/recipes/" + name + JSON_SUFFIX);
            return writeFile(file, builder);
        } catch (Exception e) {
            log.error("Could not create recipes/ file for namespace=" + namespace + ", name=" + name, e);
            throw e;
        }
    }

    public boolean generateTags(String namespace, String name, TagsBuilder builder) throws IOException {
        try {
            Path file = Paths.get(SPOORNPACKS_PREFIX + this.id + DATA_PREFIX + namespace + "/tags/" + builder.getType() + "/" + name + JSON_SUFFIX);
            return writeFile(file, builder);
        } catch (Exception e) {
            log.error("Could not create tags/ file for namespace=" + namespace + ", name=" + name, e);
            throw e;
        }
    }

    private boolean writeFile(Path file, ResourceProvider resourceProvider) throws IOException {
        Files.createDirectories(file.getParent());

        // If file already exists, don't modify it
        if (Files.exists(file)) {
            log.info("File already exists at {}", file.toString());
            return false;
        }

        Files.writeString(file, OBJECT_MAPPER.writer(PRETTY_PRINTER).writeValueAsString(resourceProvider.getJson()), StandardCharsets.UTF_8);
        return true;
    }
}
