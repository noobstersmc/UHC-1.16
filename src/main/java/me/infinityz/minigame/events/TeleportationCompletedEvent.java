package me.infinityz.minigame.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class TeleportationCompletedEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private @Getter static final HandlerList HandlerList = new HandlerList();
    private @Getter final HandlerList Handlers = HandlerList;

    public TeleportationCompletedEvent(boolean isASync) {
        super(isASync);
    }

    public TeleportationCompletedEvent() {
        super();
    }

}