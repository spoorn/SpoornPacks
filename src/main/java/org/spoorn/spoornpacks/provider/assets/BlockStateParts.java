package org.spoorn.spoornpacks.provider.assets;

import lombok.Builder;
import lombok.Getter;

public class BlockStateParts {

    @Getter
    @Builder
    static class Multipart {
        When when;
        Apply apply;
    }

    @Getter
    @Builder
    static class When {
        String north;
        String south;
        String east;
        String west;
    }

    @Getter
    @Builder
    static class Apply {
        String model;
        int y;
        boolean uvlock;
    }
}
