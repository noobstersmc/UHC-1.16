package me.infinityz.minigame.tasks;

import java.util.ArrayList;
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
    ArrayList<Long> dontScatter = new ArrayList<>();
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
        instance.getChunkManager().getAutoChunkScheduler().cancel();
        if (players.size() <= 0) {
            Bukkit.getPluginManager().callEvent(new TeleportationCompletedEvent(!Bukkit.isPrimaryThread()));
            this.cancel();
            return;
        }

        time = System.currentTimeMillis();

        Iterator<Player> iterator = players.iterator();

        while (iterator.hasNext()) {
            if (time + 100 <= System.currentTimeMillis())
                break;
            Player player = iterator.next();
            if (dontScatter.contains(player.getUniqueId().getMostSignificantBits())) {
                iterator.remove();
                continue;
            }

            if (player != null && player.isOnline()) {
                var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                if (locations.hasNext()) {
                    Location loc = locations.next();
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 60, 20));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 60, 20));
                    loc.setY(250);
                    instance.getPlayerManager().addCreateUHCPlayer(player.getUniqueId(), true);
                    player.teleport(loc);
                    if (team != null) {
                        for (var memberUUID : team.getMembers()) {
                            dontScatter.add(memberUUID.getMostSignificantBits());
                            var member = Bukkit.getPlayer(memberUUID);
                            if (member != null && member.isOnline()) {
                                member.teleport(player.getLocation());
                                instance.getPlayerManager().addCreateUHCPlayer(member.getUniqueId(), true);
                                member.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 60, 20));
                                member.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 60, 20));
                            }
                        }
                    }
                    System.out.println("Teleported player at time " + System.currentTimeMillis());
                    locations.remove();
                }

            }

            iterator.remove();
            System.out.println("Players left to be scattered: " + players.size());
        }

    }

}