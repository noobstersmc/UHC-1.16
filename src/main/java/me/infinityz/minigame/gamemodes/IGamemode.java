package me.infinityz.minigame.gamemodes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.mrmicky.fastinv.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.gamemodes.events.GamemodeDisabledEvent;
import me.infinityz.minigame.gamemodes.events.GamemodeEnabledEvent;
import net.md_5.bungee.api.ChatColor;

/*
* As of right now, there is really no reason to use abstact here.
*/
public abstract class IGamemode {
    private @Getter @Setter String name;
    private @Getter @Setter String description;
    private @Getter @Setter Material icon;
    private @Getter @Setter int iconCount = 1;
    private @Getter @Setter boolean enabled = false;

    public IGamemode(String name, String description, Material icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract boolean enableScenario();

    public abstract boolean disableScenario();

    public void callEnable() {
        if (enableScenario())
            Bukkit.getPluginManager().callEvent(new GamemodeEnabledEvent(this, !Bukkit.isPrimaryThread()));
    }

    public void callDisable() {
        if (disableScenario())
            Bukkit.getPluginManager().callEvent(new GamemodeDisabledEvent(this, !Bukkit.isPrimaryThread()));
    }

    public ItemStack getAsIcon() {
        return new ItemBuilder(this.getIcon()).name(ChatColor.YELLOW + this.getName())
                .addLore(ChatColor.WHITE + this.getDescription()).build();
    }

}