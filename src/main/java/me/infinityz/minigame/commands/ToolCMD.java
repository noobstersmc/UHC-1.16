package me.infinityz.minigame.commands;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@CommandPermission("host.perm")
@CommandAlias("tool|tools")
public @RequiredArgsConstructor class ToolCMD extends BaseCommand {

    private @NonNull UHC instance;

    @Subcommand("proximity")
    @CommandAlias("proximity")
    @CommandCompletion("@onlineplayers")
    public void proximityCMD(Player sender, Player target, @Optional Integer distance) {
        if (sender.getGameMode() != GameMode.SPECTATOR) {
            sender.sendMessage(ChatColor.RED + "You must be in spectator mode to do this.");
        } else {
            var dist = distance == null ? 100 : distance;
            var players = target.getLocation().getNearbyPlayers(dist,
                    player -> player.getUniqueId() != target.getUniqueId()
                            && player.getGameMode() == GameMode.SURVIVAL);

            var sb = new StringBuilder();
            sb.append(ChatColor.GREEN + "Players near " + target.getName().toString() + " in a radius of " + distance + " blocks: ");

            var iter = players.iterator();

            if (iter.hasNext()) {
                while (iter.hasNext()) {
                    sb.append(iter.next().getName().toString() + (iter.hasNext() ? ", " : "."));
                }
            } else
                sender.sendMessage(ChatColor.RED + "There are no players near " + target.getName().toString()
                        + " in a radius of " + distance + " blocks.");

        }
    }

    @Subcommand("ores")
    @CommandAlias("ores")
    @CommandCompletion("@onlineplayers")
    public void ores(Player sender, Player target) {
        if (sender.getGameMode() != GameMode.SPECTATOR) {
            sender.sendMessage(ChatColor.RED + "You must be in spectator mode to do this.");
        }else{
            var targetUhcPlayer = instance.getPlayerManager().getPlayer(target.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + target.getName().toString() + "'s mined ores.");
            sender.sendMessage(ChatColor.AQUA + "DIAMOND: " + targetUhcPlayer.getMinedDiamonds());
            sender.sendMessage(ChatColor.GOLD + "GOLD: " + targetUhcPlayer.getMinedGold());
            sender.sendMessage(ChatColor.of("#6d3b5e") + "ANCIENT DEBRIS: " + targetUhcPlayer.getMinedAncientDebris());
        }
    }

}