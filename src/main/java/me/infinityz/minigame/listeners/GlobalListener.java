package me.infinityz.minigame.listeners;

import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
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
    public void joinMessage(PlayerJoinEvent e) {
        e.setJoinMessage("");
        e.getPlayer().sendMessage(ChatColor.BLUE + "Discord! discord.gg/4AdHqV9\n" + ChatColor.AQUA
                + "Twitter! twitter.com/NoobstersUHC");

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage("");
    }

    @EventHandler
    public void onLeaf(LeavesDecayEvent e) {
        if (Math.random() <= 0.0080) {
            e.getBlock().setType(Material.AIR);
            e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(),
                    new ItemStack(Material.APPLE));
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
    public void onChat(AsyncPlayerChatEvent e) {
        if (instance.globalmute && !e.getPlayer().hasPermission("staff.perm")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Globalmute is Enabled.");
        }
    }

    @EventHandler
    public void onScatter(ScatterLocationsFoundEvent e) {
        Bukkit.broadcastMessage("Locations found in " + (System.currentTimeMillis() - e.getTime()));
        // Quickly ensure not null
        instance.getLocationManager().getLocationsSet()
                .addAll(e.getLocations().stream().filter(loc -> loc != null).collect(Collectors.toList()));
    }

    @EventHandler
    public void onTeleportCompleted(TeleportationCompletedEvent e) {
        Bukkit.broadcastMessage(ChatColor.of("#2be49c") + "Starting soon...");

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            instance.getScoreboardManager().purgeScoreboards();
            instance.getListenerManager().unregisterListener(instance.getListenerManager().getScatter());
            // Remove all potion effects
            Bukkit.getOnlinePlayers().forEach(players -> {
                // Send the new scoreboard;
                var sb = new IngameScoreboard(players);
                sb.update();
                instance.getScoreboardManager().getFastboardMap().put(players.getUniqueId().toString(), sb);

                players.getActivePotionEffects().forEach(all -> {
                    players.removePotionEffect(all.getType());

                });
                players.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 20));
                players.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 20));
                players.playSound(players.getLocation(), Sound.ENTITY_RAVAGER_CELEBRATE, 1, 1);
            });
            Bukkit.broadcastMessage(ChatColor.of("#2be49c") + "UHC has started!");
            instance.gameStage = Stage.INGAME;

            instance.getListenerManager().registerListener(instance.getListenerManager().getIngameListeners());
            Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
                time++;
                if (!net) {
                    World mWorld = Bukkit.getWorlds().get(0);
                    if (mWorld.getWorldBorder().getSize() <= 1000) {
                        net = true;
                        Bukkit.getScheduler().runTask(instance, () -> {
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
                        Bukkit.broadcastMessage(ChatColor.of("#4788d9") + "5 minutes left for Final Heal.");
                        break;
                    }
                    case 600: {
                        // TODO: FINAL HEAL
                        Bukkit.getOnlinePlayers().forEach(all -> {
                            all.setHealth(20.0);
                            all.setFoodLevel(20);
                            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
                        });
                        Bukkit.broadcastMessage(ChatColor.of("#2be49c") + "Final heal!");

                        break;
                    }
                    case 900: {
                        // AVISO DE PVP 5min left
                        Bukkit.getOnlinePlayers().forEach(all -> {
                            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                        });
                        Bukkit.broadcastMessage(ChatColor.of("#4788d9") + "PvP will be enabled in 5 minutes.");
                        break;
                    }
                    case 1200: {
                        // TODO: PVP ON
                        Bukkit.getOnlinePlayers().forEach(all -> {
                            all.playSound(all.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);
                        });
                        instance.pvp = true;
                        Bukkit.broadcastMessage(ChatColor.of("#2be49c") + "PvP has been enabled.");
                        break;
                    }
                    case 3600: {
                        Bukkit.broadcastMessage(ChatColor.of("#4788d9")
                                + "The world will shrink to 100 blocks in the next 25 minutes at a speed of 1 block per second!");
                        Bukkit.broadcastMessage(ChatColor.of("#2be49c")
                                + "Players in the nether will be randomly teleported to the overworld once the border reaches 500 blocks.");
                        Bukkit.getOnlinePlayers().forEach(all -> {
                            all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
                        });
                        Bukkit.getScheduler().runTask(instance, () -> {
                            var world = Bukkit.getWorlds().get(0);
                            world.setGameRule(GameRule.DO_INSOMNIA, false);
                            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                            world.setTime(400);
                            world.getWorldBorder().setSize(200, 1500);
                        });
                        break;
                    }

                }
                instance.getScoreboardManager().getFastboardMap().entrySet().forEach(entry -> {
                    entry.getValue().addUpdates(
                            new UpdateObject(ChatColor.GRAY + "Game Time: " + ChatColor.WHITE + timeConvert(time), 0));
                    entry.getValue().addUpdates(new UpdateObject(ChatColor.GRAY + "Players: " + ChatColor.WHITE
                            + instance.getPlayerManager().getAlivePlayers(), 3));
                    entry.getValue().addUpdates(new UpdateObject(ChatColor.GRAY + "Border: " + ChatColor.WHITE
                            + ((int) (Bukkit.getWorlds().get(0).getWorldBorder().getSize() / 2)), 5));
                    // TODO: Improve this method, it shouldn't be necessary to have to update this
                    // line every second.

                });

                instance.getPlayerManager().getUhcPlayerMap().entrySet().parallelStream().forEach(entry -> {
                    if (entry.getValue().isAlive()) {
                        OfflinePlayer of = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey()));
                        if (!of.isOnline()) {
                            if (System.currentTimeMillis() - of.getLastPlayed() > 600000) {
                                Bukkit.broadcastMessage(ChatColor.YELLOW + of.getName()
                                        + " has been disqualified for abandoning the game.");
                                entry.getValue().hasDied = true;
                                entry.getValue().setAlive(false);
                            }
                        }
                    }

                });

            }, 0, 20);

        }, 20 * 10);

    }

    

}