package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import me.infinityz.minigame.crafting.CustomRecipe;

public class SaddleRecipe extends CustomRecipe {

    public SaddleRecipe(NamespacedKey namespacedKey) {
        super(namespacedKey);

        final ItemStack item = new ItemStack(Material.SADDLE);

        final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, item);
        recipe.shape("LAL", "LLL", "LAL");
        recipe.setIngredient('A', Material.IRON_INGOT);
        recipe.setIngredient('L', Material.LEATHER);

        setRecipe(recipe);
    }

}
