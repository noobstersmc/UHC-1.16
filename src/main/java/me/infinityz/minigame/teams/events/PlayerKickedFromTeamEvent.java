package me.infinityz.minigame.teams.events;

import org.bukkit.entity.Player;

import me.infinityz.minigame.teams.objects.Team;

public class PlayerKickedFromTeamEvent extends PlayerLeftTeamEvent {

    public PlayerKickedFromTeamEvent(final Team team, final Player player) {
        super(team, player);
    }
}
