package me.infinityz.minigame.gamemodes.types;

import me.infinityz.minigame.gamemodes.IGamemode;

public class Cutclean extends IGamemode{
    public Cutclean() {
        super("Cutclean", "All drops are now melted", null, new CutcleanListener());
    }

}