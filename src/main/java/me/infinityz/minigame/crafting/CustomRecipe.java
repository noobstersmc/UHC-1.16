package me.infinityz.minigame.crafting;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import lombok.Getter;
import lombok.Setter;

public abstract class CustomRecipe {
    NamespacedKey namespacedKey;
    Recipe recipe;
    private @Getter @Setter boolean enabled = false;
    private @Getter @Setter String name;

    public CustomRecipe(NamespacedKey namespacedKey, Recipe recipe, String name){
        this.namespacedKey = namespacedKey;
        this.recipe = recipe;
        this.name = name;
    }

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