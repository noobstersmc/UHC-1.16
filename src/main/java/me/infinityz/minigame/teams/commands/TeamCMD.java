package me.infinityz.minigame.teams.commands;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
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
import me.infinityz.minigame.teams.events.PlayerLeftTeamEvent;
import me.infinityz.minigame.teams.events.TeamCreatedEvent;
import me.infinityz.minigame.teams.events.TeamDisbandedEvent;
import me.infinityz.minigame.teams.events.TeamInviteSentEvent;
import me.infinityz.minigame.teams.events.TeamRemovedEvent;
import me.infinityz.minigame.teams.objects.Team;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("team|teams")
public class TeamCMD extends BaseCommand {
    UHC instance;

    public TeamCMD(UHC instance) {
        this.instance = instance;
    }
    // TODO: Register TeamMember.java as an issuerAwareContext

    @Conditions("teamManagement")
    @Subcommand("create")
    public Team createTeam(Player player, @Optional String teamName) {
        Team team = getPlayerTeam(player.getUniqueId());
        // Check if the player already has a team and don't continue if so.
        if (team != null) {
            player.sendMessage(ChatColor.RED + "You already have a team.");
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
            player.sendMessage(ChatColor.of("#7ab83c") + "Team " + team.getTeamDisplayName() + " has been created!");
            // Return the team
            return team;
        }
        player.sendMessage(ChatColor.RED + "Couldn't create a team!");
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
            player.sendMessage(ChatColor.of("#7ab83c") + "You don't have a team, creating one for you...");
            team = createTeam(player, "");
        }
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Your target player cannot be null.");
            return;
        }
        if (target == player) {
            player.sendMessage(ChatColor.RED + "You can't invite yourself to your team.");
            return;
        }
        // Check if player is a team member.
        if (team.isMember(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + target.getName() + " is already in the team!");
            return;
        }
        // Check if target has a team.
        if (getPlayerTeam(target.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + target.getName() + " already has a team!");
            return;
        }
        // Ensure team size is not being violated.
        if (team.getMembers().length + 1 > instance.getTeamManger().getTeamSize()) {
            player.sendMessage(
                    ChatColor.RED + "Your team size can't be greater than " + instance.getTeamManger().getTeamSize());
            return;
        }
        // Check if there is a previous invite to the same player
        var previousInvite = instance.getTeamManger().getTeamInvite()
                .getIfPresent(player.getUniqueId().getMostSignificantBits());
        if (previousInvite != null && previousInvite.getTarget().getMostSignificantBits() == target.getUniqueId()
                .getMostSignificantBits()) {
            player.sendMessage(ChatColor.of("#DABC12") + "You've already invited " + target.getName() + ".");
            return;
        }
        Bukkit.getPluginManager().callEvent(
                new TeamInviteSentEvent(team, instance.getTeamManger().sendTeamInvite(team, player, target)));
    }

    @Conditions("teamManagement")
    @Subcommand("kick|remove|rm")
    @CommandCompletion("@teamMembers")
    @Syntax("<target> - player to be kicked.")
    public void onKickMember(@Conditions("hasTeam|isTeamLeader") Player player,
            @Flags("other") @Conditions("sameTeam") Player target) {

    }

    @Conditions("teamManagement")
    @Subcommand("leave|quit|abandon")
    public void leaveTeam(@Conditions("isNotTeamLeader") Player player) {
        var team = getPlayerTeam(player.getUniqueId());

        if (team.removeMember(player.getUniqueId())) {
            player.sendMessage(ChatColor.of("#DABC12") + "You've abandoned Team " + team.getTeamDisplayName());
            // Clear cache and team set
            instance.getTeamManger().getCache().invalidate(player.getUniqueId().getMostSignificantBits());
            // Call the event
            Bukkit.getPluginManager().callEvent(new PlayerLeftTeamEvent(team, player));
        } else {
            player.sendMessage(ChatColor.RED + "Could not remove your from the team.");
            System.err.println(player.getName() + " tried to left their team but couldn't");
        }
    }

    @Subcommand("rename|name")
    @Syntax("<teamName> - New name for your team")
    public void teamRename(@Conditions("isTeamLeader") Player player, String teamName) {
        var team = getPlayerTeam(player.getUniqueId());
        team.setTeamDisplayName(teamName);
        team.sendTeamMessage(ChatColor.of("#7ab83c") + player.getName() + " has change the team name to: " + teamName);
    }

    @Conditions("teamManagement")
    @Subcommand("disband")
    public void teamDisband(@Conditions("isTeamLeader") Player player) {
        var team = getPlayerTeam(player.getUniqueId());
        if (instance.getTeamManger().getTeamMap().remove(team.getTeamID(), team)) {
            team.sendTeamMessage(ChatColor.RED + "Team has been disbanded by " + player.getName());
            // Clear the cache
            for (var uuid : team.getMembers())
                instance.getTeamManger().getCache().invalidate(uuid.getMostSignificantBits());

            Bukkit.getPluginManager().callEvent(new TeamDisbandedEvent(team));
        } else {
            player.sendMessage(ChatColor.RED + "Couldn't disband the team.");
        }

    }

    @CommandPermission("uhc.team.reset")
    @Subcommand("reset|clear")
    public void teamReset(CommandSender sender) {
        instance.getTeamManger().getTeamMap().values().stream().forEach(team -> {
            Bukkit.getPluginManager().callEvent(new TeamRemovedEvent(team));
            team.sendTeamMessage("Your team has been removed!");
        });
        instance.getTeamManger().clearTeams();
        instance.getTeamManger().clearCache();
    }

    public void teamPromote() {

    }

    @Subcommand("chat|tc")
    @Syntax("<message> - Message to send to your team")
    @CommandAlias("tc|teamchat")
    public void teamChat(@Conditions("hasTeam") Player sender, String message) {
        var team = instance.getTeamManger().getPlayerTeam(sender.getUniqueId());
        team.sendTeamMessage(
                ChatColor.of("#DABC12") + "[TeamChat] " + sender.getName() + ": " + ChatColor.GRAY + message);
    }

    @CommandAlias("sendcoord|sendcoords|sc")
    @Subcommand("sendcoord|sendcoords|sc")
    public void teamSendCoords(@Conditions("hasTeam") Player sender) {
        teamChat(sender, "I'm at " + getLocation(sender.getLocation()));
    }

    @CommandCompletion("@otherplayers")
    @CommandAlias("tl")
    @Subcommand("list")
    public void teamList(CommandSender sender, @Optional @Flags("other") Player target) {
        if (target != null) {
            var team = instance.getTeamManger().getPlayerTeam(target.getUniqueId());
            if (team != null) {
                sender.sendMessage(team.getTeamDisplayName() + "'s members: ");
                Arrays.asList(team.getMembers()).stream().map(id -> Bukkit.getOfflinePlayer(id)).forEach(ofp -> {
                    if (ofp.isOnline()) {
                        var on = ofp.getPlayer();
                        sender.sendMessage(ChatColor.GREEN + " - " + on.getName() + ChatColor.WHITE + " "
                                + ((int) on.getHealth()) + ChatColor.DARK_RED + "❤");
                    } else {
                        sender.sendMessage(ChatColor.GRAY + " - " + ofp.getName());
                    }
                });

            } else {
                sender.sendMessage(ChatColor.of("#7ab83c") + target.getName() + " doesn't have a team.");
            }

        } else {
            if (sender instanceof Player) {
                var player = (Player) sender;
                var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                if (team != null) {
                    player.sendMessage(team.getTeamDisplayName() + "'s members: ");
                    Arrays.asList(team.getMembers()).stream().map(id -> Bukkit.getOfflinePlayer(id)).forEach(ofp -> {
                        if (ofp.isOnline()) {
                            var on = ofp.getPlayer();
                            player.sendMessage(ChatColor.GREEN + " - " + on.getName() + " " + ((int) on.getHealth())
                                    + ChatColor.DARK_RED + "❤");
                        } else {
                            player.sendMessage(ChatColor.GRAY + " - " + ofp.getName());
                        }
                    });

                } else {
                    sender.sendMessage(ChatColor.of("#7ab83c") + "You dont't have a team...");
                }
            } else {
                sender.sendMessage("Consoles dont have teams");
            }

        }

    }

    @Conditions("teamManagement")
    @Subcommand("accept")
    @CommandAlias("accept")
    @CommandCompletion("@invites")
    @Syntax("<invitor> - player to accept an invite from.")
    public void acceptInvite(@Conditions("hasNotTeam") Player sender, @Flags("other") Player invitor) {
        var invite = instance.getTeamManger().getTeamInviteForTarget(invitor.getUniqueId(), sender.getUniqueId());
        if (invite == null) {
            sender.sendMessage(ChatColor.RED + "The player hasn't invited you, or the invite has expired!");
            return;
        }
        var team = instance.getTeamManger().getTeamMap().get(invite.getTeamToJoin());
        if (team == null) {
            System.err.println("The team " + sender.getName() + " attempted to join has somehow been disbanded.");
            sender.sendMessage(ChatColor.RED + "The team you are attempting to join is no longer available");
            return;
        }
        if (team.getMembers().length + 1 > instance.getTeamManger().getTeamSize()) {
            sender.sendMessage(ChatColor.RED + "The team you are trying to join already has " + team.getMembers().length
                    + " members!");
            return;
        }
        if (team.addMember(sender.getUniqueId())) {
            // Notify the team that the player has joined
            team.sendTeamMessage(ChatColor.of("#7ab83c") + sender.getName() + " has joined the team!");
            // Call the event
            Bukkit.getPluginManager().callEvent(new PlayerJoinedTeamEvent(team, sender));
            // Delete the invite from cache
            instance.getTeamManger().getTeamInvite().invalidate(invitor.getUniqueId().getMostSignificantBits());
        } else {
            System.err.println(sender.getName() + " attempted to join the team whilst already being a member.");
            sender.sendMessage(ChatColor.RED + "Couldn't join the team...");
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
            sender.sendMessage(ChatColor.RED + "The player hasn't invited you, or the invite has expired!");
            return;
        }
        var team = instance.getTeamManger().getTeamMap().get(invite.getTeamToJoin());
        if (team == null) {
            sender.sendMessage(ChatColor.of("#DABC12") + "You've rejected " + invitor.getName() + "'s invite.");
        } else {
            sender.sendMessage(ChatColor.RED + "You've rejected " + invitor.getName() + "'s invite to join Team "
                    + team.getTeamDisplayName());
            team.sendTeamMessage(sender.getName() + " has rejected the team invite.");
        }
        // Clear invite from cache
        instance.getTeamManger().getTeamInvite().invalidate(invitor.getUniqueId().getMostSignificantBits());
    }

    @CommandPermission("uhc.team.management")
    @Subcommand("man|management|manage")
    @CommandCompletion("@bool")
    @Syntax("<bool> - True or False to set the management")
    public void teamManagement(CommandSender sender, @Optional Boolean bool) {
        if (bool == null) {
            instance.getTeamManger().setTeamManagement(!instance.getTeamManger().isTeamManagement());
        } else {
            instance.getTeamManger().setTeamManagement(bool);
        }
        sender.sendMessage(ChatColor.of("#DABC12") + "Team management has been set to "
                + instance.getTeamManger().isTeamManagement());
    }

    @CommandPermission("uhc.team.management")
    @CommandCompletion("@range:1-9")
    @Syntax("<teamSize> - Team size to be set")
    @Subcommand("size")
    public void teamSize(CommandSender sender, @Conditions("limits:min=1") Integer teamSize) {
        sender.sendMessage(ChatColor.of("#DABC12") + "Team size has been set from "
                + instance.getTeamManger().getTeamSize() + " to " + teamSize);
        instance.getTeamManger().setTeamSize(teamSize);
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