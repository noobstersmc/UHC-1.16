package me.infinityz.minigame;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import co.aikar.commands.PaperCommandManager;
import fr.mrmicky.fastinv.FastInvManager;
import lombok.Getter;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.commands.ContextConditions;
import me.infinityz.minigame.commands.GlobalMute;
import me.infinityz.minigame.commands.HelpopCommand;
import me.infinityz.minigame.commands.LatescatterCMD;
import me.infinityz.minigame.commands.LocationsCommand;
import me.infinityz.minigame.commands.PVP;
import me.infinityz.minigame.commands.StartCommand;
import me.infinityz.minigame.commands.UHCCommand;
import me.infinityz.minigame.commands.Utilities;
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
    private @Getter ScoreboardManager scoreboardManager;
    private @Getter LocationManager locationManager;
    private @Getter PaperCommandManager commandManager;
    private @Getter PlayerManager playerManager;
    private @Getter ListenerManager listenerManager;
    private @Getter CraftingManager craftingManager;
    private @Getter TeamManager teamManger;
    private @Getter ChunksManager chunkManager;
    public boolean pvp = false;
    public boolean globalmute = false;

    @Override
    public void onEnable() {
        FastInvManager.register(this);
        gameStage = Stage.LOADING;// TODO: Move this code out of here.

        // TODO: Move the command Manager to a command manager.
        commandManager = new PaperCommandManager(this);

        // Register all command conditions, completion, annotations, and context
        new ContextConditions(this);

        commandManager.registerCommand(new LocationsCommand(this));
        commandManager.registerCommand(new StartCommand(this));
        commandManager.registerCommand(new PVP(this));
        commandManager.registerCommand(new HelpopCommand(this));
        commandManager.registerCommand(new UHCCommand(this));
        commandManager.registerCommand(new LatescatterCMD(this));
        commandManager.registerCommand(new GlobalMute(this));
        commandManager.registerCommand(new TeamCMD(this));
        commandManager.registerCommand(new Utilities(this));

        teamManger = new TeamManager(this);

        scoreboardManager = new ScoreboardManager(this);
        locationManager = new LocationManager(this);
        playerManager = new PlayerManager(this);

        craftingManager = new CraftingManager(this);

        listenerManager = new ListenerManager(this);
        chunkManager = new ChunksManager(this);
        runStartUp();
    }

    void runStartUp() {
        try {
            Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            mainScoreboard.getObjectives().forEach(obj -> obj.unregister());
            Objective obj = mainScoreboard.registerNewObjective("health_name", "health", ChatColor.DARK_RED + "❤");
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
            Objective obj2 = mainScoreboard.registerNewObjective("health_list", "health", " ", RenderType.INTEGER);
            obj2.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist on");

        } catch (Exception e) {
            e.printStackTrace();
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
        });
    }

}
