package me.infinityz.minigame.teams;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.teams.objects.Team;
import me.infinityz.minigame.teams.objects.TeamInvite;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class TeamManager {
    UHC instance;
    private @Getter THashMap<UUID, Team> teamMap = new THashMap<>();
    private @Getter Cache<Long, UUID> cache = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    private @Getter Cache<Long, TeamInvite> teamInvite = Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS)
            .build();
    private @Getter @Setter int teamSize = 2;
    private @Getter @Setter boolean teamManagement = false;

    public TeamManager(final UHC instance) {
        this.instance = instance;
    }

    public void sendTeamInvite(final Team team, final Player sender, final Player target) {
        var invite = teamInvite.getIfPresent(sender.getUniqueId().getMostSignificantBits());
        if (invite != null) {
            teamInvite.invalidate(sender.getUniqueId().getMostSignificantBits());
            sender.sendActionBar(ChatColor.of("#ed1c50") + "Cancelling your previous team invite to "
                    + ChatColor.of("#d3d7db") + Bukkit.getOfflinePlayer(invite.getTarget()));
        }
        teamInvite.put(sender.getUniqueId().getMostSignificantBits(), new TeamInvite(team.getTeamID(), sender, target));
        sender.sendMessage("Invite has been sent!");
        target.sendMessage(new ComponentBuilder(
                ChatColor.WHITE + sender.getName() + ChatColor.GREEN + " has invited you to their team, ")
                        .append(ChatColor.WHITE + "click here")
                        .event(new ClickEvent(Action.RUN_COMMAND, "/team accept " + sender.getName()))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to join the team")))
                        .append(ChatColor.GREEN + " to join or type /team accept " + sender.getName() + "!").reset()
                        .create());
    }

    public TeamInvite getTeamInviteForSender(UUID sender) {
        return teamInvite.getIfPresent(sender.getMostSignificantBits());
    }

    public TeamInvite getTeamInviteForTarget(UUID sender, UUID target) {
        // Obtain the invite
        var invite = getTeamInviteForSender(sender);
        // Return null if not found
        if (invite == null)
            return null;
        // Check if the invite belong to the target.
        if (invite.getTarget().getMostSignificantBits() == target.getMostSignificantBits())
            return invite;
        // Return null if not valid
        return null;
    }

    public Team getPlayerTeam(final UUID uuid) {
        // Check if player is cached.
        var teamID = cache.getIfPresent(uuid.getMostSignificantBits());
        if (teamID != null) {
            // Check if team isn't null
            var team = teamMap.get(teamID);
            if (team != null && team.isMember(uuid)) {
                // Return team
                return team;
            }
        }
        // Remove the player from cache if they exist
        cache.invalidate(uuid.getMostSignificantBits());
        // If player wasn't chached, then check if they have a team from the set.
        var team = getPlayerTeamLegacy(uuid);
        // If they have a team, cache it.
        if (team != null) {
            cache.put(uuid.getMostSignificantBits(), team.getTeamID());
        }
        // Return team, regarless of nullity
        return team;
    }

    public Team getPlayerTeamLegacy(final UUID uuid) {
        // Find the player's team.
        if (teamMap.isEmpty())
            return null;
        return teamMap.values().stream().filter(team -> team.isMember(uuid)).findFirst().orElse(null);
    }

    public boolean addTeam(final Team team) {
        // Lambda check if the team already exist or is the the leader of new team if a
        // member of the old one.
        if (teamMap.values().stream().filter(t -> t.getTeamID() == team.getTeamID() || t.isMember(team.getTeamLeader()))
                .findFirst().isPresent())
            return false;
        // Add the new team to the map
        teamMap.put(team.getTeamID(), team);
        // Return true to signify succesfull insertion
        return true;
    }

    public void clearCache() {
        System.out.println("The player-team cache map has been cleared.");
        cache.invalidateAll();
    }

    public void clearTeams() {
        Bukkit.broadcastMessage("All teams have been cleared.");
        teamMap.clear();
    }

}