package me.infinityz.minigame;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import co.aikar.commands.PaperCommandManager;
import me.infinityz.minigame.commands.GlobalMute;
import me.infinityz.minigame.commands.HelpopCommand;
import me.infinityz.minigame.commands.LatescatterCMD;
import me.infinityz.minigame.commands.LocationsCommand;
import me.infinityz.minigame.commands.PVP;
import me.infinityz.minigame.commands.StartCommand;
import me.infinityz.minigame.commands.UHCCommand;
import me.infinityz.minigame.crafting.CraftingManager;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.listeners.ListenerManager;
import me.infinityz.minigame.locations.LocationManager;
import me.infinityz.minigame.players.PlayerManager;
import me.infinityz.minigame.scoreboard.ScoreboardManager;
import me.infinityz.minigame.teams.TeamManager;
import me.infinityz.minigame.teams.commands.TeamCMD;
import net.md_5.bungee.api.ChatColor;

public class UHC extends JavaPlugin {
    public Stage gameStage;
    ScoreboardManager scoreboardManager;
    LocationManager locationManager;
    PaperCommandManager commandManager;
    PlayerManager playerManager;
    ListenerManager listenerManager;
    CraftingManager craftingManager;
    TeamManager teamManger;
    public boolean pvp = false;
    public boolean globalmute = false;

    @Override
    public void onEnable() {
        gameStage = Stage.LOADING;// TODO: Move this code out of here.

        // TODO: Move the command Manager to a command manager.
        commandManager = new PaperCommandManager(this);

        commandManager.registerCommand(new LocationsCommand(this));
        commandManager.registerCommand(new StartCommand(this));
        commandManager.registerCommand(new PVP(this));
        commandManager.registerCommand(new HelpopCommand(this));
        commandManager.registerCommand(new UHCCommand(this));
        commandManager.registerCommand(new LatescatterCMD(this));
        commandManager.registerCommand(new GlobalMute(this));
        commandManager.registerCommand(new TeamCMD(this));

        teamManger = new TeamManager(this);

        scoreboardManager = new ScoreboardManager(this);
        locationManager = new LocationManager(this);
        playerManager = new PlayerManager(this);

        craftingManager = new CraftingManager(this);

        listenerManager = new ListenerManager(this);
        runStartUp();
        Bukkit.getServer().getWhitelistedPlayers().clear();

    }

    public TeamManager getTeamManger() {
        return teamManger;
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

    public CraftingManager getCraftingManager() {
        return craftingManager;
    }

    void runStartUp() {
        try {

            Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Objective obj = mainScoreboard.registerNewObjective("health_name", "health", ChatColor.DARK_RED + "❤");
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
            it.setSpawnLocation(0, it.getHighestBlockAt(0, 0).getZ() + 10, 0);
            it.getWorldBorder().setCenter(0, 0);
            it.getWorldBorder().setSize(101);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunky center 0 0");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chunky radius 2000");
        });
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist on");

    }

}
