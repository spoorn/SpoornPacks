package org.spoorn.spoornpacks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spoorn.spoornpacks.entity.SPEntities;
import org.spoorn.spoornpacks.entity.chest.SPChestBlockEntity;

import java.util.Optional;

public class SPChestBlock extends ChestBlock {

    private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>> NAME_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>>(){

        @Override
        public Optional<NamedScreenHandlerFactory> getFromBoth(final ChestBlockEntity chestBlockEntity, final ChestBlockEntity chestBlockEntity2) {
            final DoubleInventory inventory = new DoubleInventory(chestBlockEntity, chestBlockEntity2);
            return Optional.of(new NamedScreenHandlerFactory(){

                @Override
                @Nullable
                public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
                        chestBlockEntity.checkLootInteraction(playerInventory.player);
                        chestBlockEntity2.checkLootInteraction(playerInventory.player);
                        return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory);
                    }
                    return null;
                }

                @Override
                public Text getDisplayName() {
                    if (chestBlockEntity.hasCustomName()) {
                        return chestBlockEntity.getDisplayName();
                    }
                    if (chestBlockEntity2.hasCustomName()) {
                        return chestBlockEntity2.getDisplayName();
                    }
                    if (chestBlockEntity instanceof SPChestBlockEntity spChestBlockEntity) {
                        // Allow custom double chest names
                        return Text.translatable("container." + spChestBlockEntity.namespace + "." + spChestBlockEntity.name + ".doubleChest");
                    } else {
                        return Text.translatable("container.chestDouble");
                    }
                }
            });
        }

        @Override
        public Optional<NamedScreenHandlerFactory> getFrom(ChestBlockEntity chestBlockEntity) {
            return Optional.of(chestBlockEntity);
        }

        @Override
        public Optional<NamedScreenHandlerFactory> getFallback() {
            return Optional.empty();
        }
    };
    
    private final String namespace;
    private final String name;
    private final SPEntities spEntities;
    
    public SPChestBlock(Settings settings, String namespace, String name, SPEntities spEntities) {
        super(settings, () -> spEntities.getChestBlockEntityType(namespace));
        this.namespace = namespace;
        this.name = name;
        this.spEntities = spEntities;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SPChestBlockEntity(namespace, name, this.spEntities, pos, state);
    }

    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return this.getBlockEntitySource(state, world, pos, false).apply(NAME_RETRIEVER).orElse(null);
    }
}
