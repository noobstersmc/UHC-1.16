package me.noobsters.minigame.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class GameStartedEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({"java:S116", "java:S1170"})
    private final @Getter HandlerList Handlers = HandlerList;

    public GameStartedEvent(boolean async) {
        super(async);

    }

    public GameStartedEvent() {
        this(false);
    }

}
