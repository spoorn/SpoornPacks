package org.spoorn.spoornpacks.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spoorn.spoornpacks.screen.SPCraftingScreenHandler;

public class SPCraftingTableBlock extends CraftingTableBlock {

    private static final Text TITLE = new TranslatableText("container.crafting");

    public SPCraftingTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new SPCraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos), this), TITLE);
    }
}
