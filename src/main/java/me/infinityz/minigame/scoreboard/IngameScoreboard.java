package me.infinityz.minigame.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.players.UHCPlayer;
import net.md_5.bungee.api.ChatColor;

public class IngameScoreboard extends IScoreboard {
    private UHC instance;
    private UHCPlayer uhcPlayer;
    private WorldBorder worldBorder = Bukkit.getWorlds().get(0).getWorldBorder();
    private String color = Game.getScoreColors();

    public IngameScoreboard(Player player, UHC instance) {
        super(player);
        this.instance = instance;
        this.updateTitle(Game.getScoreboardTitle());
        this.uhcPlayer = instance.getPlayerManager().getPlayer(player.getUniqueId());
        if (instance.getTeamManger().getTeamSize() > 1)
            buildScoreboardTeam(player);
        else
            buildScoreboardSolo(player);
    }

    public void buildScoreboardTeam(Player player) {
        if (uhcPlayer == null) {
            uhcPlayer = instance.getPlayerManager().getPlayer(player.getUniqueId());
        }
        var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
        this.updateLines(
                color + "Time: " + ChatColor.WHITE + timeConvert(instance.getGame().getGameTime()), 
                "",
                color + "Kills: " + ChatColor.WHITE + (uhcPlayer != null ? uhcPlayer.getKills() : 0),
                color + "Team Kills: " + ChatColor.WHITE + (team != null ? team.getTeamKills() : 0), 
                "",
                color + "Players: " + ChatColor.WHITE + instance.getPlayerManager().getAlivePlayers(),
                color + "Border: " + ChatColor.WHITE + ((int) worldBorder.getSize() / 2), 
                "",
                color + "noobsters.net");
    }

    public void buildScoreboardSolo(Player player) {
        if (uhcPlayer == null) {
            uhcPlayer = instance.getPlayerManager().getPlayer(player.getUniqueId());
        }
        this.updateLines(
                color + "Time: " + ChatColor.WHITE + timeConvert(instance.getGame().getGameTime()), 
                "",
                color + "Kills: " + ChatColor.WHITE + (uhcPlayer != null ? uhcPlayer.getKills() : 0),
                "",
                color + "Players: " + ChatColor.WHITE + instance.getPlayerManager().getAlivePlayers(),
                color + "Border: " + ChatColor.WHITE + ((int) worldBorder.getSize() / 2), 
                "",
                color + "noobsters.net");
    }

    @Override
    public void update(String... schema) {
        if (schema != null && schema.length > 0) {
            updateLines(schema);
        } else {
            if (instance.getTeamManger().getTeamSize() > 1)
                buildScoreboardTeam(this.getPlayer());
            else
                buildScoreboardSolo(this.getPlayer());

        }
    }

    private String timeConvert(int t) {
        int hours = t / 3600;

        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }
}