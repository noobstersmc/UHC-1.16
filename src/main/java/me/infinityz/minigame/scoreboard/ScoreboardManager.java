package me.infinityz.minigame.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.infinityz.minigame.UHC;

public class ScoreboardManager {
    UHC instance;
    Map<String, IScoreboard> fastboardMap;

    public ScoreboardManager(UHC instance) {
        this.instance = instance;
        fastboardMap = new HashMap<>();

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            instance.getScoreboardManager().getFastboardMap().values().forEach(all -> {
                all.update();
            });

        }, 20, 5);
    }
    public void purgeScoreboards(){

        fastboardMap.entrySet().forEach(entry -> {
            fastboardMap.remove(entry.getKey());
        });
        fastboardMap.clear();
    }

    public Map<String, IScoreboard> getFastboardMap() {
        return fastboardMap;
    }

    public IScoreboard findScoreboard(UUID uuid){
        return fastboardMap.get(uuid.toString());
    }
}