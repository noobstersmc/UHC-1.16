package me.infinityz.minigame.teams.objects;

import java.util.Arrays;
import java.util.UUID;

import gnu.trove.map.hash.THashMap;

public class TeamTestHashMaps {
    static THashMap<UUID, Team> teamMap = new THashMap<>();
    static THashMap<Long, UUID> playerLookupTeamMap = new THashMap<>();

    public static void main(String[] args) {
        putTeam(new Team(UUID.fromString("5de6e184-af8d-498a-bbde-055e50653316")));
        for (int i = 0; i < 1000; i++) {
            putTeam(new Team(UUID.randomUUID()));
        }
        long nano_time = System.nanoTime();
        Team t = getPlayerTeam(UUID.fromString("5de6e184-af8d-498a-bbde-055e50653316"));
        System.out.println("Look up of team took " + (System.nanoTime() - nano_time) + " nano-seconds. " + t.getTeamID());

        iterateThrough();

    }

    static void iterateThrough(){
        long nano_time = System.nanoTime();
        int teamsAlive = 0;
        for(var t : teamMap.values()){
            for(UUID uuid : t.getMembers()){
                if(uuid == null)continue;
                teamsAlive++;
            }

        }

        System.out.println("Iteration took " + (System.nanoTime() - nano_time) + " nano-seconds. " + teamsAlive + " counted");
    }

    static void putTeam(Team t) {
        teamMap.put(t.getTeamID(), t);
        Arrays.stream(t.getMembers()).forEach(member -> {
            playerLookupTeamMap.put(member.getMostSignificantBits(), t.getTeamID());
        });
    }
    static Team getPlayerTeam(UUID uuid){
        var teamUUID = playerLookupTeamMap.get(uuid.getMostSignificantBits());
        return teamMap.get(teamUUID);
    }

}