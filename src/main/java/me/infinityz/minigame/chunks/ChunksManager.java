package me.infinityz.minigame.chunks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
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
                iterate(pendingChunkLoadTasks.iterator());
                notifyOnActionbar(ChatColor.GOLD + "Not ready to start, currently loading "
                        + pendingChunkLoadTasks.size() + " locations...", "staff.perm");
            } else {
                var needed = neededLocations();
                var message = needed > 0 ? ChatColor.RED + "Not ready to start. "
                        + needed + " location needed to start."
                        : ChatColor.GREEN + "Ready to start.";
                notifyOnActionbar(message, "staff.pern");

            }
        }, 5L, 20L);
    }

    public int getBorder() {
        return instance.getGame().getBorderSize() / 2;
    }

    private void iterate(Iterator<ChunkLoadTask> iter) {
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

    private List<Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getPlayer).collect(Collectors.toList());
    }

    public int neededLocations() {
        var loadedOrLoading = locations.size() + pendingChunkLoadTasks.size();
        var list = getOnlinePlayers();
        var teamManager = instance.getTeamManger();
        /*
         * SI alguien tiene team, ellos y todos los miembros de ese equipo deberia ser
         * restados de la list de jugadores en linea, pero añadidos a la lista de teams
         * en linea para posicioens.
         */

        if (teamManager.isTeams()) {
            list.removeIf(it -> teamManager.getPlayerTeam(it.getUniqueId()) != null);
        }

        return teamManager.isTeams()
                ? ((list.size() / teamManager.getTeamSize()) + (list.size() > 0 ? 1 : 0)
                        + teamManager.teamsOnline().size()) - loadedOrLoading
                : list.size() - loadedOrLoading;
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