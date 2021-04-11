package me.noobsters.minigame.tasks;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import me.noobsters.minigame.UHC;

public class AntiFallDamage extends BukkitRunnable implements Listener {
    private ArrayList<Long> firstFallProtection;
    private UHC instance;

    public AntiFallDamage(UHC instance, Collection<Long> longs){
        this.instance = instance;
        this.instance.getListenerManager().registerListener(this);
        firstFallProtection = new ArrayList<>(longs);
        this.runTaskTimerAsynchronously(instance, 2L, 20L);
    }

    @Override
    public void run() {
        if(firstFallProtection.isEmpty() || instance.getGame().getGameTime() > 90){
            this.cancel();
            this.instance.getListenerManager().unregisterListener(this);
            return;
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntityType() == EntityType.PLAYER && e.getCause() == DamageCause.FALL) {
            var player = (Player) e.getEntity();
            if(firstFallProtection.remove(player.getUniqueId().getMostSignificantBits())){
                e.setCancelled(true);
            }

        }

    }

}
