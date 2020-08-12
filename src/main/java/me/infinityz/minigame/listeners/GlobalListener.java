package me.infinityz.minigame.listeners;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import me.infinityz.minigame.tasks.GameLoop;
import net.md_5.bungee.api.ChatColor;

public class GlobalListener implements Listener {

    private UHC instance;
    public static int time = 0;

    public GlobalListener(UHC instance) {
        this.instance = instance;
    }

    @EventHandler
    public void joinMessage(PlayerJoinEvent e) {
        e.setJoinMessage("");
        e.getPlayer().sendMessage(ChatColor.BLUE + "Discord! discord.gg/4AdHqV9\n" + ChatColor.AQUA
                + "Twitter! twitter.com/NoobstersUHC\n" + ChatColor.GOLD + "Donations! noobsters.buycraft.net");

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
    public void strengthFix(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() != EntityType.PLAYER)
            return;
        var damager = (Player) e.getDamager();
        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            var strengthAmplifier = damager.getActivePotionEffects().stream()
                    .filter(type -> type.getType().equals(PotionEffectType.INCREASE_DAMAGE)).findAny().get()
                    .getAmplifier() + 1;
            var differential = strengthAmplifier * 1.5;
            e.setDamage(e.getDamage() - differential);
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent e) {
        if (instance.globalmute && !e.getPlayer().hasPermission("staff.perm")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Globalmute is Enabled.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpecChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR && !e.getPlayer().hasPermission("staff.perm")) {
            e.setCancelled(true);

            e.getRecipients().stream()
                    .filter(it -> it.getGameMode() == GameMode.SPECTATOR || it.hasPermission("staff.perm"))
                    .forEach(specs -> {
                        specs.sendMessage(ChatColor.GRAY + "[SPEC] " + e.getFormat());
                    });
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
            new GameLoop(instance).runTaskTimerAsynchronously(instance, 0L, 20L);

        }, 20 * 10);

    }

}