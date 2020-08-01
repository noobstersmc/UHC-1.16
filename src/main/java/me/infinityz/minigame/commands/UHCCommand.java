package me.infinityz.minigame.commands;

import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.tasks.ScatterTask;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("uhc|game")
public class UHCCommand extends BaseCommand {

    UHC instance;

    public UHCCommand(UHC instance) {
        this.instance = instance;
    }


    @CommandPermission("staff.perm")
    @Conditions("ingame")
    @Subcommand("respawn|revive|reinstantiate")
    @CommandCompletion("@players")
    @Syntax("<target> &e- Player that has to be scattered")
    public void onCommand(CommandSender sender, OnlinePlayer target) {
        if (target == null)
            return;
        var player = target.getPlayer();
        var uhcPlayer = instance.getPlayerManager().getPlayer(player.getUniqueId());

        if (uhcPlayer.isAlive()) {
            sender.sendMessage("Player is still alive.");
            return;
        }
        uhcPlayer.setAlive(true);

        sender.sendMessage("The player has been scattered into the world");

        var world = Bukkit.getWorlds().get(0);
        var scatterLocation = ScatterTask.findScatterLocation(world, (int) world.getWorldBorder().getSize() / 2);

        player.teleport(scatterLocation);
        player.setGameMode(GameMode.SURVIVAL);
    }

    @CommandPermission("staff.perm")
    @Conditions("ingame")
    @Subcommand("alive")
    @CommandAlias("alive")
    public void alive(CommandSender sender) {
        sender.sendMessage("Alive offline players: ");
        instance.getPlayerManager().getUhcPlayerMap().entrySet().forEach(entry -> {
            if (entry.getValue().isAlive()) {
                var of = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey()));
                if (!of.isOnline()) {
                    sender.sendMessage(" - " + of.getName());
                }
            }
        });
    }

    @Conditions("ingame")
    @CommandAlias("kt|killtop")
    @Subcommand("killtop|kt")
    public void killTop(CommandSender sender) {
        var players = instance.getPlayerManager().getUhcPlayerMap().values().parallelStream();
        // Use sorted with a comparator on player kills to sort ascending.
        var playersSorted = players.filter(player -> player.getKills() > 0)
                .sorted(Comparator.comparingInt(UHCPlayer::getKills).reversed()).limit(10).collect(Collectors.toList());

        if (playersSorted.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There isn't a kill leaderboard yet.");
            return;
        }
        sender.sendMessage("Kill top: ");
        var count = 1;

        for (var player : playersSorted) {
            sender.sendMessage(
                    count + ". " + Bukkit.getOfflinePlayer(player.getUUID()).getName() + ": " + player.getKills());
            count++;

        }

    }

    @CommandPermission("staff.perm")
    @Subcommand("givekills")
    @Syntax("<target> - UHCPlayer to recieve the kills")
    @CommandCompletion("@players")
    public void onGiveKills(CommandSender sender, OnlinePlayer target, @Default("1") Integer amount) {
        if (target == null)
            return;

        var player = target.getPlayer();
        var uhcPlayer = instance.getPlayerManager().getPlayer(player.getUniqueId());

        if (uhcPlayer == null) {
            sender.sendMessage("Player is somehow null");
            return;
        }

        uhcPlayer.setKills(uhcPlayer.getKills() + amount);
        sender.sendMessage("Gave " + amount + " kills to " + player.getName());

    }

}