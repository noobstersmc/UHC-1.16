package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class ShieldLess extends IGamemode implements Listener {
    private UHC instance;

    public ShieldLess(UHC instance) {
        super("ShieldLess", "Shields are disabled.");
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
    public void onCraft(PrepareItemCraftEvent e) {
        var recipe = e.getRecipe();
        if(recipe != null && recipe.getResult().getType() == Material.SHIELD){
            e.getInventory().setResult(null);
        }

    }

    @EventHandler
    public void onTrade(VillagerAcquireTradeEvent e){
        if(e.getRecipe().getResult().getType() == Material.SHIELD){
            e.setCancelled(true);
        }
    }



}