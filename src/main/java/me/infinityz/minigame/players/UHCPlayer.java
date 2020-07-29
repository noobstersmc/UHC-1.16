package me.infinityz.minigame.players;

import java.util.UUID;

public class UHCPlayer {
    int kills;
    boolean alive;
    public boolean hasDied;
    UUID uuid;

    public UHCPlayer(UUID uuid, int kills, boolean alive) {
        this.uuid = uuid;
        this.kills = kills;
        this.alive = alive;
        this.hasDied = false;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean bol) {
        this.alive = bol;
    }



}