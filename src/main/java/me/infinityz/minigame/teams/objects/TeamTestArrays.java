package me.infinityz.minigame.teams.objects;

import java.util.ArrayList;
import java.util.UUID;

public class TeamTestArrays {
    static ArrayList<Team> teamAr = new ArrayList<>();


    public static void main(String[] args) {

        for (int i = 0; i < 1000; i++)
        teamAr.add(new Team(UUID.randomUUID()));

        
        teamAr.add(new Team(UUID.fromString("5de6e184-af8d-498a-bbde-055e50653316")));
        long time = System.nanoTime();
        Team t = getPlayerTeam(UUID.fromString("5de6e184-af8d-498a-bbde-055e50653316"));
        System.out.println("Took " + (System.nanoTime() - time) + " nano seconds. " + t);


    }

    public static Team getPlayerTeam(UUID uuid) {
        return teamAr.stream().filter(team -> team.isMember(uuid)).findFirst().orElse(null);
    }

}