package org.spoorn.spoornpacks;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.api.ResourceFactory;
import org.spoorn.spoornpacks.core.generator.ResourceGenerator;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.registry.SpoornPacksRegistry;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;
import org.spoorn.spoornpacks.type.ResourceType;

import java.util.Optional;

@Log4j2
public class SpoornPacks implements ModInitializer {

    public static final String MODID = "spoornpacks";
    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static DefaultPrettyPrinter PRETTY_PRINTER = new DefaultPrettyPrinter();

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
//        String modid = "spoornpink";
//        ResourceGenerator RESOURCE_GENERATOR = SpoornPacksRegistry.registerResource(modid);
//        String defaultName = "pink_blossom";
//
//        ItemGroup itemGroup = FabricItemGroupBuilder.build(
//                new Identifier(modid, "general"),
//                ResourceFactory.fetchItemGroupSupplierFromBlock(modid, "pink_blossom_sapling")
//        );
//
//        ResourceBuilder rb = ResourceFactory.create(modid, defaultName, itemGroup)
//                .addBlocks(BlockType.LOG, BlockType.WOOD, BlockType.PLANKS, BlockType.FENCE, BlockType.FENCE_GATE, 
//                        BlockType.LEAVES, BlockType.BUTTON, BlockType.SLAB, BlockType.PRESSURE_PLATE, BlockType.STAIRS,
//                        BlockType.TRAPDOOR, BlockType.DOOR, BlockType.CRAFTING_TABLE, BlockType.STRIPPED_LOG, BlockType.STRIPPED_WOOD,
//                        BlockType.CHEST, BlockType.BARREL)
//                .addItems(ItemType.LOG, ItemType.WOOD, ItemType.PLANKS, ItemType.FENCE, ItemType.FENCE_GATE, 
//                        ItemType.LEAVES, ItemType.BUTTON, ItemType.SLAB, ItemType.PRESSURE_PLATE, ItemType.STAIRS,
//                        ItemType.TRAPDOOR, ItemType.DOOR, ItemType.CRAFTING_TABLE, ItemType.BOAT, ItemType.STRIPPED_LOG, ItemType.STRIPPED_WOOD,
//                        ItemType.CHEST, ItemType.BARREL)
//                .addLeavesWithSaplingOverride("dark_pink_blossom", defaultName).addItem(ItemType.LEAVES, "dark_pink_blossom")
//                .addSapling(TreeConfiguredFeatures.OAK).addItem(ItemType.SAPLING)  // You should have your own ConfiguredFeature for trees
//                /*.addCustomResourceProvider("stripped_pink_blossom_wood", ResourceType.BLOCKSTATE, new TestCustomResourceProvider())
//                .addCustomResourceProvider("stripped_pink_blossom_wood", ResourceType.BLOCK_MODEL, new TestCustomResourceProvider())
//                .addCustomResourceProvider("stripped_pink_blossom_wood", ResourceType.ITEM_MODEL, new TestCustomResourceProvider())
//                .addCustomResourceProvider("stripped_pink_blossom_wood", ResourceType.BLOCK_LOOT_TABLE, new TestCustomResourceProvider())
//                .addCustomResourceProvider("stripped_pink_blossom_wood", ResourceType.RECIPE, new TestCustomResourceProvider())*/
//                .addSmallFlower("pink_orchid", StatusEffects.SATURATION, 5)
//                .addSmallFlower("pink_orchid2", StatusEffects.SATURATION, 5)
//                .addSmallFlower("pink_orchid3", StatusEffects.SATURATION, 5)
//                .addSmallFlower("pink_orchid4", StatusEffects.SATURATION, 5)
//                .addSmallFlower("pink_orchid5", StatusEffects.SATURATION, 5)
//                .addBlock(BlockType.TALL_FLOWER, "pink_lilac").addItem(ItemType.TALL_FLOWER, "pink_lilac")
//                ;
//
//        Resource resource = RESOURCE_GENERATOR.generate(rb);
//
//        String namespace = resource.getNamespace();
//        Optional<Block> logBlock = resource.getBlock(BlockType.LOG, defaultName);
//        Optional<Block> testLog = resource.getBlock(BlockType.LOG, "testLog");
//        Optional<Item> logItem = resource.getItem(ItemType.LOG, defaultName);
//        Optional<Item> testItem = resource.getItem(ItemType.LOG, "testItem");
//        log.info("### {}, {}, {}, {}, {}, {}", namespace, defaultName, logBlock, testLog, logItem, testItem);
//    }
//
//    static class TestCustomResourceProvider implements ResourceProvider {
//
//        TestCustomResourceProvider() { }
//
//        @Override
//        public ObjectNode getJson() {
//            return OBJECT_MAPPER.createObjectNode().put("testkey", "testvalue");
//        }
//    }
}
