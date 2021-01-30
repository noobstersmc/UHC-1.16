package me.infinityz.minigame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.NonNull;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.scoreboard.IScoreboard;
import me.infinityz.minigame.scoreboard.LobbyScoreboard;

public class LobbyListeners implements Listener {
    private @NonNull UHC instance;

    public LobbyListeners(UHC instance) {
    this.instance = instance;
        final var world = Bukkit.getWorld("lobby");
        world.setSpawnLocation(new Location(world, 0, 66, 0, 90, 0));
        Game.setLobbySpawn(world.getSpawnLocation());
    }

    /*
     * Events cancelled during lobby starts.
     */

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoinTeleport(PlayerJoinEvent e) {
        var player = e.getPlayer();
        var lobby = Game.getLobbySpawn();

        if (!player.hasPlayedBefore()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(lobby);

        }
        /*
         * Teleport to spawn and set to survival mode
         */
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);

    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

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
