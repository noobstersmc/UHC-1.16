package me.infinityz.minigame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.scoreboard.IScoreboard;
import me.infinityz.minigame.scoreboard.LobbyScoreboard;

@RequiredArgsConstructor
public class LobbyListeners implements Listener {
    private @NonNull UHC instance;
    private Location spawnLoc = Bukkit.getWorlds().get(0).getBlockAt(0, 142, 0).getLocation();

    /*
     * Events cancelled during lobby starts.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().hasPermission("lobby.edit"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().hasPermission("lobby.edit"))
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
        if (!e.getPlayer().hasPermission("lobby.edit"))
            if (!(e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.LOOM))
                e.setCancelled(true);

    }

    @EventHandler
    public void invClick(InventoryClickEvent e) {
        if (e.getInventory().getType() == InventoryType.LOOM) {
            if (e.getSlot() == 3) {
                e.getInventory().setItem(0, e.getCurrentItem().clone());
            }
        }

    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!e.getEntity().hasPermission("lobby.edit"))
            e.setCancelled(true);
    }
    /*
     * Events cancelled during lobby ends.
     */

    @EventHandler
    public void onJoinTeleport(PlayerJoinEvent e) {
        var player = e.getPlayer();
        /*
         * Teleport to spawn and set to survival mode
         */
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20*999999, 69, false, false));
        if (!player.hasPlayedBefore()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(spawnLoc);
        }

    }

    /*
     * Take care of the scoreboard
     */
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
        e.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
        IScoreboard sb = instance.getScoreboardManager().getFastboardMap()
                .remove(e.getPlayer().getUniqueId().toString());
        if (sb != null) {
            sb.delete();
        }
    }

}
