package me.infinityz.minigame.chunks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

public class ChunksManager {
    private @NonNull @Getter UHC instance;

    private @Getter @Setter int distanceThresHold = 100;

    private final @Getter ArrayList<Location> locations = new ArrayList<>();
    private final @Getter LinkedList<ChunkLoadTask> pendingChunkLoadTasks = new LinkedList<>();
    private @Getter BukkitTask autoChunkScheduler;
    private @Getter @Setter int border;

    public ChunksManager(UHC instance) {
        this.instance = instance;
        border = instance.getGame().getBorderSize() / 2;

        autoChunkScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            if (!pendingChunkLoadTasks.isEmpty()) {
                iterate(pendingChunkLoadTasks.iterator());
                notifyOnActionbar(ChatColor.RED + "Not ready to start, currently loading "
                        + pendingChunkLoadTasks.size() + " locations...", "staff.perm");
            } else {
                var online = Bukkit.getOnlinePlayers().size();
                var message = online > locations.size() ? ChatColor.RED + "Not ready to start. "
                        + (online - locations.size()) + " location needed to start."
                        : ChatColor.GREEN + "Ready to start.";
                notifyOnActionbar(message, "staff.pern");

            }
        }, 5L, 20L);
    }

    private void iterate(Iterator<ChunkLoadTask> iter){
        while (iter.hasNext()) {
            var task = iter.next();
            if (task.isDone()) {
                iter.remove();
                notifyOnActionbar(ChatColor.AQUA + "" + pendingChunkLoadTasks.size() + " load tasks left.",
                        "staff.perm");
            } else {
                if (!task.isRunning()) {
                    System.out.println("Starting a new task...");
                    Bukkit.getScheduler().runTaskAsynchronously(instance, task);
                }
                break;
            }
        }
    }

    private void notifyOnActionbar(final String message, final String perm) {
        Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission(perm))
                .forEach(staff -> staff.sendActionBar(message));
    }

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

    public static Location findScatterLocation(final World world, final int radius) {
        Location loc = new Location(world, 0, 0, 0);
        // Use Math#Random to obtain a random integer that can be used as a location.
        loc.setX(loc.getX() + Math.random() * radius * 2.0 - radius);
        loc.setZ(loc.getZ() + Math.random() * radius * 2.0 - radius);
        loc = loc.getWorld().getHighestBlockAt(loc).getLocation();

        if (!isSafe(loc)) {
            return findScatterLocation(world, radius);
        }
        // A location object is returned once we reach this step, next step is to
        // validate the location from others.
        return centerLocation(loc);
    }

    public static Location centerLocation(final Location loc) {
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getBlockY() + 1.5);
        loc.setZ(loc.getBlockZ() + 0.5);
        return loc;
    }

    public static boolean isSafe(final Location loc) {
        return !(loc.getBlock().isLiquid() || loc.getBlock().getRelative(BlockFace.DOWN).isLiquid()
        || loc.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).isLiquid());
    }

}