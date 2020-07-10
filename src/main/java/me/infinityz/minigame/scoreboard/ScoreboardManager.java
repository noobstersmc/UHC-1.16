package me.infinityz.minigame.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.infinityz.minigame.UHC;

public class ScoreboardManager {
    UHC instance;
    Map<String, IScoreboard> fastboardMap;

    public ScoreboardManager(UHC instance) {
        this.instance = instance;
        fastboardMap = new HashMap<>();
    }

    public Map<String, IScoreboard> getFastboardMap() {
        return fastboardMap;
    }

    public IScoreboard findScoreboard(UUID uuid){
        return fastboardMap.get(uuid.toString());
    }
}