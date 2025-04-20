package me.noobsters.minigame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import com.google.gson.Gson;

import me.noobsters.minigame.gui.RapidManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.HandlerList;
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
import me.noobsters.minigame.chat.ChatManager;
import me.noobsters.minigame.chunks.ChunksManager;
import me.noobsters.minigame.commands.ConfigCommand;
import me.noobsters.minigame.commands.ContextConditions;
import me.noobsters.minigame.commands.GameLoopCMD;
import me.noobsters.minigame.commands.GameRestoreCMD;
import me.noobsters.minigame.commands.HelpopCommand;
import me.noobsters.minigame.commands.LatescatterCMD;
import me.noobsters.minigame.commands.PVP;
import me.noobsters.minigame.commands.StartCommand;
import me.noobsters.minigame.commands.ToolCMD;
import me.noobsters.minigame.commands.UHCCommand;
import me.noobsters.minigame.commands.Whitelist;
import me.noobsters.minigame.commands.WorldCMD;
import me.noobsters.minigame.crafting.CraftingManager;
import me.noobsters.minigame.enums.Stage;
import me.noobsters.minigame.game.Game;
import me.noobsters.minigame.gamemodes.GamemodeManager;
import me.noobsters.minigame.gamemodes.GamemodesCMD;
import me.noobsters.minigame.gui.GuiManager;
import me.noobsters.minigame.listeners.ListenerManager;
import me.noobsters.minigame.players.PlayerManager;
import me.noobsters.minigame.portals.PortalListeners;
import me.noobsters.minigame.scoreboard.ScoreboardManager;
import me.noobsters.minigame.teams.TeamManager;
import net.md_5.bungee.api.ChatColor;

public class UHC extends JavaPlugin {

    private @Getter Gson gson = new Gson();
    private @Getter @Setter Stage gameStage;
    private @Getter ScoreboardManager scoreboardManager;
    private @Getter PaperCommandManager commandManager;
    private @Getter PlayerManager playerManager;
    private @Getter ListenerManager listenerManager;
    private @Getter CraftingManager craftingManager;
    private @Getter TeamManager teamManger;
    private @Getter ChunksManager chunkManager;
    private @Getter GamemodeManager gamemodeManager;
    private @Getter ChatManager chatManager;
    private @Getter GuiManager guiManager;
    private @Getter @Setter Game game;
    private @Getter PortalListeners portalListeners;
    /* Statics */
    private static @Getter UHC instance;
    private static @Setter TaskChainFactory taskChainFactory;

    /* Condor Pre Boot-up code starts */
    private @Getter static String SEED = System.currentTimeMillis() + "";



    /**
     * Returns an UUID condor_id from properties file if present, otherwise it
     * returns null.
     * 
     * @return condor-id as String from server.properties or null
     */
    private String getCondorID() {
        var properties = new Properties();
        var propertiesFile = new File("server.properties");

        try (var is = new FileInputStream(propertiesFile)) {
            properties.load(is);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        var condor_id = properties.getProperty("condor-id");

        return condor_id != null ? condor_id : "";
    }

    /* Condor Pre Boot-up code ends */

    @Override
    public void onEnable() {
        /* Obtain kern and store it for easy use */
        RapidManager.register(this);

        try {
            Long.valueOf(SEED);
        } catch (Exception e) {
            var chars = SEED.chars().toArray();
            SEED = "";
            for (var c : chars)
                SEED += ("" + c);
            SEED = SEED.substring(0, 16);
        }

        new WorldCreator("world").seed(Long.valueOf(SEED)).environment(Environment.NORMAL).createWorld();

        /**
         * Initialize taskChain, fastInv, and set the game stage to loading
         */
        setTaskChainFactory(BukkitTaskChainFactory.create(this));

        try {
            FastInvManager.register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        gameStage = Stage.LOADING;
        instance = this;

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
        commandManager.registerCommand(new GameRestoreCMD(this));
        commandManager.registerCommand(new ConfigCommand(this));
        commandManager.registerCommand(new GamemodesCMD(this));
        commandManager.registerCommand(new WorldCMD(this));
        commandManager.registerCommand(new GameLoopCMD(this));
        commandManager.registerCommand(new ToolCMD(this));
        commandManager.registerCommand(new Whitelist(this));

        /* Initiliaze the game data */
        game = new Game();

        /*
         * Initilialize all the managers
         */
        Game.setBossbar(Bukkit.createBossBar(new NamespacedKey(this, "hynix"), "Time", BarColor.RED, BarStyle.SOLID));
        teamManger = new TeamManager(this);
        scoreboardManager = new ScoreboardManager(this);
        playerManager = new PlayerManager(this);
        craftingManager = new CraftingManager(this);
        listenerManager = new ListenerManager(this);
        chunkManager = new ChunksManager(this);
        gamemodeManager = new GamemodeManager(this);
        chatManager = new ChatManager(this);
        guiManager = new GuiManager(this);

        portalListeners = new PortalListeners(this);
        /* Run some startup code */
        runStartUp();

        /* In case the server is already running and it is a reload */
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldload");

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
        FastInvManager.closeAll();
        HandlerList.unregisterAll(this);
        // gamemodeManager.getEnabledGamemodes().forEach(IGamemode::disableScenario);
        // commandManager.unregisterCommands();
        craftingManager.purgeRecipes();
    }

    public void restartSystem() {
        Bukkit.getOnlinePlayers().forEach((p) -> p.kickPlayer("Restarting server"));
        /*
         * onDisable(); onEnable();
         */
        Bukkit.getWorlds().forEach(worlds -> {
            if (!worlds.getName().equalsIgnoreCase("lobby")) {
                Bukkit.unloadWorld(worlds, false);
                try {
                    deleteDirectory(worlds.getWorldFolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        var world = Bukkit.getWorlds().get(0).getWorldFolder();

        try {
            var advancement = new File(world, "advancements");
            if (advancement.exists())
                deleteDirectory(advancement);

            var playerdata = new File(world, "playerdata");
            if (playerdata.exists())
                deleteDirectory(playerdata);

            var stats = new File(world, "stats");
            if (stats.exists())
                deleteDirectory(stats);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
            it.setGameRule(GameRule.DO_FIRE_TICK, false);
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            it.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, game.isAdvancements());
            it.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            it.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            it.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            it.setSpawnLocation(0, it.getHighestBlockAt(0, 0).getZ() + 10, 0);
            it.getWorldBorder().setCenter(0, 0);
            it.getWorldBorder().setSize(game.getBorderSize());
            it.getWorldBorder().setDamageBuffer(0.0);
            it.getWorldBorder().setDamageAmount(0.0);
        });

    }

    private void enableScenario(String scenarioName) {
        var scenario = gamemodeManager.getGamemodesList().stream()
                .filter(scen -> scen.getName().equalsIgnoreCase(scenarioName.toLowerCase())).findFirst();
        if (scenario.isPresent()) {
            scenario.get().callEnable();
            System.out.println("[UHC] Enabled " + scenario.get().getName());
        } else {
            System.err.println("[UHC] Couldn't find scenarios named " + scenarioName);
        }
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
