package me.infinityz.minigame.teams.objects;

import java.util.UUID;

import org.bukkit.entity.Player;

public class TeamInvite {
    private UUID teamToJoin, sender, target;

    public TeamInvite(UUID teamToJoin, Player sender, Player target) {
        this.teamToJoin = teamToJoin;
        this.sender = sender.getUniqueId();
        this.target = target.getUniqueId();
    }

    public UUID getSender() {
        return sender;
    }
    public UUID getTeamToJoin() {
        return teamToJoin;
    }
    public UUID getTarget() {
        return target;
    }

}