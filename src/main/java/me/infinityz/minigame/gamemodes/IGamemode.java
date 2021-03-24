package me.infinityz.minigame.gamemodes;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.gamemodes.events.GamemodeDisabledEvent;
import me.infinityz.minigame.gamemodes.events.GamemodeEnabledEvent;

/*
* As of right now, there is really no reason to use abstact here.
*/
public abstract class IGamemode {
    private @Getter @Setter String name;
    private @Getter @Setter String description;
    private @Getter @Setter boolean enabled = false;

    public IGamemode(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract boolean enableScenario();

    public abstract boolean disableScenario();

    public void callEnable() {
        if(enableScenario())
            Bukkit.getPluginManager().callEvent(new GamemodeEnabledEvent(this));
    }

    public void callDisable() {
        if(disableScenario())
            Bukkit.getPluginManager().callEvent(new GamemodeDisabledEvent(this));
    }
    
}