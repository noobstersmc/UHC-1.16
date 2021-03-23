package me.infinityz.minigame.crafting.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import me.infinityz.minigame.crafting.CustomRecipe;

    public class DragonBreath extends CustomRecipe {
    
        public DragonBreath(NamespacedKey namespacedKey, Recipe name) {
            super(namespacedKey, name);
    
                    final ItemStack dragonRecipe = new ItemStack(Material.DRAGON_BREATH);
                    final ItemStack strengthPot = PotionItemStack(Material.POTION, PotionType.STRENGTH, false, true);

                    final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, dragonRecipe);
                    recipe.shape("AAA", "ABA", "AAA");
                    recipe.setIngredient('A', Material.GOLD_NUGGET);
                    recipe.setIngredient('B', strengthPot.getType());

                    setRecipe(recipe);
        }
        
        private ItemStack PotionItemStack(Material type, PotionType potionTypeEffect, boolean extend, boolean upgraded){
            ItemStack potion = new ItemStack(type, 1);
            PotionMeta meta = (PotionMeta) potion.getItemMeta();
            meta.setBasePotionData(new PotionData(potionTypeEffect, extend, upgraded));
            potion.setItemMeta(meta);
            return potion;
        }
        
    }