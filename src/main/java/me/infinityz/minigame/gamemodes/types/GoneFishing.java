package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.gamemodes.IGamemode;

public class GoneFishing extends IGamemode implements Listener {
    private UHC instance;

    public GoneFishing(UHC instance) {
        super("GoneFishing", "Go fishing.");
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
                var item = new ItemStack(Material.FISHING_ROD);
                var meta = item.getItemMeta();
                meta.addEnchant(Enchantment.LURE, 69420, true);
                meta.addEnchant(Enchantment.LUCK, 69420, true);
                meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
                meta.setUnbreakable(true);
                item.setItemMeta(meta);
                players.getInventory().addItem(item);
            });
        }
  
    }



}