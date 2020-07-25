package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.infinityz.minigame.crafting.CustomRecipe;
import net.md_5.bungee.api.ChatColor;

public class GoldenHead extends CustomRecipe {

    public GoldenHead(NamespacedKey namespacedKey, Recipe recipe) {
        super(namespacedKey, recipe);
    }

    public GoldenHead(NamespacedKey namespacedKey){
        super(namespacedKey, null);

        final ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE);
        final ItemMeta im = goldenHead.getItemMeta();
        im.setDisplayName(ChatColor.DARK_RED + "Golden Head");
        goldenHead.setItemMeta(im);

        final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, goldenHead);
        recipe.shape("AAA", "ABA", "AAA");
        recipe.setIngredient('A', Material.GOLD_INGOT);
        recipe.setIngredient('B', Material.PLAYER_HEAD);
        
        setRecipe(recipe);

        logic();
    }

    @Override
    public void logic() {
        Bukkit.getServer().addRecipe(getRecipe());
    }
    
}