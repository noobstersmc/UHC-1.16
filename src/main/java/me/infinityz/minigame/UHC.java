package me.infinityz.minigame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import me.infinityz.minigame.commands.LocationsCommand;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.listeners.LobbyListeners;
import me.infinityz.minigame.scoreboard.ScoreboardManager;
import me.infinityz.minigame.tasks.ScatterTask;

public class UHC extends JavaPlugin {
    public Stage gameStage;
    ScoreboardManager scoreboardManager;
    PaperCommandManager manager;
    public static List<String> colors = new ArrayList<String>();
    public static int color = 1;

    @Override
    public void onEnable() {
        gameStage = Stage.LOADING;
        for (int r = 0; r < 100; r++)
            colors.add(String.format("#%02x%02x%02x", r * 255 / 100, 255, 0));
        for (int g = 100; g > 0; g--)
            colors.add(String.format("#%02x%02x%02x", 255, g * 255 / 100, 0));
        for (int b = 0; b < 100; b++)
            colors.add(String.format("#%02x%02x%02x", 255, 0, b * 255 / 100));
        for (int r = 100; r > 0; r--)
            colors.add(String.format("#%02x%02x%02x", r * 255 / 100, 0, 255));
        for (int g = 0; g < 100; g++)
            colors.add(String.format("#%02x%02x%02x", 0, g * 255 / 100, 255));
        for (int b = 100; b > 0; b--)
            colors.add(String.format("#%02x%02x%02x", 0, 255, b * 255 / 100));
        colors.add(String.format("#%02x%02x%02x", 0, 255, 0));

        manager = new PaperCommandManager(this);
        manager.registerCommand(new LocationsCommand(this));
        scoreboardManager = new ScoreboardManager(this);
        Bukkit.getPluginManager().registerEvents(new LobbyListeners(this), this);

        //new ScatterTask(Bukkit.getWorlds().get(0), 2000, 100, 100).runTaskTimerAsynchronously(this, 20, 10);


    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public PaperCommandManager getManager() {
        return manager;
    }

}