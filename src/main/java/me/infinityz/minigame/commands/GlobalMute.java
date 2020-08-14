package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@CommandPermission("staff.perm")
@CommandAlias("globalmute")
public class GlobalMute extends BaseCommand {
    private @NonNull UHC instance;

    @Default
    public void onCommand(CommandSender sender) {
        instance.globalmute = !instance.globalmute;
        Bukkit.broadcastMessage(
                ChatColor.of("#2be49c") + (instance.globalmute ? "Globalmute Enabled." : "Globalmute Disabled."));
    }

}
