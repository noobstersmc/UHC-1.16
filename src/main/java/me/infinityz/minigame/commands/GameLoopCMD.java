package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.enums.Stage;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@CommandAlias("gameloop")
@CommandPermission("uhc.gameloop.cmd")
public class GameLoopCMD extends BaseCommand {

    private @NonNull UHC instance;

    private boolean shouldExecute(CommandSender sender){
        if(!sender.hasPermission("gameloop.bypass.stage") && instance.getGame().getGameStage() != Stage.LOBBY){
            sender.sendMessage(ChatColor.RED + "You must change this config before the game starts.");
            return false;
        }
        return true;
    }

    @Subcommand("pvptime")
    @CommandAlias("pvptime")
    public void changePvpTime(CommandSender sender, Integer newPvpTime) {

        if(!shouldExecute(sender)) return;

        instance.getGame().setPvpTime(newPvpTime * 60);
        sender.sendMessage(ChatColor.YELLOW + "PvP will be enabled at " + newPvpTime + " minutes after start.");
    }

    @Subcommand("healtime")
    @CommandAlias("healtime")
    public void changeHealTime(CommandSender sender, Integer newHealTime) {

        if(!shouldExecute(sender)) return;

        instance.getGame().setHealTime(newHealTime * 60);
        sender.sendMessage(ChatColor.YELLOW + "Heal time will be at " + newHealTime + " minutes after start.");
    }

    @Subcommand("bordertime")
    @CommandAlias("bordertime")
    public void changeBorderTime(CommandSender sender, Integer newBorderTime) {

        if(!shouldExecute(sender)) return;

        instance.getGame().setBorderTime(newBorderTime * 60);
        sender.sendMessage(ChatColor.YELLOW + "Border will start to move to the center at " + newBorderTime + " minutes after start.");
    }

    @Subcommand("bordercentertime")
    @CommandAlias("bordercentertime")
    public void changeBorderCenterTime(CommandSender sender, Integer newBorderCenterTime) {

        if(!shouldExecute(sender)) return;

        if(!sender.hasPermission("uhc.security.gameloop") && newBorderCenterTime < 15){
            sender.sendMessage(ChatColor.RED + "This value can't be less than 15 minutes.");
            sender.sendMessage(ChatColor.RED + "" + newBorderCenterTime + " is too fast.");
            return;
        }

        instance.getGame().setBorderCenterTime(newBorderCenterTime * 60);
        sender.sendMessage(ChatColor.YELLOW + "Once the border start to move will take " + newBorderCenterTime + " minutes to reach the center.");
    }


    @Subcommand("bordercenter")
    @CommandAlias("bordercenter")
    public void changeBorderCenter(CommandSender sender, Integer newBorderCenter) {

        if(!shouldExecute(sender)) return;

        if(!sender.hasPermission("uhc.security.gameloop") && newBorderCenter < 50){
            sender.sendMessage(ChatColor.RED + "This value can't be less than 50 blocks of diameter.");
            sender.sendMessage(ChatColor.RED + "" + newBorderCenter + " is too small.");
            return;
        }

        instance.getGame().setBorderCenterTime(newBorderCenter);
        sender.sendMessage(ChatColor.YELLOW + "Once the border start to move the final border size will be " + newBorderCenter + " of diameter.");
    }


    @Subcommand("bordersize")
    @CommandAlias("bordersize")
    public void changeBorderSize(CommandSender sender, Integer newBorderSize) {

        if(!shouldExecute(sender)) return;

        if(!sender.hasPermission("gameloop.bypass.bordersize") && newBorderSize < 1000){
            sender.sendMessage(ChatColor.RED + "This value can't be less than 1000 blocks of diameter.");
            sender.sendMessage(ChatColor.RED + "" + newBorderSize + " is too small.");
            return;
        } else if(!sender.hasPermission("gameloop.bypass.bordersize") && newBorderSize > 6000){
            sender.sendMessage(ChatColor.RED + "This value can't be more than 6000 blocks of diameter.");
            sender.sendMessage(ChatColor.RED + "" + newBorderSize + " is too big.");
            return;
        }

        instance.getGame().setBorderSize(newBorderSize);
        Bukkit.getWorlds().forEach(it -> it.getWorldBorder().setSize(newBorderSize));
        sender.sendMessage(ChatColor.YELLOW + "Border size changed " + newBorderSize + " blocks of diameter.");
    }

    @Subcommand("finalbordertime")
    @CommandAlias("finalbordertime")
    public void changeFinalBorderGrace(CommandSender sender, Integer newFinalBorderGrace) {

        if(!shouldExecute(sender)) return;

        instance.getGame().setFinalBorderGrace(newFinalBorderGrace);
        sender.sendMessage(ChatColor.YELLOW + "Once the border reach the center " + newFinalBorderGrace + " minutes after will start the final border.");
        sender.sendMessage(ChatColor.GREEN + "Set to 0 to disable the final border.");
    }

    @Subcommand("deathmatchtime")
    @CommandAlias("deathmatchtime")
    public void changeDMGrace(CommandSender sender, Integer newDeathMatchGrace) {

        if(!shouldExecute(sender)) return;

        instance.getGame().setFinalBorderGrace(newDeathMatchGrace);
        sender.sendMessage(ChatColor.YELLOW + "Once the final border ends " + newDeathMatchGrace + " minutes after will start the deathmatch.");
        sender.sendMessage(ChatColor.GREEN + "Use '/deathmatch false' to disable the deathmatch.");
    }

}