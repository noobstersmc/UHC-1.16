package me.infinityz.minigame.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class LobbyScoreboard extends IScoreboard {

    public LobbyScoreboard(Player player) {
        super(player);
        this.updateTitle("UHC");
        this.updateLines("Players: " + Bukkit.getOnlinePlayers().size());
    }

    @Override
    public void update() {
        this.updateLines(" Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());
    }


}