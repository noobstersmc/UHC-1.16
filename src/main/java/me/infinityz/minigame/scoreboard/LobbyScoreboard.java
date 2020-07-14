package me.infinityz.minigame.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class LobbyScoreboard extends IScoreboard {

    public LobbyScoreboard(Player player) {
        super(player);
        this.updateTitle(ChatColor.BOLD + "UHC");
        this.updateLines(ChatColor.GRAY + "Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size(),
                ChatColor.WHITE + "noobsters.net");
    }

    @Override
    public void update() {
        this.updateLines(ChatColor.GRAY + "Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size(),
                ChatColor.WHITE + "noobsters.net");
    }

}