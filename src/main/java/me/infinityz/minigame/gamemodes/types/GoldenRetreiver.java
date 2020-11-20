package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class GoldenRetreiver extends IGamemode implements Listener {
    private UHC instance;

    public GoldenRetreiver(UHC instance) {
        super("GoldenRetreiver", "Players drop GoldenHeads.");
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
        ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta im = goldenHead.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "Golden Head");
        goldenHead.setItemMeta(im);
        e.getDrops().add(goldenHead);

    }

}