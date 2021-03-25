package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import me.infinityz.minigame.crafting.CustomRecipe;

public class NotchRecipe extends CustomRecipe {

    public NotchRecipe(NamespacedKey namespacedKey) {
        super(namespacedKey);

        final ItemStack item = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);

        final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, item);
        recipe.shape("AAA", "ABA", "AAA");
        recipe.setIngredient('A', Material.GOLD_BLOCK);
        recipe.setIngredient('B', Material.APPLE);

        setRecipe(recipe);
    }

}