package me.infinityz.minigame.listeners;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.ScatterLocationsFoundEvent;
import me.infinityz.minigame.events.TeleportationCompletedEvent;
import me.infinityz.minigame.scoreboard.IngameScoreboard;
import me.infinityz.minigame.scoreboard.objects.UpdateObject;
import net.md_5.bungee.api.ChatColor;

public class GlobalListener implements Listener {
    UHC instance;

    int time = 0;

    String timeConvert(int t) {
        int hours = t / 3600;
        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
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
    public void onPVP(EntityDamageByEntityEvent e){
        //TODO: Clean up the code.
        if(e.getEntity() instanceof Player){
            Player p2 = null;
            if(e.getDamager() instanceof Player){
                p2 = (Player) e.getDamager();
            }else if(e.getDamager() instanceof Projectile){
                Projectile proj  = (Projectile) e.getDamager();
                if(proj.getShooter() != null && proj.getShooter() instanceof Player){
                    p2 = (Player) proj.getShooter();
                }
            }
            if(p2 != null){
                if(!instance.pvp)e.setCancelled(true);
            }
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
        Bukkit.broadcastMessage("Starting soon...");

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
                    players.playSound(players.getLocation(), Sound.ENTITY_RAVAGER_CELEBRATE, 1, 1);
                    players.removePotionEffect(all.getType());

                });
            });

            instance.getListenerManager().registerListener(instance.getListenerManager().getIngameListeners());
            Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
                time++;

                switch(time){
                    case 300: {
                        //AVISO DE FINAL HEAL 5min left
                    Bukkit.getOnlinePlayers().forEach(all ->{
                        all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                    });
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "5 minutes left for Final Heal.");
                    break;
                    }
                    case 600: {
                        //TODO: FINAL HEAL
                    Bukkit.getOnlinePlayers().forEach(all ->{
                        all.setHealth(20.0);
                        all.setSaturation(20.0F);
                        all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
                    });
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Final heal!");

                    break;
                    }
                    case 900: {
                        //AVISO DE PVP 5min left
                    Bukkit.getOnlinePlayers().forEach(all ->{
                        all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                    });
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "PvP will be enabled in 5 minutes.");
                    break;
                    }
                    case 1200: {
                        //TODO: PVP ON
                    Bukkit.getOnlinePlayers().forEach(all ->{
                        all.playSound(all.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);
                    });
                        instance.pvp = true;
                        Bukkit.broadcastMessage(ChatColor.GREEN + "PVP has been enabled");
                    break;
                    }

                }
                instance.getScoreboardManager().getFastboardMap().entrySet().forEach(entry -> {
                    entry.getValue().addUpdates(
                            new UpdateObject(ChatColor.GRAY + "Game Time: " + ChatColor.WHITE + timeConvert(time), 0));
                    entry.getValue().addUpdates(
                            new UpdateObject(ChatColor.GRAY + "Players: " + ChatColor.WHITE + instance.getPlayerManager().getAlivePlayers(), 3));
                            //TODO: Improve this method, it shouldn't be necessary to have to update this line every second.

                });
            }, 0, 20);

        }, 20 * 10);

    }

}