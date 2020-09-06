package me.infinityz.minigame.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class PlayerWinEvent extends Event implements Cancellable{
    /*
     * Methods Required by BukkitAPI
     */
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({"java:S116", "java:S1170"})
    private final @Getter HandlerList Handlers = HandlerList;
    private @Getter @Setter boolean cancelled;
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

    public Player getPlayer() {
        return Bukkit.getPlayer(winnerUUID);
    }

}