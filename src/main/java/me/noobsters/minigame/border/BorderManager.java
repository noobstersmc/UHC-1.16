package me.noobsters.minigame.border;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.UHC;

@RequiredArgsConstructor
public class BorderManager {
    private @NonNull UHC instance;/*
    private THashMap<UUID, Double> mapOfBorders = new THashMap<>();
    private BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(UHC.getInstance(), () -> {
        for (var world : Bukkit.getWorlds()) {
            mapOfBorders.put(world.getUID(), world.getWorldBorder().getSize());
        }

    }, 20L, 20L);

    public boolean isBorderMoving(World world) {
        return world.getWorldBorder().getSize() != mapOfBorders.get(world.getUID());
    }*/

}
