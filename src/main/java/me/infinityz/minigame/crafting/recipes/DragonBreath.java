package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import me.infinityz.minigame.crafting.CustomRecipe;

public class DragonBreath extends CustomRecipe {

    public DragonBreath(NamespacedKey namespacedKey) {
        super(namespacedKey);

        final ItemStack dragonRecipe = new ItemStack(Material.DRAGON_BREATH);

        final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, dragonRecipe);
        recipe.shape("APA", "PBP", "APA");
        recipe.setIngredient('A', Material.GOLD_INGOT);
        recipe.setIngredient('B', Material.POTION);
        recipe.setIngredient('P', Material.BLAZE_POWDER);


        setRecipe(recipe);
    }

}