package me.noobsters.minigame.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.enums.Stage;
import net.md_5.bungee.api.ChatColor;

public class GracePeriodListeners implements Listener {

    private UHC instance;
    private static String IPVP_DISABLED = ChatColor.RED + "iPvP is currently disabled.";
    private static String PVP_DISABLED = ChatColor.RED + "PvP is currently disabled.";

    public GracePeriodListeners(UHC instance) {
        this.instance = instance;
    }

    /**
     * PVP boolean code
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPVP(EntityDamageByEntityEvent e) {
        if (e.getEntity() == e.getDamager() || instance.getGame().isPvp()
                || instance.getGameStage().equals(Stage.LOBBY))
            return;

        if (e.getEntity() instanceof Player) {
            Player p2 = null;
            if (e.getDamager() instanceof Player) {
                p2 = (Player) e.getDamager();
            } else if (e.getDamager() instanceof Projectile) {
                Projectile proj = (Projectile) e.getDamager();
                if (proj.getShooter() instanceof Player) {
                    p2 = (Player) proj.getShooter();
                }
            }
            if (p2 != null) {
                p2.sendMessage(PVP_DISABLED);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHoldEgg(PlayerInteractEvent e) {
        if (instance.getGame().isPvp())
            return;
        final var player = e.getPlayer();
        final var item = player.getInventory().getItemInMainHand();
        if (item.getType().toString().contains("SPAWN")) {
            e.setCancelled(true);
            player.sendMessage(IPVP_DISABLED);
        }
    }

    @EventHandler
    public void onDamageVillager(EntityDamageByEntityEvent e) {
        if (!instance.getGame().isPvp() && e.getEntity() instanceof Villager) {
            e.setCancelled(true);
            if (e.getDamager() instanceof Player)
                e.getDamager().sendMessage(ChatColor.RED + "Villagers are invincible in no-pvp period.");
        }
    }

    @EventHandler
    public void onDamageVillagerV2(EntityDamageEvent e) {
        if (!instance.getGame().isPvp() && e.getEntity() instanceof Villager)
            e.setCancelled(true);
    }

    @EventHandler
    public void onFireiPvp(PlayerInteractEvent e) {
        if (instance.getGame().isPvp())
            return;
        var player = e.getPlayer();
        final var item = player.getInventory().getItemInMainHand().getType();

        var playerIPvP = player.getLocation().getNearbyPlayers(5).stream().filter(p -> !isTeamMate(player, p)
            && p.getUniqueId() != player.getUniqueId() && p.getGameMode() == GameMode.SURVIVAL).findFirst();

            if(playerIPvP.isPresent()){

                if (item.equals(Material.FLINT_AND_STEEL) || item.equals(Material.LAVA_BUCKET)
                || item.equals(Material.FIRE_CHARGE) || item.equals(Material.TNT_MINECART)
                || item.equals(Material.ENDER_PEARL)) {
                    e.setCancelled(true);
                    player.sendMessage(IPVP_DISABLED);

                }
            }

    }

    @EventHandler
    public void onPlaceiPvp(BlockPlaceEvent e) {
        if (instance.getGame().isPvp())
            return;
        var player = e.getPlayer();
        var block = e.getBlock().getType();

        if (player.getWorld().getEnvironment() == Environment.NETHER && block.toString().contains("BED")) {
            e.setCancelled(true);
            return;
        }

        var playerIPvP = player.getLocation().getNearbyPlayers(5).stream().filter(p -> !isTeamMate(player, p)
            && p.getUniqueId() != player.getUniqueId() && p.getGameMode() == GameMode.SURVIVAL).findFirst();

            if(playerIPvP.isPresent()){

                if(block.equals(Material.SAND) || block.equals(Material.GRAVEL) || block.toString().contains("POWDER")
                || block.toString().contains("CAMPFIRE") || block.toString().contains("ANVIL")
                || block.equals(Material.MAGMA_BLOCK) || block.equals(Material.CACTUS) || block.equals(Material.TNT)) {
                    e.setCancelled(true);
                    player.sendMessage(IPVP_DISABLED);

                }
            }

    }

    @EventHandler
    public void onBreakiPvp(BlockBreakEvent e) {
        if (instance.getGame().isPvp())
            return;
        var block = e.getBlock().getType();
        var player = e.getPlayer();
        var playerIPvP = e.getBlock().getLocation().getNearbyPlayers(5).stream().filter(p -> !isTeamMate(player, p)
            && p.getUniqueId() != player.getUniqueId() && p.getGameMode() == GameMode.SURVIVAL).findFirst();

            if(playerIPvP.isPresent()){

                if(block.equals(Material.FURNACE) || block.toString().contains("ANVIL")
                || block.toString().contains("TABLE")) {
                    e.setCancelled(true);
                    player.sendMessage(IPVP_DISABLED);

                }
            }

    }

    private boolean isTeamMate(Player p1, Player p2) {
        var p1Team = instance.getTeamManger().getPlayerTeam(p1.getUniqueId());
        if (p1Team != null && p1Team.isMember(p2.getUniqueId()) && p1.getUniqueId() != p2.getUniqueId())
            return true;
        return false;
    }

}