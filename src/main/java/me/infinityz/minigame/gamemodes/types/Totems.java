package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.crafting.recipes.TotemRecipe;
import me.infinityz.minigame.gamemodes.IGamemode;

public class Totems extends IGamemode implements Listener {
    private UHC instance;
    private TotemRecipe recipe;
    
    public Totems(UHC instance) {
        super("Totems", "Totems can be crafted.");
        this.instance = instance;
        this.recipe = new TotemRecipe(new NamespacedKey(instance, "totem"), null);
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        Bukkit.addRecipe(recipe.getRecipe());
        Bukkit.getOnlinePlayers().forEach(all -> all.discoverRecipe(this.recipe.getNamespacedKey()));
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        Bukkit.removeRecipe(recipe.getNamespacedKey());
        Bukkit.getOnlinePlayers().forEach(all -> all.undiscoverRecipe(this.recipe.getNamespacedKey()));
        setEnabled(false);
        return true;
    }



}