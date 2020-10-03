package me.infinityz.minigame.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.Getter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.crafting.recipes.GoldenHead;
import me.infinityz.minigame.crafting.recipes.NetheriteRecipe;
import me.infinityz.minigame.crafting.recipes.SimpleNetherite;
import net.md_5.bungee.api.ChatColor;

public class CraftingManager implements Listener {

    UHC instance;
    private @Getter List<CustomRecipe> recipes = new ArrayList<>();

    public CraftingManager(UHC instance) {
        this.instance = instance;
        try {
            Iterator<Recipe> iter = Bukkit.recipeIterator();

            while (iter.hasNext()) {
                if (iter.next().getResult().getType() == Material.NETHERITE_INGOT) {
                    iter.remove();
                    break;
                }
            }
            this.recipes.add(new GoldenHead(new NamespacedKey(instance, "ghead")));
            this.recipes.add(new SimpleNetherite(new NamespacedKey(instance, "netherite_simple")));
            this.recipes.add(new NetheriteRecipe(new NamespacedKey(instance, "netherite_multiple")));
        } catch (Exception ex) {
            instance.getLogger().warning("Exception occured when registering custom recipes. " + ex.getMessage());
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e) {
        if (e.isCancelled())
            return;
        if (e.getItem().getType() == Material.AIR)
            return;
        if (e.getItem().getType() != Material.GOLDEN_APPLE)
            return;
        if (!e.getItem().hasItemMeta())
            return;
        ItemMeta itemMeta = e.getItem().getItemMeta();
        if (itemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Golden Head")) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1));
        }
    }

    @EventHandler
    public void onJoinGiveRecipe(PlayerJoinEvent e) {
        discoverCustomRecipes(e.getPlayer());
    }

    public void discoverCustomRecipes(Player player) {
        recipes.stream().map(CustomRecipe::getNamespacedKey).forEach(player::discoverRecipe);

    }

    public void purgeRecipes() {
        Bukkit.resetRecipes();
    }

    public void restoreRecipes(){
        recipes.stream().forEach(all->{
            Bukkit.getServer().addRecipe(all.getRecipe());
        });
    }

}