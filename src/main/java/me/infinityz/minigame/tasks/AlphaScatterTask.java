package me.infinityz.minigame.tasks;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Getter;
import me.infinityz.minigame.events.AlphaScatterDoneEvent;
import net.md_5.bungee.api.ChatColor;

public class AlphaScatterTask implements Runnable {
    private Collection<Location> locs;
    private Collection<Player> players;
    private @Getter boolean go = true;
    private long startTime;
    private int count = 0;

    public AlphaScatterTask(Collection<Location> locs, Collection<Player> players) {
        this.locs = locs;
        this.players = players;
        if (locs.size() < players.size())
            go = false;

    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        var iterator = players.iterator();
        var locIterator = locs.iterator();

        while (iterator.hasNext()) {
            var loc = locIterator.next();
            var player = iterator.next();
            player.teleportAsync(loc.add(0.0, 50, 0.0)).thenAccept(result -> {
                loc.getChunk().setForceLoaded(false);
                if (result) {
                    count++;
                    var c = locs.size() - count;
                    Bukkit.broadcastMessage("Teleported " + player.getName() + " " + c + " teleports left.");
                    if (c <= 0) {
                        Bukkit.broadcastMessage(ChatColor.RED + "Scatter finished. It took"
                                + ((System.currentTimeMillis() - startTime) / 1000) + " seconds to complete.");
                        Bukkit.getPluginManager().callEvent(new AlphaScatterDoneEvent(true));
                    }
                } else {
                    System.err.println("Couldn't teleport player");
                }

            });
        }

        Bukkit.broadcastMessage("exit loop");

    }

}