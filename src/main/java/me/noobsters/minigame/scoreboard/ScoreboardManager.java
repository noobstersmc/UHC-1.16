package me.noobsters.minigame.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import me.noobsters.minigame.UHC;

public class ScoreboardManager {
    private @Getter Map<String, IScoreboard> fastboardMap = new HashMap<>();
    private @Getter BukkitTask updateTask;

    public ScoreboardManager(UHC instance) {
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(instance,
                () -> instance.getScoreboardManager().getFastboardMap().values().forEach(IScoreboard::update), 20, 5);


    }

    public void purgeScoreboards() {
        fastboardMap.values().forEach(IScoreboard::delete);
        fastboardMap.clear();
    }

    public IScoreboard findScoreboard(UUID uuid) {
        return fastboardMap.get(uuid.toString());
    }

    public <T extends IScoreboard> List<IScoreboard> getScoreboardsOfType(Class<T> clazz) {

        var iter = fastboardMap.values().iterator();
        var array = new ArrayList<IScoreboard>();
        while (iter.hasNext()) {
            var object = iter.next();
            if (object.getClass() == clazz)
                array.add(object);
        }
        return array;

    }
}