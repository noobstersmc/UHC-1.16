package me.infinityz.minigame.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import io.papermc.lib.PaperLib;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunkObject;

public class AlphaLoadNeighbouringChunksTask implements Runnable {
    UHC instance;
    Collection<Location> locations;
    Collection<ChunkObject> chunks = new ArrayList<>();
    World world = Bukkit.getWorld("world");
    int count = 0;

    public AlphaLoadNeighbouringChunksTask(UHC instance, Collection<Location> locations) {
        this.instance = instance;
        this.locations = locations;
    }

    ChunkObject getChunkObjectFromLocation(Location loc) {
        return ChunkObject.of((int) Math.floor(loc.getBlockX() / 16), (int) Math.floor(loc.getBlockZ() / 16));
    }

    @Override
    public void run() {
        var semaphore = new Semaphore(50);
        for (var loc : locations) {
            var obj = getChunkObjectFromLocation(loc);
            System.out.println("Queueing chunk " + obj.toString());
            chunks.addAll(AlphaLocationFindTask.getNeighbouringChunks(obj.getX(), obj.getZ(), 8));
        }
        Bukkit.broadcastMessage("Step #1 completed");

        var chunkIterator = chunks.iterator();
        while (chunkIterator.hasNext()) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            var chunk = chunkIterator.next();
            PaperLib.getChunkAtAsyncUrgently(world, chunk.getX(), chunk.getZ(), true).thenAccept(c -> {
                count++;
                if (chunks.size() - count > 0) {
                    Bukkit.broadcastMessage("Chunks left " + (chunks.size() - count));
                } else {
                    Bukkit.broadcastMessage("DONE");
                    Bukkit.getScheduler().runTask(instance, new AlphaScatterTask(locations,
                            Bukkit.getOnlinePlayers().stream().map(it -> it.getPlayer()).collect(Collectors.toList())));
                }
                semaphore.release();
                c.setForceLoaded(true);
                Bukkit.broadcastMessage("Loaded this " + c.toString());
            });
        }
    }

}