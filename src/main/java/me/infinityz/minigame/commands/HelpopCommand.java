package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("helpop, hp")
public class HelpopCommand extends BaseCommand {

    UHC instance;

    public HelpopCommand(UHC instance) {
        this.instance = instance;
    }

    @Default
    @Syntax("&c<message> The message you want.")
    public static void onList(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("Not enough arguments...");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String string : args) {
            sb.append(string + " ");
        }
        Bukkit.broadcast(ChatColor.YELLOW + "[Helpop] " + ChatColor.GRAY + player.getName()+ ": " + sb.toString(), "uhc.admin");
    }

}