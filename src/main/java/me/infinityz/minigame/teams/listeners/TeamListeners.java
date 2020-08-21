package me.infinityz.minigame.teams.listeners;

import java.util.stream.Collectors;

import com.github.benmanes.caffeine.cache.RemovalCause;

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
import me.infinityz.minigame.teams.events.TeamInviteExpireEvent;
import me.infinityz.minigame.teams.events.TeamRemovedEvent;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class TeamListeners implements Listener {
    private @NonNull UHC instance;
    /* Constant */
    private static final ChatColor SUSHI_GREEN = ChatColor.of("#7ab83c");

    @EventHandler
    public void onInviteExpireEvent(TeamInviteExpireEvent e) {
        var invite = e.getInvite();
        var target = Bukkit.getOfflinePlayer(invite.getTarget());
        var sender = Bukkit.getOfflinePlayer(invite.getSender());

        if (e.getRemovalCause() != RemovalCause.EXPLICIT && sender.isOnline()) {
            sender.getPlayer().sendMessage(ChatColor.RED + "Your invite to " + target.getName() + " has expired.");
        }
        if (target.isOnline()) {
            var team = instance.getTeamManger().getPlayerTeam(target.getUniqueId());
            if (team != null
                    && team.getTeamID().getMostSignificantBits() == invite.getTeamToJoin().getMostSignificantBits()) {
                return;
            }

            target.getPlayer().sendMessage(ChatColor.RED + sender.getName() + "'s team invite expired.");

        }
    }

    @EventHandler
    public void onCreate(TeamCreatedEvent e) {
        var team = e.getTeam();
        e.getPlayer().sendMessage(SUSHI_GREEN + "Team " + team.getTeamDisplayName() + " has been created!");
        var nameList = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        team.getPlayerStream().forEach(all -> {
            try {
                FastBoard.removeTeam(all);
                FastBoard.createTeam(nameList, all, team.getTeamPrefix(), team.getTeamColorIndex());
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
        team.sendTeamMessage(SUSHI_GREEN + e.getPlayer().getName() + " has joined the team!");

        var nameList = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        team.getPlayerStream().forEach(all -> {
            try {
                FastBoard.removeTeam(all);
                FastBoard.createTeam(nameList, all, team.getTeamPrefix(), team.getTeamColorIndex());
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        });

    }

    @EventHandler
    public void onLeftTeam(PlayerLeftTeamEvent e) {
        var team = e.getTeam();
        e.getPlayer().sendMessage(ChatColor.of("#DABC12") + "You've abandoned Team " + team.getTeamDisplayName());
        team.sendTeamMessage(SUSHI_GREEN + e.getPlayer().getName() + " has abandoned the team!");
        var nameList = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        team.getPlayerStream().forEach(all -> {
            try {
                FastBoard.removeTeam(all);
                FastBoard.createTeam(nameList, all, team.getTeamPrefix(), team.getTeamColorIndex());
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
                FastBoard.createTeam(nameList, player, team.getTeamPrefix(), team.getTeamColorIndex());
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }

    }

}