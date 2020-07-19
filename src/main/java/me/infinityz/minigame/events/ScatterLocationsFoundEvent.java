package me.infinityz.minigame.events;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ScatterLocationsFoundEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    Set<Location> locs;
    long time;
    int taskID;

    public ScatterLocationsFoundEvent(final Set<Location> locs, final long time, final int taskID){
        super(true);
        this.locs = locs;
        this.time = time;
        this.taskID = taskID;
    }
    public ScatterLocationsFoundEvent(final Set<Location> locs, final long time, final boolean syncronhus){
        this.locs = locs;
        this.time = time;
    }

    public Set<Location> getLocations() {
        return locs;
    }
    public long getTime(){
        return time;
    }

    public int getTaskID() {
        return taskID;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}