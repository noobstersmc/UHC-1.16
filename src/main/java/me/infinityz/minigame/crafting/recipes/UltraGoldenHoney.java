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

public class UltraGoldenHoney extends CustomRecipe { 

    public UltraGoldenHoney(NamespacedKey namespacedKey, Recipe recipe) {
        super(namespacedKey, recipe);
    }

    public UltraGoldenHoney(NamespacedKey namespacedKey) {
        super(namespacedKey, null);

        final ItemStack ultraGoldenHoney = new ItemStack(Material.HONEY_BOTTLE);
        final ItemMeta im = ultraGoldenHoney.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "Ultra Golden Honey");
        ultraGoldenHoney.setItemMeta(im);

        final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, ultraGoldenHoney);
        recipe.shape("AAA", "ABA", "ACA");
        recipe.setIngredient('A', Material.GOLD_INGOT);
        recipe.setIngredient('B', Material.ENCHANTED_GOLDEN_APPLE);
        recipe.setIngredient('C', Material.GLASS_BOTTLE);

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