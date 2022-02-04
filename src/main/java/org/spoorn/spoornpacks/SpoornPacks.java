package org.spoorn.spoornpacks;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.api.ResourceFactory;
import org.spoorn.spoornpacks.exception.DuplicateNameException;
import org.spoorn.spoornpacks.generator.ResourceGenerator;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;

import java.util.Optional;

@Log4j2
public class SpoornPacks implements ModInitializer {

    public static final String MODID = "SpoornPacks";
    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static DefaultPrettyPrinter PRETTY_PRINTER = new DefaultPrettyPrinter();
    public static ResourceGenerator RESOURCE_GENERATOR = new ResourceGenerator();

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        PRETTY_PRINTER.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    }

    @Override
    public void onInitialize() {
        log.info("Hello from SpoornPacks!");

        test();
    }

    public static void test() {
        try {
            String defaultName = "pink_blossom";
            ResourceBuilder rb = ResourceFactory.create("spoornpink", defaultName)
                    .addBlock(BlockType.LOG, "testLog")
                    .addBlock(BlockType.LOG)
                    .addItem(ItemType.LOG)
                    .addItem(ItemType.LOG, "testItem");

            Resource resource = RESOURCE_GENERATOR.generate(rb);

            String namespace = resource.getNamespace();
            Optional<Block> logBlock = resource.getBlock(BlockType.LOG, defaultName);
            Optional<Block> testLog = resource.getBlock(BlockType.LOG, "testLog");
            Optional<Item> logItem = resource.getItem(ItemType.LOG, defaultName);
            Optional<Item> testItem = resource.getItem(ItemType.LOG, "testItem");
            log.info("### {}, {}, {}, {}, {}, {}", namespace, defaultName, logBlock, testLog, logItem, testItem);
        } catch (DuplicateNameException e) {
            log.error("Duplicate names", e);
            throw new RuntimeException(e);
        }
    }
}
