package me.infinityz.minigame.players;

import java.util.UUID;

import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.infinityz.minigame.UHC;

@RequiredArgsConstructor
public class PlayerManager {
    private @NonNull UHC instance;
    private @Getter @Setter THashMap<Long, UHCPlayer> uhcPlayerMap = new THashMap<>();

    public UHCPlayer addCreateUHCPlayer(UUID uuid, boolean alive) {
        var uhcPlayer = new UHCPlayer(uuid);
        uhcPlayer.setAlive(alive);

        uhcPlayerMap.putIfAbsent(uuid.getMostSignificantBits(), uhcPlayer);

        return uhcPlayerMap.get(uuid.getMostSignificantBits());
    }

    public UHCPlayer getPlayer(UUID uuid) {
        return uhcPlayerMap.get(uuid.getMostSignificantBits());
    }

    public int getAlivePlayers() {
        return (int) uhcPlayerMap.values().stream().filter(UHCPlayer::isAlive).count();
    }

}