package org.spoorn.spoornpacks.api;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spoorn.spoornpacks.impl.DefaultResourceBuilder;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ResourceFactory {
    
    public static Supplier<ItemStack> fetchItemGroupSupplierFromBlock(String namespace, String name) {
        return () -> new ItemStack(Registries.BLOCK.get(new Identifier(namespace, name)));
    }

    public static Supplier<ItemStack> fetchItemGroupSupplierFromItem(String namespace, String name) {
        return () -> new ItemStack(Registries.ITEM.get(new Identifier(namespace, name)));
    }

    public static ResourceBuilder create(String namespace) {
        return create(namespace, null);
    }

    public static ResourceBuilder create(String namespace, @Nullable String defaultName) {
        return create(namespace, defaultName, null);
    }

    public static ResourceBuilder create(String namespace, @Nullable String defaultName, @Nullable ItemGroup itemGroup) {
        return new DefaultResourceBuilder(namespace, defaultName, itemGroup);
    }
}
