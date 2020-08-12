package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;

/**
 * Utilities
 */
@CommandAlias("u|utilities")
public @RequiredArgsConstructor class Utilities extends BaseCommand {

    private @NonNull UHC instance;

    
    @CommandPermission("admin.perm")
    @Subcommand("vip")
    @CommandAlias("vip")
    @Syntax("<target> - player to change")
    @CommandCompletion("@players")
    public void onVipCommand(CommandSender sender, @Flags("other") OfflinePlayer target) {
        Bukkit.dispatchCommand(sender, "lp user " + target.getName() + " parent addtemp vip 30d");
    }

    @CommandPermission("admin.perm")
    @Subcommand("vip+")
    @CommandAlias("vip+")
    @Syntax("<target> - player to change")
    @CommandCompletion("@players")
    public void onVipPlusCommand(CommandSender sender, @Flags("other") OfflinePlayer target) {
        Bukkit.dispatchCommand(sender, "lp user " + target.getName() + " parent addtemp vip+ 30d");
    }

    @CommandPermission("admin.perm")
    @Subcommand("worldload")
    @CommandAlias("worldload")
    public void wordLoad(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "chunky radius 2000");
        Bukkit.dispatchCommand(sender, "chunky start");
        Bukkit.dispatchCommand(sender, "chunky world world_nether");
        Bukkit.dispatchCommand(sender, "chunky start"); 
    }
}