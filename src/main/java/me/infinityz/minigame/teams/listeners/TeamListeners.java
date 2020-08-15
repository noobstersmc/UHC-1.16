package me.infinityz.minigame.teams.listeners;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.scoreboard.objects.FastBoard;
import me.infinityz.minigame.teams.events.PlayerJoinedTeamEvent;
import me.infinityz.minigame.teams.events.PlayerLeftTeamEvent;
import me.infinityz.minigame.teams.events.TeamCreatedEvent;
import me.infinityz.minigame.teams.events.TeamDisbandedEvent;
import me.infinityz.minigame.teams.events.TeamRemovedEvent;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class TeamListeners implements Listener {
    private @NonNull UHC instance;

    @EventHandler
    public void onCreate(TeamCreatedEvent e) {
        var team = e.getTeam();
        e.getPlayer().sendMessage(ChatColor.of("#7ab83c") + "Team " + team.getTeamDisplayName() + " has been created!");
        var nameList = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        team.getPlayerStream().forEach(all -> {
            try {
                FastBoard.removeTeam(all);
                FastBoard.createTeam(nameList, all, ChatColor.GREEN + "[Team] ");
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onRemove(TeamRemovedEvent e) {
        var team = e.getTeam();
        team.sendTeamMessage("Your team has been removed!");
        team.getPlayerStream().forEach(all -> {
            try {
                FastBoard.removeTeam(all);
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        });

    }

    @EventHandler
    public void onRemove(TeamDisbandedEvent e) {
        var team = e.getTeam();
        team.sendTeamMessage(ChatColor.RED + "Team has been disbanded by "
                + Bukkit.getOfflinePlayer(e.getTeam().getTeamLeader()).getName());
        team.getPlayerStream().forEach(all -> {
            try {
                FastBoard.removeTeam(all);
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        });

    }

    @EventHandler
    public void onJoinTeam(PlayerJoinedTeamEvent e) {
        var team = e.getTeam();
        // Notify the team that the player has joined
        team.sendTeamMessage(ChatColor.of("#7ab83c") + e.getPlayer().getName() + " has joined the team!");

        var nameList = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        team.getPlayerStream().forEach(all -> {
            try {
                FastBoard.removeTeam(all);
                FastBoard.createTeam(nameList, all, ChatColor.GREEN + "[Team] ");
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        });

    }

    @EventHandler
    public void onLeftTeam(PlayerLeftTeamEvent e) {
        var team = e.getTeam();
        e.getPlayer().sendMessage(ChatColor.of("#DABC12") + "You've abandoned Team " + team.getTeamDisplayName());
        team.sendTeamMessage(ChatColor.of("#7ab83c") + e.getPlayer().getName() + " has abandoned the team!");
        var nameList = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        team.getPlayerStream().forEach(all -> {
            try {
                FastBoard.removeTeam(all);
                FastBoard.createTeam(nameList, all, ChatColor.GREEN + "[Team] ");
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        });

        var player = e.getPlayer();
        try {
            FastBoard.removeTeam(player);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
        if (team != null) {
            var nameList = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
            try {
                FastBoard.removeTeam(player);
                FastBoard.createTeam(nameList, player, ChatColor.GREEN + "[Team] ");
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }

    }

}