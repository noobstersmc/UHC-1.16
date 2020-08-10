package me.infinityz.minigame.commands;

import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunkLoadTask;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.tasks.AlphaLocationFindTask;
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
    @CommandAlias("respawn")
    @Subcommand("respawn|revive|reinstantiate")
    @CommandCompletion("@players")
    @Syntax("<uhcPlayer> &e- Player that has to be scattered")
    public void onCommand(CommandSender sender, @Conditions("dead") @Flags("other") UHCPlayer uhcPlayer) {
        uhcPlayer.setAlive(true);
        sender.sendMessage(ChatColor.of("#7ab83c") + "The player has been scattered into the world");

        var world = Bukkit.getWorlds().get(0);
        var scatterLocation = ScatterTask.findScatterLocation(world, (int) world.getWorldBorder().getSize() / 2);
        var target = Bukkit.getPlayer(uhcPlayer.getUUID());

        target.teleport(scatterLocation);
        target.setGameMode(GameMode.SURVIVAL);
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
        sender.sendMessage(ChatColor.of("#83436d") + "Kill top: ");
        var count = 1;

        for (var player : playersSorted) {
            sender.sendMessage(ChatColor.of("#cec7c8") + "" + count + ". "
                    + Bukkit.getOfflinePlayer(player.getUUID()).getName() + ": " + player.getKills());
            count++;

        }

    }

    @CommandPermission("staff.perm")
    @Subcommand("givekills")
    @Syntax("<target> - UHCPlayer to recieve the kills")
    @CommandCompletion("@players")
    public void onGiveKills(CommandSender sender, @Flags("other") UHCPlayer target) {
        target.setKills(target.getKills() + 1);
        sender.sendMessage("Gave " + 1 + " kills to " + Bukkit.getOfflinePlayer(target.getUUID()).getName());

    }

    @Subcommand("status|check")
    @CommandCompletion("@players ")
    @CommandPermission("uhc.admin")
    @Syntax("<target> - UHCPlayer to recieve the kills")
    public void checkPlayerObjectStatus(CommandSender sender, @Flags("other") UHCPlayer target) {
        sender.sendMessage(target.toString());
    }

    @Subcommand("alpha")
    @CommandCompletion("@worlds")
    @Syntax("<world> <radius> - world to scatter and radius")
    @CommandPermission("uhc.admin")
    public void alpha(CommandSender sender, World world, int radius) {
        instance.getServer().getScheduler().runTaskAsynchronously(instance, new AlphaLocationFindTask(world, radius,
                Bukkit.getOnlinePlayers().stream().map(Player::getPlayer).collect(Collectors.toList()), instance));

    }

    @Subcommand("delta")
    @CommandPermission("uhc.admin")
    public void onDelta(Player sender, int size) {
        var task = new ChunkLoadTask(sender.getWorld(), instance.getChunkManager());
        Bukkit.getScheduler().runTaskAsynchronously(instance, task);

    }

}