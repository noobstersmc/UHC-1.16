package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import me.infinityz.minigame.crafting.CustomRecipe;

public class NetheriteRecipe extends CustomRecipe {

    public NetheriteRecipe(NamespacedKey namespacedKey, Recipe recipe) {
        super(namespacedKey, recipe);
    }

    public NetheriteRecipe(NamespacedKey namespacedKey) {
        super(namespacedKey, null);

        final ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, new ItemStack(Material.NETHERITE_INGOT, 2));
        recipe.addIngredient(4, Material.NETHERITE_SCRAP);
        recipe.addIngredient(4, Material.GOLD_INGOT);

        setRecipe(recipe);

        logic();
    }

    @Override
    public void logic() {
        Bukkit.getServer().addRecipe(getRecipe());
    }

}