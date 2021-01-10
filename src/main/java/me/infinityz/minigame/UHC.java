package me.infinityz.minigame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.HandlerList;
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
import me.infinityz.minigame.border.BorderManager;
import me.infinityz.minigame.chat.ChatManager;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.commands.ConfigCommand;
import me.infinityz.minigame.commands.ContextConditions;
import me.infinityz.minigame.commands.GameRestoreCMD;
import me.infinityz.minigame.commands.GlobalMute;
import me.infinityz.minigame.commands.HelpopCommand;
import me.infinityz.minigame.commands.InventoriesCMD;
import me.infinityz.minigame.commands.LatescatterCMD;
import me.infinityz.minigame.commands.PVP;
import me.infinityz.minigame.commands.StartCommand;
import me.infinityz.minigame.commands.UHCCommand;
import me.infinityz.minigame.commands.Utilities;
import me.infinityz.minigame.commands.WorldCMD;
import me.infinityz.minigame.condor.CondorAPI;
import me.infinityz.minigame.condor.CondorConfig;
import me.infinityz.minigame.condor.CondorManager;
import me.infinityz.minigame.condor.JsonConfig;
import me.infinityz.minigame.crafting.CraftingManager;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.gamemodes.GamemodeManager;
import me.infinityz.minigame.gamemodes.GamemodesCMD;
import me.infinityz.minigame.listeners.ListenerManager;
import me.infinityz.minigame.players.PlayerManager;
import me.infinityz.minigame.portals.PortalListeners;
import me.infinityz.minigame.scoreboard.ScoreboardManager;
import me.infinityz.minigame.teams.TeamManager;
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
    private @Getter @Setter Game game;
    private @Getter BorderManager borderManager;
    private @Getter CondorManager condorManager;
    private @Getter JsonObject condorConfig;
    private @Getter PortalListeners portalListeners;
    /* Statics */
    private static @Getter UHC instance;
    private static @Setter TaskChainFactory taskChainFactory;

    /* Condor Pre Boot-up code starts */
    private @Getter CondorConfig condorDataConfig;
    private static JsonConfig JSON_CONFIG;
    private static String CONDOR_ID = null;
    private @Getter static String SEED = "599751388478452208";

    static {
        try {
            JSON_CONFIG = new JsonConfig("condor.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoad() {
        /* Before anything else happens, try to obtain information from condor/lair */
        String condor_secret = null;
        CONDOR_ID = getCondorID();

        if (JSON_CONFIG != null) {
            var json = JSON_CONFIG.getJsonObject();
            var element = json.get("condor_id");
            if (element != null) {
                CONDOR_ID = element.getAsString();
            }
            var secret = json.get("condor_secret");
            if (secret != null) {
                condor_secret = secret.getAsString();
            }
        }
        System.out.println("[CONDOR] Condor id is: " + CONDOR_ID);
        if (CONDOR_ID != null) {
            condorConfig = CondorAPI.getGameJsonConfig(CONDOR_ID,
                    condor_secret != null ? condor_secret : "Condor-Secreto");

        }
        try {
            if (condorConfig != null) {
                condorDataConfig = CondorConfig.ofJson(condorConfig);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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
        /*Create the base world with the correct seed */
        try {
            if(condorDataConfig != null){
                var level_seed = condorDataConfig.getLevel_seed();
                if(level_seed.contains("random")){    
                    SEED = CondorAPI.getCondorRandomSeed();
                }else{
                    SEED =level_seed; 
                }
            }else{                
                SEED = CondorAPI.getCondorRandomSeed(); 
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[CONDOR] Seed will be " + SEED);

        condorManager = new CondorManager(this);
        /* TODO: Make condor send a ping as soon as the server goes online! */

        new WorldCreator("world").seed(Long.parseLong(SEED)).environment(Environment.NORMAL).createWorld();

        /**
         * Initialize taskChain, fastInv, and set the game stage to loading
         */

        setTaskChainFactory(BukkitTaskChainFactory.create(this));
        /* Soon to be deprecated */
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
        commandManager.registerCommand(new GlobalMute(this));
        commandManager.registerCommand(new Utilities(this));
        commandManager.registerCommand(new GameRestoreCMD(this));
        commandManager.registerCommand(new ConfigCommand(this));
        commandManager.registerCommand(new GamemodesCMD(this));
        commandManager.registerCommand(new WorldCMD(this));

        commandManager.registerCommand(new InventoriesCMD());

        /*
         * Initilialize all the managers
         */
        Game.setBossbar(Bukkit.createBossBar(new NamespacedKey(this, "henix"), "Time", BarColor.RED, BarStyle.SOLID));
        teamManger = new TeamManager(this);
        scoreboardManager = new ScoreboardManager(this);
        playerManager = new PlayerManager(this);
        craftingManager = new CraftingManager(this);
        listenerManager = new ListenerManager(this);
        chunkManager = new ChunksManager(this);
        gamemodeManager = new GamemodeManager(this);
        chatManager = new ChatManager(this);
        borderManager = new BorderManager(this);
        /* Initiliaze the game data */
        game = new Game();
        portalListeners = new PortalListeners(this);

        /* Install the config */
        processConfig();

        /* Run some startup code */
        runStartUp();

        /* In case the server is already running and it is a reload */
        Bukkit.getOnlinePlayers().forEach(all -> Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(all, "")));
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
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            it.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
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

    void processConfig() {
        try {
            if (condorConfig != null) {
                var config = CondorConfig.ofJson(condorConfig);

                game.setGameID(UUID.fromString(CONDOR_ID));

                game.setHostname(config.getHost());
                game.setHostUUID(config.getHost_uuid());

                var team_size = config.getTeam_size();
                if (team_size > 1) {
                    teamManger.setTeamManagement(true);
                    teamManger.setTeamSize(team_size);
                }

                for (var scenarios : config.getScenarios()) {
                    enableScenario(scenarios);
                }

                var gameType = config.getGame_type();
                if (!gameType.equalsIgnoreCase("UHC"))
                    enableScenario(gameType.replace("-", " "));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableScenario(String scenarioName) {
        var scenario = gamemodeManager.getGamemodesList().stream()
                .filter(scen -> scen.getName().equalsIgnoreCase(scenarioName.toLowerCase())).findFirst();
        if (scenario.isPresent()) {
            scenario.get().enableScenario();
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
