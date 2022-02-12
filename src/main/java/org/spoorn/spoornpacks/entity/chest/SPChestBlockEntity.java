package org.spoorn.spoornpacks.entity.chest;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import org.spoorn.spoornpacks.entity.SPEntities;

public class SPChestBlockEntity extends ChestBlockEntity {

    public String namespace;
    public String name;
    
    public SPChestBlockEntity(String namespace, String name, SPEntities spEntities, BlockPos blockPos, BlockState blockState) {
        super(spEntities.getChestBlockEntityType(namespace), blockPos, blockState);
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container." + namespace + "." + name + "_chest");
    }
}
