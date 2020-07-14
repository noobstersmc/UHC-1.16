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

    int time = 0;

    String timeConvert(int t) {
        int hours = t / 3600;
        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

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
    public void onTeleportCompleted(TeleportationCompletedEvent e) {
        Bukkit.broadcastMessage("Starting in 10 seconds...");

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            instance.getScoreboardManager().purgeScoreboards();
            instance.getListenerManager().unregisterListener(instance.getListenerManager().getScatter());
            // Remove all potion effects
            Bukkit.getOnlinePlayers().forEach(players -> {
                // Send the new scoreboard;
                IngameScoreboard sb = new IngameScoreboard(players);
                sb.update();
                instance.getScoreboardManager().getFastboardMap().put(players.getUniqueId().toString(), sb);

                players.getActivePotionEffects().forEach(all -> {
                    players.removePotionEffect(all.getType());
                });
            });

            instance.getListenerManager().registerListener(instance.getListenerManager().getIngameListeners());
            Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
                time++;
                instance.getScoreboardManager().getFastboardMap().entrySet().forEach(entry -> {
                    entry.getValue().updateLine(0, "Game Time: " + timeConvert(time));
                });
            }, 0, 20);

        }, 20 * 10);

    }

}