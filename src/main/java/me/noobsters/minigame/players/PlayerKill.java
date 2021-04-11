package me.noobsters.minigame.players;

import java.util.UUID;

public class PlayerKill {
    //Maybe save the player's inventory here?
    long timeOfDeath;
    UUID victimUUID;

    public PlayerKill(UUID victimUUID, long timeOfDeath){
        this.victimUUID = victimUUID;
        this.timeOfDeath = timeOfDeath;
    }

    public PlayerKill(UUID victimUUID){
        this(victimUUID, System.currentTimeMillis());
    }

    public long getTimeOfDeath() {
        return timeOfDeath;
    }

    public UUID getVictimUUID() {
        return victimUUID;
    }
    
}