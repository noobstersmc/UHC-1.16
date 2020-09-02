package me.infinityz.minigame.gamemodes;

import gnu.trove.set.hash.THashSet;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.types.Cutclean;

public class GamemodeManager {
    private UHC instance;
    private THashSet<IGamemode> gamemodesList = new THashSet<>();

    public GamemodeManager(UHC instance){
        this.instance = instance;
        new Cutclean().enableScenario(instance);
        
    }

    public void registerGamemode(IGamemode gamemode){
        gamemodesList.add(gamemode);

        
    }
    
}