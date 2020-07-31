package me.infinityz.minigame.teams;

import java.util.ArrayList;
import java.util.UUID;

import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.teams.objects.Team;

public class TeamManager {
    UHC instance;
    ArrayList<Team> teamArray = new ArrayList<>();
    private @Getter THashMap<UUID, Team> teamMap;
    private @Getter THashMap<Long, UUID> playerTeamCache;
    

    public TeamManager(UHC instance) {
        this.instance = instance;
    }

    public Team getPlayerTeam(UUID uuid){
        //Check if player is cached.
        var teamID = playerTeamCache.get(uuid.getMostSignificantBits());
        if(teamID != null){
            //Check if team isn't null
            var team = teamMap.get(teamID);
            if(team != null && team.isMember(uuid)){
                //Return team
                return team;
            }
        }
        //If player wasn't chached, then check if they have a team.
        var team = getPlayerTeamLegacy(uuid);
        //If they have a team, cache it.
        if(team != null){
            playerTeamCache.put(uuid.getMostSignificantBits(), team.getTeamID());
        }
        return team;

    }

    public Team getPlayerTeamLegacy(UUID uuid) {
        return teamMap.values().stream().filter(team -> team.isMember(uuid)).findFirst().orElse(null);
    }

    public boolean addTeam(Team team) {
        
        for (var teams : teamArray) {
            if (teams.isMember(team.getTeamLeader()) || team.isTeamLeader(team.getTeamLeader()))
                return false;
        }
        
        teamArray.add(team);
        return true;
    }


}