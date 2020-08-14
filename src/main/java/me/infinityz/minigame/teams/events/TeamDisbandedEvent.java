package me.infinityz.minigame.teams.events;

import me.infinityz.minigame.teams.objects.Team;

public class TeamDisbandedEvent extends TeamRemovedEvent {
    /*
     * TeamDisbandEvent is a type of TeamRemovedEvent but carries different data.
     */
    public TeamDisbandedEvent(Team team) {
        super(team);
    }

}