package me.infinityz.minigame.tasks;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatColor;

public class Crafts extends JavaPlugin{
    @Override
    public void onEnable() {
        ItemStack goldenHead = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta im = goldenHead.getItemMeta();
        im.setDisplayName(ChatColor.DARK_RED + "Golden Head");
        goldenHead.setItemMeta(im);
        ShapedRecipe recipe = new ShapedRecipe(goldenHead);
        recipe.shape("A", "A", "A", "A", "B", "A", "A", "A", "A");
        recipe.setIngredient('A', Material.GOLD_INGOT);
        recipe.setIngredient('B', Material.PLAYER_HEAD);
        Bukkit.getServer().addRecipe(recipe);
    }
}