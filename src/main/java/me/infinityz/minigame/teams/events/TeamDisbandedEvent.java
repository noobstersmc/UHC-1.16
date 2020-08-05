package me.infinityz.minigame.teams.events;

import me.infinityz.minigame.teams.objects.Team;

public class TeamDisbandedEvent extends TeamRemovedEvent{

    public TeamDisbandedEvent(Team team) {
        super(team);
    }
    
}