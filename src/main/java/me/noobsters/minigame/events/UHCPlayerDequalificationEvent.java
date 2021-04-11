package me.noobsters.minigame.events;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.noobsters.minigame.enums.DQReason;
import me.noobsters.minigame.players.UHCPlayer;

public class UHCPlayerDequalificationEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({ "java:S116", "java:S1170" })
    private final @Getter HandlerList Handlers = HandlerList;
    /*
     * Custom data
     */
    private @Getter DQReason reason;
    private @Getter UHCPlayer uhcPlayer;

    public UHCPlayerDequalificationEvent(UHCPlayer uhcPlayer, DQReason reason, boolean async) {
        super(async);
        this.uhcPlayer = uhcPlayer;
        this.reason = reason;
    }

    public UHCPlayerDequalificationEvent(UHCPlayer uhcPlayer, DQReason reason) {
        this(uhcPlayer, reason, true);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uhcPlayer.getUUID());
    }
    public OfflinePlayer getOfflinePlayer(){
        return Bukkit.getOfflinePlayer(uhcPlayer.getUUID());
    }

}