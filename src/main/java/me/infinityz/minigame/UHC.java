package me.infinityz.minigame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
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
import me.infinityz.minigame.border.BorderManager;
import me.infinityz.minigame.chat.ChatManager;
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
    private static JsonConfig JSON_CONFIG;
    private static String CONDOR_ID = null;

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
        } else {
            CONDOR_ID = getCondorID();
        }
        System.out.println("[CONDOR] Condor id is: " + CONDOR_ID);
        if (CONDOR_ID != null)
            condorConfig = CondorAPI.getGameJsonConfig(CONDOR_ID,
                    condor_secret != null ? condor_secret : "Condor-Secreto");

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
        if(condorConfig != null){
            var error = condorConfig.get("error");
            if(error == null){
                var config = CondorConfig.ofJson(condorConfig);
                /* TODO: Use config to load required worlds with seed */
            }else{
                System.out.println(error);
            }
        }

        /**
         * Initialize taskChain, fastInv, and set the game stage to loading
         */

        setTaskChainFactory(BukkitTaskChainFactory.create(this));
        FastInvManager.register(this);
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
        condorManager = new CondorManager(this);
        /* Initiliaze the game data */
        game = new Game();
        portalListeners = new PortalListeners(this);

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
        // gamemodeManager.getEnabledGamemodes().forEach(IGamemode::disableScenario);
        // commandManager.unregisterCommands();
        craftingManager.purgeRecipes();
    }

    private void createMainWorld(Long seed) {
        // world code
        WorldCreator world = new WorldCreator("world");
        world.environment(Environment.NORMAL);
        if (seed != null)
            world.seed(seed);

        world.createWorld();

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
            it.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
            it.setSpawnLocation(0, it.getHighestBlockAt(0, 0).getZ() + 10, 0);
            it.getWorldBorder().setCenter(0, 0);
            it.getWorldBorder().setSize(game.getBorderSize());
            it.getWorldBorder().setDamageBuffer(0.0);
            it.getWorldBorder().setDamageAmount(0.0);
        });

    }

    @Deprecated
    private void setCondorConfig(final Gson gson) {
        var condor_id = getCondorID();
        // If ID exist, pull data from redis

        var scenarios_linked_list = new LinkedList<String>();

        if (!condor_id.isBlank()) {
            var condor_data = getCondorManager().getJedis().get("data:" + condor_id);
            var server_data = gson.fromJson(condor_data, JsonObject.class);
            // Set hostname and gameID
            var hostname = server_data.get("host").getAsString();
            game.setHostname(hostname);
            game.setGameID(UUID.fromString(condor_id));
            // Set the gameType
            scenarios_linked_list
                    .add("scenario " + server_data.get("game_type").getAsString().replace("-", " ").toLowerCase());
            // Process extra data
            var uhc_extra_data = server_data.get("extra_data").getAsJsonObject();
            uhc_extra_data.get("scenarios").getAsJsonArray()
                    .forEach(e -> scenarios_linked_list.add("scenario " + e.getAsString()));

            // Handle team size
            var team_size = uhc_extra_data.get("team_size").getAsInt();
            if (team_size > 1) {
                getTeamManger().setTeamManagement(true);
                getTeamManger().setTeamSize(team_size);
            }
        }

        scenarios_linked_list.add("worldload");

        // Execute commands
        scenarios_linked_list.stream().forEachOrdered(e -> {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                runCommand(e);
            }, 10L);
        });

    }

    private void runCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
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

    public void changeSeed(String seed) {
        Properties properties = new Properties();
        File propertiesFile = new File("server.properties");

        try {
            try (InputStream is = new FileInputStream(propertiesFile)) {
                properties.load(is);
            }

            getLogger().info("Level seed has been updated!");
            properties.setProperty("level-seed", seed);

            try (OutputStream os = new FileOutputStream(propertiesFile)) {
                properties.store(os, "Minecraft server properties");
            }
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "An error occurred while updating the server properties", e);
        }
    }

}
