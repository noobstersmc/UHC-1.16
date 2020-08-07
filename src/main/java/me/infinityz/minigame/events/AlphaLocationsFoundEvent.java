package me.infinityz.minigame.events;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class AlphaLocationsFoundEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private @Getter Collection<Location> locs;

    // Async event
    public AlphaLocationsFoundEvent(Collection<Location> locs, boolean bool) {
        super(bool);
        this.locs = locs;
    }

    // Sync event
    public AlphaLocationsFoundEvent(Collection<Location> locs) {
        this(locs, false);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}