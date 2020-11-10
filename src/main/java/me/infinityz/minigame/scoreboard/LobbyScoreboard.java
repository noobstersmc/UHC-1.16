package me.infinityz.minigame.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.infinityz.minigame.game.Game;
import net.md_5.bungee.api.ChatColor;

public class LobbyScoreboard extends IScoreboard {
    private String color = Game.getScoreColors();

    public LobbyScoreboard(Player player) {
        super(player);
        this.updateTitle(Game.getScoreboardTitle());
        this.updateLines(color + "Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size(),
                ChatColor.WHITE + "noobsters.net");
    }

    @Override
    public void update(String... schema) {
        this.updateLines(color + "Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size(),
                ChatColor.WHITE + "noobsters.net");
    }

}