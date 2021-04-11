package me.noobsters.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.enums.Stage;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@CommandAlias("gameloop")
@CommandPermission("uhc.gameloop.cmd")
public class GameLoopCMD extends BaseCommand {

    private @NonNull UHC instance;
    private String permissionDebug = "uhc.configchanges.see";

    @Subcommand("pvptime")
    @CommandAlias("pvptime")
    public void changePvpTime(CommandSender sender, Integer newPvpTime) {
        var game = instance.getGame();
        
        if(game.isPvp()){
            sender.sendMessage(ChatColor.RED + "PvP is already enabled.");
            return;
        }else if(newPvpTime <= 0){
            sender.sendMessage(ChatColor.RED + "Invalid number.");
            return;
        }

        game.setPvpTime(newPvpTime * 60);
        
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "PvP will be enabled at " + newPvpTime + " minutes after start.", permissionDebug);
    }

    @Subcommand("healtime")
    @CommandAlias("healtime")
    public void changeHealTime(CommandSender sender, Integer newHealTime) {

        instance.getGame().setHealTime(newHealTime * 60);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Heal time will be at " + newHealTime + " minutes after start.", permissionDebug);
    }

    @Subcommand("bordertime")
    @CommandAlias("bordertime")
    public void changeBorderTime(CommandSender sender, Integer newBorderTime) {
        var game = instance.getGame();

        if(!sender.hasPermission("uhc.gameloop.security") && game.getGameTime() > game.getBorderTime()-1) {
            sender.sendMessage(ChatColor.RED + "Is too late to change this config.");
            return;
        }

        game.setBorderTime(newBorderTime * 60);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor. YELLOW + "Border will start to move to the center at " + newBorderTime + " minutes after start.", permissionDebug);
    }

    @Subcommand("bordercentertime")
    @CommandAlias("bordercentertime")
    public void changeBorderCenterTime(CommandSender sender, Integer newBorderCenterTime) {

        var game = instance.getGame();
        if(!sender.hasPermission("uhc.gameloop.security") && game.getGameTime() > game.getBorderTime()-1) {
            sender.sendMessage(ChatColor.RED + "Is too late to change this config.");
            return;
        }

        if(!sender.hasPermission("uhc.gameloop.security") && newBorderCenterTime < 15){
            sender.sendMessage(ChatColor.RED + "This value can't be less than 15 minutes.");
            sender.sendMessage(ChatColor.RED + "" + newBorderCenterTime + " is too fast.");
            return;
        }

        game.setBorderCenterTime(newBorderCenterTime * 60);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Once the border start to move will take " + newBorderCenterTime + " minutes to reach the center.", permissionDebug);
    }


    @Subcommand("bordercenter")
    @CommandAlias("bordercenter")
    public void changeBorderCenter(CommandSender sender, Integer newBorderCenter) {

        var game = instance.getGame();
        if(!sender.hasPermission("uhc.gameloop.security") && game.getGameTime() > game.getBorderTime()-1) {
            sender.sendMessage(ChatColor.RED + "Is too late to change this config.");
            return;
        }

        if(!sender.hasPermission("uhc.gameloop.security") && newBorderCenter < 50){
            sender.sendMessage(ChatColor.RED + "This value can't be less than 50 blocks of diameter.");
            sender.sendMessage(ChatColor.RED + "" + newBorderCenter + " is too small.");
            return;
        }

        game.setBorderCenter(newBorderCenter);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Once the border start to move the final border size will be " + newBorderCenter + " of diameter.", permissionDebug);
    }


    @Subcommand("bordersize")
    @CommandAlias("bordersize")
    public void changeBorderSize(CommandSender sender, Integer newBorderSize) {

        if(!sender.hasPermission("uhc.gameloop.security") && instance.getGame().getGameStage() != Stage.LOBBY){
                sender.sendMessage(ChatColor.RED + "Is too late to change this config.");
                return;
        }

        if(!sender.hasPermission("uhc.gameloop.security") && newBorderSize < 1000){
            sender.sendMessage(ChatColor.RED + "This value can't be less than 1000 blocks of diameter.");
            sender.sendMessage(ChatColor.RED + "" + newBorderSize + " is too small.");
            return;
        } else if(!sender.hasPermission("uhc.gameloop.security") && newBorderSize > 6000){
            sender.sendMessage(ChatColor.RED + "This value can't be more than 6000 blocks of diameter.");
            sender.sendMessage(ChatColor.RED + "" + newBorderSize + " is too big.");
            return;
        }

        instance.getGame().setBorderSize(newBorderSize);
        Bukkit.getWorlds().forEach(it -> it.getWorldBorder().setSize(newBorderSize));
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Border size changed to " + newBorderSize + " blocks of diameter.", permissionDebug);
    }

    @Subcommand("finalbordertime")
    @CommandAlias("finalbordertime")
    public void changeFinalBorderGrace(CommandSender sender, Integer newFinalBorderGrace) {

        var game = instance.getGame();
        if(!sender.hasPermission("uhc.gameloop.security") && game.getGameTime() > game.getBorderTime()-1) {
            sender.sendMessage(ChatColor.RED + "Is too late to change this config.");
            return;
        }

        game.setFinalBorderGrace(newFinalBorderGrace);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Once the border reach the center " + newFinalBorderGrace + " minutes after will start the final border.", permissionDebug);
        sender.sendMessage(ChatColor.GREEN + "Set to 0 to disable the final border.");
    }

    @Subcommand("deathmatchtime")
    @CommandAlias("deathmatchtime")
    public void changeDMGrace(CommandSender sender, Integer newDeathMatchGrace) {

        var game = instance.getGame();
        if(!sender.hasPermission("uhc.gameloop.security") && game.getGameTime() > game.getBorderTime()-1) {
            sender.sendMessage(ChatColor.RED + "Is too late to change this config.");
            return;
        }

        game.setFinalBorderGrace(newDeathMatchGrace);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Once the final border ends " + newDeathMatchGrace + " minutes after will start the deathmatch.", permissionDebug);
        sender.sendMessage(ChatColor.GREEN + "Use '/deathmatch false' to disable the deathmatch.");
    }

}