package me.infinityz.minigame.players;

import java.util.UUID;

import com.google.gson.GsonBuilder;

import lombok.Getter;
import lombok.Setter;

public class UHCPlayer {
    private @Getter UUID UUID;
    private @Getter @Setter int kills;
    private @Getter @Setter boolean alive;
    private @Getter @Setter boolean dead;

    public UHCPlayer(UUID uuid, int kills, boolean alive) {
        this.UUID = uuid;
        this.kills = kills;
        this.alive = alive;
        this.dead = false;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

}