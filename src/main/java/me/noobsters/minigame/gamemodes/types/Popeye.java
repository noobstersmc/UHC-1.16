package me.noobsters.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.noobsters.minigame.UHC;
import me.noobsters.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

public class Popeye extends IGamemode implements Listener {
    private UHC instance;

    public Popeye(UHC instance) {
        super("Popeye", "Eat Spinach(Kelp) to gain haste.", Material.DRIED_KELP);
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

    @EventHandler(ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent e) {
        var item = e.getItem().getType();
        if (item == Material.AIR || item != Material.DRIED_KELP || !e.getItem().hasItemMeta())
            return;
        ItemMeta itemMeta = e.getItem().getItemMeta();
        if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_GREEN + "Spinach")) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 99));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 5, 99));
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        var stack = e.getEntity().getItemStack();
        var type = stack.getType();
        if (type == Material.DRIED_KELP) {
            var meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.DARK_GREEN + "Spinach");
            stack.setItemMeta(meta);
        }
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
    
    @EventHandler
    public void onCook(FurnaceSmeltEvent e){
        var item = e.getResult();
        if(item.getType() == Material.DRIED_KELP){
            var meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.DARK_GREEN + "Spinach");
                item.setItemMeta(meta);
        }

    }



}