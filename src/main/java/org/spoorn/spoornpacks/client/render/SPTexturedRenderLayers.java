package org.spoorn.spoornpacks.client.render;

import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class SPTexturedRenderLayers {

    public static Map<Pair<String, String>, Map<ChestType, SpriteIdentifier>>  TEXTURED_RENDER_LAYERS = new HashMap<>();
    
    public static void registerChest(String namespace, String name) {
        Pair<String, String> key = Pair.of(namespace, name);
        if (TEXTURED_RENDER_LAYERS.containsKey(key)) {
            throw new IllegalArgumentException("SPTexturedRenderLayer namespace=" + namespace + ", name=" + name + " was already registered!");
        }
        
        Map<ChestType, SpriteIdentifier> spriteIdentifiers = new HashMap<>();
        spriteIdentifiers.put(ChestType.SINGLE, new SpriteIdentifier(TexturedRenderLayers.CHEST_ATLAS_TEXTURE, new Identifier(namespace, "entity/chest/" + name)));
        spriteIdentifiers.put(ChestType.LEFT, new SpriteIdentifier(TexturedRenderLayers.CHEST_ATLAS_TEXTURE, new Identifier(namespace, "entity/chest/" + name + "_left")));
        spriteIdentifiers.put(ChestType.RIGHT, new SpriteIdentifier(TexturedRenderLayers.CHEST_ATLAS_TEXTURE, new Identifier(namespace, "entity/chest/" + name + "_right")));
        TEXTURED_RENDER_LAYERS.put(key, spriteIdentifiers);
    }
    
    public static SpriteIdentifier getChest(String namespace, String name, ChestType chestType) {
        Map<ChestType, SpriteIdentifier> spriteIdentifiers = TEXTURED_RENDER_LAYERS.get(Pair.of(namespace, name));
        if (spriteIdentifiers == null || spriteIdentifiers.isEmpty()) {
            throw new RuntimeException("Expected SpriteIdentifier for chest namespace=" + namespace + ", name=" + 
                    name + ", chestType=" + chestType + " in SPTexturedRenderLayers, but was not found!");
        }
        
        return spriteIdentifiers.get(chestType);
    }
}
