package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import me.infinityz.minigame.crafting.CustomRecipe;

public class SimpleNetherite extends CustomRecipe {

    public SimpleNetherite(NamespacedKey namespacedKey, Recipe craft, String name) {
        super(namespacedKey, craft, name);

        final ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, new ItemStack(Material.NETHERITE_INGOT));
        recipe.addIngredient(2, Material.NETHERITE_SCRAP);
        recipe.addIngredient(2, Material.GOLD_INGOT);

        setRecipe(recipe);
    }

}