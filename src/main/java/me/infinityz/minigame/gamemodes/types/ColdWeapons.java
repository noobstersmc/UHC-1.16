package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
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
    public void onEnchantItemEvent(EnchantItemEvent e) {
        e.getEnchantsToAdd().entrySet().forEach(entry -> {
            var enchant = entry.getKey().getKey().getKey();
            if(enchant.contains("fire")){
                e.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void prepareAnvilEvent(PrepareAnvilEvent e) {
        var result = e.getResult();
        if (result != null) {
            if (result.getType() == Material.ENCHANTED_BOOK) {
                var bookMeta = (EnchantmentStorageMeta) result.getItemMeta();
                if (bookMeta.getStoredEnchantLevel(Enchantment.FIRE_ASPECT) >= 1 
                || bookMeta.getStoredEnchantLevel(Enchantment.ARROW_FIRE) == 1) {
                    e.setResult(null);
                }

            } else if (result.getEnchantmentLevel(Enchantment.FIRE_ASPECT) >= 1
            || result.getEnchantmentLevel(Enchantment.ARROW_FIRE) == 1) {
                e.setResult(null);
            }
        }
    }

    @EventHandler
    public void onPreEnchant(PrepareItemEnchantEvent e) {
        for (var offer : e.getOffers()) {
            if (offer == null || offer.getEnchantment().toString().contains("fire"))
                continue;
            if(offer.getEnchantment() == Enchantment.FIRE_ASPECT) {
                offer.setEnchantment(Enchantment.DAMAGE_ALL);
            }else if(offer.getEnchantment() == Enchantment.ARROW_FIRE){
                offer.setEnchantment(Enchantment.ARROW_DAMAGE);
            }
        }
    }

    @EventHandler
    public void onLootGeneration(LootGenerateEvent e) {
        e.getLoot().forEach((stack) -> {
            if (stack != null) {
                if (stack.getType() == Material.ENCHANTED_BOOK) {
                    var bookMeta = (EnchantmentStorageMeta) stack.getItemMeta();
                    bookMeta.getStoredEnchants().entrySet().forEach(entry ->{
                        if(entry.getKey() == Enchantment.FIRE_ASPECT){
                            stack.removeEnchantment(Enchantment.FIRE_ASPECT);
                        }else if(entry.getKey() == Enchantment.ARROW_FIRE){
                            stack.removeEnchantment(Enchantment.ARROW_FIRE);
                        }
                    });
                } else if (stack.hasItemMeta()) {
                    stack.getEnchantments().entrySet().forEach(entry ->{
                        if(entry.getKey() == Enchantment.FIRE_ASPECT){
                            stack.removeEnchantment(Enchantment.FIRE_ASPECT);
                        }else if(entry.getKey() == Enchantment.ARROW_FIRE){
                            stack.removeEnchantment(Enchantment.ARROW_FIRE);
                        }
                    });
                }
            }
        });

    }

}
