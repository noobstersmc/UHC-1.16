package me.noobsters.minigame.listeners;

import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import lombok.NonNull;
import me.noobsters.minigame.UHC;

public class StatsListener implements Listener {

    private @NonNull UHC instance;

    public StatsListener(UHC instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onMobs(EntityDeathEvent e) {
        var entity = e.getEntity();
        if (entity.getKiller() != null && entity.getKiller() instanceof Player && !(entity instanceof Player)) {
            var player = (Player) entity.getKiller();
            var uuid = player.getUniqueId();
            var manager = instance.getPlayerManager();
            if (manager.getUhcPlayerMap().containsKey(uuid.getMostSignificantBits())) {
                var uhcPlayer = manager.getPlayer(uuid);
                if (entity instanceof Monster || entity instanceof Slime) {
                    uhcPlayer.setHostileMobs(uhcPlayer.getHostileMobs() + 1);
                } else {
                    uhcPlayer.setPeacefulMobs(uhcPlayer.getPeacefulMobs() + 1);
                }
            }
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        var entity = e.getEntity();
        if (entity instanceof Player) {
            var player = (Player) entity;
            var uuid = player.getUniqueId();
            var manager = instance.getPlayerManager();
            if (manager.getUhcPlayerMap().containsKey(uuid.getMostSignificantBits())) {
                var uhcPlayer = manager.getPlayer(uuid);
                uhcPlayer.setProjectileShoots(uhcPlayer.getProjectileShoots() + 1);
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        var damager = e.getDamager();
        if (damager instanceof Projectile) {
            var projectile = (Projectile) damager;
            if (projectile.getShooter() instanceof Player) {
                var player = (Player) projectile.getShooter();
                var uuid = player.getUniqueId();
                var manager = instance.getPlayerManager();
                if (manager.getUhcPlayerMap().containsKey(uuid.getMostSignificantBits())) {
                    var uhcPlayer = manager.getPlayer(uuid);
                    uhcPlayer.setProjectileHits(uhcPlayer.getProjectileHits() + 1);
                }
            }
        }
    }
}
