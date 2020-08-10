package me.infinityz.minigame.chunks;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

public @Data @AllArgsConstructor(staticName = "of") class ChunkObject {
    private @Setter(value = AccessLevel.PRIVATE) int x;
    private @Setter(value = AccessLevel.PRIVATE) int z;
}