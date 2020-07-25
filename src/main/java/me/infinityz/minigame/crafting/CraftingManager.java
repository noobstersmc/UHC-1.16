package me.infinityz.minigame.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.crafting.recipes.GoldenHead;
import me.infinityz.minigame.crafting.recipes.NetheriteRecipe;
import me.infinityz.minigame.crafting.recipes.SimpleNetherite;
import net.md_5.bungee.api.ChatColor;

public class CraftingManager implements Listener {

    UHC instance;
    List<CustomRecipe> recipes;

    public CraftingManager(UHC instance) {
        this.instance = instance;
        this.recipes = new ArrayList<>();



        Iterator<Recipe> iter = Bukkit.recipeIterator();

        while(iter.hasNext()){
            if(iter.next().getResult().getType() == Material.NETHERITE_INGOT){
                iter.remove();
                break;
            }
        }
        
        this.recipes.add(new GoldenHead(new NamespacedKey(instance, "ghead")));
        this.recipes.add(new SimpleNetherite(new NamespacedKey(instance, "netherite_simple")));
        this.recipes.add(new NetheriteRecipe(new NamespacedKey(instance, "netherite_multiple")));
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e) {
        if (e.isCancelled())
            return;
        if (e.getItem().getType() == Material.AIR || e.getItem() == null)
            return;
        if (e.getItem().getType() != Material.GOLDEN_APPLE)
            return;
        if (!e.getItem().hasItemMeta())
            return;
        ItemMeta itemMeta = e.getItem().getItemMeta();
        if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + "Golden Head")) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1));
        }
    }

    @EventHandler
    public void onJoinGiveRecipe(PlayerJoinEvent e) {
        recipes.stream().forEach(recipe -> e.getPlayer().discoverRecipe(recipe.namespacedKey));
    }

}