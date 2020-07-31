package me.infinityz.minigame.teams.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.teams.events.TeamCreatedEvent;
import me.infinityz.minigame.teams.objects.Team;

@CommandAlias("team|teams")
public class TeamCMD extends BaseCommand {
    UHC instance;

    public TeamCMD(UHC instance) {
        this.instance = instance;
    }

    @Subcommand("create")
    public Team createTeam(Player player, @Optional String teamName) {
        var team = getPlayerTeam(player.getUniqueId());
        // Check if the player already has a team and don't continue if so.
        if (team != null) {
            player.sendMessage("You already have a team.");
            System.err.println(player.getName() + " attempted to create a team, whislt already having one.");
            return team;
        }
        // At this point, team can only be null. Create a new one.
        team = new Team(player.getUniqueId());
        // If the displayname isn't null, then set it.
        if (teamName != null && teamName.length() > 0) {
            team.setTeamDisplayName(teamName);
        }
        // Attempt to add the team.
        if (instance.getTeamManger().addTeam(team)) {
            // Call the event to make everyone aware.
            Bukkit.getPluginManager().callEvent(new TeamCreatedEvent(team, player));
            // Return the team
            return team;
        }
        return null;
    }

    @CommandAlias("invite")
    @Subcommand("invite")
    @Syntax("<target> - Player you wish to invite")
    public void invitePlayer(Player player, OnlinePlayer target) {
        //Check if the sender has a team
        var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
        //Create a team if sender doesn't have one.
        if (team == null) {
            player.sendMessage("You don't have a team, creating one for you...");
            team = createTeam(player, "");
            player.sendMessage("Team has been created!");
        }

    }

    Team getPlayerTeam(UUID uuid) {
        return instance.getTeamManger().getPlayerTeam(uuid);
    }

}