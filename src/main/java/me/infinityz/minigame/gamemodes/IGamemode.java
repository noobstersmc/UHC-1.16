package me.infinityz.minigame.gamemodes;

import lombok.Getter;
import lombok.Setter;

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

    public abstract boolean enableScenario();

    public abstract boolean disableScenario();

}