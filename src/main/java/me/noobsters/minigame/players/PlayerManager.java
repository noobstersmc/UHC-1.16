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
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.Kern;

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

    public void updateStats(List<UHCPlayer> winners){
        uhcPlayerMap.values().parallelStream().forEach(player->{
            var statsManager = Kern.getInstance().getStatsManager();
            var uuid = player.getUUID().toString();

            if(winners.contains(player)){
                statsManager.incUHCStats(uuid, "win_streak_count", 1);
                statsManager.incUHCStats(uuid, "wins", 1);
            }else{
                statsManager.setUHCStats(uuid, "win_streak_count", 0);
            }

            var playerStats = statsManager.incUHCStats(uuid, "deaths", 1);
            var stats = playerStats.obtainUHCStats();

            if(stats.getWinStreakCount() > stats.getWinStreak()) statsManager.setUHCStats(uuid, "win_streak", stats.getWinStreakCount());
            
            statsManager.incUHCStats(uuid, "kills", player.getKills());

            if(stats.getKillRecord() > player.getKills()) statsManager.setUHCStats(uuid, "kill_record", player.getKills());

            var time = player.getStopToPlay() == 0L ? System.currentTimeMillis()-player.getStartToPlay() : player.getStopToPlay()-player.getStartToPlay();
            statsManager.incUHCStats(uuid, "time_played", time);

            statsManager.incUHCStats(uuid, "hostile_mobs", player.getHostileMobs());

            statsManager.incUHCStats(uuid, "peaceful_mobs", player.getPeacefulMobs());

            statsManager.incUHCStats(uuid, "projectile_shoot", player.getProjectileShoots());

            statsManager.incUHCStats(uuid, "projectile_hit", player.getProjectileHits());

            statsManager.incUHCStats(uuid, "notch_apple", player.getNotchApples());

            statsManager.incUHCStats(uuid, "golden_head", player.getGoldenHeads());

            statsManager.incUHCStats(uuid, "golden_apple", player.getGoldenApples());

            statsManager.incUHCStats(uuid, "diamond", player.getMinedDiamonds());

            statsManager.incUHCStats(uuid, "gold", player.getMinedGold());

            statsManager.incUHCStats(uuid, "netherite", player.getMinedAncientDebris());
        });

        Bukkit.broadcastMessage(ChatColor.of("#c76905") + "UHC statistics updated.");
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