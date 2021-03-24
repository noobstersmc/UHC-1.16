package me.infinityz.minigame.gamemodes.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.gamemodes.IGamemode;

@RequiredArgsConstructor
public class GamemodeDisabledEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private @Getter static final HandlerList HandlerList = new HandlerList();
    private @Getter final HandlerList Handlers = HandlerList;
    private @Getter @NonNull IGamemode gamemode;

    public GamemodeDisabledEvent(IGamemode gamemode, boolean async) {
        super(async);
        this.gamemode = gamemode;
    }

}