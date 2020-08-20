package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@CommandAlias("helpop")
public class HelpopCommand extends BaseCommand {

    private @NonNull UHC instance;
    private final ChatColor sunflowerYellow = ChatColor.of("#DABC12");

    @Default
    @Syntax("<message> &e- The message you want ")
    public void onList(Player player, String message) {
        if (message.length() < 1) {
            player.sendMessage("Correct usage: /helpop <message>");
            return;
        }

        player.sendMessage(sunflowerYellow + "Your message has been sent!");

        Bukkit.broadcast(sunflowerYellow + "[Helpop] " + ChatColor.GRAY + player.getName() + ": " + message,
                "staff.perm");
    }

}