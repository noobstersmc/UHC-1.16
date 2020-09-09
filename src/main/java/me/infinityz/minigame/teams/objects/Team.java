package me.infinityz.minigame.teams.objects;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.scoreboard.objects.FastBoard;
import net.md_5.bungee.api.chat.BaseComponent;

public class Team {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private @Getter @Setter UUID teamLeader;
    private @Getter UUID teamID;
    private @Getter @Setter UUID[] members;
    private @Getter @Setter String teamDisplayName;
    private @Getter @Setter int teamKills;
    private @Getter @Setter String teamPrefix = "➤ ";
    private @Getter @Setter int teamColorIndex = 10;

    public Team(UUID teamLeader) {
        this.teamID = UUID.randomUUID();
        this.teamLeader = teamLeader;
        this.teamKills = 0;
        this.teamDisplayName = teamID.toString().substring(0, 6);
        addMember(teamLeader);
    }

    public boolean isMember(UUID uuid) {
        return (members == null || members.length < 1) ? false
                : getMembersUUIDStream().anyMatch(member -> member.compareTo(uuid) == 0
                        || member.getMostSignificantBits() == uuid.getMostSignificantBits());
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

    public void sendTeamMessage(BaseComponent component) {
        getPlayerStream().forEach(player -> player.sendMessage(component));
    }

    public void sendTeamMessage(String str) {
        getPlayerStream().forEach(player -> player.sendMessage(str));
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

    public void updateDisplay() {
        getPlayerStream().forEach(this::updateDisplayToPlayer);
    }

    private void updateDisplayToPlayer(Player player) {
        try {
            FastBoard.removeTeam(player);
            FastBoard.createTeam(getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList()),
                    player, getTeamPrefix(), getTeamColorIndex());
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }

    }

}