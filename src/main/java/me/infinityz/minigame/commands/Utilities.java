package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
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
    @Subcommand("promo")
    @CommandAlias("promo")
    public void promotion(CommandSender sender){
        Bukkit.broadcastMessage(ChatColor.BLUE + "Discord! discord.noobsters.net\n" + ChatColor.AQUA
        + "Twitter! twitter.com/NoobstersMC\n" + ChatColor.GOLD + "Donations! noobsters.buycraft.net");
    }

    @CommandPermission("staff.perm")
    @Subcommand("togglespec|ts")
    @CommandAlias("togglespec|ts")
    public void onToggleSpec(Player sender) {
        toggleGm(sender);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + ChatColor.GRAY
        + (sender.getGameMode() == GameMode.SPECTATOR ? "Temporal Spectator Enabled." : "Temporal Spectator Disabled."), "uhc.configchanges.see");

    }

    @CommandCompletion("@onlineplayers")
    @Subcommand("t")
    @CommandAlias("t")
    public void teleportCMD(Player sender, @Flags("other") Player target) {
        if(sender.getGameMode() != GameMode.SPECTATOR ){
            sender.sendMessage(ChatColor.RED + "You must be in spectator mode.");
            return;
        }
        sender.teleportAsync(target.getLocation());
        sender.sendActionBar(ChatColor.GRAY + "Teleported to " + target.getName().toString());

    }

    @CommandPermission("tpworld.cmd")
    @Subcommand("tpworld")
    @CommandAlias("tpworld")
    @CommandCompletion("@worlds")
    public void tpWorld(Player player, World world) {
        player.teleport(world.getSpawnLocation());
        player.sendActionBar(ChatColor.GRAY + "Teleported to world " + world.toString());
    }

    @CommandPermission("guest.cmd")
    @CommandCompletion("@onlineplayers")
    @Subcommand("guest")
    @CommandAlias("guest")
    public void guestCMD(Player sender, @Flags("other") Player target) {
        if (!target.hasPermission("group.guest")) {
            sender.sendMessage(ChatColor.GREEN + target.getName().toString() + " is now guest of this UHC!");
            target.addAttachment(instance).setPermission("group.guest", true);
            target.updateCommands();
        }
    }

    public boolean toggleGm(Player player){
        if(player.getGameMode() == GameMode.SURVIVAL){
            player.setGameMode(GameMode.SPECTATOR);
            return true;
        }  
        else if(player.getGameMode() == GameMode.SPECTATOR){
            player.setGameMode(GameMode.SURVIVAL);
            return false;
        }
        return false;
    }

    @CommandPermission("test.cmd")
    @CommandCompletion("@onlineplayers")
    @Subcommand("test")
    @CommandAlias("test")
    public void testCMD(Player sender) {

    }

}