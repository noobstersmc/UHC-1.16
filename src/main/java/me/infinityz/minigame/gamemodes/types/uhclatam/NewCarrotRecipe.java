    package me.infinityz.minigame.gamemodes.types.uhclatam;

    import org.bukkit.Bukkit;
    import org.bukkit.Material;
    import org.bukkit.NamespacedKey;
    import org.bukkit.inventory.ItemStack;
    import org.bukkit.inventory.Recipe;
    import org.bukkit.inventory.ShapedRecipe;
    import org.bukkit.inventory.meta.ItemMeta;
    
    import me.infinityz.minigame.crafting.CustomRecipe;
    import net.md_5.bungee.api.ChatColor;

    public class NewCarrotRecipe extends CustomRecipe {
    
        public NewCarrotRecipe(NamespacedKey namespacedKey, Recipe test) {
            super(namespacedKey, test);
    
                    final ItemStack newcarrotrecipe = new ItemStack(Material.GOLDEN_CARROT);

                    final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, newcarrotrecipe);
                    recipe.shape("AAA", "ABA", "AAA");
                    recipe.setIngredient('A', Material.GOLD_INGOT);
                    recipe.setIngredient('B', Material.CARROT);

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