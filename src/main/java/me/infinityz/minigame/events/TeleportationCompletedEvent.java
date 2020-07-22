package me.infinityz.minigame.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeleportationCompletedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public TeleportationCompletedEvent(boolean isASync){
        super(isASync);
    }

    public TeleportationCompletedEvent(){
        super();
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}