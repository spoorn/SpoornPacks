package org.spoorn.spoornpacks.api;

import org.spoorn.spoornpacks.impl.DefaultResourceBuilder;

import javax.annotation.Nullable;

public class ResourceFactory {

    public static ResourceBuilder create(String namespace) {
        return create(namespace, null);
    }

    public static ResourceBuilder create(String namespace, @Nullable String defaultName) {
        return new DefaultResourceBuilder(namespace, defaultName);
    }
}
