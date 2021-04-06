package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class BleedingSweets extends IGamemode implements Listener {
    private UHC instance;

    public BleedingSweets(UHC instance) {
        super("BleedingSweets", "Players drop Books, Diamond, Gold, String and Arrows.", Material.SWEET_BERRIES);
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
    public void onDeath(PlayerDeathEvent e){
        e.getDrops().add(new ItemStack(Material.DIAMOND, 1));
        e.getDrops().add(new ItemStack(Material.GOLD_INGOT, 5));
        e.getDrops().add(new ItemStack(Material.BOOK, 1));
        e.getDrops().add(new ItemStack(Material.STRING, 2));
        e.getDrops().add(new ItemStack(Material.ARROW, 16));
    }

}