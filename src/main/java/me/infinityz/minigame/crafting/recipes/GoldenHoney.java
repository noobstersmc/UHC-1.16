package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.infinityz.minigame.crafting.CustomRecipe;
import net.md_5.bungee.api.ChatColor;

public class GoldenHoney extends CustomRecipe {

    public GoldenHoney(NamespacedKey namespacedKey, Recipe recipe) {
        super(namespacedKey, recipe);
    }

    public GoldenHoney(NamespacedKey namespacedKey) {
        super(namespacedKey, null);

        final ItemStack goldenHoney = new ItemStack(Material.HONEY_BOTTLE);
        final ItemMeta im = goldenHoney.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "Golden Honey");
        goldenHoney.setItemMeta(im);
        final ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, goldenHoney);

        recipe.addIngredient(Material.GLASS_BOTTLE);
        recipe.addIngredient(Material.GOLDEN_APPLE);

        setRecipe(recipe);
    }

    @Override
    public void logic() {
        var recipe = Bukkit.getServer().getRecipe(getNamespacedKey());
        if(recipe == null){
            Bukkit.getServer().addRecipe(getRecipe());
        }
    }

}