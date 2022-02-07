package org.spoorn.spoornpacks.item;

import lombok.extern.log4j.Log4j2;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.spoorn.spoornpacks.core.generator.BlocksRegistry;
import org.spoorn.spoornpacks.mixin.AxeItemAccessor;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class SPAxeItemModifier {

    /**
     * Assumes that the unstripped block is available in the registry and passed in strippedBlocks are actually stripped blocks.
     * Also assumes the left of strippedBlocks pairs is the name of the stripped block without the "stripped_" prefix
     */
    public static void registerStrippedLog(String namespace, List<Pair<String, Block>> strippedBlocks, BlocksRegistry blocksRegistry) {
        if (!strippedBlocks.isEmpty()) {
            Map<Block, Block> newStrippedBlocks = new IdentityHashMap<>(AxeItemAccessor.getStrippedBlocks());
            for (Pair<String, Block> pair : strippedBlocks) {
                Block unstrippedBlock = blocksRegistry.register.get(new Identifier(namespace, pair.getLeft()));

                if (unstrippedBlock == null) {
                    log.error("Block {} does not have an unstripped version to map to!", pair.getRight());
                } else {
                    newStrippedBlocks.put(unstrippedBlock, pair.getRight());
                }
            }
            AxeItemAccessor.setStrippedBlocks(newStrippedBlocks);
        }
    }
}
