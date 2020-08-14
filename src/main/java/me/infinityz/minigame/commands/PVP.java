package me.infinityz.minigame.commands;

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
@CommandAlias("pvp")
public class PVP extends BaseCommand {
    private @NonNull UHC instance;

    @Default
    public void onCommand(CommandSender sender) {
        instance.pvp = !instance.pvp;
        sender.sendMessage(ChatColor.of("#DABC12") + "PvP has been set to " + instance.pvp);
    }

}