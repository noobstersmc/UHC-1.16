package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import me.infinityz.minigame.crafting.CustomRecipe;

public class MelonRecipe extends CustomRecipe {

    public MelonRecipe(NamespacedKey namespacedKey, Recipe test) {
        super(namespacedKey, test);

                final ItemStack newmelonrecipe = new ItemStack(Material.GLISTERING_MELON_SLICE);

                final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, newmelonrecipe);
                recipe.shape("AAA", "ABA", "AAA");
                recipe.setIngredient('A', Material.GOLD_INGOT);
                recipe.setIngredient('B', Material.MELON_SLICE);

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