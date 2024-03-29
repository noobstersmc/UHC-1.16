package me.noobsters.minigame.gamemodes.types;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gamemodes.IGamemode;

public class Metrallesta extends IGamemode implements Listener {
    private UHC instance;

    public Metrallesta(UHC instance) {
        super("Metrallesta", "CrossBows reload instantly.", Material.CROSSBOW);
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
    public void onShoorV2(EntityShootBowEvent e ){
        var item = e.getBow();
        if(item.getType() == Material.CROSSBOW){
            var player = (Player) e.getEntity();
            var ammo = new ItemStack(Material.ARROW);
            if(player.getInventory().containsAtLeast(ammo, 1)){
                Bukkit.getScheduler().runTaskLater(instance, ()->{
                    var meta = (CrossbowMeta) item.getItemMeta();
                    var proyectiles = List.of(ammo);
                    meta.setChargedProjectiles(proyectiles);
                    item.setItemMeta(meta);
                    player.getInventory().removeItem(ammo);
                }, 4);

            }

        }
    }

    @EventHandler
    public void onArrowCraft(PrepareItemCraftEvent e) {
        var result = e.getInventory().getResult();
        if (result == null || result.getType() == Material.AIR) return;
        
        var re = result.getType();
        if(re == Material.ARROW) result.setAmount(8);
        
    }

    @EventHandler
    public void onCraftShield(PrepareItemCraftEvent e) {
        var result = e.getInventory().getResult();
        if (result == null || result.getType() == Material.AIR) return;
        
        var re = result.getType();
        if(re == Material.SHIELD){
            var meta = result.getItemMeta();
            meta.addEnchant(Enchantment.DURABILITY, 3, true);
            result.setItemMeta(meta);
        }
        
    }

}