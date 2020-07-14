package me.infinityz.minigame.listeners;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.ScatterLocationsFoundEvent;
import me.infinityz.minigame.events.TeleportationCompletedEvent;
import me.infinityz.minigame.scoreboard.IngameScoreboard;

public class GlobalListener implements Listener {
    UHC instance;

    public GlobalListener(UHC instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        switch (instance.gameStage) {
            case INGAME:
            case LOBBY:
            case SCATTER: {

                break;
            }
            default:
                break;
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (instance.getScoreboardManager().getFastboardMap().containsKey(e.getPlayer().getUniqueId().toString())) {

        }

    }


    @EventHandler
    public void onScatter(ScatterLocationsFoundEvent e) {
        System.out.println("Scatter task completed in " + (System.currentTimeMillis() - e.getTime()));
        // Quickly ensure not null
        instance.getLocationManager().getLocationsSet()
                .addAll(e.getLocations().stream().filter(loc -> loc != null).collect(Collectors.toList()));
    }

    @EventHandler
    public void onTeleportCompleted(TeleportationCompletedEvent e){
        instance.getScoreboardManager().purgeScoreboards();
        instance.getListenerManager().unregisterListener(instance.getListenerManager().getScatter());

        
        //Remove all potion effects
        Bukkit.getOnlinePlayers().forEach(players->{
            //Send the new scoreboard;
            IngameScoreboard sb = new IngameScoreboard(players);
            sb.update();
            instance.getScoreboardManager().getFastboardMap().put(players.getUniqueId().toString(), sb);
            
            players.getActivePotionEffects().forEach(all ->{
                players.removePotionEffect(all.getType());
            });
        });

        instance.getListenerManager().registerListener(instance.getListenerManager().getIngameListeners());
        
    }

}