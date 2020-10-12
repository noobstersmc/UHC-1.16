package me.infinityz.minigame.teams.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.scoreboard.objects.FastBoard;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

public class Team {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Random localRandom = new Random();
    private @Getter @Setter UUID teamLeader;
    private @Getter UUID teamID;
    private @Getter @Setter UUID[] members;
    private @Getter @Setter String teamDisplayName;
    private @Getter @Setter int teamKills;
    private @Getter @Setter String teamPrefix = "➤ ";
    private @Getter @Setter int teamColorIndex = 10;
    private @Getter @Setter List<Pattern> teamShieldPattern;
    private @Getter Inventory teamInventory;

    public Team(UUID teamLeader) {
        this.teamID = UUID.randomUUID();
        this.teamLeader = teamLeader;
        this.teamKills = 0;
        this.teamDisplayName = teamID.toString().substring(0, 6);
        this.teamColorIndex = localRandom.nextInt(13)+1;
        addMember(teamLeader);
    }

    public boolean isMember(UUID uuid) {
        return (members == null || members.length < 1) ? false
                : getMembersUUIDStream().anyMatch(member -> member.compareTo(uuid) == 0
                        || member.getMostSignificantBits() == uuid.getMostSignificantBits());
    }

    public String getIdentifier() {
        return teamID.toString().substring(0, 14);
    }

    public boolean isCustomName() {
        return !teamID.toString().substring(0, 6).equalsIgnoreCase(teamDisplayName);
    }

    public boolean isTeamLeader(UUID member) {
        return member.compareTo(this.teamLeader) == 0;
    }

    public boolean addMember(UUID uuid) {
        if (isMember(uuid))// Return false if already a member
            return false;
        members = (UUID[]) ArrayUtils.add(members, uuid);// Add the member and return true
        return true;
    }

    public boolean removeMember(UUID uuid) {
        if (!isMember(uuid))
            return false;
        members = (UUID[]) ArrayUtils.removeElement(members, uuid);
        return true;
    }

    public Collection<UHCPlayer> getAliveMembers(UHC instance) {
        return getMembersUUIDStream().map(id -> instance.getPlayerManager().getPlayer(id))
                .filter(uhcp -> uhcp.isAlive()).collect(Collectors.toList());
    }

    public void sendTeamMessage(BaseComponent component) {
        getPlayerStream().forEach(player -> player.sendMessage(component));
    }

    public void sendTeamMessage(String str) {
        getPlayerStream().forEach(player -> player.sendMessage(str));
    }

    public void sendTeamMessageWithPrefix(String message) {
        sendTeamMessage(
                ChatColor.of("#DABC12") + "[Team" + (isCustomName() ? " " + getTeamDisplayName() : "") + "]" + message);
    }

    public Stream<Player> getPlayerStream() {
        return Arrays.stream(members).map(Bukkit::getOfflinePlayer).filter(Objects::nonNull)
                .filter(OfflinePlayer::isOnline).map(OfflinePlayer::getPlayer);
    }

    public Stream<OfflinePlayer> getOfflinePlayersStream() {
        return Arrays.stream(members).map(Bukkit::getOfflinePlayer).filter(Objects::nonNull);
    }

    public Stream<UUID> getMembersUUIDStream() {
        return Arrays.stream(members).filter(Objects::nonNull);
    }

    public void addKills(int kill) {
        this.teamKills = this.teamKills + kill;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public List<String> getListOfMembers() {
        var names = new ArrayList<String>();
        for (var member : members) {
            var ofPlayer = Bukkit.getOfflinePlayer(member);
            if (ofPlayer != null)
                names.add(ofPlayer.getName());
        }
        return names;
    }

    public void removeDisplay() {
        Bukkit.getOnlinePlayers().forEach(all -> {
            if (isMember(all.getUniqueId())) {
                removeDisplayToMember(all);
            } else {
                removeDisplayToPlayer(all);
            }

        });

    }

    public void removeDisplayToPlayer(Player player) {
        try {
            var ID = "1" + getIdentifier();
            FastBoard.removeTeam(player, ID);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    public void removeDisplayToMember(Player player) {
        try {
            var ID = "0" + getIdentifier();
            FastBoard.removeTeam(player, ID);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }

    }

    public void updateDisplay(boolean broadcastColor, boolean showPrefix) {
        var members = getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        if (broadcastColor) {
            Bukkit.getOnlinePlayers().forEach(all -> {
                if (isMember(all.getUniqueId()))
                    updateDisplayToMember(all, members);
                else
                    updateDisplayToPlayer(all, members, showPrefix);
            });
        } else {
            for (var uuid : getMembers()) {
                var ofp = Bukkit.getOfflinePlayer(uuid);

                if (ofp != null && ofp.isOnline())
                    updateDisplayToMember(ofp.getPlayer(), members);
            }
        }
    }

    public void updateDisplayToPlayer(Player player, Collection<String> members, boolean showPrefix) {
        try {
            var ID = "1" + getIdentifier();
            FastBoard.removeTeam(player, ID);
            FastBoard.createTeam(player, members, ID, showPrefix ? "["+ getTeamDisplayName() + "] " : "", getTeamColorIndex());
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    public void updateDisplayToMember(Player player, Collection<String> members) {
        try {
            var ID = "0" + getIdentifier();
            FastBoard.removeTeam(player, ID);
            FastBoard.createTeam(player, members, ID, getTeamPrefix(), getTeamColorIndex());
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }

    }

    //team inventory

    public void createTeamInventory(){
        teamInventory = Bukkit.createInventory(null, 36, isCustomName() ? 
        getTeamDisplayName()+ "'s team inventory" : "Team inventory");

    }

    public void destroyTeamInventory(){
        teamInventory = null;
    }
}