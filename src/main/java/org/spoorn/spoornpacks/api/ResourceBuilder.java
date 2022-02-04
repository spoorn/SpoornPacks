package org.spoorn.spoornpacks.api;

import org.spoorn.spoornpacks.exception.DuplicateNameException;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;

public interface ResourceBuilder {

    ResourceBuilder addBlock(BlockType type) throws DuplicateNameException;

    ResourceBuilder addBlock(BlockType type, String name) throws DuplicateNameException;

    ResourceBuilder addItem(ItemType type) throws DuplicateNameException;

    ResourceBuilder addItem(ItemType type, String name) throws DuplicateNameException;
}
