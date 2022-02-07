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
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.api.ResourceFactory;
import org.spoorn.spoornpacks.exception.DuplicateNameException;
import org.spoorn.spoornpacks.core.generator.ResourceGenerator;
import org.spoorn.spoornpacks.registry.SpoornPacksRegistry;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;

import java.util.Optional;

@Log4j2
public class SpoornPacks implements ModInitializer {

    public static final String MODID = "SpoornPacks";
    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static DefaultPrettyPrinter PRETTY_PRINTER = new DefaultPrettyPrinter();
    private static ResourceGenerator RESOURCE_GENERATOR = SpoornPacksRegistry.registerResource("spoornpink");

    public static ResourcePackSource RESOURCE_PACK_SOURCE = ResourcePackSource.nameAndSource("pack.source.spoornpacks");

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        // When de/serializing, only access the fields and ignore getters, setters, etc.
        OBJECT_MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        PRETTY_PRINTER.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    }

    @Override
    public void onInitialize() {
        log.info("Hello from SpoornPacks!");

        //test();
    }

    public static void test() {
        try {
            String defaultName = "pink_blossom";
            
            ItemGroup itemGroup = FabricItemGroupBuilder.build(
                    new Identifier("spoornpink", "general"),
                    ResourceFactory.fetchItemGroupSupplierFromBlock("spoornpink", "pink_blossom_sapling")
            );
            
            ResourceBuilder rb = ResourceFactory.create("spoornpink", defaultName, itemGroup)
                    .addBlocks(BlockType.LOG, BlockType.WOOD, BlockType.PLANKS, BlockType.FENCE, BlockType.FENCE_GATE, 
                            BlockType.LEAVES, BlockType.BUTTON, BlockType.SLAB, BlockType.PRESSURE_PLATE, BlockType.STAIRS,
                            BlockType.TRAPDOOR, BlockType.DOOR, BlockType.CRAFTING_TABLE, BlockType.STRIPPED_LOG, BlockType.STRIPPED_WOOD)
                    .addItems(ItemType.LOG, ItemType.WOOD, ItemType.PLANKS, ItemType.FENCE, ItemType.FENCE_GATE, 
                            ItemType.LEAVES, ItemType.BUTTON, ItemType.SLAB, ItemType.PRESSURE_PLATE, ItemType.STAIRS,
                            ItemType.TRAPDOOR, ItemType.DOOR, ItemType.CRAFTING_TABLE, ItemType.BOAT, ItemType.STRIPPED_LOG, ItemType.STRIPPED_WOOD)
                    .addLeavesWithSaplingOverride("dark_pink_blossom", defaultName).addItem(ItemType.LEAVES, "dark_pink_blossom")
                    .addSapling(TreeConfiguredFeatures.OAK).addItem(ItemType.SAPLING)  // You should have your own ConfiguredFeature for trees
                    ;

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
