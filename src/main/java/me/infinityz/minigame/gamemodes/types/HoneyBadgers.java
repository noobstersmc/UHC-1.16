package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.crafting.recipes.GoldenHoney;
import me.infinityz.minigame.crafting.recipes.SuperGoldenHoney;
import me.infinityz.minigame.crafting.recipes.UltraGoldenHoney;
import me.infinityz.minigame.gamemodes.IGamemode;

/**
 * EnderRespawn
 */
public class HoneyBadgers extends IGamemode implements Listener {
    private UHC instance;
    private GoldenHoney goldenHoney;
    private SuperGoldenHoney superGoldenHoney;
    private UltraGoldenHoney ultraGoldenHoney;

    public HoneyBadgers(UHC instance) {
        super("HoneyBadgers", "The Golden Honey is the new Golden Apple.");
        this.instance = instance;
        this.goldenHoney = new GoldenHoney(new NamespacedKey(instance, "golden_honey"), null);
        this.superGoldenHoney = new SuperGoldenHoney(new NamespacedKey(instance, "super_golden_honey"), null);
        this.ultraGoldenHoney = new UltraGoldenHoney(new NamespacedKey(instance, "ultra_golden_honey"), null);
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        Bukkit.addRecipe(goldenHoney.getRecipe());
        Bukkit.addRecipe(superGoldenHoney.getRecipe());
        Bukkit.addRecipe(ultraGoldenHoney.getRecipe());


        Bukkit.getOnlinePlayers().forEach(all -> {
            all.discoverRecipe(this.goldenHoney.getNamespacedKey());
            all.discoverRecipe(this.superGoldenHoney.getNamespacedKey());
            all.discoverRecipe(this.ultraGoldenHoney.getNamespacedKey());
        });
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        Bukkit.removeRecipe(goldenHoney.getNamespacedKey());
        Bukkit.removeRecipe(superGoldenHoney.getNamespacedKey());
        Bukkit.removeRecipe(ultraGoldenHoney.getNamespacedKey());

        Bukkit.getOnlinePlayers().forEach(all -> {
            all.undiscoverRecipe(this.goldenHoney.getNamespacedKey());
            all.undiscoverRecipe(this.superGoldenHoney.getNamespacedKey());
            all.undiscoverRecipe(this.ultraGoldenHoney.getNamespacedKey());
        });
        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().discoverRecipe(this.goldenHoney.getNamespacedKey());
        e.getPlayer().discoverRecipe(this.superGoldenHoney.getNamespacedKey());
        e.getPlayer().discoverRecipe(this.ultraGoldenHoney.getNamespacedKey());

    }

    @EventHandler(ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent e) {
        var player = e.getPlayer();
        var item = e.getItem().getType();
        if (item == Material.AIR || item != Material.HONEY_BOTTLE || !e.getItem().hasItemMeta())
            return;
        ItemMeta itemMeta = e.getItem().getItemMeta();

        if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Golden Honey")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 60 * 2, 1));
        }else if(itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Super Golden Honey")){
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                    .setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 2);
        }else if(itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Ultra Golden Honey")){
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                    .setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 20);
        }
    }

}