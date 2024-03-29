package me.noobsters.minigame.game;

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
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.condor.CondorAPI;
import me.noobsters.minigame.enums.Stage;
import me.noobsters.minigame.events.ConfigChangeEvent;
import me.noobsters.minigame.gamemodes.types.UHCMeetup;
import me.noobsters.minigame.gamemodes.types.UHCRun;
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

    private static @Getter @Setter String COLOR_1 = ChatColor.of("#0eaa83") + "";
    private static @Getter @Setter String COLOR_2 = ChatColor.of("#aa0e28") + "";

    /* Game data */
    private UUID gameID = UUID.randomUUID();
    private UUID hostUUID = UUID.randomUUID();
    private long startTime;
    private int gameTime = 0;
    private boolean pvp = false;
    private boolean hasSomeoneWon = false;
    private boolean deathMatchDamage = false;
    private boolean antiMining = false;
    private int uhcslots = 55;
    private boolean whitelistEnabled = false;
    private HashMap<String, String> whitelist = new HashMap<>();
    private HashMap<String, UUID> combatLoggers = new HashMap<>();
    private boolean combatLog = false;
    private String[] rules = new String[] { 
            COLOR_2 + "UHC RULES:",
            COLOR_1 + "1. " + ChatColor.WHITE + "Stalking is not allowed. ",
            COLOR_1 + "2. " + ChatColor.WHITE + "Stealing is not allowed. ",
            COLOR_1 + "3. " + ChatColor.WHITE + "iPvP is not allowed. ",
            COLOR_1 + "4. " + ChatColor.WHITE + "Nether portal camping is not allowed.",
            COLOR_1 + "5. " + ChatColor.WHITE + "Nether portal trapping is not allowed.",
            COLOR_1 + "6. " + ChatColor.WHITE + "CrossTeam, Truce or Teaming someone else is not your team is not allowed.",
            COLOR_1 + "7. " + ChatColor.WHITE + "Camping is not allowed at meetup.",
            COLOR_1 + "8. " + ChatColor.WHITE + "Improper actions inciting hate speech are not allowed.",
            COLOR_1 + "9. " + ChatColor.WHITE + "Inappropriate/Lag structures are not allowed.",
            COLOR_1 + "10. " + ChatColor.WHITE + "SkyBases are not allowed at meetup.",
            COLOR_1 + "11. " + ChatColor.WHITE + "Mining is not allowed at meetup.",
            COLOR_2 + "Good Luck!" };
    /* Game config */
    private boolean autoDestruction = true;
    private boolean deathMatch = false;
    private boolean privateGame = false;
    private boolean nether = true;
    private boolean end = false;
    private boolean advancements = false;
    private double appleRate = 0.80f;
    private double flintRate = 3.00f;
    private int maxDisconnectTime = 600;
    private GameInfo gameInfo = GameInfo.COMMUNITY;
    /* Game Nerfs && Buffers */
    private boolean cobweb = true;
    private boolean potions = true;
    private boolean potionsTier = true;
    private boolean beds = true;
    private boolean strength = true;
    private boolean itemsBurn = true;
    private boolean strengthNerf = true;
    private boolean bedsNerf = true;
    private boolean tears = true;
    private boolean trades = true;
    private boolean horses = true;
    private boolean trident = true;
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
    String hostname = "Noobsters";
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

    public enum GameInfo {
        PRIVATE,
        COMMUNITY,
        OFFICIAL
    }

    public enum ConfigType {
        APPLE_RATE,
        FLINT_RATE,
        NETHER, 
        ADVANCEMENTS, 
        HORSES, BEDS, 
        BEDS_NERF, 
        POTIONS, 
        STRENGTH, 
        STRENGTH_NERF, 
        TRADES,
        ITEMS_BURN, 
        TRIDENT, 
        TEARS,
        POTIONS_TIER,
        COBWEB,

        GAME, 
        HOSTNAME, 
        SLOTS, 
        TEAM_SIZE, 
        BORDER_SIZE, 
        BORDER_CENTER, 
        BORDER_TIME, 
        BORDER_CENTER_TIME,
        PVP_TIME,
        HEAL_TIME
    }

    //change privategame to enum

    public void setGameInfo(GameInfo gameInfo){
        this.gameInfo = gameInfo;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.GAME));
    }

    public void setCobweb(boolean cobweb) {
        this.cobweb = cobweb;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.COBWEB));
    }

    public void setPotionsTier(boolean potionsTier) {
        this.potionsTier = potionsTier;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.POTIONS_TIER));
    }

    public void setPrivateGame(boolean privateGame) {
        this.privateGame = privateGame;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.GAME));
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.HOSTNAME));
    }

    public void setUhcSlots(int uhcslots) {
        this.uhcslots = uhcslots;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.SLOTS));
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.BORDER_SIZE));
    }

    public void setBorderCenter(int borderCenter) {
        this.borderCenter = borderCenter;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.BORDER_CENTER));
    }

    public void setBorderTime(int borderTime) {
        this.borderTime = borderTime;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.BORDER_TIME));
    }

    public void setBorderCenterTime(int borderCenterTime) {
        this.borderCenterTime = borderCenterTime;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.BORDER_CENTER_TIME));
    }

    public void setPvpTime(int pvpTime) {
        this.pvpTime = pvpTime;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.PVP_TIME));
    }

    public void setHealTime(int healTime) {
        this.healTime = healTime;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.HEAL_TIME));
    }

    public void setAppleRate(double appleRate) {
        this.appleRate = appleRate;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.APPLE_RATE));
    }

    public void setFlintRate(double flintRate) {
        this.flintRate = flintRate;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.FLINT_RATE));
    }

    public void setNether(boolean nether) {
        this.nether = nether;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.NETHER));
    }

    public void setAdvancements(boolean advancements) {
        this.advancements = advancements;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.ADVANCEMENTS));
    }

    public void setHorses(boolean horses) {
        this.horses = horses;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.HORSES));
    }

    public void setBeds(boolean beds) {
        this.beds = beds;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.BEDS));
    }

    public void setBedsNerf(boolean bedsNerf) {
        this.bedsNerf = bedsNerf;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.BEDS_NERF));
    }

    public void setPotions(boolean potions) {
        this.potions = potions;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.POTIONS));
    }

    public void setStrength(boolean strength) {
        this.strength = strength;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.STRENGTH));
    }

    public void setStrengthNerf(boolean strengthNerf) {
        this.strengthNerf = strengthNerf;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.STRENGTH_NERF));
    }

    public void setTrades(boolean trades) {
        this.trades = trades;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.TRADES));
    }

    public void setItemsBurn(boolean itemsBurn) {
        this.itemsBurn = itemsBurn;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.ITEMS_BURN));
    }

    public void setTrident(boolean trident) {
        this.trident = trident;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.TRIDENT));
    }

    public void setTears(boolean tears) {
        this.tears = tears;
        Bukkit.getPluginManager().callEvent(new ConfigChangeEvent(ConfigType.TEARS));
    }

}
