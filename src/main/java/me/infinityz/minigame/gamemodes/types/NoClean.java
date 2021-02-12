package me.infinityz.minigame.gamemodes.types;

import java.awt.Color;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import gnu.trove.map.hash.THashMap;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class NoClean extends IGamemode implements Listener {
    private UHC instance;
    private THashMap<UUID, Long> noCleanTimeMap;
    private BukkitTask task;
    private static String NO_CLEAN_OBTAINED = ChatColor.RED + "You will be invincible for the next 20 seconds.";
    private static String NO_CLEAN_PLAYER_PROTECTED = ChatColor.RED + "You can't damage %s for the next %.1f" + "s";
    private static String NO_CLEAN_LOST = ChatColor.RED + "You have lost your invincibility.";
    private static String NO_CLEAN_OVER_ACTIONBAR = ChatColor.YELLOW + " ⚠ ";
    private static String NO_CLEAN_STATUS_ACTIONBAR = ChatColor.GREEN + "Clean Protection: %.1f" + "s";
    private static String NO_CLEAN_SAFELOOT = ChatColor.RED + "Don't disturb %s safe loot is enabled.";

    public NoClean(UHC instance) {
        super("No Clean", "Limpiar es inmoral.");
        this.instance = instance;
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled()) {
            return false;
        }
        noCleanTimeMap = new THashMap<>();
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            var iterator = noCleanTimeMap.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                var player = Bukkit.getPlayer(entry.getKey());
                if (player == null || !player.isOnline()) {
                    iterator.remove();
                    return;
                }
                runUpdateColor(player, entry.getValue(), iterator);
            }
        }, 2L, 2L);

        instance.getListenerManager().registerListener(this);
        setEnabled(true);

        return true;
    }

    public void runUpdateColor(Player player, Long time, Iterator<? extends Object> iterator) {
        var mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        var split_id = player.getUniqueId().toString().split("-");
        var color_id = split_id[0] + split_id[1];
        var team = mainScoreboard.getTeam(color_id);
        if (team == null) {
            team = mainScoreboard.registerNewTeam(color_id);
        }
        if (!team.hasEntry(player.getName()))
            team.addEntry(player.getName());

        var differential = time - System.currentTimeMillis();

        if (differential <= 0) {
            team.removeEntry(player.getName());
            iterator.remove();
            if (player != null && player.isOnline()) {
                player.sendMessage(NO_CLEAN_LOST);
                player.sendActionBar(NO_CLEAN_OVER_ACTIONBAR);

                Bukkit.getScheduler().runTask(instance, () -> player.removePotionEffect(PotionEffectType.GLOWING));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
            }
        } else if (player != null && player.isOnline()) {
            player.sendActionBar(String.format(NO_CLEAN_STATUS_ACTIONBAR, differential / 1000.0D));
            var secs = (int) (differential / 1000);
            Bukkit.getScheduler().runTask(instance, () -> {
                player.removePotionEffect(PotionEffectType.GLOWING);
                System.out.println("Update of glowing");
                player.addPotionEffect(PotionEffectType.GLOWING.createEffect(20, 1));
            });
            var col = Color.getHSBColor((secs * 2.5F) / 100.0F, 1.0F, 1.0F);
            team.setDisplayName(ChatColor.RESET + " ");
            team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
            team.setPrefix(ChatColor.of(col) + "[" + secs + "s] ");

            if (secs >= 15) {
                System.out.println("Update of color");
                team.setColor(org.bukkit.ChatColor.GREEN);
            } else if (secs >= 10) {
                System.out.println("Update of color");
                team.setColor(org.bukkit.ChatColor.YELLOW);
            } else if (secs >= 5) {
                System.out.println("Update of color");
                team.setColor(org.bukkit.ChatColor.RED);
            } else if (secs >= 0) {
                System.out.println("Update of color");
                team.setColor(org.bukkit.ChatColor.AQUA);
            }
        }
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled()) {
            return false;
        }
        instance.getListenerManager().unregisterListener(this);
        // Clean un ram
        noCleanTimeMap.keySet().forEach(key -> {
            var player = Bukkit.getPlayer(key);
            if (player != null && player.isOnline()) {
                player.sendActionBar(NO_CLEAN_OVER_ACTIONBAR);
                Bukkit.getScheduler().runTask(instance, () -> player.removePotionEffect(PotionEffectType.GLOWING));

            }
        });
        noCleanTimeMap.clear();
        noCleanTimeMap = null;
        task.cancel();
        task = null;

        setEnabled(false);
        return true;
    }

    private boolean isTeamMate(Player p1, Player p2) {
        var p1Team = instance.getTeamManger().getPlayerTeam(p1.getUniqueId());
        if (p1Team != null && p1Team.isMember(p2.getUniqueId()) && p1.getUniqueId() != p2.getUniqueId())
            return true;
        return false;
    }

    @EventHandler
    public void onOpenChest(InventoryOpenEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR)
            return;
        if (e.getInventory().getType().toString().equals("CHEST")) {
            var cleaner = (Player) e.getPlayer();
            var playerProtected = cleaner.getLocation().getNearbyPlayers(5).stream()
                    .filter(p -> !isTeamMate(cleaner, p) && p.getUniqueId() != cleaner.getUniqueId()
                            && p.getGameMode() == GameMode.SURVIVAL && noCleanTimeMap.contains(p.getUniqueId()))
                    .findFirst();
            if (playerProtected.isPresent()) {
                cleaner.sendMessage(String.format(NO_CLEAN_SAFELOOT, playerProtected.get().getName().toString()));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        var killer = e.getEntity().getKiller();
        if (killer != null) {
            noCleanTimeMap.put(killer.getUniqueId(), System.currentTimeMillis() + 20_000);
            killer.sendMessage(NO_CLEAN_OBTAINED);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            var damagerFromProj = getProjectileOwner(e);
            var damager = damagerFromProj != null ? damagerFromProj
                    : (e.getDamager() instanceof Player ? (Player) e.getDamager() : null);
            var time = noCleanTimeMap.get(e.getEntity().getUniqueId());

            if (damager != null && time != null) {
                damager.sendMessage(String.format(NO_CLEAN_PLAYER_PROTECTED, e.getEntity().getName(),
                        (time - System.currentTimeMillis()) / 1000.0D));
                e.setCancelled(true);
            } else if (damager != null && noCleanTimeMap.contains(damager.getUniqueId())) {
                noCleanTimeMap.remove(damager.getUniqueId());
                damager.sendMessage(NO_CLEAN_LOST);
                damager.sendActionBar(NO_CLEAN_OVER_ACTIONBAR);
                Bukkit.getScheduler().runTask(instance, () -> damager.removePotionEffect(PotionEffectType.GLOWING));
            }

        }
    }

    @EventHandler
    public void onAllDamage(EntityDamageEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            var player = (Player) e.getEntity();
            var time = noCleanTimeMap.get(player.getUniqueId());
            if (time != null) {
                e.setCancelled(true);
            }
        }
    }

    private Player getProjectileOwner(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Projectile) {
            var proj = (Projectile) e.getDamager();
            if (proj.getShooter() != null && proj.getShooter() instanceof Player)
                return (Player) proj.getShooter();
        }
        return null;
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            var cleaner = (Player) e.getEntity();
            var playerProtected = cleaner.getLocation().getNearbyPlayers(5).stream()
                    .filter(p -> !isTeamMate(cleaner, p) && p.getUniqueId() != cleaner.getUniqueId()
                            && p.getGameMode() == GameMode.SURVIVAL && noCleanTimeMap.contains(p.getUniqueId()))
                    .findFirst();
            if (playerProtected.isPresent()) {
                cleaner.sendActionBar(String.format(NO_CLEAN_SAFELOOT, playerProtected.get().getName().toString()));
                e.setCancelled(true);
            }

        }

    }

    @EventHandler
    public void noCleanZone(PlayerInteractEvent e) {

        var cleaner = e.getPlayer();
        final var item = cleaner.getInventory().getItemInMainHand().getType();

        var playerProtected = cleaner.getLocation().getNearbyPlayers(5).stream()
                .filter(p -> !isTeamMate(cleaner, p) && p.getUniqueId() != cleaner.getUniqueId()
                        && p.getGameMode() == GameMode.SURVIVAL && noCleanTimeMap.contains(p.getUniqueId()))
                .findFirst();
        if (playerProtected.isPresent()) {

            if (item.equals(Material.FLINT_AND_STEEL) || item.equals(Material.LAVA_BUCKET)
                    || item.equals(Material.FIRE_CHARGE)) {
                e.setCancelled(true);
                cleaner.sendMessage(String.format(NO_CLEAN_SAFELOOT, playerProtected.get().getName().toString()));

            }
        }

    }

}
