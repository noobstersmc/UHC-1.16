package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class DiamondLess extends IGamemode implements Listener {
    private UHC instance;

    public DiamondLess(UHC instance) {
        super("DiamondLess", "Diamonds are cancelled.");
        this.instance = instance;
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled()) {
            return false;
        }
        instance.getListenerManager().registerListener(this);
        setEnabled(true);

        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled()) {
            return false;
        }
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }


    @EventHandler
    public void onOpenChest(InventoryOpenEvent e){
        if(e.getInventory().getType().toString().equals("CHEST")){
            var items = e.getInventory().getContents();
            for (int i = 0; i < items.length; i++) {
                if(items[i] != null && items[i].getType() == Material.DIAMOND){
                    items[i].setType(Material.IRON_INGOT);
                }
            }
        }
    }

    @EventHandler
    public void onBreak(ItemSpawnEvent e){
        var item = e.getEntity().getItemStack().getType();
        if(item.toString().contains("DIAMOND")){
            e.getEntity().getItemStack().setType(Material.IRON_INGOT);
        }

    }

    @EventHandler
    public void onTrade(VillagerAcquireTradeEvent e){
        if(e.getRecipe().getResult().getType().toString().contains("DIAMOND")){
            e.setCancelled(true);
        }
    }




}
