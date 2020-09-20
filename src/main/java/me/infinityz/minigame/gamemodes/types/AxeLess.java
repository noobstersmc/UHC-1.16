package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class AxeLess extends IGamemode implements Listener {
    private UHC instance;

    public AxeLess(UHC instance) {
        super("AxeLess", "Axes are disabled.");
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
    public void onCraft(CraftItemEvent e) {
        if (isAxe(e.getCurrentItem()))
            e.setCancelled(true);

    }

    @EventHandler
    public void onDrop(ItemSpawnEvent e) {
        if (isAxe(e.getEntity().getItemStack()))
            e.setCancelled(true);

    }

    @EventHandler
    public void onHold(PlayerInteractEvent e) {
        if(e.getAction() == Action.PHYSICAL)
            return;
        var item = e.getItem();      
        if(isAxe(item))
            item.setType(Material.AIR);
    }

    private boolean isAxe(ItemStack e){
        return e != null && e.getType().toString().contains("_AXE");
    }

}