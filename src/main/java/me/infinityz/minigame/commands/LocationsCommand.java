package me.infinityz.minigame.commands;

import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.tasks.ScatterTask;
import net.minecraft.server.v1_16_R1.MinecraftServer;

@CommandPermission("staff.perm")
@CommandAlias("locations|loc")
public class LocationsCommand extends BaseCommand {

    UHC instance;
    Iterator<Location> iter;
    LinkedList<Location> locs;

    public LocationsCommand(UHC instance) {
        this.instance = instance;
    }

    @Subcommand("load")
    @Syntax("[i] &e- Location to be loaded")
    public void start(CommandSender sender, int i) {
        // TODO: Make it so that you can find more locations by executing command.
        if (iter == null) {
            Bukkit.broadcastMessage("Iter is null.");
            this.iter = instance.getLocationManager().getLocationsSet().iterator();
        }
        Bukkit.getScheduler().runTaskTimer(instance, () -> {
        }, 20, 20);
        if (iter.hasNext()) {
            Bukkit.getScheduler().runTask(instance, () -> {
                Location loc = iter.next();
                getNeighbouringChunks(loc.getChunk(), loc.getWorld());
                Bukkit.broadcastMessage("Location loaded");
            });
        } else {
            sender.sendMessage("Iter is empty.");
        }
    }

    @SuppressWarnings("all")
    @Subcommand("load new")
    public void altMode(CommandSender sender) {
        if (locs == null) {
            if (instance.getLocationManager().getLocationsSet() == null
                    || instance.getLocationManager().getLocationsSet().isEmpty()) {
                sender.sendMessage("Try /loc find first");
                return;
            }
            locs = new LinkedList<>(instance.getLocationManager().getLocationsSet());
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                if (locs.isEmpty()) {
                    this.cancel();
                    return;
                }
                if (MinecraftServer.getServer().recentTps[0] < 16) {
                    Bukkit.broadcastMessage("Waiting for tps.");
                    return;
                }
                Location loc = locs.getFirst();
                getNeighbouringChunks(loc.getChunk(), loc.getWorld());
                locs.removeFirst();
                Bukkit.broadcastMessage(locs.size() + "left to be loaded");
            }

        }.runTaskTimer(instance, 20, 40);
    }

    @Subcommand("find")
    @Syntax("[i] &e- Location to be found")
    public void find(CommandSender sender, int i) {
        sender.sendMessage("Starting locations task");
        new ScatterTask(Bukkit.getWorlds().get(0), 2000, 100, i).runTaskTimer(instance, 20, 20);
    }

    void getNeighbouringChunks(Chunk c, World world) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.println("Loading chunk: " + (c.getX() + i) + ", " + (c.getZ() + j));
                world.loadChunk(c.getX() + i, c.getZ() + j);
                world.setChunkForceLoaded(c.getX() + i, c.getZ() + j, true);
            }

        }
    }
}