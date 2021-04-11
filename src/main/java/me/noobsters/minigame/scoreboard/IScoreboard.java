package me.noobsters.minigame.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import lombok.Getter;
import me.noobsters.minigame.scoreboard.objects.FastBoard;
import me.noobsters.minigame.scoreboard.objects.UpdateObject;

public abstract class IScoreboard extends FastBoard {
    private @Getter Set<UpdateObject> updateQueue = ConcurrentHashMap.newKeySet();

    public IScoreboard(Player player) {
        super(player);
    }

    public abstract void update(String... schema);

    public void runUpdates() {
        Iterator<UpdateObject> iterator = updateQueue.iterator();
        while (iterator.hasNext()) {
            UpdateObject updateObject = iterator.next();
            this.updateLine(updateObject.getLine(), updateObject.getText());
        }
        updateQueue.clear();
    }

    public void addUpdates(UpdateObject updateObject) {
        updateQueue.add(updateObject);
    }
    public void addAllUpdates(UpdateObject... updateObject) {
        addAllUpdates(Arrays.asList(updateObject));
    }

    public void addAllUpdates(Collection<UpdateObject> updateCollection){
        updateQueue.addAll(updateCollection);
    }

}