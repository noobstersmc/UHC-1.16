package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.gamemodes.IGamemode;

public class InfiniteEnchanter extends IGamemode implements Listener {
    private UHC instance;

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
                players.getInventory().addItem(new ItemStack(Material.LAPIS_BLOCK, 16));
                players.setLevel(10000);
            });
        }
  
    }



}