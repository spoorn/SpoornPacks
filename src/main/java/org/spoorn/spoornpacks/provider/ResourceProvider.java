package org.spoorn.spoornpacks.provider;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Provides the Json for a resource .json file.
 */
public interface ResourceProvider {

    ObjectNode getJson();
}
