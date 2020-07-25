package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import me.infinityz.minigame.crafting.CustomRecipe;

public class SimpleNetherite extends CustomRecipe {

    public SimpleNetherite(NamespacedKey namespacedKey, Recipe recipe) {
        super(namespacedKey, recipe);
    }

    public SimpleNetherite(NamespacedKey namespacedKey){
        super(namespacedKey, null);

        final ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, new ItemStack(Material.NETHERITE_INGOT));
        recipe.addIngredient(Material.NETHERITE_SCRAP);
        recipe.addIngredient(Material.GOLD_INGOT);
        
        setRecipe(recipe);
        logic();
    }

    @Override
    public void logic() {
        Bukkit.getServer().addRecipe(getRecipe());
    }
    
    
}