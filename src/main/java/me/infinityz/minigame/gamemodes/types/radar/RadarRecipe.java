package me.infinityz.minigame.gamemodes.types.radar;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import me.infinityz.minigame.crafting.CustomRecipe;

public class RadarRecipe extends CustomRecipe {

    public RadarRecipe(NamespacedKey namespacedKey, Recipe test) {
        super(namespacedKey, test);

        final ItemStack newRadarRecipe = new ItemStack(Material.COMPASS);
        var meta = newRadarRecipe.getItemMeta();

        final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, newRadarRecipe);
        recipe.shape("AAA", "ABA", "AAA");
        recipe.setIngredient('A', Material.AIR);
        recipe.setIngredient('B', Material.COMPASS);

        setRecipe(recipe);
    }

    @Override
    public void logic() {

        var recipe = Bukkit.getServer().getRecipe(getNamespacedKey());
        if (recipe == null) {
            Bukkit.getServer().addRecipe(getRecipe());
        }

    }

}