package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.infinityz.minigame.crafting.CustomRecipe;
import net.md_5.bungee.api.ChatColor;

public class GoldenHead extends CustomRecipe {

    public GoldenHead(NamespacedKey namespacedKey, Recipe name) {
        super(namespacedKey, null);

        final ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE);
        final ItemMeta im = goldenHead.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "Golden Head");
        goldenHead.setItemMeta(im);

        final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, goldenHead);
        recipe.shape("AAA", "ABA", "AAA");
        recipe.setIngredient('A', Material.GOLD_INGOT);
        recipe.setIngredient('B', new RecipeChoice.MaterialChoice(Material.WITHER_SKELETON_SKULL, Material.PLAYER_HEAD,
                Material.SKELETON_SKULL, Material.CREEPER_HEAD, Material.ZOMBIE_HEAD));

        setRecipe(recipe);

    }

}