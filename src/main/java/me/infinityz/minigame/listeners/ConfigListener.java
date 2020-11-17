package me.infinityz.minigame.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunksManager;
import net.md_5.bungee.api.ChatColor;

public class ConfigListener implements Listener {

    private UHC instance;

    public ConfigListener(UHC instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        if(!instance.getGame().isTearsNerf()) return;
        var stack = e.getEntity().getItemStack();
        var type = stack.getType();
        if (type == Material.GHAST_TEAR) {
            stack.setType(Material.GOLD_INGOT);
        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent e) {
        if (!instance.getGame().isNether() && e.getTo().getWorld().getEnvironment() == Environment.NETHER) {
            e.getPlayer().sendMessage(ChatColor.RED + "Nether is currently disabled!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent e) {
        if (!instance.getGame().isNether() && e.getReason() == CreateReason.FIRE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoinInNether(PlayerJoinEvent e) {
        if (!instance.getGame().isNether()) {// If nether is disabled
            var player = e.getPlayer();
            // If player's world is nether, scatter them in the overworld.
            if (player.getWorld().getEnvironment() == Environment.NETHER) {
                var worldToTeleport = Bukkit.getWorlds().get(0);
                var radius = (int) worldToTeleport.getWorldBorder().getSize() / 2;
                // Teleport Async to save resources.
                player.teleportAsync(
                        ChunksManager.centerLocation(ChunksManager.findScatterLocation(worldToTeleport, radius)));
            }
        }
    }

    /**
     * APPLE RATE
     */
    @EventHandler
    public void onLeaf(LeavesDecayEvent e) {
        if (instance.getGame().getApplerate() == 0.5)
            return;
        if (Math.random() <= (instance.getGame().getApplerate() / 100)) {
            e.getBlock().setType(Material.AIR);
            e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(),
                    new ItemStack(Material.APPLE));
        }

    }

    /**
     * Strength nerf
     */
    @EventHandler
    public void strengthFix(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() != EntityType.PLAYER || !instance.getGame().isStrengthNerf())
            return;
        var damager = (Player) e.getDamager();
        var strength = damager.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
        if (strength != null) {
            var strengthAmplifier = 1 + strength.getAmplifier();
            var differential = strengthAmplifier * 1.5;
            e.setDamage(e.getDamage() - differential);
        }

    }

    /**
     * Nerfed critical damage to + 2 instead of 1.5x
     */
    @EventHandler(priority = EventPriority.LOW)
    public void nerfCriticalDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER || e.getDamager().getType() != EntityType.PLAYER
                || !instance.getGame().isCriticalNerf())
            return;
        var damager = (Player) e.getDamager();
        if (isCritical(damager)) {
            final var damage = e.getDamage();
            final var backToBaseDamage = damage / 1.5;
            final var damageDifferential = damage - backToBaseDamage;
            if (damageDifferential > 2)
                e.setDamage(backToBaseDamage + 4);

        }
    }

    @SuppressWarnings("all")
    private boolean isCritical(Player player) {
        return player.getFallDistance() > 0.0F && !player.isOnGround() && !player.isInsideVehicle()
                && !player.hasPotionEffect(PotionEffectType.BLINDNESS)
                && player.getLocation().getBlock().getType() != Material.LADDER
                && player.getLocation().getBlock().getType() != Material.VINE;
    }

    /*
     * End portal cancel creation
     */

    @EventHandler
    public void onInteractWithPortal(PlayerInteractEvent e) {
        if (!instance.getGame().isEnd() && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getItem() != null
                && e.getItem().getType() == Material.ENDER_EYE)
            e.setCancelled(true);

    }

    /** INCREASED TRIDENT DROP START */

    @EventHandler
    public void onDrownedDeath(EntityDeathEvent e) {
        if (e.getEntity().getType() != EntityType.DROWNED)
            return;
        var drowned = (Drowned) e.getEntity();
        var equipment = drowned.getEquipment();
        var mainHand = drowned.getEquipment().getItemInMainHand().clone();
        var offHand = drowned.getEquipment().getItemInOffHand().clone();
        equipment.clear();

        if (mainHand.getType() != Material.AIR) {
            if (mainHand.getType() == Material.TRIDENT) {
                Damageable dmg = (Damageable) mainHand.getItemMeta();
                dmg.setDamage(new Random().nextInt(125));
                mainHand.setItemMeta((ItemMeta) dmg);

            }
            drowned.getLocation().getWorld().dropItemNaturally(drowned.getLocation(), mainHand);

        }
        if (offHand.getType() != Material.AIR) {
            if (offHand.getType() == Material.TRIDENT) {
                Damageable dmg = (Damageable) offHand.getItemMeta();
                dmg.setDamage(new Random().nextInt(125));
                offHand.setItemMeta((ItemMeta) dmg);
            }
            drowned.getLocation().getWorld().dropItemNaturally(drowned.getLocation(), offHand);
        }
    }

    /*
     * BED NERF
     */

    private static String FULL_MESSAGE = ChatColor.translateAlternateColorCodes('&',
            "&fServer is full! \n &aUpgrade your rank at &6noobsters.buycraft.net");

    @EventHandler
    public void nerfBedExplosion(PlayerBedEnterEvent e) {
        if (instance.getGame().isBedsNerf() && e.getBed().getWorld().getEnvironment() == Environment.NETHER) {
            e.setCancelled(true);
            e.setUseBed(Result.DENY);
            e.getBed().setType(Material.AIR);
            e.getBed().getLocation().createExplosion(2.0f, true, true);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void slotLimit(PlayerLoginEvent e) {
        final var player = e.getPlayer();
        if (!shoudLogin(player))
            e.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_FULL, FULL_MESSAGE);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void slotLimit(PlayerJoinEvent e) {
        final var player = e.getPlayer();
        if (!shoudLogin(player))
            player.kickPlayer(FULL_MESSAGE);

    }

    private boolean shoudLogin(final Player player) {
        final var online = Bukkit.getOnlinePlayers().size();
        final var maxSlots = instance.getGame().getUhcslots();

        return online <= maxSlots || player.hasPermission("reserved.slot");
    }

}
