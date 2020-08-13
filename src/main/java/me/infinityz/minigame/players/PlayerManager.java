package me.infinityz.minigame.players;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.infinityz.minigame.UHC;

public class PlayerManager {
    UHC instance;
    Map<String, UHCPlayer> uhcPlayerMap;

    public PlayerManager(UHC instance) {
        this.instance = instance;
        this.uhcPlayerMap = new HashMap<>();
    }

    public Map<String, UHCPlayer> getUhcPlayerMap() {
        return uhcPlayerMap;
    }

    public UHCPlayer addCreateUHCPlayer(UUID uuid, boolean alive) {
        UHCPlayer uhcPlayer = new UHCPlayer(uuid, 0, alive);

        uhcPlayerMap.putIfAbsent(uuid.toString(), uhcPlayer);

        return uhcPlayerMap.get(uuid.toString());
    }

    public UHCPlayer getPlayer(UUID uuid) {
        return uhcPlayerMap.get(uuid.toString());
    }

    public int getAlivePlayers() {
        return (int) uhcPlayerMap.entrySet().stream().filter(c -> c.getValue().isAlive() == true).count();
    }

}