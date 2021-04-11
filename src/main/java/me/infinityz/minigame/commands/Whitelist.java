package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.utils.PlayerDBUtil;

@CommandPermission("host.perm")
@CommandAlias("wl|whitelist")
public class Whitelist extends BaseCommand {

    private UHC instance;
    private String permissionDebug = "uhc.configchanges.see";

    public Whitelist(UHC instance){
        this.instance = instance;

        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("whitelist", c -> {
            return instance.getGame().getWhitelist().keySet();

        });

    }

    @Default
    @CommandCompletion("@bool")
    public void enableDisable(CommandSender sender, Boolean bool) {
        instance.getGame().setWhitelistEnabled(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.AQUA + "Whitelist " + bool, permissionDebug);

    }

    @Subcommand("info")
    public void defaultCMD(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "Whitelist is " + instance.getGame().isWhitelistEnabled());
        sender.sendMessage(ChatColor.AQUA + "   /whitelist true/false");
        sender.sendMessage(ChatColor.AQUA + "   /whitelist list");
        sender.sendMessage(ChatColor.AQUA + "   /whitelist add [nickname]");
        sender.sendMessage(ChatColor.AQUA + "   /whitelist remove [nickname]");
        sender.sendMessage("");

    }

    @Subcommand("list")
    public void list(CommandSender sender) {
        var whitelist = instance.getGame().getWhitelist();
        sender.sendMessage(ChatColor.AQUA + "Whitelist: " + whitelist.keySet().toString());

    }

    @CommandCompletion("@onlineplayers")
    @Subcommand("add")
    public void add(CommandSender sender, @Flags("other") String target){
        var whitelist = instance.getGame().getWhitelist();

        try {
            PlayerDBUtil.getPlayerObjectAsync(target).thenAccept(player -> {
                if(player == null){
                    sender.sendMessage(ChatColor.RED + "Player " + target + " doesn't exist.");
                    return;
                }
                var username = player.get("username").getAsString();
                var uuid = player.get("id").getAsString();
        
                whitelist.put(username, uuid);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                Bukkit.broadcast(senderName + ChatColor.AQUA + username + " added to the whitelist.", permissionDebug);
            });

        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "An error ocurred.");
        }

    }

    @CommandCompletion("@whitelist")
    @Subcommand("remove")
    public void remove(CommandSender sender, @Flags("other") String target) {
        var whitelist = instance.getGame().getWhitelist();
        if(!whitelist.containsKey(target)){
            sender.sendMessage(ChatColor.RED + "Player " + target + " is not whitelisted.");
        }else{
            whitelist.remove(target);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            Bukkit.broadcast(senderName + ChatColor.AQUA + target + " removed from the whitelist.", permissionDebug);
        }

    }

    @Subcommand("clear")
    public void clear(CommandSender sender) {
        var whitelist = instance.getGame().getWhitelist();
        whitelist.clear();
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.AQUA + "Whitelist cleared.", permissionDebug);
        
    }

    @Subcommand("all")
    public void all(CommandSender sender) {
        var whitelist = instance.getGame().getWhitelist();
        Bukkit.getOnlinePlayers().forEach(player ->{
            var name = player.getName().toString();
            var uuid = player.getUniqueId().toString();
            whitelist.put(name, uuid);
        });
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.AQUA + "All players added to the whitelist.", permissionDebug);
        
    }



}