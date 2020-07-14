package me.infinityz.minigame.commands;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.tasks.TeleportTask;

@CommandPermission("uhc.admin")
@CommandAlias("locations|loc")
public class LocationsCommand extends BaseCommand {

    UHC instance;

    public LocationsCommand(UHC instance) {
        this.instance = instance;
    }

    @Subcommand("scatter")
    @Syntax("[world] &e- Uses the default world")
    public void start(CommandSender sender, String world) {
        HashSet<Location> locs = instance.getLocationManager().getLocationsSet();
        if(locs == null || locs.isEmpty()){
            sender.sendMessage("No locations have been found yet.");
            return;
        }
        if(locs.size() < Bukkit.getOnlinePlayers().size()){
            sender.sendMessage("Not enough locations have been found. (" + locs.size()+ "/"+ Bukkit.getOnlinePlayers().size() + ")");
            return;
        }
        sender.sendMessage("Starting the task");
        new TeleportTask(locs, new ArrayList<>(Bukkit.getOnlinePlayers())).runTaskTimer(instance, 10L, 10L);

    }

}