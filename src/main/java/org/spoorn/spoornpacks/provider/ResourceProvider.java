package org.spoorn.spoornpacks.provider;

import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.annotation.Nullable;

/**
 * Provides the Json for a resource .json file.
 */
public interface ResourceProvider {

    /**
     * If the provided Json is NULL, no resource file will be produced.
     */
    @Nullable
    ObjectNode getJson();
}
