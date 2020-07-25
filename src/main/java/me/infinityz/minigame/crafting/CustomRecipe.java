package me.infinityz.minigame.crafting;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

public abstract class CustomRecipe {
    NamespacedKey namespacedKey;
    Recipe recipe;

    public CustomRecipe(NamespacedKey namespacedKey, Recipe recipe){
        this.namespacedKey = namespacedKey;
        this.recipe = recipe;
    }

    public abstract void logic();

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
    public void setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }
    public Recipe getRecipe() {
        return recipe;
    }
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
    
}