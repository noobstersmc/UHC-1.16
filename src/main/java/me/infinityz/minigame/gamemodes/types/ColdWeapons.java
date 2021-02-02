package me.infinityz.minigame.gamemodes.types;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class ColdWeapons extends IGamemode implements Listener {
    private UHC instance;

    public ColdWeapons(UHC instance) {
        super("ColdWeapons", "Fire weapons are disabled.");
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

        instance.getListenerManager().registerListener(this);

        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        Map<Enchantment, Integer> toAdd = e.getEnchantsToAdd();

        if(toAdd.containsKey(Enchantment.ARROW_FIRE))
            toAdd.remove(Enchantment.ARROW_FIRE);
        
        if(toAdd.containsKey(Enchantment.FIRE_ASPECT))
            toAdd.remove(Enchantment.FIRE_ASPECT);
        
        
    }

    @EventHandler
    public void onTrade(VillagerAcquireTradeEvent e){
        if(e.getRecipe().getResult().getType().toString().contains("SWORD")){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent e){
        var item = e.getResult();
        if(item == null) return;
        if(item.getType() == Material.ENCHANTED_BOOK){
            var bookMeta = (EnchantmentStorageMeta) item.getItemMeta();
            if(bookMeta.getStoredEnchants().containsKey(Enchantment.FIRE_ASPECT) 
                || bookMeta.getStoredEnchants().containsKey(Enchantment.ARROW_FIRE)){
                item.setType(Material.POISONOUS_POTATO);
            }
        }else{
            if(item.containsEnchantment(Enchantment.FIRE_ASPECT))
            item.removeEnchantment(Enchantment.FIRE_ASPECT);
        
            if(item.containsEnchantment(Enchantment.ARROW_FIRE))
                item.removeEnchantment(Enchantment.ARROW_FIRE);
        }
    }
    
    @EventHandler
    public void onOpenChest(InventoryOpenEvent e){
        if(e.getInventory().getType().toString().equals("CHEST")){
            var items = e.getInventory().getContents();
            for (int i = 0; i < items.length; i++) {
                var item = items[i];
                if(item != null){

                    if(item.getType() == Material.ENCHANTED_BOOK){
                        var bookMeta = (EnchantmentStorageMeta) item.getItemMeta();
                        if(bookMeta.getStoredEnchants().containsKey(Enchantment.FIRE_ASPECT) 
                            || bookMeta.getStoredEnchants().containsKey(Enchantment.ARROW_FIRE)){
                            item.setType(Material.POISONOUS_POTATO);
                        }
                    }else{
                        if(item.containsEnchantment(Enchantment.FIRE_ASPECT))
                        item.removeEnchantment(Enchantment.FIRE_ASPECT);
                    
                        if(item.containsEnchantment(Enchantment.ARROW_FIRE))
                            item.removeEnchantment(Enchantment.ARROW_FIRE);
                    }

                }
            }
        }
    }

}