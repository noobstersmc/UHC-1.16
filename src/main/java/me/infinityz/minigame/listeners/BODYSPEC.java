package me.infinityz.minigame.listeners;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.mrmicky.fastinv.FastInv;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class BODYSPEC{
    private @NonNull UHC instance;

    /**
     * Body spec experimental code:
     */

    @EventHandler
    public void onRightClickAir(PlayerInteractEvent e) {
        var player = e.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            var inv = new FastInv(9 * 6, ChatColor.GOLD + "Spectator GUI");
            inv.open(player);
        }
    }

    @EventHandler
    public void onMoveSpecNoClip(PlayerMoveEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.SPECTATOR)
            return;
        if (e.getPlayer().hasPermission("uhc.spec.noclip"))
            return;
        if (e.getTo().getBlock().getType() != Material.AIR) {
            e.setCancelled(true);
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 10000));
        }
    }

    @EventHandler
    public void onStopSpectating(PlayerStopSpectatingEntityEvent e) {
        // TODO: Right now this causes the client to glitch up.
        var player = e.getPlayer();
        player.setPlayerListName(e.getPlayer().getName());

        if (!player.hasPermission("uhc.spec.unmount")) {
            e.setCancelled(true);
        } else {
            player.teleportAsync(
                    player.getWorld().getHighestBlockAt(player.getLocation()).getLocation().add(0.0, 2.0, 0.0));
        }
    }

    @EventHandler
    public void onStartSpec(PlayerStartSpectatingEntityEvent e) {
        // TODO: Right now this causes the client to glitch up.
        if (e.getNewSpectatorTarget().getType() != EntityType.PLAYER) {
            e.setCancelled(true);
            return;
        }
        var player = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(instance, () -> player.closeInventory(), 1l);
        if (player.hasPermission("uhc.spec.name")) {
            player.setPlayerListName(player.getName() + ChatColor.RESET + "" + ChatColor.DARK_GRAY + " ("
                    + e.getNewSpectatorTarget().getName() + ")");
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == TeleportCause.SPECTATE) {
            e.setCancelled(true);
            var player = e.getPlayer();
            e.getTo().getNearbyPlayers(1.0).stream().findFirst().ifPresent(target -> {
                player.setSpectatorTarget(target);
            });
        }
    }

}