package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class SwordLess extends IGamemode implements Listener {
    private UHC instance;

    public SwordLess(UHC instance) {
        super("SwordLess", "Swords are disabled.");
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
        if (e.getCurrentItem().getType().toString().contains("_SWORD"))
            e.setCancelled(true);

    }

    @EventHandler
    public void onDrop(ItemSpawnEvent e) {
        if (e.getEntity().getItemStack().getType().toString().contains("_SWORD"))
            e.setCancelled(true);

    }

    @EventHandler
    public void onHold(PlayerInteractEvent e) {
        if(e.getAction() == Action.PHYSICAL)
            return;        
        if(e.getItem() != null && e.getItem().getType().toString().contains("_SWORD"))
            e.getItem().setType(Material.AIR);
    }

}