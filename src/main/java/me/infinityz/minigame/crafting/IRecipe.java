package me.infinityz.minigame.crafting;

import lombok.Getter;
import lombok.Setter;


public abstract class IRecipe {
    private @Getter @Setter String name;
    private @Getter @Setter CustomRecipe recipe;
    private @Getter @Setter boolean enabled = false;

    public IRecipe(String name, CustomRecipe recipe) {
        this.name = name;
        this.recipe = recipe;
    }

}