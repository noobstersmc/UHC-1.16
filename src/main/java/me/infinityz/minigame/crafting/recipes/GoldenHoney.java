package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.infinityz.minigame.crafting.CustomRecipe;
import net.md_5.bungee.api.ChatColor;

public class GoldenHoney extends CustomRecipe {

    public GoldenHoney(NamespacedKey namespacedKey) {
        super(namespacedKey);

        final ItemStack item = new ItemStack(Material.HONEY_BOTTLE);
        final ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "Golden Honey");
        item.setItemMeta(im);
        final ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, item);

        recipe.addIngredient(Material.GLASS_BOTTLE);
        recipe.addIngredient(Material.GOLDEN_APPLE);

        setRecipe(recipe);
    }

}