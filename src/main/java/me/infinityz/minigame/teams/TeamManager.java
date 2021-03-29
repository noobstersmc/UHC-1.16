package me.infinityz.minigame.teams;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.ConfigChangeEvent;
import me.infinityz.minigame.game.Game.ConfigType;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.teams.commands.TeamCMD;
import me.infinityz.minigame.teams.events.TeamInviteExpireEvent;
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
    private @Getter @Setter THashMap<UUID, Team> teamMap = new THashMap<>();
    private @Getter Cache<Long, UUID> cache = Caffeine.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    private @Getter LoadingCache<Long, TeamInvite> teamInvite = Caffeine.newBuilder()
            .scheduler(Scheduler.systemScheduler())
            .removalListener((key, value, cause) -> Bukkit.getPluginManager()
                    .callEvent(new TeamInviteExpireEvent((TeamInvite) value, cause, true)))
            .expireAfterWrite(10, TimeUnit.SECONDS).build(entry -> new TeamInvite(null, null, null));
    private @Getter int teamSize = 1;
    private @Getter @Setter boolean teamManagement = false;
    private @Getter @Setter boolean broacastColor = true;
    private @Getter @Setter boolean showPrefix = true;
    private @Getter @Setter boolean friendlyFire = true;
    private @Getter TeamCMD teamCommand;

    public TeamManager(final UHC instance) {
        this.instance = instance;
        this.teamCommand = new TeamCMD(instance);
        instance.getCommandManager().registerCommand(teamCommand);
    }

    public TeamInvite sendTeamInvite(final Team team, final Player sender, final Player target) {
        var invite = teamInvite.getIfPresent(sender.getUniqueId().getMostSignificantBits());
        if (invite != null) {
            teamInvite.invalidate(sender.getUniqueId().getMostSignificantBits());
            sender.sendActionBar(ChatColor.RED + "Cancelling your previous team invite to "
                    + Bukkit.getOfflinePlayer(invite.getTarget()));
        }
        var newInvite = new TeamInvite(team.getTeamID(), sender.getUniqueId(), target.getUniqueId());
        teamInvite.put(sender.getUniqueId().getMostSignificantBits(), newInvite);
        sender.sendMessage(ChatColor.GREEN + "Invite has been sent!");
        target.sendMessage(new ComponentBuilder(
                ChatColor.WHITE + sender.getName() + ChatColor.GREEN + " has invited you to their team, ")
                        .append(ChatColor.WHITE + "click here")
                        .event(new ClickEvent(Action.RUN_COMMAND, "/team accept " + sender.getName()))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to join the team")))
                        .append(ChatColor.GREEN + " to join or type /team accept " + sender.getName() + "!").reset()
                        .create());
        return newInvite;
    }

    public boolean isTeams() {
        return teamSize > 1;
    }

    public void setTeamSize(int teamSize){
        this.teamSize = teamSize;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.TEAM_SIZE));
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

    public boolean hasTeam(final UHCPlayer player) {
        return getPlayerTeam(player.getUUID()) != null;
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
        if (teamMap.values().stream()
                .anyMatch(t -> t.getTeamID() == team.getTeamID() || t.isMember(team.getTeamLeader())))
            return false;
        // Add the new team to the map
        teamMap.put(team.getTeamID(), team);
        // Return true to signify succesfull insertion
        return true;
    }

    public List<Team> getAliveTeams() {
        return teamMap.values().stream()
                .filter(all -> all.getOfflinePlayersStream().filter(Objects::nonNull)
                        .map(uuid -> instance.getPlayerManager().getPlayer(uuid.getUniqueId())).filter(Objects::nonNull)
                        .anyMatch(UHCPlayer::isAlive))
                .collect(Collectors.toList());
    }

    public List<Team> teamsOnline() {
        return teamMap.values().stream()
                .filter(all -> all.getOfflinePlayersStream().filter(Objects::nonNull).anyMatch(a -> a.isOnline()))
                .collect(Collectors.toList());
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