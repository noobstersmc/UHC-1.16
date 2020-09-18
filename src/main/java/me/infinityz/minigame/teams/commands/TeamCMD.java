package me.infinityz.minigame.teams.commands;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
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
import me.infinityz.minigame.scoreboard.objects.FastBoard;
import me.infinityz.minigame.teams.events.PlayerJoinedTeamEvent;
import me.infinityz.minigame.teams.events.PlayerKickedFromTeamEvent;
import me.infinityz.minigame.teams.events.PlayerLeftTeamEvent;
import me.infinityz.minigame.teams.events.PlayerPromotedToLeaderEvent;
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
        if (target == player) {
            player.sendMessage(ChatColor.RED + "You can't kick yourself...");
            return;
        }

        var team = getPlayerTeam(player.getUniqueId());
        if (team != null && team.removeMember(target.getUniqueId())) {
            Bukkit.getPluginManager().callEvent(new PlayerKickedFromTeamEvent(team, target));
        }

    }

    @Subcommand("promote|leader")
    @CommandCompletion("@teamMembers")
    @Syntax("<target> - player to be promoted")
    public void teamPromote(@Conditions("hasTeam|isTeamLeader") Player player,
            @Flags("other") @Conditions("sameTeam") Player target) {
        if (target == player) {
            player.sendMessage(ChatColor.RED + "You can't promote yourself...");
            return;
        }
        var team = getPlayerTeam(player.getUniqueId());
        if (team != null) {
            team.setTeamLeader(target.getUniqueId());
            Bukkit.getPluginManager().callEvent(new PlayerPromotedToLeaderEvent(team, target));
        }
    }

    @CommandCompletion("@otherplayers")
    @CommandPermission("uhc.team.forcejoin")
    @Subcommand("forcejoin|fj")
    @Syntax("/team forcejoin <target> <targetTeam>")
    public void forceJoin(CommandSender sender, @Flags("other") Player target,
            @Flags("other") @Conditions("hasTeam") Player toTeamPlayer) {
        var targetTeam = getPlayerTeam(target.getUniqueId());
        if (targetTeam != null) {
            if (targetTeam.isTeamLeader(target.getUniqueId()) && disbandTeam(targetTeam, true))
                targetTeam.sendTeamMessageWithPrefix(" Your team has been removed by an admin.");
            else
                removePlayerFromTeam(targetTeam, target);
        }
        var toTeam = getPlayerTeam(toTeamPlayer.getUniqueId());
        if (toTeam != null && toTeam.addMember(target.getUniqueId())){
            toTeam.sendTeamMessageWithPrefix(" " + target.getName() + " has been added to the team by an admin.");
            var event = new PlayerJoinedTeamEvent(toTeam, target);
            event.setQuiet(true);
            Bukkit.getPluginManager().callEvent(event);
        }
        else
            sender.sendMessage(target.getName() + " could not be added to the target team.");

    }

    
    public void forceRemove(CommandSender sender,  @Flags("other") Player target,
    @Flags("other") @Conditions("hasTeam") Player toTeamPlayer){

    }

    // TODO: ADD FORCE JOIN | REMOVE

    @Conditions("teamManagement")
    @Subcommand("leave|quit|abandon")
    public void leaveTeam(@Conditions("isNotTeamLeader") Player player) {
        var team = getPlayerTeam(player.getUniqueId());
        removePlayerFromTeam(team, player);
    }

    private void removePlayerFromTeam(Team team, Player player) {
        if (team.removeMember(player.getUniqueId())) {
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
    public void teamRename(@Conditions("hasTeam|isTeamLeader") Player player, String teamName) {
        var team = getPlayerTeam(player.getUniqueId());
        team.setTeamDisplayName(teamName);
        team.sendTeamMessage(ChatColor.of("#7ab83c") + player.getName() + " has change the team name to: " + teamName);
    }

    @Subcommand("color|colorchange")
    @CommandCompletion("@range:0-21")
    @Syntax("<newColorIndex> - New EnumChatFormat Var Int")
    public void changeColorIndex(@Conditions("hasTeam|isTeamLeader") Player player,
            @Conditions("limits:min=0,max=21") Integer newColorIndex) {
        var team = getPlayerTeam(player.getUniqueId());
        team.setTeamColorIndex(newColorIndex);
        try {
            team.sendTeamMessage(ChatColor.of("#7ab83c") + player.getName() + " has change the team color to: "
                    + FastBoard.getEnumChatFormat(newColorIndex) + "Color");
            team.updateDisplay();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Subcommand("prefix")
    @Syntax("<prefix> - New team prefix")
    public void changeTeamPrefix(@Conditions("hasTeam|isTeamLeader") Player player, String newPrefix) {
        var team = getPlayerTeam(player.getUniqueId());
        newPrefix = ChatColor.translateAlternateColorCodes('&', newPrefix);
        team.setTeamPrefix(newPrefix);
        team.sendTeamMessage(
                ChatColor.of("#7ab83c") + player.getName() + " has change the team prefix to: " + newPrefix);
        team.updateDisplay();
    }

    @Conditions("teamManagement")
    @Subcommand("disband")
    public void teamDisband(@Conditions("hasTeam|isTeamLeader") Player player) {
        var team = getPlayerTeam(player.getUniqueId());
        if (!disbandTeam(team))
            player.sendMessage(ChatColor.RED + "Couldn't disband the team.");

    }

    private boolean disbandTeam(Team team) {
        return disbandTeam(team, false);
    }

    private boolean disbandTeam(Team team, boolean quiet) {
        if (instance.getTeamManger().getTeamMap().remove(team.getTeamID(), team)) {
            // Clear the cache
            for (var uuid : team.getMembers())
                instance.getTeamManger().getCache().invalidate(uuid.getMostSignificantBits());

            var event = new TeamDisbandedEvent(team);
            event.setQuiet(quiet);
            Bukkit.getPluginManager().callEvent(event);
            return true;
        }
        return false;
    }

    @CommandPermission("uhc.team.reset")
    @Subcommand("reset|clear")
    public void teamReset(CommandSender sender) {
        instance.getTeamManger().getTeamMap().values().stream().forEach(team -> {
            Bukkit.getPluginManager().callEvent(new TeamRemovedEvent(team));
        });
        instance.getTeamManger().clearTeams();
        instance.getTeamManger().clearCache();
    }

    @Subcommand("chat|tc")
    @Syntax("<message> - Message to send to your team")
    @CommandAlias("tc|teamchat")
    public void teamChat(@Conditions("hasTeam") Player sender, String message) {
        var team = instance.getTeamManger().getPlayerTeam(sender.getUniqueId());

        team.getPlayerStream().filter(player -> player != sender).forEach(members -> {
            members.playSound(members.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.25f, 1);
        });
        team.sendTeamMessageWithPrefix(sender.getName() + ": " + ChatColor.GRAY + message);
    }

    @CommandAlias("sendcoord|sendcoords|sc")
    @Subcommand("sendcoord|sendcoords|sc")
    public void teamSendCoords(@Conditions("hasTeam") Player sender) {
        if (sender.getGameMode() == GameMode.SPECTATOR) {
            sender.sendMessage(ChatColor.RED + "Error: Spectators can't send coords.");
            return;
        }
        teamChat(sender, "I'm at " + getLocation(sender.getLocation()));
    }

    @CommandCompletion("@otherplayers")
    @CommandAlias("tl|kc")
    @Subcommand("list")
    public void teamList(CommandSender sender, @Optional @Flags("other") OfflinePlayer target) {
        // Verify is target is not null
        if (target != null) {
            var team = instance.getTeamManger().getPlayerTeam(target.getUniqueId());
            if (team != null) {
                sender.sendMessage(team.getTeamDisplayName() + "'s members: ");

                team.getOfflinePlayersStream().forEach(members -> {
                    var uhcMember = instance.getPlayerManager().getPlayer(members.getUniqueId());
                    var kills = uhcMember != null ? uhcMember.getKills() : 0;
                    var message = ChatColor.GREEN + "%s" + ChatColor.WHITE + " %s" + ChatColor.DARK_RED + " ❤ "
                            + ChatColor.GRAY + "kills: " + ChatColor.WHITE + "%d";

                    if (members.isOnline()) {
                        Player onlineMember = members.getPlayer();
                        // Player online and not in game yet or is still alive
                        if ((uhcMember != null && uhcMember.isAlive()) || uhcMember == null) {
                            message = String.format(message, onlineMember.getName(),
                                    "" + (int) (onlineMember.getHealth() + onlineMember.getAbsorptionAmount()), kills);
                        } else { // Player can only be dead at this point
                            message = String.format(message,
                                    ChatColor.RED + "" + ChatColor.STRIKETHROUGH + onlineMember.getName(), "0", kills);
                        }

                    } else if (uhcMember != null) {
                        // Player offline but still a member. If alive show normally, when dead show
                        // STRIKETHROUGH
                        message = String.format(message,
                                (!uhcMember.isAlive() ? ChatColor.RED + "" + ChatColor.STRIKETHROUGH : "")
                                        + members.getName(),
                                (int) uhcMember.getLastKnownHealth(), kills);
                    } else {
                        // If offline and no player data is known, display unknown.
                        message = String.format(message, members.getName(), "Unknown", kills);
                    }

                    sender.sendMessage(message);
                });

            } else {
                var uhcp = instance.getPlayerManager().getPlayer(target.getUniqueId());
                var player = target.isOnline() ? target.getPlayer() : null;
                var health = player != null ? player.getHealth() : (uhcp != null ? uhcp.getLastKnownHealth() : 0);
                var p_kills = uhcp != null ? uhcp.getKills() : 0;
                if (uhcp != null) {
                    if (uhcp.isDead()) {
                        sender.sendMessage((ChatColor.RED + "" + ChatColor.STRIKETHROUGH) + "" + target.getName()
                                + ChatColor.WHITE + " " + (int) health + ChatColor.DARK_RED + " ❤ " + ChatColor.GRAY
                                + "kills: " + ChatColor.WHITE + p_kills);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "" + target.getName() + ChatColor.WHITE + " " + health
                                + ChatColor.DARK_RED + " ❤ " + ChatColor.GRAY + "kills: " + ChatColor.WHITE + p_kills);
                    }

                } else {
                    sender.sendMessage((health > 0 ? ChatColor.GREEN : ChatColor.RED + "" + ChatColor.STRIKETHROUGH)
                            + "" + target.getName() + ChatColor.WHITE + " " + health + ChatColor.DARK_RED + " ❤ "
                            + ChatColor.GRAY + "kills: " + ChatColor.WHITE + p_kills);

                }
            }

        } else { // When target null, assume targeting self.
            if (sender instanceof Player) {
                // Recursive method to save code
                teamList(sender, (Player) sender);
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

    @CommandPermission("uhc.team.random")
    @Subcommand("random")
    public void randomizeTeams(CommandSender sender, @Optional String args) {
        // By default, it should respect current teams, and team size limit
        var teamSize = instance.getTeamManger().getTeamSize();
        if (teamSize > 1) {
            if (args != null && args.length() > 0) {
                // Arguments provided
            } else {
                // Default behavior
                var playersWithoutTeam = Bukkit.getOnlinePlayers().stream()
                        .filter(player -> getPlayerTeam(player.getUniqueId()) == null).map(Player::getPlayer)
                        .collect(Collectors.toList());
                Collections.shuffle(playersWithoutTeam);
                final var playersWithoutTeamAmount = playersWithoutTeam.size();
                var teamsNeed = (int) Math.ceil(playersWithoutTeamAmount / teamSize) + 1;
                sender.sendMessage(ChatColor.GREEN + "Creating " + teamsNeed + " random teams of " + teamSize + " for "
                        + playersWithoutTeamAmount + " players!");
                // Have it inside a try to that it finishes when error is encountered.
                try {
                    for (int i = 0; i < teamsNeed; i++) {
                        var leader = playersWithoutTeam.remove(0);
                        var team = createTeam(leader, "R" + (i + 1));

                        for (int j = 0; j < teamSize - 1; j++) {
                            var member = playersWithoutTeam.remove(0);
                            team.addMember(member.getUniqueId());
                            Bukkit.getPluginManager().callEvent(new PlayerJoinedTeamEvent(team, member));
                        }

                    }
                } catch (Exception ignore) { // Expected behavior
                }
                sender.sendMessage(ChatColor.GREEN + "" + teamsNeed + " teams have been created!");

            }
        }

    }

    @CommandPermission("uhc.team.management")
    @CommandCompletion("@range:1-9")
    @Syntax("<number> - Team size to be set")
    @Subcommand("size")
    public void teamSize(CommandSender sender, @Conditions("limits:min=1,max=100") Integer number) {
        sender.sendMessage(ChatColor.of("#DABC12") + "Team size has been set from "
                + instance.getTeamManger().getTeamSize() + " to " + number);
        instance.getTeamManger().setTeamSize(number);
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