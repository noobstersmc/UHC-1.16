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
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
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
import me.infinityz.minigame.condor.CondorManager;
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
    private @Getter EditSession session;
    private @Getter CondorManager condorManager;
    /* Statics */
    private static @Getter UHC instance;
    private static @Setter TaskChainFactory taskChainFactory;

    @Override
    public void onEnable() {
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
        // commandManager.unregisterCommands();
        craftingManager.purgeRecipes();
    }

    private void pasteSchematic(File file, Location loc) {
        try {
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            ClipboardReader reader = format.getReader(new FileInputStream(file));
            Clipboard clipboard = reader.read();
            com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(loc.getWorld());

            session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1);

            // Saves our operation and builds the paste - ready to be completed.
            Operation operation = new ClipboardHolder(clipboard).createPaste(session).to(BlockVector3.at(0, 140, 0))
                    .ignoreAirBlocks(true).build();

            try { // This simply completes our paste and then cleans up.
                Operations.complete(operation);
                session.flushSession();

            } catch (WorldEditException e) { // If worldedit generated an exception it will go here
                Bukkit.broadcastMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
                e.printStackTrace();
            }

        } catch (Exception e) {
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
        Bukkit.getScheduler().runTaskLater(this, () -> {
            var antiDoublePaste = new File(Bukkit.getWorldContainer().getPath() + File.separatorChar + "seed"
                    + File.separatorChar + Bukkit.getWorlds().get(0).getSeed() + ".json");
            if (!antiDoublePaste.exists()) {
                getLogger().info("Pasting a lobby in the world...");
                try {
                    antiDoublePaste.mkdirs();
                    antiDoublePaste.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pasteSchematic(new File(Bukkit.getWorldContainer().getPath() + File.separatorChar + "lobby.schem"),
                        Bukkit.getWorlds().get(0).getSpawnLocation());
            }

        }, 60L);
        setCondorConfig(new Gson());
    }

    private void setCondorConfig(final Gson gson) {
        var condor_id = getCondorID();
        // If ID exist, pull data from redis
        if (!condor_id.isBlank()) {
            var condor_data = getCondorManager().getJedis().get("data:" + condor_id);
            var server_data = gson.fromJson(condor_data, JsonObject.class);
            var scenarios_linked_list = new LinkedList<String>();
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
            scenarios_linked_list.add("worldload");
            // Execute commands
            scenarios_linked_list.stream().forEachOrdered(e -> {
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    runCommand(e);
                }, 10L);
            });
        }

    }

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

            getLogger().info("Saving max players to server.properties...");
            properties.setProperty("level-seed", seed);

            try (OutputStream os = new FileOutputStream(propertiesFile)) {
                properties.store(os, "Minecraft server properties");
            }
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "An error occurred while updating the server properties", e);
        }
    }

}
