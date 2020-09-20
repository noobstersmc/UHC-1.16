package me.infinityz.minigame.gamemodes.types.erespawn;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.infinityz.minigame.crafting.CustomRecipe;
import net.md_5.bungee.api.ChatColor;

public class EnderRespawnRecipe extends CustomRecipe {

    public EnderRespawnRecipe(NamespacedKey namespacedKey, Recipe test) {
        super(namespacedKey, test);
        // TODO Auto-generated constructor stub

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

    @Override
    public void logic() {
        
        var recipe = Bukkit.getServer().getRecipe(getNamespacedKey());
        if(recipe == null){
            Bukkit.getServer().addRecipe(getRecipe());
        }

    }

    
}