package me.infinityz.minigame.teams.objects;

import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Team {

    private @Getter @Setter UUID teamLeader;
    private @Getter UUID teamID;
    private @Getter @Setter UUID[] members;
    private @Getter @Setter String teamDisplayName;
    private @Getter @Setter int teamKills;

    public Team(UUID teamLeader) {
        this.teamID = UUID.randomUUID();
        this.teamLeader = teamLeader;
        this.teamKills = 0;
        this.teamDisplayName = teamID.toString().substring(0, 6);
        addMember(teamLeader);
    }

    public boolean isMember(UUID uuid) {
        if (members == null || members.length < 1)
            return false;
        for (var m : members)
            if (m.compareTo(uuid) == 0)
                return true;

        return false;
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
        for (var uuid : members) {
            var player = Bukkit.getOfflinePlayer(uuid);
            if (player.isOnline())
                player.getPlayer().spigot().sendMessage(component);
        }
    }

    public void sendTeamMessage(String str) {
        sendTeamMessage(new TextComponent(str));
    }

    public void addKills(int kill) {
        this.teamKills = this.teamKills + kill;
    }

}