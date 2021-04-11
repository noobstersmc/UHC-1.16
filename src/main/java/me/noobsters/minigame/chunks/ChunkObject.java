package me.noobsters.minigame.chunks;

import org.bukkit.Chunk;
import org.bukkit.World;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

public @Data @AllArgsConstructor(staticName = "of") class ChunkObject {
    private @Setter(value = AccessLevel.PRIVATE) int x;
    private @Setter(value = AccessLevel.PRIVATE) int z;

    public Chunk toChunk(World world) {
        return world.getChunkAt(x / 16, z / 16);
    }

    public static Chunk toChunk(ChunkObject chunkObject, World world) {
        return world.getChunkAt(chunkObject.getX() / 16, chunkObject.getZ() / 16);
    }
}