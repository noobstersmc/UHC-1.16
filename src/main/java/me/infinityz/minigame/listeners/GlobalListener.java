package me.infinityz.minigame.listeners;

import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.events.ScatterLocationsFoundEvent;
import me.infinityz.minigame.events.TeleportationCompletedEvent;
import me.infinityz.minigame.scoreboard.IngameScoreboard;
import me.infinityz.minigame.scoreboard.objects.UpdateObject;
import me.infinityz.minigame.tasks.ScatterTask;
import net.md_5.bungee.api.ChatColor;

public class GlobalListener implements Listener {
    UHC instance;

    public static int time = 0;
    boolean net = false;

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
    public void onPVP(EntityDamageByEntityEvent e) {
        // TODO: Clean up the code.
        if (e.getEntity() instanceof Player) {
            Player p2 = null;
            if (e.getDamager() instanceof Player) {
                p2 = (Player) e.getDamager();
            } else if (e.getDamager() instanceof Projectile) {
                Projectile proj = (Projectile) e.getDamager();
                if (proj.getShooter() != null && proj.getShooter() instanceof Player) {
                    p2 = (Player) proj.getShooter();
                }
            }
            if (p2 != null) {
                if (!instance.pvp)
                    e.setCancelled(true);
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
        Bukkit.broadcastMessage(ChatColor.GREEN + "Starting soon...");

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
                players.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 20));
                players.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 20));
                players.playSound(players.getLocation(), Sound.ENTITY_RAVAGER_CELEBRATE, 1, 1);
            });
            Bukkit.broadcastMessage(ChatColor.GREEN + "UHC has started!");
            instance.gameStage = Stage.INGAME;

            instance.getListenerManager().registerListener(instance.getListenerManager().getIngameListeners());
            Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
                time++;
                if(!net){
                    World mWorld = Bukkit.getWorlds().get(0);
                    if (mWorld.getWorldBorder().getSize() <= 1000) {
                        net = true;
                        Bukkit.getScheduler().runTask(instance, ()->{
                            Bukkit.getOnlinePlayers().stream().forEach(player -> {
                                if (player.getWorld().getEnvironment() == Environment.NETHER) {
                                    player.teleport(ScatterTask.findScatterLocation(mWorld, 499));
                                }
                            });

                        });
                    }

                }
                switch (time) {
                    case 300: {
                        // AVISO DE FINAL HEAL 5min left
                        Bukkit.getOnlinePlayers().forEach(all -> {
                            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                        });
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "5 minutes left for Final Heal.");
                        break;
                    }
                    case 600: {
                        // TODO: FINAL HEAL
                        Bukkit.getOnlinePlayers().forEach(all -> {
                            all.setHealth(20.0);
                            all.setSaturation(20.0F);
                            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
                        });
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Final heal!");

                        break;
                    }
                    case 900: {
                        // AVISO DE PVP 5min left
                        Bukkit.getOnlinePlayers().forEach(all -> {
                            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                        });
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "PvP will be enabled in 5 minutes.");
                        break;
                    }
                    case 1200: {
                        // TODO: PVP ON
                        Bukkit.getOnlinePlayers().forEach(all -> {
                            all.playSound(all.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);
                        });
                        instance.pvp = true;
                        Bukkit.broadcastMessage(ChatColor.GREEN + "PvP has been enabled.");
                        break;
                    }
                    case 3600: {
                        World world = Bukkit.getWorlds().get(0);
                        world.setGameRule(GameRule.DO_INSOMNIA, false);
                        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                        world.setTime(400);
                        world.getWorldBorder().setSize(200, 1500);
                        break;
                    }

                }
                instance.getScoreboardManager().getFastboardMap().entrySet().forEach(entry -> {
                    entry.getValue().addUpdates(
                            new UpdateObject(ChatColor.GRAY + "Game Time: " + ChatColor.WHITE + timeConvert(time), 0));
                    entry.getValue().addUpdates(new UpdateObject(ChatColor.GRAY + "Players: " + ChatColor.WHITE
                            + instance.getPlayerManager().getAlivePlayers(), 3));
                    entry.getValue().addUpdates(new UpdateObject(ChatColor.GRAY + "Border: " + ChatColor.WHITE
                            + ((int)(Bukkit.getWorlds().get(0).getWorldBorder().getSize() / 2)), 5));
                    // TODO: Improve this method, it shouldn't be necessary to have to update this
                    // line every second.

                });

                instance.getPlayerManager().getUhcPlayerMap().entrySet().parallelStream().forEach(entry ->{
                    if(entry.getValue().isAlive()){
                        OfflinePlayer of = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey()));
                        if(!of.isOnline()){
                            if(System.currentTimeMillis() - of.getLastPlayed() > 600000){
                            entry.getValue().hasDied = true;
                            entry.getValue().setAlive(false);
                            entry.getValue().setSpectator(true);
                        }
                        }
                    }

                }
                );

            }, 0, 20);

        }, 20 * 10);

    }

}