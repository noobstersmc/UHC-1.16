package me.infinityz.minigame.teams.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.infinityz.minigame.teams.objects.Team;

public class TeamRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Team team;

    public TeamRemovedEvent(Team team) {
        this.team = team;
    }

    public Team getTeam(){
        return team;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}