package me.infinityz.minigame.commands;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.scoreboard.ScatterScoreboard;
import me.infinityz.minigame.tasks.TeleportTask;

@CommandPermission("uhc.admin")
@CommandAlias("start")
public class StartCommand extends BaseCommand {
    private UHC instance;

    public StartCommand(UHC instance) {
        this.instance = instance;
    }

    @Default
    public void scatter(CommandSender sender) {

        HashSet<Location> locs = instance.getLocationManager().getLocationsSet();
        if(locs == null || locs.isEmpty()){
            sender.sendMessage("No locations have been found yet.");
            return;
        }
        if(locs.size() < Bukkit.getOnlinePlayers().size()){
            sender.sendMessage("Not enough locations have been found. (" + locs.size()+ "/"+ Bukkit.getOnlinePlayers().size() + ")");
            return;
        }
        sender.sendMessage("Starting the teleport task...");
        new TeleportTask(locs, new ArrayList<>(Bukkit.getOnlinePlayers())).runTaskTimer(instance, 10L, 10L);
        
        instance.getListenerManager().unregisterListener(instance.getListenerManager().getLobby());
        
        instance.getScoreboardManager().purgeScoreboards();

        Bukkit.getOnlinePlayers().forEach(players -> {
            //Efectos
            players.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 5));
            players.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 20, 5));
            players.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING , 20 * 20, 5));
            ScatterScoreboard sb = new ScatterScoreboard(players);
            sb.update();
            instance.getScoreboardManager().getFastboardMap().put(players.getUniqueId().toString(), sb);
        });

        instance.getListenerManager().registerListener(instance.getListenerManager().getScatter());

    }

}