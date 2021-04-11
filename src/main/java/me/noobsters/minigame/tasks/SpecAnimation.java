package me.noobsters.minigame.tasks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.UHC;

@RequiredArgsConstructor
public class SpecAnimation extends BukkitRunnable {
    private @NonNull UHC instance;
    private @NonNull Player player;
    private @NonNull Player targetPlayer;
    private double theta = 0.0;

    @Override
    public void run() {
        if (theta == Math.PI) {
            cancel();
        }
        getDifferentialLocation();

    }

    private void getDifferentialLocation() {
        double x = Math.cos(theta) * 1.7, z = Math.sin(theta) * 1.7;
        Location toTeleport = targetPlayer.getLocation().add(x, 0, z); // This may need to be cloned. Not sure it
        toTeleport.setYaw(player.getLocation().getYaw());
        toTeleport.setPitch(player.getLocation().getPitch());

        player.teleport(toTeleport);
        theta += Math.PI / 40;
    }

}