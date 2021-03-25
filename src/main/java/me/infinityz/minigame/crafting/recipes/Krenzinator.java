package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import me.infinityz.minigame.crafting.CustomRecipe;

public class Krenzinator extends CustomRecipe {

    public Krenzinator(NamespacedKey namespacedKey, Recipe craft, String name) {
        super(namespacedKey, craft, name);

        final ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, new ItemStack(Material.DIAMOND));
        recipe.addIngredient(9, Material.REDSTONE_BLOCK);

        setRecipe(recipe);
    }

}