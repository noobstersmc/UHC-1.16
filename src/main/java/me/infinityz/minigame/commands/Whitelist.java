package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("wwl|wwhitelist")
public class Whitelist extends BaseCommand {

    private UHC instance;

    public Whitelist(UHC instance){
        this.instance = instance;

        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("whitelist", c -> {
            return instance.getGame().getWhitelist();

        });

    }
    @Subcommand("list")
    public void list(Player sender) {
        var whitelist = instance.getGame().getWhitelist();
        sender.sendMessage(ChatColor.AQUA + "Whitelist: " + whitelist.toString());

    }

    @CommandCompletion("@onlineplayers")
    @Subcommand("add")
    public void add(Player sender, @Flags("other") String target) {
        var whitelist = instance.getGame().getWhitelist();
        var player = Bukkit.getPlayer(target.toString());
        if(player == null){
            sender.sendMessage(ChatColor.RED + "Player " + target + " doesn't exist.");
        }else{
            whitelist.add(player.getUniqueId().toString());
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.AQUA + player.getName().toString() + " added to the whitelist.", "uhc.configchanges.see");
        }

    }

    @CommandCompletion("@whitelist")
    @Subcommand("remove")
    public void remove(Player sender, @Flags("other") String target) {
        var whitelist = instance.getGame().getWhitelist();
        var player = Bukkit.getPlayer(target.toString());
        if(player == null || !whitelist.contains(target)){
            sender.sendMessage(ChatColor.RED + "Player " + target + " is not whitelisted.");
        }else{
            whitelist.remove(player.getUniqueId().toString());
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.AQUA + target + " removed from the whitelist.", "uhc.configchanges.see");
        }

    }

}