package me.infinityz.minigame.scoreboard;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import me.infinityz.minigame.scoreboard.objects.FastBoard;
import me.infinityz.minigame.scoreboard.objects.UpdateObject;

public abstract class IScoreboard extends FastBoard {
    public Set<UpdateObject> updateQueue;

    public IScoreboard(Player player) {
        super(player);
        updateQueue = ConcurrentHashMap.newKeySet();
    }

    public abstract void update();

    public void runUpdates() {
        Iterator<UpdateObject> iterator = updateQueue.iterator();
        while (iterator.hasNext()) {
            UpdateObject updateObject = iterator.next();
            this.updateLine(updateObject.getLine(), updateObject.getText());
            updateObject = null;
        }
        updateQueue.clear();
        iterator = null;
    }

    public void addUpdates(UpdateObject updateObject) {
        updateQueue.add(updateObject);
    }

}