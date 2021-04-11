package me.noobsters.minigame.teams.events;

import org.bukkit.entity.Player;

import me.noobsters.minigame.teams.objects.Team;

public class PlayerKickedFromTeamEvent extends PlayerLeftTeamEvent {
    /*
     * PlayerKicked is a PlayerLeftTeamEvent but with different meaning.
     */
    public PlayerKickedFromTeamEvent(final Team team, final Player player) {
        super(team, player);
    }
}
