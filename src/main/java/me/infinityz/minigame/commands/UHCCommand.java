package me.infinityz.minigame.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunkLoadTask;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.players.PositionObject;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.scoreboard.objects.FastBoard;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

@RequiredArgsConstructor
@CommandAlias("uhc|game")
public class UHCCommand extends BaseCommand {

    private @NonNull UHC instance;

    @CommandPermission("staff.perm")
    @Conditions("ingame")
    @CommandAlias("respawn")
    @Subcommand("respawn|revive|reinstantiate")
    @CommandCompletion("@onlineplayers @respawnArgs")
    @Syntax("<uhcPlayer> &e- Player that has to be scattered")
    public void onLateScatter(CommandSender sender, @Conditions("dead") @Flags("other") UHCPlayer uhcPlayer,
            @Optional String[] args) {

        uhcPlayer.setAlive(true);
        sender.sendMessage(ChatColor.of("#7ab83c") + "The player has been scattered into the world");
        Location teleportLocation = null;
        var world = Bukkit.getWorlds().get(0);
        var target = Bukkit.getPlayer(uhcPlayer.getUUID());

        if (args != null && args.length > 0) {
            if (Arrays.stream(args).filter(it -> it.equalsIgnoreCase("--i") || it.equalsIgnoreCase("-inventory"))
                    .findAny().isPresent()) {
                target.getInventory().setContents(uhcPlayer.getLastKnownInventory());
            }
            if (Arrays.stream(args).filter(it -> it.equalsIgnoreCase("--l") || it.toLowerCase().contains("-loc"))
                    .findAny().isPresent()) {
                teleportLocation = uhcPlayer.getLastKnownPosition().toLocation();

                if (teleportLocation == null || !world.getWorldBorder().isInside(teleportLocation))
                    teleportLocation = ChunksManager.findScatterLocation(world,
                            (int) world.getWorldBorder().getSize() / 2);
            } else {
                teleportLocation = ChunksManager.findScatterLocation(world, (int) world.getWorldBorder().getSize() / 2);

            }

        } else {
            teleportLocation = ChunksManager.findScatterLocation(world, (int) world.getWorldBorder().getSize() / 2);

        }

        target.teleport(teleportLocation);
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
                var of = Bukkit.getOfflinePlayer(entry.getValue().getUUID());
                if (!of.isOnline()) {
                    sender.sendMessage(" - " + of.getName());
                }
            }
        });
    }

    @Subcommand("color")
    @CommandPermission("uhc.admin")
    public void onTeamColor(Player sender) {
        var team = instance.getTeamManger().getPlayerTeam(sender.getUniqueId());
        var nameList = team != null
                ? team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList())
                : Collections.singleton(sender.getName());
        try {
            FastBoard.removeTeam(sender);
            FastBoard.createTeam(nameList, sender);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
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
    @CommandCompletion("@onlineplayers")
    public void onGiveKills(CommandSender sender, @Flags("other") UHCPlayer target) {
        target.setKills(target.getKills() + 1);
        sender.sendMessage("Gave " + 1 + " kills to " + Bukkit.getOfflinePlayer(target.getUUID()).getName());

    }

    @Subcommand("status|check")
    @CommandCompletion("@onlineplayers @statusArgs")
    @CommandPermission("uhc.admin")
    @Syntax("<target> - UHCPlayer to recieve the kills")
    public void checkPlayerObjectStatus(CommandSender sender, @Flags("other") UHCPlayer target,
            @Optional String args[]) {
        var player = Bukkit.getPlayer(target.getUUID());
        if (player != null && player.isOnline()) {
            target.setLastKnownHealth(player.getHealth() + player.getAbsorptionAmount());
            target.setLastKnownPosition(PositionObject.getPositionFromWorld(player.getLocation()));
            target.setLastKnownInventory(player.getInventory().getContents());
        }
        // Check if optional argument --i is present
        if (args.length > 0
                && Arrays.stream(args).filter(all -> all.equalsIgnoreCase("--i") || all.toLowerCase().contains("-inv"))
                        .findFirst().isPresent()) {
            sender.sendMessage(target.toString());
            return;
        }
        sender.sendMessage(target.toStringNoInventory());
    }

    @Subcommand("locs")
    @CommandPermission("uhc.admin")
    public void alpha(CommandSender sender) {
        sender.sendMessage("Current scatter locations (" + instance.getChunkManager().getLocations().size() + "):");
        if (sender instanceof Player) {
            var player = (Player) sender;
            instance.getChunkManager().getLocations().stream().forEach(locs -> {
                player.sendMessage(new ComponentBuilder(
                        " - " + locs.getBlockX() + ", " + locs.getBlockY() + ", " + locs.getBlockZ())
                                .event(new ClickEvent(Action.RUN_COMMAND,
                                        String.format("/tp %d %d %d", locs.getBlockX(), locs.getBlockY(),
                                                locs.getBlockZ())))
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")))
                                .create());
            });
        } else {

            instance.getChunkManager().getLocations().stream().forEach(all -> {
                sender.sendMessage("- " + all.toString());
            });
        }
    }

    @Subcommand("load")
    @CommandPermission("uhc.admin")
    public void onDelta(Player sender, int size) {
        for (var i = 0; i < size; i++) {
            var task = new ChunkLoadTask(sender.getWorld(), instance.getChunkManager());
            instance.getChunkManager().getPendingChunkLoadTasks().add(task);
        }
        sender.sendActionBar("Queued up " + size + " task(s)...");

    }

    @Subcommand("pending")
    @CommandPermission("uhc.admin")
    public void onPending(Player sender) {
        sender.sendMessage("Pending chunk load tasks, -1 means not started:");
        instance.getChunkManager().getPendingChunkLoadTasks().forEach(all -> {
            sender.sendMessage(
                    all.getLocationID().toString().substring(0, 8) + " running: " + all.getChunksLeft() + " left");
        });

    }

    @Subcommand("dq")
    @CommandCompletion("@uhcPlayers")
    @CommandPermission("uhc.admin")
    public void dq(CommandSender sender, @Flags("other") UHCPlayer target) {
        // TODO: Implement the Dequalify command.

    }

    @CommandPermission("uhc.admin")
    @Subcommand("deleteworld")
    @CommandCompletion("@worlds")
    public void deleteWorldOnRestart(CommandSender sender, World world) {

    }

}