package me.infinityz.minigame.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;

public class TeleportationCompletedEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private @Getter static final HandlerList HandlerList = new HandlerList();
    private @Getter final HandlerList Handlers = HandlerList;
    private @Getter @Setter int startDelayTicks = 200;

    public TeleportationCompletedEvent(boolean isASync) {
        super(isASync);
    }

    public TeleportationCompletedEvent() {
        super();
    }

}