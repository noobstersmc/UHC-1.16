package me.infinityz.minigame.teams.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.infinityz.minigame.teams.objects.Team;
import me.infinityz.minigame.teams.objects.TeamInvite;

public class TeamInviteSentEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private @Getter Team team;
    private @Getter TeamInvite invite;

    public TeamInviteSentEvent(Team team, TeamInvite invite) {
        this.team = team;
        this.invite = invite;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}