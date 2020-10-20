package me.infinityz.minigame.border;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import gnu.trove.map.hash.THashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;

@RequiredArgsConstructor
public class BorderManager {
    private @NonNull UHC instance;
    private THashMap<UUID, Double> mapOfBorders = new THashMap<>();
    private BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(UHC.getInstance(), () -> {
        for (var world : Bukkit.getWorlds()) {
            mapOfBorders.put(world.getUID(), world.getWorldBorder().getSize());
        }

    }, 20L, 20L);

    public boolean isBorderMoving(World world) {
        return world.getWorldBorder().getSize() != mapOfBorders.get(world.getUID());
    }

}
