package me.infinityz.minigame.gamemodes.types;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.world.LootGenerateEvent;
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

        if(toAdd.containsKey(Enchantment.ARROW_FIRE)){
            toAdd.remove(Enchantment.ARROW_FIRE);
            
        }else if(toAdd.containsKey(Enchantment.FIRE_ASPECT)){
            toAdd.remove(Enchantment.FIRE_ASPECT);
        }
        
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        if(e.getResult().getEnchantments().containsKey(Enchantment.ARROW_FIRE)){
            e.getResult().removeEnchantment(Enchantment.ARROW_FIRE);
            
        }else if(e.getResult().getEnchantments().containsKey(Enchantment.FIRE_ASPECT)){
            e.getResult().removeEnchantment(Enchantment.FIRE_ASPECT);
        }
        
    }

    @EventHandler
    public void onLootGeneration(LootGenerateEvent e) {
        e.getLoot().forEach((stack) -> {
            if (stack != null) {
                if (stack.getType() == Material.ENCHANTED_BOOK) {
                    var bookMeta = (EnchantmentStorageMeta) stack.getItemMeta();
                    bookMeta.getStoredEnchants().entrySet().forEach(entry -> {
                        if (entry.getKey() == Enchantment.FIRE_ASPECT) {
                            stack.removeEnchantment(Enchantment.FIRE_ASPECT);
                        } else if (entry.getKey() == Enchantment.ARROW_FIRE) {
                            stack.removeEnchantment(Enchantment.ARROW_FIRE);
                        }
                    });
                } else if (stack.hasItemMeta()) {
                    stack.getEnchantments().entrySet().forEach(entry -> {
                        if (entry.getKey() == Enchantment.FIRE_ASPECT) {
                            stack.removeEnchantment(Enchantment.FIRE_ASPECT);
                        } else if (entry.getKey() == Enchantment.ARROW_FIRE) {
                            stack.removeEnchantment(Enchantment.ARROW_FIRE);
                        }
                    });
                }
            }
        });

    }

}