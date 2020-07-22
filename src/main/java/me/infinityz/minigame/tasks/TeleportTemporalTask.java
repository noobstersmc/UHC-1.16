package me.infinityz.minigame.tasks;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.TeleportationCompletedEvent;

public class TeleportTemporalTask extends BukkitRunnable {
    Iterator<Location> locations;
    Collection<Player> players;
    long time, start_time;
    UHC instance;

    public TeleportTemporalTask(UHC instance, Collection<Location> locations, Collection<Player> players) {
        this.instance = instance;
        this.locations = locations.iterator();
        this.start_time = System.currentTimeMillis();
        this.players = players;
    }

    @Override
    public void run() {
        if (players.size() <= 0) {
            Bukkit.getPluginManager().callEvent(new TeleportationCompletedEvent(!Bukkit.isPrimaryThread()));
            this.cancel();
            return;
        }
        
        Iterator<Player> iterator = players.iterator();
        int i = 1;

        while (iterator.hasNext()) {
            if (i > 1)
                return;
            Player player = iterator.next();

            if(player != null && player.isOnline()){
                if(locations.hasNext()){
                    Location loc = locations.next();
                    loc.setY(150);
                    instance.getPlayerManager().addCreateUHCPlayer(player.getUniqueId());
                    player.teleport(loc);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 60, 3));
                    System.out.println("Teleported player at time " + System.currentTimeMillis());
                    locations.remove();
                }

            }

            iterator.remove();
            System.out.println("Players left to be scattered: " + players.size());

            i++;
        }

    }

}