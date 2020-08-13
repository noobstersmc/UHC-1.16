package me.infinityz.minigame.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.NonNull;

public class PlayerWinEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private @Getter static final HandlerList HandlerList = new HandlerList();
    private @Getter final HandlerList Handlers = HandlerList;
    /*
     * Custom data
     */
    private @NonNull @Getter UUID winnerUUID;

    public PlayerWinEvent(UUID uuid, boolean async) {
        super(async);
        this.winnerUUID = uuid;
    }

    public PlayerWinEvent(UUID uuid) {
        super(false);
        this.winnerUUID = uuid;
    }
}