package me.infinityz.minigame.game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BossBar;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.enums.Stage;
import net.md_5.bungee.api.ChatColor;

@Data
public class Game {
    /* Static data */
    private static @Getter @Setter BossBar bossbar;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static @Getter @Setter String scoreboardTitle = ChatColor.of("#A40A0A") + "" + ChatColor.BOLD + "UHC";
    private static @Getter @Setter String tablistHeader = ChatColor.of("#A40A0A") + "" + ChatColor.BOLD
            + "\nNOOBSTERS\n";
    private static @Getter @Setter String scoreColors = ChatColor.of("#AB4A7C") + "";
    /* Game data */
    private UUID gameID = UUID.randomUUID();
    private long startTime;
    private int gameTime = 0;
    private boolean pvp = false;
    private boolean globalMute = false;
    private boolean hasSomeoneWon = false;
    private boolean deathMatch = false;
    private boolean antiMining = false;
    private int uhcslots = 40;
    /* Game config */
    private boolean privateGame = false;
    private boolean nether = true;
    private boolean end = false;
    private boolean strengthNerf = true;
    private boolean criticalNerf = true;
    private boolean bedsNerf = true;
    private boolean tearsNerf = false;
    private double applerate = 0.80;
    private int maxDisconnectTime = 600;
    /* program */
    private int borderSize = 3000;
    private int borderCenter = 200;
    private int borderTime = 3600;
    private int borderCenterTime = 1500;
    private int pvpTime = 1200;
    private int healTime = 120;
    /* Other */
    String scenarios;
    Stage gameStage;
    double currentBorder;
    int playersAlive;
    int spectators;
    int playersOnline;
    String gameType;
    String hostname;
    String ip = obtainPublicIP();

    @Override
    public String toString() {
        //Maybe move this out of here? Might cause performance penalty
        var instance = UHC.getInstance();
        //Retrieve all data
        scenarios = getScenarios(instance);
        currentBorder = getCurrentBorderSize();
        gameStage = getGameStage(instance);
        spectators = getSpectators(instance);
        playersAlive = getPlayersAlive(instance);
        playersOnline = Bukkit.getOnlinePlayers().size();
        var teamSize = instance.getTeamManger().getTeamSize();
        gameType = teamSize > 1 ? "To" + teamSize : "FFA";
    
        //Return as json
        return gson.toJson(this);
    }

    private String obtainPublicIP(){
        String systemIpAddress = ""; 
        try
        { 
            URL url_name = new URL("http://bot.whatismyipaddress.com"); 
  
            BufferedReader sc = 
            new BufferedReader(new InputStreamReader(url_name.openStream())); 
  
            // reads system IPAddress 
            systemIpAddress = sc.readLine().trim(); 
        } 
        catch (Exception e) 
        { 
            systemIpAddress = "Cannot Execute Properly"; 
        } 
        return systemIpAddress;
    }

    double getCurrentBorderSize() {
        return Bukkit.getWorlds().get(0).getWorldBorder().getSize();
    }

    String getScenarios(UHC instance) {
        return instance.getGamemodeManager().getEnabledGamemodesToString();
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