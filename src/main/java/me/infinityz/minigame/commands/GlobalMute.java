package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@CommandPermission("staff.perm")
@CommandAlias("globalmute")
public class GlobalMute extends BaseCommand {
    UHC instance;

    public GlobalMute(UHC instance) {
        this.instance = instance;
    }

    @Default
    public void onCommand(CommandSender sender) {
        instance.globalmute = !instance.globalmute;
        Bukkit.broadcastMessage(
                ChatColor.of("#2be49c") + (instance.globalmute ? "Globalmute Enabled." : "Globalmute Disabled."));
    }

}
