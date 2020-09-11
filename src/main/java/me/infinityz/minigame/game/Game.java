package me.infinityz.minigame.game;

import java.util.UUID;

import com.google.gson.GsonBuilder;

import org.bukkit.boss.BossBar;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

@Data
public class Game {
    /* Static data */
    private static @Getter @Setter BossBar bossbar;
    private static @Getter @Setter String scoreboardTitle = ChatColor.BOLD + "UHC";
    private static @Getter @Setter String tablistHeader =  ChatColor.of("#A40A0A") + "" + ChatColor.BOLD + "\nNoobsters\n";
    /* Game data */
    private UUID gameID = UUID.randomUUID();
    private long startTime;
    private int gameTime = 0;
    private boolean pvp = false;
    private boolean globalMute = false;
    private boolean nether = true;
    private boolean end = true;
    /* Game config */
    private int borderTime = 3600;
    private int pvpTime = 1200;
    private int healTime = 120;
    private int borderCenterTime = 1800;
    private int borderSize = 4000;

    @Override
    public String toString() {

        var gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

}