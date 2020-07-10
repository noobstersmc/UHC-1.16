package me.infinityz.minigame;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import me.infinityz.minigame.commands.LocationsCommand;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.listeners.GlobalListener;
import me.infinityz.minigame.locations.LocationManager;
import me.infinityz.minigame.scoreboard.ScoreboardManager;
import me.infinityz.minigame.tasks.ScatterTask;

public class UHC extends JavaPlugin {
    public Stage gameStage;
    ScoreboardManager scoreboardManager;
    LocationManager locationManager;
    PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        gameStage = Stage.LOADING;

        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new LocationsCommand(this));

        scoreboardManager = new ScoreboardManager(this);
        locationManager = new LocationManager(this);
        //TODO: Create a ListenersManager to handle all the events in a better way
        Bukkit.getPluginManager().registerEvents(new GlobalListener(this), this);

        //Add a boolean to auto find locs on start.
        new ScatterTask(Bukkit.getWorlds().get(0), 2000, 100, 100).runTaskTimerAsynchronously(this, 20*5, 10);
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

}