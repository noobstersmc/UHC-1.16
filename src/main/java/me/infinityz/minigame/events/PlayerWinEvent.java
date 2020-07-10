package me.infinityz.minigame.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerWinEvent extends Event{
    
    private static final HandlerList handlers = new HandlerList();
    UUID uuid;

    public PlayerWinEvent(UUID uuid){
        this.uuid = uuid;
    }

    public UUID getWinnerUUID() {
        return uuid;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}