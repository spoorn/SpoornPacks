package org.spoorn.spoornpacks;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resource.ResourcePackSource;

@Log4j2
public class SpoornPacks implements ModInitializer {

    public static final String MODID = "spoornpacks";
    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static DefaultPrettyPrinter PRETTY_PRINTER = new DefaultPrettyPrinter();
    //private static ResourceGenerator RESOURCE_GENERATOR = SpoornPacksRegistry.registerResource("mymodid");

    public static ResourcePackSource RESOURCE_PACK_SOURCE = ResourcePackSource.nameAndSource("pack.source.spoornpacks");

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        // When de/serializing, only access the fields and ignore getters, setters, etc.
        OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        PRETTY_PRINTER.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    }

    @Override
    public void onInitialize() {
        log.info("Hello from SpoornPacks!");
        
        //test();
    }

//    public static void test() {
//        try {
//            String defaultName = "red_blossom";
//
//            ItemGroup itemGroup = FabricItemGroupBuilder.build(
//                    new Identifier("mymodid", "general"),
//                    ResourceFactory.fetchItemGroupSupplierFromBlock("mymodid", "red_blossom_sapling")
//            );
//
//            ResourceBuilder rb = ResourceFactory.create("mymodid", defaultName, itemGroup)
//                    .addBlocks(BlockType.LOG, BlockType.WOOD, BlockType.PLANKS, BlockType.FENCE, BlockType.FENCE_GATE, 
//                            BlockType.LEAVES, BlockType.BUTTON, BlockType.SLAB, BlockType.PRESSURE_PLATE, BlockType.STAIRS,
//                            BlockType.TRAPDOOR, BlockType.DOOR, BlockType.CRAFTING_TABLE, BlockType.STRIPPED_LOG, BlockType.STRIPPED_WOOD)
//                    .addItems(ItemType.LOG, ItemType.WOOD, ItemType.PLANKS, ItemType.FENCE, ItemType.FENCE_GATE, 
//                            ItemType.LEAVES, ItemType.BUTTON, ItemType.SLAB, ItemType.PRESSURE_PLATE, ItemType.STAIRS,
//                            ItemType.TRAPDOOR, ItemType.DOOR, ItemType.CRAFTING_TABLE, ItemType.BOAT, ItemType.STRIPPED_LOG, ItemType.STRIPPED_WOOD)
//                    .addLeavesWithSaplingOverride("yellow_blossom", defaultName).addItem(ItemType.LEAVES, "yellow_blossom")
//                    .addSapling(TreeConfiguredFeatures.OAK).addItem(ItemType.SAPLING)  // You should have your own ConfiguredFeature for trees
//                    ;
//
//            Resource resource = RESOURCE_GENERATOR.generate(rb);
//
//            String namespace = resource.getNamespace();
//            Optional<Block> logBlock = resource.getBlock(BlockType.LOG, defaultName);
//            Optional<Block> testLog = resource.getBlock(BlockType.LOG, "testLog");
//            Optional<Item> logItem = resource.getItem(ItemType.LOG, defaultName);
//            Optional<Item> testItem = resource.getItem(ItemType.LOG, "testItem");
//            log.info("### {}, {}, {}, {}, {}, {}", namespace, defaultName, logBlock, testLog, logItem, testItem);
//        } catch (DuplicateNameException e) {
//            log.error("Duplicate names", e);
//            throw new RuntimeException(e);
//        }
//    }
}
