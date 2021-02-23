package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.players.UHCPlayer;
import net.md_5.bungee.api.ChatColor;

@CommandPermission("host.perm")
@CommandAlias("tool|tools")
public @RequiredArgsConstructor class ToolCMD extends BaseCommand {

    private @NonNull UHC instance;

    @Subcommand("proximity")
    @CommandAlias("proximity")
    @CommandCompletion("@onlineplayers")
    public void proximityCMD(Player sender, @Flags("other") Player target, @Optional Integer distance) {
        if (sender.getGameMode() != GameMode.SPECTATOR && !sender.hasPermission("uhc.admin")) {
            sender.sendMessage(ChatColor.RED + "You must be in spectator mode to do this.");
        } else {
            var dist = distance == null ? 100 : distance;
            var players = target.getPlayer().getLocation().getNearbyPlayers(dist,
                    player -> player.getUniqueId() != target.getUniqueId()
                            && player.getGameMode() == GameMode.SURVIVAL);

            var sb = new StringBuilder();
            sb.append(ChatColor.GREEN + "Players near " + target.getPlayer().getName() + " in a radius of " + dist + " blocks: " + ChatColor.YELLOW);

            if (!players.isEmpty()) {
                players.forEach(player ->{
                    sb.append("" + player.getName().toString() + " ");
                });
                sender.sendMessage(sb.toString());
            } else
                sender.sendMessage(ChatColor.RED + "There are no players near " + target.getPlayer().getName().toString()
                        + " in a radius of " + dist + " blocks.");

        }
    }

    @Subcommand("ores")
    @CommandAlias("ores")
    @CommandCompletion("@onlineplayers")
    public void ores(Player sender, @Flags("other") Player target) {
        UHCPlayer uhcPlayer = instance.getPlayerManager().getPlayer(target.getUniqueId());
        if (sender.getGameMode() != GameMode.SPECTATOR && !sender.hasPermission("uhc.admin")) {
            sender.sendMessage(ChatColor.RED + "You must be in spectator mode to do this.");
        }else{
            sender.sendMessage(ChatColor.GRAY + target.getName().toString() + "'s mined ores: " 
            + ChatColor.AQUA + "DIAMOND: " + uhcPlayer.getMinedDiamonds()
            + ChatColor.GOLD + " GOLD: " + uhcPlayer.getMinedGold()
            + ChatColor.of("#95562F") + " ANCIENT DEBRIS: " + uhcPlayer.getMinedAncientDebris());
        }
    }

    @Subcommand("specinfo")
    @CommandAlias("specinfo")
    @CommandCompletion("@onlineplayers")
    public void specInfo(Player sender, @Flags("other") Player target) {
        UHCPlayer uhcPlayer = instance.getPlayerManager().getPlayer(target.getUniqueId());
        uhcPlayer.setSpecInfo(!uhcPlayer.isSpecInfo());
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + target.getName() + "'s SpecInfo has been set to " + uhcPlayer.isSpecInfo(), "uhc.configchanges.see");


    }

    @Subcommand("spme")
    @CommandAlias("spme")
    public void spme(Player sender) {
        var playerManager = instance.getPlayerManager();
        var uhcPlayer = playerManager.getPlayer(sender.getUniqueId());
        Bukkit.dispatchCommand(sender, "specinfo " + sender.getName().toString());
        uhcPlayer.getPlayer().damage(100);
        

    }

}