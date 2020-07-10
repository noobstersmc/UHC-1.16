package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.tasks.ScatterTask;

@CommandPermission("uhc.admin")
@CommandAlias("locations|loc")
public class LocationsCommand extends BaseCommand {

    UHC instance;

    public LocationsCommand(UHC instance) {
        this.instance = instance;
    }

    @Subcommand("scatter")
    public void start(CommandSender sender, String world) {
        sender.sendMessage("Start");
        new ScatterTask(Bukkit.getWorlds().get(0), 2000, 100, 50).runTaskTimer(instance, 0, 10);

    }

}