package me.infinityz.minigame.chunks;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.infinityz.minigame.UHC;

public @RequiredArgsConstructor class ChunksManager {
    private @NonNull @Getter UHC instance;

    private @Getter @Setter int distanceThresHold = 100;

    private final @Getter ArrayList<Location> locations = new ArrayList<>();
    private final @Getter ArrayList<ChunkLoadTask> pendingChunkLoadTasks = new ArrayList<>();

    public static Collection<ChunkObject> getNeighbouringChunks(final ChunkObject chunkObject, final int distance) {
        return getNeighbouringChunks(chunkObject.getX(), chunkObject.getZ(), distance);
    }

    public static Collection<ChunkObject> getNeighbouringChunks(final int x, final int z, final int distance) {
        var chunksCollection = new ArrayList<ChunkObject>();

        var size = (distance * 2) + 1;
        var offsetX = x - distance;
        var offsetZ = z + distance;

        for (var xx = 0; xx < size; xx++)
            for (var zz = 0; zz < size; zz++)
                chunksCollection.add(ChunkObject.of(offsetX + xx, offsetZ - zz));

        return chunksCollection;
    }

    public static Location centerLocation(final Location loc) {
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getBlockY() + 1.5);
        loc.setZ(loc.getBlockZ() + 0.5);
        return loc;
    }

    public static boolean isSafe(final Location loc) {
        if (loc.getBlock().isLiquid() || loc.getBlock().getRelative(BlockFace.DOWN).isLiquid()
                || loc.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).isLiquid())
            return false;
        return true;
    }

}