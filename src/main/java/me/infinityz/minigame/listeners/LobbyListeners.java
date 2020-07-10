package me.infinityz.minigame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.ScatterLocationsFoundEvent;
import me.infinityz.minigame.scoreboard.IScoreboard;
import me.infinityz.minigame.scoreboard.LobbyScoreboard;

public class LobbyListeners implements Listener {
    UHC instance;
    BukkitTask lobbyScoreboardTask;

    public LobbyListeners(UHC instance) {
        this.instance = instance;

        lobbyScoreboardTask =  Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {

            if(UHC.color >= UHC.colors.size()) UHC.color = 1;
            instance.getScoreboardManager().getFastboardMap().values().forEach(all -> {
                if (all instanceof LobbyScoreboard)
                    all.update();
            });
            UHC.color++;

        }, 20, 2);
    }
    
    public BukkitTask getLobbyScoreboardTask() {
        return lobbyScoreboardTask;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        e.setCancelled(true);

    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
        e.setCancelled(true);
    }

    @EventHandler
    public void onAction(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {
        if (instance.getScoreboardManager().findScoreboard(e.getPlayer().getUniqueId()) != null) {
            return;
        }
        final IScoreboard sb = new LobbyScoreboard(e.getPlayer());
        instance.getScoreboardManager().getFastboardMap().put(e.getPlayer().getUniqueId().toString(), sb);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        IScoreboard sb = instance.getScoreboardManager().getFastboardMap()
                .remove(e.getPlayer().getUniqueId().toString());
        if (sb != null) {
            sb.delete();
        }
    }

    @EventHandler
    public void onScatter(ScatterLocationsFoundEvent e){
        Bukkit.broadcastMessage("scatter completed in " + (System.currentTimeMillis() - e.time) + "ms");
        Bukkit.broadcastMessage(e.getLocations().size() + " locs");
        
    }

}