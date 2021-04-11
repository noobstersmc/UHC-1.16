package me.noobsters.minigame.teams.listeners;

import com.github.benmanes.caffeine.cache.RemovalCause;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.enums.Stage;
import me.noobsters.minigame.scoreboard.objects.FastBoard;
import me.noobsters.minigame.teams.events.PlayerJoinedTeamEvent;
import me.noobsters.minigame.teams.events.PlayerKickedFromTeamEvent;
import me.noobsters.minigame.teams.events.PlayerLeftTeamEvent;
import me.noobsters.minigame.teams.events.PlayerPromotedToLeaderEvent;
import me.noobsters.minigame.teams.events.TeamCreatedEvent;
import me.noobsters.minigame.teams.events.TeamDisbandedEvent;
import me.noobsters.minigame.teams.events.TeamDisplayUpdateEvent;
import me.noobsters.minigame.teams.events.TeamInviteExpireEvent;
import me.noobsters.minigame.teams.events.TeamRemovedEvent;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class TeamListeners implements Listener {
    private @NonNull UHC instance;
    /* Constant */
    private static final ChatColor SUSHI_GREEN = ChatColor.of("#7ab83c");

    @EventHandler(priority = EventPriority.LOW)
    public void friendlyFire(EntityDamageByEntityEvent e) {
        if(instance.getTeamManger().isFriendlyFire() || e.getEntity() == e.getDamager()
        || instance.getGameStage().equals(Stage.LOBBY)) return;

        if (e.getEntity() instanceof Player) {

            Player p2 = null;
            var player = (Player) e.getEntity();

            if (e.getDamager() instanceof Player) {
                var team = instance.getTeamManger().getPlayerTeam(e.getDamager().getUniqueId());
                if(team.isMember(player.getUniqueId())){
                    p2 = (Player) e.getDamager();
                }
            } else if (e.getDamager() instanceof Projectile) {
                Projectile proj = (Projectile) e.getDamager();
                if (proj.getShooter() instanceof Player) {
                    var shooter = (Player) proj.getShooter();
                    var team = instance.getTeamManger().getPlayerTeam(shooter.getUniqueId());
                    if(team.isMember(player.getUniqueId())){
                        p2 = (Player) proj.getShooter();
                    }
                }
            }
            
            if (p2 != null) {
                e.setCancelled(true);
            }
        }
    }
    
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
    public void onTeamPromotedEvent(PlayerPromotedToLeaderEvent e) {
        var team = e.getTeam();
        team.sendTeamMessage(
                ChatColor.of("#DABC12") + "[Team" + (team.isCustomName() ? " " + team.getTeamDisplayName() : "") + "] "
                        + e.getPlayer().getName() + " has been promoted to team leader.");
    }

    @EventHandler
    public void onPlayerKicked(PlayerKickedFromTeamEvent e) {
        var team = e.getTeam();
        var player = e.getPlayer();
        team.sendTeamMessage(SUSHI_GREEN + player.getName() + " has been kicked from the team!");
        player.sendMessage(SUSHI_GREEN + "You've been kicked out of your team!");
    }

    @EventHandler
    public void onCreate(TeamCreatedEvent e) {
        var team = e.getTeam();
        e.getPlayer().sendMessage(SUSHI_GREEN + "Team " + team.getTeamDisplayName() + " has been created!");
        team.updateDisplay(instance.getTeamManger().isBroacastColor(), instance.getTeamManger().isShowPrefix());
    }

    @EventHandler
    public void onTeamUpdateDisplay(TeamDisplayUpdateEvent e) {
        var team = e.getTeam();
        team.updateDisplay(instance.getTeamManger().isBroacastColor(), instance.getTeamManger().isShowPrefix());

    }

    @EventHandler
    public void onRemove(TeamRemovedEvent e) {
        var team = e.getTeam();
        team.removeDisplay();

        team.sendTeamMessage("Your team has been removed!");
    }

    @EventHandler
    public void onRemove(TeamDisbandedEvent e) {
        var team = e.getTeam();
        if (!e.isQuiet())
            team.sendTeamMessage(ChatColor.RED + "Team has been disbanded by "
                    + Bukkit.getOfflinePlayer(e.getTeam().getTeamLeader()).getName());

    }

    @EventHandler
    public void onJoinTeam(PlayerJoinedTeamEvent e) {
        var team = e.getTeam();
        // Notify the team that the player has joined
        if (!e.isQuiet())
            team.sendTeamMessage(SUSHI_GREEN + e.getPlayer().getName() + " has joined the team!");

        team.updateDisplay(instance.getTeamManger().isBroacastColor(), instance.getTeamManger().isShowPrefix());

    }

    @EventHandler
    public void onLeftTeam(PlayerLeftTeamEvent e) {
        var player = e.getPlayer();
        var team = e.getTeam();
        try {
            FastBoard.removeTeam(player);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }

        team.updateDisplay(instance.getTeamManger().isBroacastColor(), instance.getTeamManger().isShowPrefix());

        if (e instanceof PlayerKickedFromTeamEvent)
            return;

        e.getPlayer().sendMessage(ChatColor.of("#DABC12") + "You've abandoned Team " + team.getTeamDisplayName());
        team.sendTeamMessage(SUSHI_GREEN + e.getPlayer().getName() + " has abandoned the team!");

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        if(instance.getTeamManger().isBroacastColor() && instance.getTeamManger().isTeams()){
            instance.getTeamManger().getTeamMap().values().forEach(team -> {
                final var members = team.getListOfMembers();
                if (team.isMember(player.getUniqueId())) {
                    team.updateDisplayToMember(player, members);
                } else {
                    team.updateDisplayToPlayer(player, members, instance.getTeamManger().isBroacastColor());
                }
            });
        }

    }




}