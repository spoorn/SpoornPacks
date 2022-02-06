package org.spoorn.spoornpacks.util;

import org.spoorn.spoornpacks.SpoornPacks;
import org.spoorn.spoornpacks.jsont.JsonT;

import java.io.IOException;

/**
 * Utility for invoking JsonT APIs.
 */
public class JsonTUtil {
    
    private final JsonT jsonT = new JsonT(SpoornPacks.OBJECT_MAPPER);
    
    public JsonTUtil() {

    }

    /**
     * Avoids the checked IOException that JsonT throws.
     */
    public <T> T substitute(String resourcePath, Class<T> clazz, String... args) {
        try {
            return jsonT.substitute(resourcePath, clazz, args);
        } catch (IOException e) {
            throw new RuntimeException("Could not generate substitution Json for resource at path " + resourcePath, e);
        }
    }
}
