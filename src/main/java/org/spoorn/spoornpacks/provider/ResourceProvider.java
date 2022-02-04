package org.spoorn.spoornpacks.provider;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface ResourceProvider {

    ObjectNode getJson();
}
