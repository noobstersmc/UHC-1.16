package me.infinityz.minigame.teams.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.infinityz.minigame.teams.objects.Team;

public class PlayerJoinedTeamEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private @Getter Team team;
    private @Getter Player player;

    public PlayerJoinedTeamEvent(final Team team, final Player player) {
        this.team = team;
        this.player = player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}