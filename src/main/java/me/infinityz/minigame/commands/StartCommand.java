package me.infinityz.minigame.commands;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.World;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.scoreboard.ScatterScoreboard;
import me.infinityz.minigame.tasks.TeleportTask;
import me.infinityz.minigame.tasks.TeleportTemporalTask;
import net.md_5.bungee.api.ChatColor;

@CommandPermission("start.perm")
@CommandAlias("start")
public class StartCommand extends BaseCommand {
    private UHC instance;

    public StartCommand(UHC instance) {
        this.instance = instance;
    }
    @Default
    public void newScatter(CommandSender sender){
        HashSet<Location> locs = instance.getLocationManager().getLocationsSet();
        if(locs == null || locs.isEmpty()){
            sender.sendMessage("No locations have been found yet.");
            return;
        }
        if(locs.size() < Bukkit.getOnlinePlayers().size()){
            sender.sendMessage("Not enough locations have been found. (" + locs.size()+ "/"+ Bukkit.getOnlinePlayers().size() + ")");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Starting the teleportation task...");
        //Start Parameters
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist on");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add @a");

        Bukkit.getWorlds().forEach(it->{
            it.getWorldBorder().setSize(4001);
            it.setDifficulty(Difficulty.HARD);
            it.setGameRule(GameRule.DO_MOB_SPAWNING, true);
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            it.setTime(400);
        });

        new TeleportTemporalTask(instance, locs, new ArrayList<>(Bukkit.getOnlinePlayers())).runTaskTimer(instance, 20L, 20L);
        
        instance.getListenerManager().unregisterListener(instance.getListenerManager().getLobby());
        
        instance.getScoreboardManager().purgeScoreboards();

        Bukkit.getOnlinePlayers().forEach(players -> {
            //cosas del inicio
            players.getInventory().clear();
            players.setGameMode(GameMode.SURVIVAL);
            ScatterScoreboard sb = new ScatterScoreboard(players);
            sb.update();
            instance.getScoreboardManager().getFastboardMap().put(players.getUniqueId().toString(), sb);
        });

        instance.getListenerManager().registerListener(instance.getListenerManager().getScatter());
    }

}
