package me.infinityz.minigame.chunks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    public ChunksManager(UHC instance) {
        this.instance = instance;

        autoChunkScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            if (!pendingChunkLoadTasks.isEmpty()) {
                Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("locations.perm"))
                        .forEach(staff -> staff.sendActionBar(ChatColor.RED + "Not ready to start, currently loading "
                                + pendingChunkLoadTasks.size() + " locations..."));
                var iter = pendingChunkLoadTasks.iterator();
                while (iter.hasNext()) {
                    var task = iter.next();
                    if (task.isDone()) {
                        iter.remove();
                        Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("locations.perm"))
                                .forEach(staff -> {
                                    staff.sendActionBar(
                                            ChatColor.AQUA + "" + pendingChunkLoadTasks.size() + " load tasks left.");
                                });
                    } else {
                        if (task.isRunning()) {
                            break;
                        } else if (!task.isRunning()) {
                            System.out.println("Starting a new task...");
                            Bukkit.getScheduler().runTaskAsynchronously(instance, task);
                            break;
                        }
                    }
                }
            } else {
                var online = Bukkit.getOnlinePlayers().size();
                var message = online > locations.size() ? ChatColor.RED + "Not ready to start. "
                        + (online - locations.size()) + " location needed to start."
                        : ChatColor.GREEN + "Ready to start.";
                Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("locations.perm"))
                        .forEach(staff -> staff.sendActionBar(message));

            }
        }, 20 * 5, 20);
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

    public static Location centerLocation(final Location loc) {
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getBlockY() + 1.5);
        loc.setZ(loc.getBlockZ() + 0.5);
        return loc;
    }

    public static boolean isSafe(final Location loc) {
        if (loc.getBlock().isLiquid() || loc.getBlock().getRelative(BlockFace.DOWN).isLiquid()
                || loc.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).isLiquid())
            return false;
        return true;
    }

}