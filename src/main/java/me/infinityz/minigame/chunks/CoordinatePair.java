package me.infinityz.minigame.chunks;

import org.bukkit.Location;
import org.bukkit.World;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

public @Data @AllArgsConstructor(staticName = "of") class CoordinatePair {
    private @Setter(value = AccessLevel.PRIVATE) int x;
    private @Setter(value = AccessLevel.PRIVATE) int z;

    public double distanceTo(CoordinatePair coordinatePair) {
        return distanceTo(this, coordinatePair);
    }

    public ChunkObject toChunkObject() {
        return ChunkObject.of((int) x / 16, (int) z / 16);
    }

    public static double distanceTo(CoordinatePair pair1, CoordinatePair pair2) {
        return Math.sqrt(Math.pow(pair2.getX() - pair1.getX(), 2) + Math.pow(pair2.getZ() - pair1.getZ(), 2));
    }

    public Location toLocation(World world) {
        return toLocation(this, world);
    }

    public Location toLocation(World world, boolean highestBlock) {
        return CoordinatePair.toLocation(this, world, highestBlock);
    }

    public static Location toLocation(CoordinatePair pair, World world) {
        return new Location(world, pair.getX(), 65, pair.getZ());
    }

    public static Location toLocation(CoordinatePair pair, World world, boolean highestBlock) {
        if (highestBlock) {
            return world.getHighestBlockAt(pair.getX(), pair.getZ()).getLocation();
        } else {
            return toLocation(pair, world);
        }
    }

    public static CoordinatePair fromLocation(Location loc) {
        return of(loc.getBlockX(), loc.getBlockZ());
    }
}