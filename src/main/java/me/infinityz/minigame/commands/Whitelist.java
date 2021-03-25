package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@CommandPermission("pff.perm")
@CommandAlias("wwl|wwhitelist")
public class Whitelist extends BaseCommand {

    private UHC instance;
    private String permissionDebug = "uhc.configchanges.see";

    public Whitelist(UHC instance){
        this.instance = instance;

        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("whitelist", c -> {
            return instance.getGame().getWhitelist().values();

        });

    }

    @Default
    public void defaultCMD(Player sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "Whitelist is " + instance.getGame().isWhitelistEnabled());
        sender.sendMessage(ChatColor.AQUA + "   /whitelist true/false");
        sender.sendMessage(ChatColor.AQUA + "   /whitelist list");
        sender.sendMessage(ChatColor.AQUA + "   /whitelist add [nickname]");
        sender.sendMessage(ChatColor.AQUA + "   /whitelist remove [nickname]");
        sender.sendMessage("");

    }

    @CommandCompletion("@bool")
    public void enableDisable(Player sender, Boolean bool) {
        instance.getGame().setWhitelistEnabled(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.AQUA + "Whitelist " + bool, permissionDebug);

    }

    @Subcommand("list")
    public void list(Player sender) {
        var whitelist = instance.getGame().getWhitelist();
        sender.sendMessage(ChatColor.AQUA + "Whitelist: " + whitelist.values().toString());

    }

    @CommandCompletion("@onlineplayers")
    @Subcommand("add")
    public void add(Player sender, @Flags("other") String target) {
        var whitelist = instance.getGame().getWhitelist();
        var player = Bukkit.getPlayer(target);
        if(player == null){
            sender.sendMessage(ChatColor.RED + "Player " + target + " doesn't exist.");
            return;
        }
        
        whitelist.put(player.getUniqueId().toString(), player.getName().toString());
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.AQUA + player.getName().toString() + " added to the whitelist.", permissionDebug);

    }

    @CommandCompletion("@whitelist")
    @Subcommand("remove")
    public void remove(Player sender, @Flags("other") String target) {
        var whitelist = instance.getGame().getWhitelist();
        var player = Bukkit.getPlayer(target);
        if(player == null || !whitelist.containsValue(target)){
            sender.sendMessage(ChatColor.RED + "Player " + target + " is not whitelisted.");
        }else{
            whitelist.remove(player.getUniqueId().toString());
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.AQUA + target + " removed from the whitelist.", permissionDebug);
        }

    }



}