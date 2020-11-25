package me.infinityz.minigame.gamemodes.types;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.ScoreboardUpdateEvent;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;


public class UHCVandalico extends IGamemode implements Listener {
    private UHC instance;
    private WorldBorder worldBorder = Bukkit.getWorlds().get(0).getWorldBorder();

    public UHCVandalico(UHC instance) {
        super("UHC Vandálico", "UHCs Nórdicos.");
        this.instance = instance;
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game score VANDAL");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "config advancements true");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "config privateGame true");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist on");
        instance.getGame().setTearsNerf(true);

        instance.getGame().setDeathMatch(false);

        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        instance.getGame().setDeathMatch(true);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game score UHC");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "config advancements false");
        setEnabled(false);
        return true;
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onInterceptUpate(ScoreboardUpdateEvent e) {
        if(instance.getGamemodeManager().isScenarioEnable(UHCMeetup.class) 
            || instance.getGamemodeManager().isScenarioEnable(UHCRun.class)) return;

        e.setCancelled(true);
    }

    private String timeConvert(int t) {
        int hours = t / 3600;

        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }
     

    @EventHandler(priority = EventPriority.HIGH)
    public void onModifyScoreboard(ScoreboardUpdateEvent e) {
        if(instance.getGamemodeManager().isScenarioEnable(UHCMeetup.class) 
            || instance.getGamemodeManager().isScenarioEnable(UHCRun.class)) return;

        e.setCancelled(false);
        var uhcPlayer = instance.getPlayerManager().getPlayer(e.getScoreboard().getPlayer().getUniqueId());
        var isTeams = instance.getTeamManger().getTeamSize() > 1;

        if (isTeams) {
            var team = instance.getTeamManger().getPlayerTeam(e.getScoreboard().getPlayer().getUniqueId());
            e.setLinesArray(
                "━━━━━━━━━━━━━━━━━━━━━━━━━",
                ChatColor.of("#00d0db") + "Time: " + ChatColor.WHITE + timeConvert(instance.getGame().getGameTime()),
                ChatColor.of("#00d0db") + "Border: " + ChatColor.WHITE + ((int) worldBorder.getSize() / 2), 
                "━━━━━━━━━━━━━━━━━━━━━━━━━",
                ChatColor.of("#00d0db") + "Kills: " + ChatColor.WHITE + (uhcPlayer != null ? uhcPlayer.getKills() : 0),
                ChatColor.of("#00d0db") + "Team Kills: " + ChatColor.WHITE + (team != null ? team.getTeamKills() : 0), 
                ChatColor.of("#00d0db") + "Players Left: " + ChatColor.WHITE + instance.getPlayerManager().getAlivePlayers(),
                "━━━━━━━━━━━━━━━━━━━━━━━━━",
                ChatColor.of("#00d0db") + "Hosted by: " + ChatColor.WHITE + "noobsters.net",
                "━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        } else {
            e.setLinesArray(
                "━━━━━━━━━━━━━━━━━━━━━━━━━",
                ChatColor.of("#00d0db") + "Time: " + ChatColor.WHITE + timeConvert(instance.getGame().getGameTime()),
                ChatColor.of("#00d0db") + "Border: " + ChatColor.WHITE + ((int) worldBorder.getSize() / 2), 
                "━━━━━━━━━━━━━━━━━━━━━━━━━",
                ChatColor.of("#00d0db") + "Kills: " + ChatColor.WHITE + (uhcPlayer != null ? uhcPlayer.getKills() : 0),
                ChatColor.of("#00d0db") + "Players Left: " + ChatColor.WHITE + instance.getPlayerManager().getAlivePlayers(),
                "━━━━━━━━━━━━━━━━━━━━━━━━━",
                ChatColor.of("#00d0db") + "Hosted by: " + ChatColor.WHITE + "noobsters.net",
                "━━━━━━━━━━━━━━━━━━━━━━━━━");
        }

    }

}

