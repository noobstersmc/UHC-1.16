package me.infinityz.minigame.gamemodes.types;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class UnDamaxe extends IGamemode implements Listener {
    private UHC instance;

    public UnDamaxe(UHC instance) {
        super("UnDamaxe", "Axe damage is disabled.");
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
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            var player = (Player) e.getDamager();
            if (isAxe(player.getInventory().getItemInMainHand()))
                e.setCancelled(true);

        }
    }

    private boolean isAxe(ItemStack e) {
        return e != null && e.getType().toString().contains("_AXE");
    }

}