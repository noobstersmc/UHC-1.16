package me.infinityz.minigame.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.infinityz.minigame.UHC;

public class ScoreboardManager {
    UHC instance;
    private Map<String, IScoreboard> fastboardMap;

    public ScoreboardManager(UHC instance) {
        this.instance = instance;
        fastboardMap = new HashMap<>();

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            instance.getScoreboardManager().getFastboardMap().values().forEach(all -> {
                all.update();
            });

        }, 20, 5);

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            instance.getScoreboardManager().getFastboardMap().values().forEach(all -> {
                all.runUpdates();
            });

        }, 20, 20);
        
    }
    public void purgeScoreboards(){

        fastboardMap.entrySet().forEach(entry -> {
            entry.getValue().delete();
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