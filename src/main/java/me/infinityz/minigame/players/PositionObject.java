package me.infinityz.minigame.players;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

/**
 * PositionObject
 */
@AllArgsConstructor(staticName = "of")
public @Data class PositionObject {
    private @Setter(value = AccessLevel.PRIVATE) double x;
    private @Setter(value = AccessLevel.PRIVATE) double y;
    private @Setter(value = AccessLevel.PRIVATE) double z;
    private @Setter(value = AccessLevel.PRIVATE) String world;

    public static PositionObject getPositionFromWorld(Location loc) {
        return of(loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName());
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

}