package me.infinityz.minigame.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Crafts extends JavaPlugin{
    @Override
    public void onEnable() {
        ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta im = goldenHead.getItemMeta();
        im.setDisplayName(ChatColor.DARK_RED + "Golden Head");
        goldenHead.setItemMeta(im);
        ShapedRecipe recipe = new ShapedRecipe(goldenHead);
        recipe.shape("A", "A", "A", "A", "B", "A", "A", "A", "A");
        recipe.setIngredient('A', Material.GOLD_INGOT);
        recipe.setIngredient('B', Material.PLAYER_HEAD);
        Bukkit.getServer().addRecipe(recipe);
    }
}