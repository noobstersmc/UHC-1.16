package me.infinityz.minigame.gamemodes.types;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.mrmicky.fastinv.ItemBuilder;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.gamemodes.IGamemode;

public class InfiniteEnchanter extends IGamemode implements Listener {
    private UHC instance;
    private final ItemStack lapis = new ItemBuilder(Material.LAPIS_LAZULI).amount(64).build();

    public InfiniteEnchanter(UHC instance) {
        super("InfiniteEnchanter", "Unlimited enchanted resources.");
        this.instance = instance;

    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onStart(GameTickEvent e) {
        if (e.getSecond() == 1) {
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.getInventory().addItem(new ItemStack(Material.BOOK, 32));
                players.getInventory().addItem(new ItemStack(Material.BOOKSHELF, 32));
                players.getInventory().addItem(new ItemStack(Material.ANVIL, 8));
                players.getInventory().addItem(new ItemStack(Material.ENCHANTING_TABLE, 8));
                players.setLevel(100);
            });
        }

    }

    @EventHandler
    public void openInventoryEvent(InventoryOpenEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, lapis);
        }
    }

    @EventHandler
    public void closeInventoryEvent(InventoryCloseEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, null);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        ItemStack item = e.getCurrentItem();
        if (inv instanceof EnchantingInventory) {

            if (item.getType().equals(lapis.getType())) {
                e.setCancelled(true);
            } else {
                e.getInventory().setItem(1, lapis);
            }
        }
    }

    @EventHandler
    public void onAnvilDamage(AnvilDamagedEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onExpChange(PlayerLevelChangeEvent e) {
        Bukkit.getScheduler().runTaskLater(instance, () -> e.getPlayer().setLevel(100), 0);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().setLevel(100);
    }

}