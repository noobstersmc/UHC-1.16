package me.infinityz.minigame.gamemodes.types.radar;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import me.infinityz.minigame.crafting.CustomRecipe;
import net.md_5.bungee.api.ChatColor;

public class RadarRecipe extends CustomRecipe {

    public RadarRecipe(NamespacedKey namespacedKey, Recipe test) {
        super(namespacedKey, test);

        final ItemStack newRadarRecipe = new ItemStack(Material.COMPASS);
        var meta = newRadarRecipe.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+ "" + ChatColor.BOLD + "RADAR");
        newRadarRecipe.setItemMeta(meta);

        final ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, newRadarRecipe);
        recipe.addIngredient(1, Material.DIAMOND);
        recipe.addIngredient(1, Material.COMPASS);

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