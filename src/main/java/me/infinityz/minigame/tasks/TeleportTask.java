package me.infinityz.minigame.tasks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.TeleportationCompletedEvent;

public class TeleportTask extends BukkitRunnable {
    Iterator<Location> locations;
    List<Player> players;
    long time, start_time;
    UHC instance;

    public TeleportTask(UHC instance, HashSet<Location> locations, List<Player> players) {
        this.instance = instance;
        this.locations = locations.iterator();
        this.start_time = System.currentTimeMillis();
        this.players = players;
    }

    @Override
    public void run() {
        if (players.size() <= 0) {
            Bukkit.getPluginManager().callEvent(new TeleportationCompletedEvent());
            this.cancel();
            return;
        }
        time = System.currentTimeMillis();
        int i =0;

        while (players.size() > 0) {
            if (time + 100 <= System.currentTimeMillis() || i>1)
                break;
            Player player = players.get(0);
            if (player == null || !player.isOnline()) {
                player = null;
            } else if (locations.hasNext()) {
                Location tpLocation = locations.next();
                Chunk chunk = tpLocation.getChunk();
                System.out.println("Loading chunk (" + chunk.getX() + ", " + chunk.getZ() + ")");
                instance.getPlayerManager().addCreateUHCPlayer(player.getUniqueId());
                player.teleport(tpLocation);
                
                tpLocation = null;
                chunk = null;
            }
            players.remove(0);
            System.out.println("Players left to be scattered " + players.size());
            i++;

        }

    }

}