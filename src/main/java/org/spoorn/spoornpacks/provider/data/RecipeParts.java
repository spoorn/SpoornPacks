package org.spoorn.spoornpacks.provider.data;

import lombok.Builder;
import lombok.Getter;

public class RecipeParts {

    @Getter
    @Builder
    static class Key {
        String item;
    }
}
