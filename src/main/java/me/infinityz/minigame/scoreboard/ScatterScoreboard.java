package me.infinityz.minigame.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScatterScoreboard extends IScoreboard {

    public ScatterScoreboard(Player player) {
        super(player);
        this.updateTitle("UHC");
        this.updateLines("Players: " + Bukkit.getOnlinePlayers().size());
    }

    @Override
    public void update() {
        this.updateLines("Players: " + Bukkit.getOnlinePlayers().size());
    }

}