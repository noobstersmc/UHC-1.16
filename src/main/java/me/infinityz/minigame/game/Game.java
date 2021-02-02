package me.infinityz.minigame.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.condor.CondorAPI;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.gamemodes.types.UHCMeetup;
import me.infinityz.minigame.gamemodes.types.UHCRun;
import net.md_5.bungee.api.ChatColor;

@Data
public class Game {
    /* Static data */
    private static @Getter @Setter BossBar bossbar;
    private static Gson gson = new GsonBuilder().create();
    private static @Getter @Setter String scoreboardTitle = ChatColor.AQUA + "" + ChatColor.BOLD + "UHC";
    private static @Getter @Setter String tablistHeader = ChatColor.DARK_RED + "" + ChatColor.BOLD + "NOOBSTERS\n"
            + ChatColor.of("#4788d9") + "\nJoin Our Community!\n" + ChatColor.of("#2be49c") + "discord.noobsters.net\n"
            + ChatColor.AQUA + "twitter.com/NoobstersMC\n " + ChatColor.GOLD + "noobsters.buycraft.net\n";
    private static @Getter @Setter String scoreColors = ChatColor.of("#0ca2d4") + "";
    private static @Getter @Setter String UpToMVP = ChatColor.RED + "This action is only available for "
            + ChatColor.of("#1af4c1") + "MVP" + ChatColor.RED + " and UP! \n" + ChatColor.GREEN
            + "Upgrade your rank at " + ChatColor.GOLD + "noobsters.buycraft.net";
    private static @Getter @Setter String UpToVIP = ChatColor.RED + "This action is only available for "
            + ChatColor.of("#f4c91a") + "VIP" + ChatColor.RED + " and UP! \n" + ChatColor.GREEN
            + "Upgrade your rank at " + ChatColor.GOLD + "noobsters.buycraft.net";
    private static @Getter @Setter Location lobbySpawn;

    /* Game data */
    private UUID gameID = UUID.randomUUID();
    private UUID hostUUID = UUID.randomUUID();
    private long startTime;
    private int gameTime = 0;
    private boolean pvp = false;
    private boolean globalMute = false;
    private boolean hasSomeoneWon = false;
    private boolean deathMatchDamage = false;
    private boolean antiMining = false;
    private int uhcslots = 50;
    /* Game config */
    private boolean autoDestruction = true;
    private boolean deathMatch = false;
    private boolean privateGame = false;
    private boolean nether = true;
    private boolean end = false;
    private boolean strengthNerf = true;
    private boolean criticalNerf = true;
    private boolean bedsNerf = true;
    private boolean tearsDropGold = false;
    private boolean advancements = false;
    private boolean potions = true;
    private double applerate = 0.80;
    private int maxDisconnectTime = 600;
    /* Game Loop */
    private int borderSize = 3000;
    private int borderCenter = 200;
    private int borderTime = 3600;
    private int borderCenterTime = 1200;
    private int pvpTime = 1200;
    private int healTime = 120;
    private int DMgrace = 600;
    private int finalBorderGrace = 300;
    /* UHC MEETUP */
    private int autoStart = 12;
    private boolean hasAutoStarted = false;
    /* Other */
    String[] scenarios;
    Stage gameStage;
    double currentBorder;
    int playersAlive;
    int spectators;
    int playersOnline;
    String teamSize;
    String hostname;
    String ip = obtainPublicIP() + ":" + Bukkit.getServer().getPort();

    @Override
    public String toString() {
        // Maybe move this out of here? Might cause performance penalty
        var instance = UHC.getInstance();
        // Retrieve all data
        scenarios = getScenarios(instance);
        currentBorder = getCurrentBorderSize();
        gameStage = getGameStage(instance);
        spectators = getSpectators(instance);
        playersAlive = getPlayersAlive(instance);
        playersOnline = Bukkit.getOnlinePlayers().size();
        var ts = instance.getTeamManger().getTeamSize();
        teamSize = ts > 1 ? "To" + ts : "FFA";

        // Return as json
        return gson.toJson(this);
    }

    public void sendSelfDestroyRequest(UHC instance) {
        if (instance.getGamemodeManager().isScenarioEnable(UHCMeetup.class)) {
            Bukkit.getScheduler().runTask(instance, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop"));
            return;
        }
        try {
            // TODO: Try to ensure server stops emmitting data before deleting.
            Bukkit.getScheduler().runTaskLater(instance, () -> {
                try {
                    CondorAPI.delete("6QR3W05K3F", instance.getGame().getGameID().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, 1);
            Bukkit.getScheduler().runTaskLater(instance,
                    () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop"), 2);

        } catch (Exception e) {
            Bukkit.broadcastMessage("Error while autodeleting instance: " + e.getMessage());
            // TODO: handle exception
        }
    }

    public void selfDestroyTimed() {
        var instance = UHC.getInstance();
        var delay = instance.getGamemodeManager().isScenarioEnable(UHCMeetup.class) ? 20 : 60;

        if (!isAutoDestruction())
            return;

        Bukkit.broadcast(ChatColor.GRAY + "[UHC] This game will be self destructed in " + delay + " seconds.",
                "uhc.destroy.self");

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            if (!instance.getGame().isAutoDestruction()) {
                Bukkit.broadcast(ChatColor.GRAY + "[UHC] Self-destruction was cancelled.", "uhc.destroy.self");
                return;
            }

            var scheduler = Bukkit.getScheduler();
            var count = 0;
            for (var p : Bukkit.getOnlinePlayers()) {
                scheduler.runTaskLater(instance, () -> p.kickPlayer("Thanks for playing."), count += 5);
            }
            scheduler.runTaskLater(instance, () -> sendSelfDestroyRequest(instance), count += 5);

        }, 20 * delay);

    }

    public String newFormatJson() {
        var newData = new HashMap<String, Object>();
        newData.put("ipv4", ip);
        newData.put("game_id", gameID);
        newData.put("private_game", privateGame);
        var extra_data = new HashMap<String, Object>();
        extra_data.put("uhc-data", toString());
        extra_data.put("game-type", getSorter());
        newData.put("extra_data", extra_data);

        return gson.toJson(newData);
    }

    public String getSorter() {
        var manager = UHC.getInstance().getGamemodeManager();
        if (manager.isScenarioEnable(UHCRun.class)) {
            return "RUN";
        } else if (manager.isScenarioEnable(UHCMeetup.class))
            return "MEETUP";

        return "UHC";
    }

    private String obtainPublicIP() {
        String systemIpAddress = "";
        try {
            URL url_name = new URL("http://bot.whatismyipaddress.com");

            BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));

            // reads system IPAddress
            systemIpAddress = sc.readLine().trim();
        } catch (Exception e) {
            systemIpAddress = "0.0.0.0";
        }
        return systemIpAddress;
    }

    double getCurrentBorderSize() {
        var world = Bukkit.getWorld("world");
        return world != null ? world.getWorldBorder().getSize() : 0;
    }

    String[] getScenarios(UHC instance) {
        return instance.getGamemodeManager().getEnabledAsArray();
    }

    Stage getGameStage(UHC instance) {
        return instance.getGameStage();
    }

    int getSpectators(UHC instance) {
        return (int) Bukkit.getOnlinePlayers().stream().filter(p -> p.getGameMode() != GameMode.SURVIVAL).count();
    }

    int getPlayersAlive(UHC instance) {
        return instance.getPlayerManager().getAlivePlayers();
    }

}
