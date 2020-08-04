package me.infinityz.minigame.teams.commands;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.teams.events.PlayerJoinedTeamEvent;
import me.infinityz.minigame.teams.events.TeamCreatedEvent;
import me.infinityz.minigame.teams.objects.Team;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("team|teams")
public class TeamCMD extends BaseCommand {
    UHC instance;

    public TeamCMD(UHC instance) {
        this.instance = instance;
        // Add a condition to test for team management.
        instance.getCommandManager().getCommandConditions().addCondition("teamManagement", (context) -> {
            if (!instance.getTeamManger().isTeamManagement())
                throw new ConditionFailedException((ChatColor.RED + "Team management is currently disabled!"));
        });
        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "hasTeam",
                (context, executionContext, player) -> {
                    if (instance.getTeamManger().getPlayerTeam(player.getUniqueId()) == null)
                        throw new ConditionFailedException("You must be in a team to do that");
                });
        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "hasNotTeam",
                (context, executionContext, player) -> {
                    if (instance.getTeamManger().getPlayerTeam(player.getUniqueId()) != null)
                        throw new ConditionFailedException("You must not be in a team to do that");
                });
        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "isTeamLeader",
                (context, executionContext, player) -> {
                    var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                    // Don't check is the team is null, hasTeam should do that for you
                    if (team != null && !team.isTeamLeader(player.getUniqueId()))
                        throw new ConditionFailedException("You must be the team leader to do that");
                });
        instance.getCommandManager().getCommandConditions().addCondition(Player.class, "sameTeam",
                (context, executionContext, player) -> {
                    var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                    // Check if team is null
                    if (team == null)
                        throw new ConditionFailedException("Player is not in the same team.");
                    // Check if issuer is a player
                    if (context.getIssuer().getIssuer() instanceof Player) {
                        var issuer = context.getIssuer().getUniqueId();
                        // Check if issuer's team matches player's team
                        var teamIssuer = instance.getTeamManger().getPlayerTeam(issuer);
                        if (teamIssuer == null)
                            throw new ConditionFailedException("Player is not in the same team.");
                        // Compare the teamID.
                        if (teamIssuer.getTeamID().getMostSignificantBits() != team.getTeamID()
                                .getMostSignificantBits())
                            throw new ConditionFailedException("Player is not in the same team.");
                    }
                });
        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("bool", c -> {
            return ImmutableList.of("true", "false");
        });
        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("otherplayers", c -> {
            if (c.getSender() instanceof Player) {
                return Bukkit.getOnlinePlayers().stream().filter(player -> player.getName() != c.getSender().getName())
                        .map(Player::getName).collect(Collectors.toList());
            }
            return null;
        });
        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("teamMembers", c -> {
            if (c.getSender() instanceof Player) {
                var team = instance.getTeamManger().getPlayerTeam(c.getIssuer().getUniqueId());
                if (team == null)
                    return null;
                return Arrays.asList(team.getMembers()).stream().filter(
                        uuid -> uuid.getMostSignificantBits() != c.getIssuer().getUniqueId().getMostSignificantBits())
                        .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).collect(Collectors.toList());
            }
            return null;
        });
        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("invites", c -> {
            if (c.getSender() instanceof Player) {
                var uuid = ((Player) c.getSender()).getUniqueId();
                return instance.getTeamManger().getTeamInvite().asMap().values().stream()
                        .filter(invite -> invite.getTarget().getMostSignificantBits() == uuid.getMostSignificantBits())
                        .map(invite -> Bukkit.getOfflinePlayer(invite.getSender()).getName())
                        .collect(Collectors.toList());
            }
            return null;
        });
    }

    @Conditions("teamManagement")
    @Subcommand("create")
    @CommandCompletion("@otherplayers")
    public Team createTeam(Player player, @Optional String teamName) {
        Team team = getPlayerTeam(player.getUniqueId());
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
            player.sendMessage("Team " + team.getTeamDisplayName() + " has been created!");
            // Return the team
            return team;
        }
        player.sendMessage("Couldn't create a team!");
        return null;
    }

    @Conditions("teamManagement")
    @Subcommand("invite")
    @CommandAlias("invite")
    @CommandCompletion("@otherplayers")
    @Syntax("<target> - Player you wish to invite")
    public void invitePlayer(@Conditions("isTeamLeader") Player player, @Flags("other") Player target) {
        // Check if the sender has a team
        var team = getPlayerTeam(player.getUniqueId());
        // Create a team if sender doesn't have one.
        if (team == null) {
            player.sendMessage("You don't have a team, creating one for you...");
            team = createTeam(player, "");
        }
        if (target == null) {
            player.sendMessage("Your target player cannot be null.");
            return;
        }
        if (target == player) {
            player.sendMessage("You can't invite yourself to your team.");
            return;
        }
        // Check if player is a team member.
        if (team.isMember(target.getUniqueId())) {
            player.sendMessage(target.getName() + " is already in the team!");
            return;
        }
        // Check if target has a team.
        if (getPlayerTeam(target.getUniqueId()) != null) {
            player.sendMessage(target.getName() + " already has a team!");
            return;
        }
        // Ensure team size is not being violated.
        if (team.getMembers().length + 1 > instance.getTeamManger().getTeamSize()) {
            player.sendMessage("Your team size can't be greater than " + instance.getTeamManger().getTeamSize());
            return;
        }
        // Check if there is a previous invite to the same player
        var previousInvite = instance.getTeamManger().getTeamInvite()
                .getIfPresent(player.getUniqueId().getMostSignificantBits());
        if (previousInvite != null && previousInvite.getTarget().getMostSignificantBits() == target.getUniqueId()
                .getMostSignificantBits()) {
            player.sendMessage("You've already invited " + target.getName() + ".");
            return;
        }
        instance.getTeamManger().sendTeamInvite(team, player, target);
        // TODO: Add a team invite event and call it
    }

    @Conditions("teamManagement")
    @Subcommand("kick|remove|rm")
    @CommandCompletion("@teamMembers")
    @Syntax("<target> - player to be kicked.")
    public void onKickMember(@Conditions("hasTeam|isTeamLeader") Player player,
            @Flags("other") @Conditions("sameTeam") Player target) {
        // TODO: finish team kick cmd

    }

    // TODO: Finish some team commands
    public void leaveTeam() {

    }

    public void teamDisband() {

    }

    public void teamPromote() {

    }

    public void teamChat() {

    }

    @Conditions("teamManagement")
    @Subcommand("accept")
    @CommandAlias("accept")
    @CommandCompletion("@invites")
    @Syntax("<invitor> - player to accept an invite from.")
    public void acceptInvite(@Conditions("hasNotTeam") Player sender, @Flags("other") Player invitor) {
        var invite = instance.getTeamManger().getTeamInviteForTarget(invitor.getUniqueId(), sender.getUniqueId());
        if (invite == null) {
            sender.sendMessage("The player hasn't invited you, or the invite has expired!");
            return;
        }
        var team = instance.getTeamManger().getTeamMap().get(invite.getTeamToJoin());
        if (team == null) {
            System.err.println("The team " + sender.getName() + " attempted to join has somehow been disbanded.");
            sender.sendMessage("The team you are attempting to join is no longer available");
            return;
        }
        if (team.getMembers().length + 1 > instance.getTeamManger().getTeamSize()) {
            sender.sendMessage("The team you are trying to join already has " + team.getMembers().length + " members!");
            return;
        }
        if (team.addMember(sender.getUniqueId())) {
            // Notify the team that the player has joined
            team.sendTeamMessage(ChatColor.GREEN + sender.getName() + " has joined the team!");
            // Call the event
            Bukkit.getPluginManager().callEvent(new PlayerJoinedTeamEvent(team, sender));
            // Delete the invite from cache
            instance.getTeamManger().getTeamInvite().invalidate(invitor.getUniqueId().getMostSignificantBits());
        } else {
            System.err.println(sender.getName() + " attempted to join the team whilst already being a member.");
            sender.sendMessage("Couldn't join the team...");
        }
    }

    @Conditions("teamManagement")
    @Subcommand("reject|deny")
    @CommandAlias("reject|deny")
    @CommandCompletion("@invites")
    @Syntax("<invitor> - player to accept an invite from.")
    public void rejectInvite(Player sender, @Flags("other") Player invitor) {
        var invite = instance.getTeamManger().getTeamInviteForTarget(invitor.getUniqueId(), sender.getUniqueId());
        if (invite == null) {
            sender.sendMessage("The player hasn't invited you, or the invite has expired!");
            return;
        }
        var team = instance.getTeamManger().getTeamMap().get(invite.getTeamToJoin());
        if (team == null) {
            sender.sendMessage("You've rejected " + invitor.getName() + "'s invite.");
        } else {
            sender.sendMessage(
                    "You've rejected " + invitor.getName() + "'s invite to join Team " + team.getTeamDisplayName());
            team.sendTeamMessage(sender.getName() + " has rejected the team invite.");
        }
        // Clear invite from cache
        instance.getTeamManger().getTeamInvite().invalidate(invitor.getUniqueId().getMostSignificantBits());
    }

    @CommandPermission("uhc.team.management")
    @Subcommand("man|management|manage")
    @CommandCompletion("@bool")
    @Syntax("<bool> - True or False to set the management")
    public void teamManagement(CommandSender sender, Boolean bool) {
        sender.sendMessage("Team management has been set to " + bool);
        instance.getTeamManger().setTeamManagement(bool);
    }

    @CommandPermission("uhc.team.management")
    @CommandCompletion("@range:1-9")
    @Syntax("<teamSize> - Team size to be set")
    @Subcommand("size")
    public void teamSize(CommandSender sender, @Conditions("limits:min=1") Integer teamSize) {
        sender.sendMessage("Team size has been set from " + instance.getTeamManger().getTeamSize() + " to " + teamSize);
        instance.getTeamManger().setTeamSize(teamSize);
    }

    @CommandAlias("sendcoord|sendcoords|sc")
    @Subcommand("sendcoord|sendcoords|sc")
    public void teamSendCoords(@Conditions("hasTeam") Player sender) {
        var team = instance.getTeamManger().getPlayerTeam(sender.getUniqueId());
        team.sendTeamMessage("[TeamChat] " + sender.getName() + ": I'm at " + getLocation(sender.getLocation()));
    }

    private String getLocation(Location loc) {
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", " + loc.getWorld().getName()
                + ")";
    }

    // Method to reduce boiler-plate
    private Team getPlayerTeam(UUID uuid) {
        return instance.getTeamManger().getPlayerTeam(uuid);
    }

}