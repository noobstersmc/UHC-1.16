package me.infinityz.minigame.tasks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.minigame.events.TeleportationCompletedEvent;

public class TeleportTask extends BukkitRunnable {
    Iterator<Location> locations;
    List<Player> players;
    long time, start_time;

    public TeleportTask(HashSet<Location> locations, List<Player> players) {
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

        while (players.size() > 0) {
            if (time + 1000 <= System.currentTimeMillis())
                break;
            if(locations.hasNext()){
                players.get(0).teleport(locations.next());
            }
            players.remove(0);
            
        }

    }

}