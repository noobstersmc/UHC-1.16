package me.infinityz.minigame.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Semaphore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import io.papermc.lib.PaperLib;
import me.infinityz.minigame.UHC;

public class AlphaLoadNeighbouringChunksTask implements Runnable {
    UHC instance;
    Collection<Location> locations;
    Collection<ChunkObject> chunks = new ArrayList<>();
    World world;

    public AlphaLoadNeighbouringChunksTask(UHC instance, Collection<Location> locations) {
        this.instance = instance;
        this.locations = locations;
    }

    @Override
    public void run() {
        var semaphore = new Semaphore(50);
        for (var loc : locations) {
            try {
                semaphore.acquire();
                PaperLib.getChunkAtAsync(loc).thenAccept(chunk->{
                    if(world == null){
                        world = chunk.getWorld();
                    }
                    semaphore.release();
                    chunk.setForceLoaded(true);
                    chunks.addAll(AlphaLocationFindTask.getNeighbouringChunks(chunk.getX(), chunk.getZ(), 4));
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
        Bukkit.broadcastMessage("Step #1 completed");

        var chunkIterator = chunks.iterator();
        while(chunkIterator.hasNext()){
            var chunk = chunkIterator.next();
            PaperLib.getChunkAtAsyncUrgently(world, chunk.getX(), chunk.getZ(), true).thenAccept(c ->{
                Bukkit.broadcastMessage("Loaded " + c.toString());
            });
        }
        Bukkit.broadcastMessage("Step #2 completed");
    }

}