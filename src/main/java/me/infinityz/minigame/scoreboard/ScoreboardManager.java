package me.infinityz.minigame.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import lombok.Getter;
import me.infinityz.minigame.UHC;

public class ScoreboardManager {
    private @Getter Map<String, IScoreboard> fastboardMap = new HashMap<>();

    public ScoreboardManager(UHC instance) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,
                () -> instance.getScoreboardManager().getFastboardMap().values().forEach(IScoreboard::update), 20, 5);

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,
                () -> instance.getScoreboardManager().getFastboardMap().values().forEach(IScoreboard::runUpdates), 20,
                20);

    }
    public void purgeScoreboards() {
        fastboardMap.values().forEach(IScoreboard::delete);
        fastboardMap.clear();
    }

    public IScoreboard findScoreboard(UUID uuid) {
        return fastboardMap.get(uuid.toString());
    }
}