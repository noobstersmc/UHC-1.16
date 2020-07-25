package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("helpop")
public class HelpopCommand extends BaseCommand {

    UHC instance;

    public HelpopCommand(UHC instance) {
        this.instance = instance;
    }

    @Default
    @Syntax("<message> &e- The message you want ")
    public void onList(Player player, String[] message) {
        if (message.length < 1) {
            player.sendMessage("Correct usage: /helpop <message>");
            return;
        }
        player.sendMessage("Your message has been sent!");

        StringBuilder sb = new StringBuilder();
        for (String string : message) {
            sb.append(string + " ");
        }
        Bukkit.broadcast(ChatColor.YELLOW + "[Helpop] " + ChatColor.GRAY + player.getName() + ": " + sb.toString(),
                "staff.perm");
    }

}