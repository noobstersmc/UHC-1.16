package me.infinityz.minigame.events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ScatterLocationsFoundEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    List<Location> locs;
    public long time;

    public ScatterLocationsFoundEvent(List<Location> locs, long time){
        super(true);
        this.locs = locs;
        this.time = time;
    }

    public List<Location> getLocations() {
        return locs;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}