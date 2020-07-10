package me.infinityz.minigame.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

public class LobbyScoreboard extends IScoreboard {

    public LobbyScoreboard(Player player) {
        super(player);
        this.updateTitle("UHC");
        this.updateLines("Players: " + Bukkit.getOnlinePlayers().size());
    }

    @Override
    public void update() {
        this.updateTitle(ChatColor.of(getRandomColor()) + "U" + ChatColor.of(getRandomColor()) + "H"+ ChatColor.of(getRandomColor()) + "C " + ChatColor.of(getRandomColor()) + "P"+ ChatColor.of(getRandomColor()) + "R" + ChatColor.of(getRandomColor()) + "O");

        this.updateLines(" Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());
    }

    String getRandomColor() {
        Random random = new Random();

        // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
        int nextInt = random.nextInt(0xffffff + 1);
        String colorCode = String.format("#%06x", nextInt);

        return colorCode;
    }


}