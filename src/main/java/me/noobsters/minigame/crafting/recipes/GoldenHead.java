package me.noobsters.minigame.crafting.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import fr.mrmicky.fastinv.ItemBuilder;
import me.noobsters.minigame.crafting.CustomRecipe;
import net.md_5.bungee.api.ChatColor;

public class GoldenHead extends CustomRecipe {

    public GoldenHead(NamespacedKey namespacedKey) {
        super(namespacedKey);

        final ItemStack goldenHead = new ItemBuilder(Material.GOLDEN_APPLE).name(ChatColor.GOLD + "Golden Head").build();

        final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, goldenHead);
        recipe.shape("AAA", "ABA", "AAA");
        recipe.setIngredient('A', Material.GOLD_INGOT);
        recipe.setIngredient('B', new RecipeChoice.MaterialChoice(Material.PLAYER_HEAD, Material.WITHER_SKELETON_SKULL,
                Material.SKELETON_SKULL, Material.CREEPER_HEAD, Material.ZOMBIE_HEAD));

        setRecipe(recipe);

    }

}