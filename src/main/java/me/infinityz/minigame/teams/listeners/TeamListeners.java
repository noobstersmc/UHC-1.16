package me.infinityz.minigame.teams.listeners;

import java.util.stream.Collectors;

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

@RequiredArgsConstructor
public class TeamListeners implements Listener {
    private @NonNull UHC instance;

    @EventHandler
    public void onCreate(TeamCreatedEvent e) {
        var team = e.getTeam();
        var nameList = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        team.getPlayerStream().forEach(all -> {
            try {
                FastBoard.removeTeam(all);
                FastBoard.createTeam(nameList, all);
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onRemove(TeamRemovedEvent e) {
        var team = e.getTeam();
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
        var nameList = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        team.getPlayerStream().forEach(all -> {
            try {
                FastBoard.removeTeam(all);
                FastBoard.createTeam(nameList, all);
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        });

    }

    @EventHandler
    public void onLeftTeam(PlayerLeftTeamEvent e) {
        var team = e.getTeam();
        var nameList = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        team.getPlayerStream().forEach(all -> {
            try {
                FastBoard.removeTeam(all);
                FastBoard.createTeam(nameList, all);
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
                FastBoard.createTeam(nameList, player);
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }

    }

}