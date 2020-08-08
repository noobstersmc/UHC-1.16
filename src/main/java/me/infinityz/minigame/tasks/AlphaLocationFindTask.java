package me.infinityz.minigame.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import io.papermc.lib.PaperLib;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.AlphaLocationsFoundEvent;
import net.md_5.bungee.api.ChatColor;

public class AlphaLocationFindTask implements Runnable {
    private World world;
    private int radius;
    private List<Player> players;
    private int other = 0;
    private int unsafeCount = 0;
    private long startTime = System.currentTimeMillis();
    public List<Location> locations = new ArrayList<>();
    private UHC instance;

    public AlphaLocationFindTask(World world, int radius, List<Player> players, UHC instance) {
        this.world = world;
        this.radius = radius;
        this.players = players;
        this.instance = instance;
        Bukkit.broadcastMessage(players.size() + " locations have to be found.");
    }

    @Override
    public void run() {
        // Create a semaphore for shared resources.
        final var semaphore = new Semaphore(50);
        var iterator = players.iterator();

        startTime = System.currentTimeMillis();

        while (iterator.hasNext()) {
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
                var highestLoc = loc.getWorld().getHighestBlockAt(loc);
                if (highestLoc.isLiquid()) {
                    unsafeCount++;
                    System.err.println("Unsafe location was found. " + unsafeCount + " so far. "
                            + locInfo(highestLoc.getLocation()));
                } else {
                    getNeighbouringChunks(chunk, 1, highestLoc.isLiquid());
                    iterator.next();
                    chunk.setForceLoaded(true);
                    locations.add(highestLoc.getLocation().add(0.0, 1.5, 0.0));
                    other++;
                    Bukkit.broadcastMessage("Location found, " + (players.size() - other) + " left.");
                    Bukkit.broadcastMessage("Loc: " + locInfo(highestLoc.getLocation()));
                    if ((players.size() - other) == 0) {
                        Bukkit.broadcastMessage("Took " + ((System.currentTimeMillis() - startTime) / 1000)
                                + " seconds to find the locations...");

                    }

                }
            });
        }
        Bukkit.getPluginManager().callEvent(new AlphaLocationsFoundEvent(locations, true));

        Bukkit.broadcastMessage(ChatColor.RED + "Finished finding locations");
    }

    public static void getNeighbouringChunks(final Chunk c, final int size, final boolean bol) {
        if (bol) {
            System.out.println("Not loading chunk...");
            return;
        }
        System.out.println("Initial x= " + c.getX() + " z= " + (c.getZ()));

        final int distance = (size * 2) + 1;
        final int x = c.getX() - size;
        final int z = c.getZ() + size;

        for (int xx = 0; xx < distance; xx++) {
            for (int zz = 0; zz < distance; zz++) {
                int fX = x + xx;
                int fZ = z - zz;
                System.out.println("Position x=" + fX + " z=" + fZ);
                PaperLib.getChunkAtAsyncUrgently(c.getWorld(), fX, fZ, false).thenAccept(chunk -> {
                    System.out.println("Loaded (" + fX + ", " + fZ + ")");
                });
            }
        }
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

    String locInfo(Location loc) {
        return String.format("(%.2f, %.2f, %.2f, %s)", loc.getX(), loc.getY(), loc.getZ(), loc.getBlock().getType());
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