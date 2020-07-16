package me.infinityz.minigame;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import co.aikar.commands.PaperCommandManager;
import me.infinityz.minigame.commands.HelpopCommand;
import me.infinityz.minigame.commands.LocationsCommand;
import me.infinityz.minigame.commands.PVP;
import me.infinityz.minigame.commands.StartCommand;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.listeners.ListenerManager;
import me.infinityz.minigame.locations.LocationManager;
import me.infinityz.minigame.players.PlayerManager;
import me.infinityz.minigame.scoreboard.ScoreboardManager;
import me.infinityz.minigame.tasks.ScatterTask;
import net.md_5.bungee.api.ChatColor;

public class UHC extends JavaPlugin {
    public Stage gameStage;
    ScoreboardManager scoreboardManager;
    LocationManager locationManager;
    PaperCommandManager commandManager;
    PlayerManager playerManager;
    ListenerManager listenerManager;
    public boolean pvp = false;
    @Override
    public void onEnable() {
        gameStage = Stage.LOADING;

        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new LocationsCommand(this));
        commandManager.registerCommand(new StartCommand(this));
        commandManager.registerCommand(new PVP(this));
        commandManager.registerCommand(new HelpopCommand(this));

        scoreboardManager = new ScoreboardManager(this);
        locationManager = new LocationManager(this);
        playerManager = new PlayerManager(this);

        listenerManager = new ListenerManager(this);
        runStartUp();

        // Add a boolean to auto find locs on start.
        new ScatterTask(Bukkit.getWorlds().get(0), 2000, 100, 100).runTaskTimerAsynchronously(this, 20 * 5, 10);
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

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    void runStartUp() {
        try {

            Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Objective obj = mainScoreboard.registerNewObjective("health_name", "health", ChatColor.RED + "❤");
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);

            Objective obj2 = mainScoreboard.registerNewObjective("health_list", "health", "", RenderType.INTEGER);
            obj2.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        } catch (Exception e) {
            // TODO: handle exception
        }

        Bukkit.getWorlds().forEach(it -> {
            it.setGameRule(GameRule.NATURAL_REGENERATION, false);
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            it.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            it.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            it.setDifficulty(Difficulty.PEACEFUL);
            it.setSpawnLocation(0, it.getHighestBlockAt(0, 0).getZ() + 10, 0);
            it.getWorldBorder().setCenter(0, 0);
            it.getWorldBorder().setSize(101);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb " + it.getName() + " set 2020 2020 0 0");
        });

    }

}