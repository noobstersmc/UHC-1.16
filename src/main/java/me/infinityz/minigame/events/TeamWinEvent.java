package me.infinityz.minigame.events;

import java.util.UUID;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class TeamWinEvent extends Event implements Cancellable {
    /*
     * Methods Required by BukkitAPI
     */
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({ "java:S116", "java:S1170" })
    private final @Getter HandlerList Handlers = HandlerList;
    private @Getter @Setter boolean cancelled;
    /*
     * Custom data
     */
    private @NonNull @Getter UUID teamUUID;

    public TeamWinEvent(UUID uuid, boolean async) {
        super(async);
        this.teamUUID = uuid;
    }

    public TeamWinEvent(UUID uuid) {
        super(false);
        this.teamUUID = uuid;
    }


}