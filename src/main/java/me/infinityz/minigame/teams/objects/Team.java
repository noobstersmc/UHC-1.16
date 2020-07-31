package me.infinityz.minigame.teams.objects;

import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Team {
    private UUID teamLeader, teamID;
    private UUID[] members;
    private String teamDisplayName;
    private int teamKills;

    public Team(UUID teamLeader) {
        this.teamID = UUID.randomUUID();
        addMember(teamLeader);
        this.teamLeader = teamLeader;
        this.teamKills = 0;
    }

    public boolean isMember(UUID uuid) {
        for (var m : members)
            if (m.compareTo(uuid) == 0)
                return true;

        return false;
    }

    public void sendTeamMessage(BaseComponent component) {
        for (var uuid : members) {
            var player = Bukkit.getOfflinePlayer(uuid);
            if (player.isOnline())
                player.getPlayer().spigot().sendMessage(component);
        }
    }

    public void sendTeamMessage(String str){
        sendTeamMessage(new TextComponent(str));
    }

    public UUID getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(UUID teamLeader) {
        this.teamLeader = teamLeader;
    }

    public boolean isTeamLeader(UUID member) {
        return member.compareTo(this.teamLeader) == 0;
    }

    public UUID[] getMembers() {
        return members;
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

    public void setMembers(UUID[] members) {
        this.members = members;
    }

    public String getTeamDisplayName() {
        return teamDisplayName;
    }

    public void setTeamDisplayName(String teamDisplayName) {
        this.teamDisplayName = teamDisplayName;
    }

    public int getTeamKills() {
        return teamKills;
    }

    public void setTeamKills(int teamKills) {
        this.teamKills = teamKills;
    }

    public void addKills(int kill) {
        this.teamKills = this.teamKills + kill;
    }

    public UUID getTeamID() {
        return teamID;
    }

}