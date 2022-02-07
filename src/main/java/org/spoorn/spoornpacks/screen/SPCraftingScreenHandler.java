package org.spoorn.spoornpacks.screen;

import net.minecraft.block.CraftingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;

/**
 * We create our own CraftingScreenHandler so we can override canUse() which statically checked for the vanilla
 * Minecraft crafting block.  Instead, we check our own crafting blocks.
 */
public class SPCraftingScreenHandler extends CraftingScreenHandler {

    private final ScreenHandlerContext context;
    private final CraftingTableBlock craftingTableBlock;

    public SPCraftingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CraftingTableBlock craftingTableBlock) {
        super(syncId, playerInventory, context);
        this.context = context;
        this.craftingTableBlock = craftingTableBlock;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, this.craftingTableBlock);
    }
}
