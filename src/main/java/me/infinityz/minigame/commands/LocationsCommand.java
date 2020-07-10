package me.infinityz.minigame.commands;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.infinityz.minigame.UHC;

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

    }

}