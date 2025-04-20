package me.noobsters.minigame.gui;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

/**
 * Manager for handling inventory events to FastInv
 *
 * @author MrMicky
 */
public final class RapidManager {

    private static final AtomicBoolean REGISTER = new AtomicBoolean(false);

    /**
     * Register events for FastInv
     *
     * @param plugin Plugin to register
     * @throws NullPointerException  if plugin is null
     * @throws IllegalStateException if FastInv is already registered
     */
    public static void register(Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        if (REGISTER.getAndSet(true)) {
            throw new IllegalStateException("FastInv is already registered");
        }

        Bukkit.getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
    }

    /**
     * Close all open FastInv inventories
     */
    public static void closeAll() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getOpenInventory().getTopInventory().getHolder() instanceof RapidInv)
                .forEach(Player::closeInventory);
    }

    public static final class InventoryListener implements Listener {

        private final Plugin plugin;

        public InventoryListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClick(InventoryClickEvent e) {
            if (e.getInventory().getHolder() instanceof RapidInv && e.getClickedInventory() != null) {
                RapidInv inv = (RapidInv) e.getInventory().getHolder();

                boolean wasCancelled = e.isCancelled();
                e.setCancelled(true);

                inv.handleClick(e);

                // This prevent to uncancel the event if an other plugin cancelled it before
                if (!wasCancelled && !e.isCancelled()) {
                    e.setCancelled(false);
                }
            }
        }

        // PATCHED BY JCEDENO
        @EventHandler(priority = EventPriority.HIGHEST)
        public void preventMoving(InventoryClickEvent e) {
            var clickedInventory = e.getClickedInventory();
            var inv = e.getInventory();
            if (isRapidInventory(inv) || isRapidInventory(clickedInventory)) {
                e.setCancelled(true);
            }

        }

        private boolean isRapidInventory(Inventory inv) {
            return inv != null && inv.getHolder() instanceof RapidInv;
        }

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent e) {
            if (e.getInventory().getHolder() instanceof RapidInv) {
                RapidInv inv = (RapidInv) e.getInventory().getHolder();

                inv.handleOpen(e);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            if (e.getInventory().getHolder() instanceof RapidInv) {
                RapidInv inv = (RapidInv) e.getInventory().getHolder();

                if (inv.handleClose(e)) {
                    Bukkit.getScheduler().runTask(plugin, () -> inv.open((Player) e.getPlayer()));
                }
                // Clean up children on close.
                if (inv.parentInventory != null) {
                    inv.parentInventory.children.remove(inv);
                }
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent e) {
            if (e.getPlugin() == plugin) {
                closeAll();

                REGISTER.set(false);
            }
        }
    }
}
