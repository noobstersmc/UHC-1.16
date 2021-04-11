package me.noobsters.minigame.teams.events;

import lombok.Getter;
import lombok.Setter;
import me.noobsters.minigame.teams.objects.Team;

public class TeamDisbandedEvent extends TeamRemovedEvent implements Quiet{
    private @Getter @Setter boolean quiet;
    /*
     * TeamDisbandEvent is a type of TeamRemovedEvent but carries different data.
     */
    public TeamDisbandedEvent(Team team) {
        super(team);
    }

}