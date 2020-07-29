package me.infinityz.minigame.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.tasks.ScatterTask;

@CommandPermission("staff.perm")
@CommandAlias("uhc|game")
public class UHCCommand extends BaseCommand {

    UHC instance;

    public UHCCommand(UHC instance) {
        this.instance = instance;
    }

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

    @Conditions("ingame")
    @Subcommand("alive")
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

}