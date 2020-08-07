package me.infinityz.minigame.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AlphaScatterDoneEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    // Async event
    public AlphaScatterDoneEvent(boolean bool) {
        super(bool);
    }

    // Sync event
    public AlphaScatterDoneEvent() {
        this(false);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}