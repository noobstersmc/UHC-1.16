package me.infinityz.minigame.teams;

import java.util.ArrayList;
import java.util.UUID;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.teams.objects.Team;

public class TeamManager {
    UHC instance;
    ArrayList<Team> teamArray = new ArrayList<>();

    public TeamManager(UHC instance) {
        this.instance = instance;
    }

    public Team getPlayerTeam(UUID uuid) {
        return teamArray.parallelStream().filter(team -> team.isMember(uuid)).findFirst().orElse(null);
    }

    public boolean addTeam(Team team) {
        for (var teams : teamArray) {
            if (teams.isMember(team.getTeamLeader()) || team.isTeamLeader(team.getTeamLeader()))
                return false;
        }
        teamArray.add(team);
        return true;
    }

    public ArrayList<Team> getTeamArray() {
        return teamArray;
    }

}