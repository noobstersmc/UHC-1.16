package me.noobsters.minigame.crafting.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.noobsters.minigame.crafting.CustomRecipe;
import net.md_5.bungee.api.ChatColor;

public class EnderRespawnRecipe extends CustomRecipe {

    public EnderRespawnRecipe(NamespacedKey namespacedKey) {
        super(namespacedKey);

        final ItemStack respawnCrystal = new ItemStack(Material.END_CRYSTAL);
        final ItemMeta im = respawnCrystal.getItemMeta();
        im.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Respawn Crystal");
        respawnCrystal.setItemMeta(im);

        final ShapelessRecipe recipe = new ShapelessRecipe(getNamespacedKey(), respawnCrystal);
        recipe.addIngredient(2, Material.DIAMOND);
        recipe.addIngredient(2, Material.BONE);
        recipe.addIngredient(2, Material.LEATHER);
        recipe.addIngredient(1, Material.ENDER_EYE);
        recipe.addIngredient(2, Material.GOLDEN_APPLE);

        setRecipe(recipe);
    }

}