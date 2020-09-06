package me.infinityz.minigame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitWorker;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import co.aikar.commands.PaperCommandManager;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import fr.mrmicky.fastinv.FastInvManager;
import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.commands.ConfigCommand;
import me.infinityz.minigame.commands.ContextConditions;
import me.infinityz.minigame.commands.GameRestoreCMD;
import me.infinityz.minigame.commands.GlobalMute;
import me.infinityz.minigame.commands.HelpopCommand;
import me.infinityz.minigame.commands.LatescatterCMD;
import me.infinityz.minigame.commands.PVP;
import me.infinityz.minigame.commands.StartCommand;
import me.infinityz.minigame.commands.UHCCommand;
import me.infinityz.minigame.commands.Utilities;
import me.infinityz.minigame.crafting.CraftingManager;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.gamemodes.GamemodeManager;
import me.infinityz.minigame.gamemodes.GamemodesCMD;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.listeners.ListenerManager;
import me.infinityz.minigame.players.PlayerManager;
import me.infinityz.minigame.scoreboard.ScoreboardManager;
import me.infinityz.minigame.teams.TeamManager;
import me.infinityz.minigame.teams.commands.TeamCMD;
import net.md_5.bungee.api.ChatColor;

public class UHC extends JavaPlugin {

    private @Getter @Setter Stage gameStage;
    private @Getter ScoreboardManager scoreboardManager;
    private @Getter PaperCommandManager commandManager;
    private @Getter PlayerManager playerManager;
    private @Getter ListenerManager listenerManager;
    private @Getter CraftingManager craftingManager;
    private @Getter TeamManager teamManger;
    private @Getter ChunksManager chunkManager;
    private @Getter GamemodeManager gamemodeManager;
    private @Getter @Setter Game game;
    private static @Setter TaskChainFactory taskChainFactory;

    @Override
    public void onEnable() {
        /**
         * Initialize taskChain, fastInv, and set the game stage to loading
         */
        setTaskChainFactory(BukkitTaskChainFactory.create(this));
        FastInvManager.register(this);
        gameStage = Stage.LOADING;

        /*
         * Register commands and contexts for AFC
         */
        commandManager = new PaperCommandManager(this);
        new ContextConditions(this);
        commandManager.registerCommand(new StartCommand(this));
        commandManager.registerCommand(new PVP(this));
        commandManager.registerCommand(new HelpopCommand(this));
        commandManager.registerCommand(new UHCCommand(this));
        commandManager.registerCommand(new LatescatterCMD(this));
        commandManager.registerCommand(new GlobalMute(this));
        commandManager.registerCommand(new TeamCMD(this));
        commandManager.registerCommand(new Utilities(this));
        commandManager.registerCommand(new GameRestoreCMD(this));
        commandManager.registerCommand(new ConfigCommand(this));
        commandManager.registerCommand(new GamemodesCMD(this));

        /*
         * Initilialize all the managers
         */
        teamManger = new TeamManager(this);
        scoreboardManager = new ScoreboardManager(this);
        playerManager = new PlayerManager(this);
        craftingManager = new CraftingManager(this);
        listenerManager = new ListenerManager(this);
        chunkManager = new ChunksManager(this);
        gamemodeManager = new GamemodeManager(this);
        game = new Game();
        Game.setBossbar(Bukkit.createBossBar(new NamespacedKey(this, "henix"), "Time", BarColor.RED, BarStyle.SOLID));

        /* Run some startup code */
        runStartUp();

        /* In case the server is already running and it is a reload */
        Bukkit.getOnlinePlayers().forEach(all -> Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(all, "")));

        /* Lobby stage has been reached */
        gameStage = Stage.LOBBY;
    }

    @Override
    public void onDisable() {
        /* Clean up in case it was a reload */
        Game.getBossbar().removeAll();
        scoreboardManager.purgeScoreboards();
        Bukkit.getWorlds()
                .forEach(world -> world.getForceLoadedChunks().forEach(chunks -> chunks.setForceLoaded(false)));
        getServer().getScheduler().getActiveWorkers().stream().filter(w -> w.getOwner() == this)
                .map(BukkitWorker::getThread).forEach(Thread::interrupt);
        getServer().getScheduler().cancelTasks(this);
        gamemodeManager.getEnabledGamemodes().forEach(IGamemode::disableScenario);
        commandManager.unregisterCommands();
        craftingManager.purgeRecipes();
    }

    void runStartUp() {
        try {
            Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            mainScoreboard.getObjectives().forEach(Objective::unregister);
            Objective obj = mainScoreboard.registerNewObjective("health_name", "health", ChatColor.DARK_RED + "❤");
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
            Objective obj2 = mainScoreboard.registerNewObjective("health_list", "health", " ", RenderType.INTEGER);
            obj2.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getWorlds().forEach(it -> {
            it.setGameRule(GameRule.NATURAL_REGENERATION, false);
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            it.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            it.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            it.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            it.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            it.setSpawnLocation(0, it.getHighestBlockAt(0, 0).getZ() + 10, 0);
            it.getWorldBorder().setCenter(0, 0);
            it.getWorldBorder().setSize(101);
            it.getWorldBorder().setDamageBuffer(0.0);
            it.getWorldBorder().setDamageAmount(0.0);
        });
    }

    void deleteDirectory(File directoryToBeDeleted) throws IOException {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }

        Files.delete(directoryToBeDeleted.toPath());
    }

    /* Task Chain factories */
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

}
