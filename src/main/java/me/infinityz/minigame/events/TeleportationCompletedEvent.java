package me.infinityz.minigame.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;

public class TeleportationCompletedEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({ "java:S116", "java:S1170" })
    private final @Getter HandlerList Handlers = HandlerList;
    /*
     * Custom Data
     */
    private @Getter @Setter int startDelayTicks = 20;

    public TeleportationCompletedEvent(boolean isASync) {
        super(isASync);
    }

    public TeleportationCompletedEvent() {
        super();
    }

}