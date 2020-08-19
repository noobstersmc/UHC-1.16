package me.infinityz.minigame.game;

import java.util.UUID;

import org.bukkit.boss.BossBar;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

@Data
public class Game {
    private BossBar bossbar;
    private long startTime;
    private int gameTime = 0;
    private boolean pvp = false;
    private boolean globalMute = false;
    private boolean nether = true;
    private boolean end = true;
    private UUID gameID = UUID.randomUUID();
    private static @Getter @Setter String scoreboardTitle = ChatColor.BOLD + "UHC";
    private static @Getter @Setter String tablistHeader = ChatColor.DARK_RED + "Noobsters UHC";

}