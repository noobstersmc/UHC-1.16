package me.infinityz.minigame.tasks;

import java.util.List;
import java.util.concurrent.Semaphore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import io.papermc.lib.PaperLib;

public class ScatterTeleportTask implements Runnable {
    private World world;
    private int radius;
    private List<Player> players;
    public int count = 0;
    public int other = 0;
    public long startTime = System.currentTimeMillis();

    public ScatterTeleportTask(World world, int radius, List<Player> players) {
        this.world = world;
        this.radius = radius;
        this.players = players;
        Bukkit.broadcastMessage(players.size() + " locations have to be found.");
    }

    @Override
    public void run() {
        // Create a semaphore for shared resources.
        final var semaphore = new Semaphore(50);
        var iterator = players.iterator();

        while (iterator.hasNext()) {
            var player = iterator.next();
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            var loc = new Location(world, 0, 0, 0);
            loc.setX(loc.getX() + Math.random() * radius * 2.0 - radius);
            loc.setZ(loc.getZ() + Math.random() * radius * 2.0 - radius);
            // Use #get() to wait in the thread.
            PaperLib.getChunkAtAsync(loc).thenAccept(chunk -> {
                semaphore.release();
                other++;
                chunk.setForceLoaded(true);
                System.out.println("Location found at time " + System.currentTimeMillis() + "\n"
                        + (players.size() - other) + "left");
                PaperLib.teleportAsync(player, loc.getWorld().getHighestBlockAt(loc).getLocation().add(0.0, 2.5, 0.0))
                        .thenAccept(bol -> {
                            count++;
                            var c = (players.size() - count);
                            System.out.println("Teleported at time " + System.currentTimeMillis() + "\n" + c + "left");
                            if (c == 0) {
                                Bukkit.broadcastMessage("Scatter completed. Took "
                                        + ((System.currentTimeMillis() - startTime) / 1000) + " seconds");
                            }
                        });
            });
        }
    }

    Location findScatterLocation(final Semaphore semaphore) {
        var loc = new Location(world, 0, 0, 0);
        // Use Math#Random to obtain a random integer that can be used as a location.
        loc.setX(loc.getX() + Math.random() * radius * 2.0 - radius);
        loc.setZ(loc.getZ() + Math.random() * radius * 2.0 - radius);
        PaperLib.getChunkAtAsync(loc).thenAccept(chunk -> {
            semaphore.release();
            chunk.setForceLoaded(true);
        });

        // A location object is returned once we reach this step, next step is to
        // validate the location from others.
        return loc;
    }

}