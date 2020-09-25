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
import net.md_5.bungee.api.ChatColor;

/**
 * Utilities. command shortcuts
 */
@CommandAlias("u|utilities")
public @RequiredArgsConstructor class Utilities extends BaseCommand {

    private @NonNull UHC instance;

    @CommandPermission("admin.perm")
    @Subcommand("vip")
    @CommandAlias("vip")
    @Syntax("<target> - player to change")
    @CommandCompletion("@onlineplayers")
    public void onVipCommand(CommandSender sender, @Flags("other") OfflinePlayer target) {
        Bukkit.dispatchCommand(sender, "lp user " + target.getName() + " parent set vip");
    }

    @CommandPermission("admin.perm")
    @Subcommand("vip+")
    @CommandAlias("vip+")
    @Syntax("<target> - player to change")
    @CommandCompletion("@onlineplayers")
    public void onVipPlusCommand(CommandSender sender, @Flags("other") OfflinePlayer target) {
        Bukkit.dispatchCommand(sender, "lp user " + target.getName() + " parent set vip+");
    }

    @CommandPermission("admin.perm")
    @Subcommand("worldload")
    @CommandAlias("worldload")
    public void wordLoad(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "chunky world world");
        Bukkit.dispatchCommand(sender, "chunky radius " + instance.getChunkManager().getBorder());
        Bukkit.dispatchCommand(sender, "chunky start");
        Bukkit.dispatchCommand(sender, "chunky world world_nether");
        Bukkit.dispatchCommand(sender, "chunky radius " + instance.getChunkManager().getBorder());
        Bukkit.dispatchCommand(sender, "chunky start");
    }

    @CommandPermission("admin.perm")
    @Subcommand("portalplace")
    @CommandAlias("portalplace")
    public void portalSet(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "fill 2 70 -1 -2 70 1 minecraft:end_portal_frame");
        Bukkit.dispatchCommand(sender, "fill -1 70 2 1 70 -2 minecraft:end_portal_frame");
        Bukkit.dispatchCommand(sender, "fill -1 70 1 1 70 -1 air");
    }
    
    @CommandPermission("admin.perm")
    @Subcommand("promo")
    @CommandAlias("promo")
    public void promotion(CommandSender sender){
        Bukkit.broadcastMessage(ChatColor.BLUE + "Discord! discord.noobsters.net\n" + ChatColor.AQUA
        + "Twitter! twitter.com/NoobstersMC\n" + ChatColor.GOLD + "Donations! noobsters.buycraft.net");
    }

    @CommandPermission("admin.perm")
    @Subcommand("portalopen")
    @CommandAlias("portalopen")
    public void portalOpen(CommandSender sender){
        Bukkit.broadcastMessage("");
        Bukkit.dispatchCommand(sender, "fill -1 70 1 1 70 -1 minecraft:end_portal");
        Bukkit.dispatchCommand(sender, "playsound minecraft:block.end_portal.spawn ambient @a 0 0 0 11111111");
    }
}