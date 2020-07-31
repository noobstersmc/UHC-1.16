package me.infinityz.minigame.teams.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.infinityz.minigame.teams.objects.Team;

public class TeamCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Team team;
    private Player player;

    public TeamCreatedEvent(Team team, Player player) {
        this.team = team;
        this.player = player;
    }

    public Team getTeam() {
        return team;
    }

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}