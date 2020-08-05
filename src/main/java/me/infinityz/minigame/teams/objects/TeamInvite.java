package me.infinityz.minigame.teams.objects;

import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;

public class TeamInvite {
    private @Getter UUID teamToJoin, sender, target;

    public TeamInvite(UUID teamToJoin, Player sender, Player target) {
        this.teamToJoin = teamToJoin;
        this.sender = sender.getUniqueId();
        this.target = target.getUniqueId();
    }

}