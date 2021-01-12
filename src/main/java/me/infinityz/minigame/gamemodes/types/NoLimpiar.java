package me.infinityz.minigame.gamemodes.types;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitTask;

import gnu.trove.map.hash.THashMap;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class NoLimpiar extends IGamemode implements Listener {
    private UHC instance;
    private THashMap<UUID, Long> noCleanTimeMap;
    private BukkitTask task;
    private static String NO_CLEAN_OBTAINED = ChatColor.RED
            + "You will be invincible for the next 20 seconds.";
    private static String NO_CLEAN_PLAYER_PROTECTED = ChatColor.RED + "You can't damage %s for the next %.1f"
            + "s";
    private static String NO_CLEAN_LOST = ChatColor.RED + "You have lost your invincibility.";
    private static String NO_CLEAN_OVER_ACTIONBAR = ChatColor.YELLOW + " ⚠ ";
    private static String NO_CLEAN_STATUS_ACTIONBAR = ChatColor.GREEN + "Clean Protection: %.1f" + "s";

    public NoLimpiar(UHC instance) {
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
                var differential = entry.getValue() - System.currentTimeMillis();
                if (differential <= 0) {
                    iterator.remove();
                    if (player != null && player.isOnline()) {
                        player.sendMessage(NO_CLEAN_LOST);
                        player.sendActionBar(NO_CLEAN_OVER_ACTIONBAR);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                    }
                } else if (player != null && player.isOnline()) {
                    player.sendActionBar(String.format(NO_CLEAN_STATUS_ACTIONBAR, differential / 1000.0D));
                }
            }
        }, 2L, 2L);

        instance.getListenerManager().registerListener(this);
        setEnabled(true);

        return true;
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
            if (player != null && player.isOnline())
                player.sendActionBar(NO_CLEAN_OVER_ACTIONBAR);
        });
        noCleanTimeMap.clear();
        noCleanTimeMap = null;
        task.cancel();
        task = null;

        setEnabled(false);
        return true;
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
            }else if(damager != null && noCleanTimeMap.contains(damager.getUniqueId())){                
                noCleanTimeMap.remove(damager.getUniqueId());
                damager.sendMessage(NO_CLEAN_LOST);
                damager.sendActionBar(NO_CLEAN_OVER_ACTIONBAR);
            }

        }
    }
    @EventHandler
    public void onAllDamage(EntityDamageEvent e){
        if(e.getEntityType() == EntityType.PLAYER){
            var player = (Player)e.getEntity();
            var time = noCleanTimeMap.get(player.getUniqueId());
            if(time != null){
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

}
