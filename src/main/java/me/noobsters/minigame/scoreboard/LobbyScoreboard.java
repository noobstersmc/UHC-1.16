package me.noobsters.minigame.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.noobsters.minigame.game.Game;
import net.md_5.bungee.api.ChatColor;

public class LobbyScoreboard extends IScoreboard {

    public LobbyScoreboard(Player player) {
        super(player);
        this.updateTitle(Game.getScoreboardTitle());
        this.updateLines(Game.getScoreColors() + "Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size(),
                ChatColor.WHITE + "noobsters.net");
    }

    @Override
    public void update(String... schema) {
        this.updateLines(Game.getScoreColors() + "Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size(),
                ChatColor.WHITE + "noobsters.net");
    }

}