package me.infinityz.minigame.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@CommandAlias("chat")
public class ChatCommand extends BaseCommand {

    private @NonNull UHC instance;

    @CommandCompletion("@channels")
    @Default
    public void toggleChannel(final Player sender, String chat) {
        if (sender.hasPermission("uhc.staff.chat") && chat.equalsIgnoreCase("staff")) {
            changeDefaultChat(sender, "staff");
        } else if (chat.equalsIgnoreCase("global") || chat.equalsIgnoreCase("gc")) {
            changeDefaultChat(sender, "global");
        } else if (chat.equalsIgnoreCase("team") || chat.equalsIgnoreCase("teams")) {
            if (instance.getTeamManger().isTeams())
                changeDefaultChat(sender, "team");
            else
                sender.sendMessage(
                        ChatColor.RED + "Channel " + ChatColor.WHITE + chat + ChatColor.RED + " is not enabled.");
        } else {
            sender.sendMessage(ChatColor.RED + "Channel " + ChatColor.WHITE + chat + ChatColor.RED + " doesn't exist.");
        }
    }

    @CommandPermission("world.oi")
    @Subcommand("oi")
    public void oi(CommandSender sender) {
        try {
            instance.getSession().undo(instance.getSession());
            Bukkit.getScheduler().runTaskLater(instance, () -> {
            Bukkit.dispatchCommand(sender, "kill @e[type=minecraft:item]");
            Bukkit.dispatchCommand(sender, "kill @e[type=minecraft:falling_block]");
    
            }, 10);
            
        } catch (Exception e) {
        }
    }

    @CommandPermission("world.oi")
    @Subcommand("hun")
    public void s(Player sender) {
        sender.sendMessage("Sat / Hunguer : " + sender.getSaturation() + " / " + sender.getFoodLevel());
    }


    private void changeDefaultChat(final Player player, final String newChat) {
        instance.getChatManager().getDefaultChat().put(player.getUniqueId(), newChat.toLowerCase());
        player.sendMessage(ChatColor.GOLD + "Changing default chat to: " + ChatColor.WHITE + newChat.toLowerCase());

    }
}
