package me.infinityz.minigame.tasks;

import java.util.Iterator;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import io.netty.util.internal.ConcurrentSet;
import me.infinityz.minigame.events.ScatterLocationsFoundEvent;

public class ScatterTask extends BukkitRunnable {
    World world;
    int radius, distanceThreshold, quantity;
    ConcurrentSet<Location> locations;
    long time, start_time;

    public ScatterTask(World world, int radius, int distanceThreshold, int quantity) {
        this.world = world;
        this.radius = radius;
        this.distanceThreshold = distanceThreshold;
        this.quantity = quantity;
        locations = new ConcurrentSet<>();
        this.start_time = System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (quantity <= 0) {
            Bukkit.getPluginManager().callEvent(new ScatterLocationsFoundEvent(
                    locations.stream().collect(Collectors.toSet()), this.start_time, true));
            this.cancel();
            return;
        }
        time = System.currentTimeMillis();

        while (quantity > 0) {
            if (time + 1000 <= System.currentTimeMillis())
                break;
            Location loc = findScatterLocation(world, radius);
            while (validate(loc, distanceThreshold) == false) {
                if (time + 1000 <= System.currentTimeMillis())
                    break;
                loc = findScatterLocation(world, radius);
            }
            locations.add(centerLocation(loc));
            quantity--;
            System.out.println("Scatter locations reamining to be found " + quantity);
        }

    }


    public static Location findScatterLocation(final World world, final int radius) {
        Location loc = new Location(world, 0, 0, 0);
        // Use Math#Random to obtain a random integer that can be used as a location.
        loc.setX(loc.getX() + Math.random() * radius * 2.0 - radius);
        loc.setZ(loc.getZ() + Math.random() * radius * 2.0 - radius);
        loc = loc.getWorld().getHighestBlockAt(loc).getLocation();

        if(!isSafe(loc)){
            return findScatterLocation(world, radius);
        }
        // A location object is returned once we reach this step, next step is to
        // validate the location from others.
        return centerLocation(loc);
    }

    private static Location centerLocation(final Location loc) {
        loc.setX(loc.getBlockX() +0.5);
        loc.setY(loc.getBlockY() +1.5);
        loc.setZ(loc.getBlockZ() +0.5);
        return loc;
    }

    private boolean validate(final Location loc, final int distance) {
        try {
            Iterator<Location> iter = locations.iterator();
            while (iter.hasNext()) {
                Location oi = iter.next();
                if (loc.distance(oi) < distance)
                    return false;
            }
            iter = null;
        } catch (Exception e) {
            System.err.println(e.toString() + " ocurred.");
        }

        return true;
    }

    private static boolean isSafe(final Location loc) {
        if (loc.getBlock().isLiquid() || loc.getBlock().getRelative(BlockFace.DOWN).isLiquid()
                || loc.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).isLiquid())
            return false;
        return true;
    }

}