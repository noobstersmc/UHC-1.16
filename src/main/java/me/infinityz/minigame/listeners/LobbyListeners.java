package me.infinityz.minigame.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.NonNull;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.scoreboard.IScoreboard;
import me.infinityz.minigame.scoreboard.LobbyScoreboard;

public class LobbyListeners implements Listener {
    private @NonNull UHC instance;

    private List<Location> portals;
    private int count = 0;

    public LobbyListeners(UHC instance) {
        this.instance = instance;
        final var world = Bukkit.getWorld("lobby");
        world.setSpawnLocation(new Location(world, 121, 66, 0, 90, 0));

        portals = List.of(world.getBlockAt(89, 66, -37).getLocation(), world.getBlockAt(96, 66, -22).getLocation(),
                world.getBlockAt(103, 66, -7).getLocation(), world.getBlockAt(103, 66, 7).getLocation(),
                world.getBlockAt(96, 66, 22).getLocation(), world.getBlockAt(89, 66, 37).getLocation());

        portals.forEach(l -> {
            l.setYaw(90);
            l.setPitch(0);
            l.add(0.5, 0.5, 0.5);
        });
    }

    /*
     * Events cancelled during lobby starts.
     */

    public void teleportToPortal(final Player player) {
        player.teleport(portals.get(count >= 5 ? (count = 0) : count++));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoinTeleport(PlayerJoinEvent e) {
        var player = e.getPlayer();

        if (!player.hasPlayedBefore()) {
            player.setGameMode(GameMode.SURVIVAL);
            teleportToPortal(player);

        }
        /*
         * Teleport to spawn and set to survival mode
         */
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 999999, 69, false, false));

    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == DamageCause.VOID && e.getEntity() instanceof Player) {
            teleportToPortal((Player) e.getEntity());
        }
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

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!e.getEntity().hasPermission("lobby.edit"))
            e.setCancelled(true);
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
