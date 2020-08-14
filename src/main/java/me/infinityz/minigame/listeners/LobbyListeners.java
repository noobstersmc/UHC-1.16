package me.infinityz.minigame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Statistic;
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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.scoreboard.IScoreboard;
import me.infinityz.minigame.scoreboard.LobbyScoreboard;

@RequiredArgsConstructor
public class LobbyListeners implements Listener {
    private @NonNull UHC instance;
    private Location spawnLocation = Bukkit.getWorlds().get(0).getHighestBlockAt(0, 0).getLocation().add(0.0, 15.0, 0);

    /*
     * Events cancelled during lobby starts.
     */
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
    /*
     * Events cancelled during lobby ends.
     */

    @EventHandler
    public void onJoinTeleport(PlayerJoinEvent e) {
        // TODO: Sometimes teleport location doesn't work
        var player = e.getPlayer();
        /*
         * Teleport to spawn and set to survival mode
         */
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(spawnLocation);
        /*
         * Clean the player's inventory, XP, Potions, and heal them
         */
        player.setLevel(0);
        player.setExp(0f);
        player.setTotalExperience(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(20.0F);
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

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
        IScoreboard sb = instance.getScoreboardManager().getFastboardMap()
                .remove(e.getPlayer().getUniqueId().toString());
        if (sb != null) {
            sb.delete();
        }
    }

}
