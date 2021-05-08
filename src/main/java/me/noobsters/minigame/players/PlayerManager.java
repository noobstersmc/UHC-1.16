package me.noobsters.minigame.players;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.noobsters.minigame.UHC;

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

    public void updateStats(){
        Bukkit.broadcastMessage("START");
        uhcPlayerMap.values().parallelStream().forEach(player->{
            var kern = instance.getKern().getInstance();
        });
    }

    public UHCPlayer getPlayer(UUID uuid) {
        return uhcPlayerMap.get(uuid.getMostSignificantBits());
    }

    public int getAlivePlayers() {
        return (int) uhcPlayerMap.values().stream().filter(UHCPlayer::isAlive).count();
    }

    public List<UHCPlayer> getAlivePlayersListNonLambda() {
        final ArrayList<UHCPlayer> listOfPlayers = new ArrayList<>();

        for (var player : uhcPlayerMap.values())
            if (player.isAlive())
                listOfPlayers.add(player);

        return listOfPlayers;
    }

    public List<UHCPlayer> getAliveSoloPlayersListNonLambda() {
        final ArrayList<UHCPlayer> listOfPlayers = new ArrayList<>();
        final var teamManager = instance.getTeamManger();

        for (var player : uhcPlayerMap.values())
            if (player.isAlive() && !teamManager.hasTeam(player))
                listOfPlayers.add(player);

        return listOfPlayers;
    }

}