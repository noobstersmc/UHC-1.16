package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import me.infinityz.minigame.UHC;

@CommandAlias("helpop")
public class HelpopCommand extends BaseCommand {

    UHC instance;

    public HelpopCommand(UHC instance) {
        this.instance = instance;
    }

    @Default
    @Syntax("<message> &e- The message you want ")
    public static void onList(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("Not enough arguments...");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String string : args) {
            sb.append(string + " ");
        }
        Bukkit.broadcast("[Helpop] " + player.getName()+ ": " + sb.toString(), "uhc.admin");
    }

}