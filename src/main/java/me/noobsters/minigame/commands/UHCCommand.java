package me.noobsters.minigame.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import com.destroystokyo.paper.Title;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.chunks.ChunkLoadTask;
import me.noobsters.minigame.chunks.ChunksManager;
import me.noobsters.minigame.condor.CondorAPI;
import me.noobsters.minigame.events.PlayerJoinedLateEvent;
import me.noobsters.minigame.game.Game;
import me.noobsters.minigame.game.Game.GameInfo;
import me.noobsters.minigame.game.border.FortniteBorder;
import me.noobsters.minigame.players.PositionObject;
import me.noobsters.minigame.players.UHCPlayer;
import me.noobsters.minigame.scoreboard.objects.FastBoard;
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
    private String permissionDebug = "uhc.configchanges.see";

    void countDown(final int time) {
        final var title = Title.builder().title("")
                .subtitle(new ComponentBuilder("" + time).bold(true).color(ChatColor.GREEN).create()).stay(20).fadeIn(0)
                .build();
        Bukkit.getOnlinePlayers().forEach(players -> {

            players.sendTitle(title);
            if (time < 4)
                players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
        });
    }

    @CommandAlias("refresh")
    @CommandPermission("uhc.refresh")
    public void refreshCommands(Player sender) throws IOException {
        sender.updateCommands();
        // instance.restartSystem();
        CondorAPI.delete("6QR3W05K3F", instance.getGame().getGameID().toString());
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    private static class ReviveArgs {
        boolean withItems = false;
        boolean withLocation = false;

        public static ReviveArgs from(UHCPlayer uhcPlayer, String[] args) {
            var reviveArgs = of(false, false);
            if (args == null || args.length == 0)
                return reviveArgs;

            for (var arg : args) {
                var lowerArg = arg.toLowerCase();
                if (lowerArg.startsWith("--i") || lowerArg.startsWith("-inv"))
                    reviveArgs.setWithItems(true);
                else if (lowerArg.startsWith("--l") || lowerArg.startsWith("-loc"))
                    reviveArgs.setWithLocation(true);
            }

            if (reviveArgs.isWithItems() && uhcPlayer.getLastKnownInventory() == null)
                reviveArgs.setWithItems(false);

            if (reviveArgs.isWithLocation() && uhcPlayer.getLastKnownPosition() == null)
                reviveArgs.setWithLocation(false);

            return reviveArgs;
        }
    }

    @CommandPermission("respawn.cmd")
    @Conditions("ingame")
    @CommandAlias("respawn")
    @Subcommand("respawn|revive|reinstantiate")
    @CommandCompletion("@onlineplayers @respawnArgs")
    @Syntax("<uhcPlayer> &e- Player that has to be scattered")
    public void onLateScatter(CommandSender sender, @Conditions("dead") @Flags("other") UHCPlayer uhcPlayer,
            @Optional String[] args) {

        var world = Bukkit.getWorld("world");
        var target = uhcPlayer.getPlayer();
        var reviveArgs = ReviveArgs.from(uhcPlayer, args);
        
        target.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);

        Bukkit.getPluginManager().callEvent(PlayerJoinedLateEvent.of(target));

        target.teleportAsync(reviveArgs.isWithLocation() ? uhcPlayer.getLastKnownPosition().toLocation()
                : ChunksManager.findLateScatterLocation(world))
                .thenAccept(result -> target.sendMessage("You've been scattered into the world by an admin."));
        uhcPlayer.setAlive(true);

        if (reviveArgs.isWithItems())
            target.getInventory().setContents(uhcPlayer.getLastKnownInventory());

        target.setGameMode(GameMode.SURVIVAL);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + ChatColor.of("#7ab83c") + target.getName().toString() + " has been scattered into the world.", permissionDebug);
    }

    @CommandPermission("staff.perm")
    @Conditions("ingame")
    @Subcommand("alive")
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

    @Subcommand("autostart")
    @CommandPermission("uhc.auto.start.players")
    public void onAutoStartChange(CommandSender sender, @Default("Players to auto-start") Integer players) {
        instance.getGame().setAutoStart(players);
        sender.sendMessage("Auto start changed to " + players);

    }

    @Subcommand("chunkradius")
    @CommandPermission("uhc.admin")
    public void changeChunkLoadRadius(CommandSender sender, Integer chunkr) {
        ChunkLoadTask.setRadius(chunkr);
        sender.sendMessage("Chunk Radius load changed to: " + chunkr);

    }

    @Subcommand("color")
    @CommandPermission("uhc.admin")
    public void onTeamColor(Player sender, @Optional Player target, Integer index, String str) {
        var team = instance.getTeamManger().getPlayerTeam(sender.getUniqueId());
        var nameList = team != null
                ? team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList())
                : Collections.singleton(sender.getName());
        try {
            FastBoard.removeTeam(sender);
            FastBoard.createTeam(nameList, sender, ChatColor.translateAlternateColorCodes('&', str), index);
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

    @CommandPermission("uhc.scoreboard.change")
    @Subcommand("title")
    @CommandCompletion("@chatcolors @players")
    @Syntax("<title> - New title string")
    public void changeScoreboardTitle(CommandSender sender, final String newTitle) {
        var coloredTitle = ChatColor.translateAlternateColorCodes('&', newTitle);

        sender.sendMessage("Changing scoreboard title to: " + coloredTitle);
        Game.setScoreboardTitle(coloredTitle);

        instance.getScoreboardManager().getFastboardMap().values().forEach(all -> all.updateTitle(coloredTitle));
    }

    @CommandPermission("uhc.scoreboard.change")
    @Subcommand("score")
    public void changeScoreboardColor(CommandSender sender, final String score) {
        var newTittle = "";
        switch (score) {
            case "UHC": {
                newTittle = ChatColor.AQUA + "" + ChatColor.BOLD + "UHC";
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game title " + newTittle);
                Game.setScoreColors(ChatColor.of("#0ca2d4") + "");
            }
                break;
            case "RUN": {
                newTittle = ChatColor.GOLD + "" + ChatColor.BOLD + "UHC RUN";
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game title " + newTittle);
                Game.setScoreColors(ChatColor.of("#FFFF55") + "");
            }
                break;
            case "GAMES": {
                newTittle = ChatColor.of("#e672f8") + "" + ChatColor.BOLD + "Community UHC";
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game title " + newTittle);
                Game.setScoreColors(ChatColor.of("#c44faf") + "");
            }
                break;
            case "VANDAL": {
                newTittle = ChatColor.of("#ef9e48") + "" + ChatColor.BOLD + "UHC VANDÁLICO";
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game title " + newTittle);
                Game.setScoreColors(ChatColor.of("#00d0db") + "");
            }
                break;
            default: {

            }
                break;

        }
        sender.sendMessage("Changing scoreboard design to " + score);

    }

    @CommandPermission("staff.perm")
    @Subcommand("end")
    @Syntax("<bol> - Set end to enabled or disabled")
    @CommandCompletion("@bool")
    public void onEnd(CommandSender sender, @Optional Boolean bool) {
        if (bool != null) {
            instance.getGame().setEnd(bool);
        } else {
            instance.getGame().setEnd(!instance.getGame().isEnd());
        }
        sender.sendMessage("End has been set to " + instance.getGame().isEnd());

    }

    @Subcommand("status|check")
    @CommandCompletion("@onlineplayers @statusArgs")
    @CommandPermission("uhc.admin")
    @Syntax("<target> - UHCPlayer to recieve the kills")
    public void checkPlayerObjectStatus(CommandSender sender, @Flags("other") UHCPlayer target,
            @Optional String[] args) {
        var player = Bukkit.getPlayer(target.getUUID());
        if (player != null && player.isOnline()) {
            target.setLastKnownHealth(player.getHealth() + player.getAbsorptionAmount());
            target.setLastKnownPosition(PositionObject.getPositionFromWorld(player.getLocation()));
            target.setLastKnownInventory(player.getInventory().getContents());
        }
        // Check if optional argument --i is present
        if (args.length > 0 && Arrays.stream(args)
                .anyMatch(all -> all.equalsIgnoreCase("--i") || all.toLowerCase().contains("-inv"))) {
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
            instance.getChunkManager().getLocations().stream().forEach(locs -> player.sendMessage(
                    new ComponentBuilder(" - " + locs.getBlockX() + ", " + locs.getBlockY() + ", " + locs.getBlockZ())
                            .event(new ClickEvent(Action.RUN_COMMAND,
                                    String.format("/tp %d %d %d", locs.getBlockX(), locs.getBlockY(),
                                            locs.getBlockZ())))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")))
                            .create()));
        } else {

            instance.getChunkManager().getLocations().stream()
                    .forEach(all -> sender.sendMessage("- " + all.toString()));
        }
    }

    @Subcommand("load")
    @CommandPermission("staff.perm")
    public void onDelta(Player sender, int size) {
        for (var i = 0; i < size; i++) {
            var task = new ChunkLoadTask(Bukkit.getWorld("world"), instance.getChunkManager());
            instance.getChunkManager().getPendingChunkLoadTasks().add(task);
        }
        sender.sendActionBar("Queued up " + size + " task(s)...");

    }

    @Subcommand("unload")
    @CommandPermission("uhc.admin")
    public void unloadForceLoaded(CommandSender sender) {
        sender.sendMessage("Unloaded all chunks");
        Bukkit.getWorld("world").getForceLoadedChunks().forEach(all -> all.setForceLoaded(false));

    }

    @Subcommand("pending")
    @CommandPermission("uhc.admin")
    public void onPending(Player sender) {
        sender.sendMessage("Pending chunk load tasks, -1 means not started:");
        instance.getChunkManager().getPendingChunkLoadTasks().forEach(all -> sender.sendMessage(
                all.getLocationID().toString().substring(0, 8) + " running: " + all.getChunksLeft() + " left"));

    }

    @Subcommand("addtime")
    @CommandPermission("uhc.admin")
    public void onAddTime(CommandSender sender, Integer seconds) {
        sender.sendMessage("Adding " + seconds + " to the game...");
        instance.getGame().setStartTime(instance.getGame().getStartTime() - (seconds * 1000));

    }

    @Subcommand("fortnite")
    @CommandCompletion("@worlds @range:0-200 @range:0-200 @range:100-25 @range:30-120")
    @CommandPermission("uhc.admin")
    public void onFortniteBorder(CommandSender sender, World world, Integer newX, Integer newZ, Integer newRadius,
            Integer seconds) {
        var border = new FortniteBorder(world, instance);

        sender.sendMessage(String.format("[ForniteBorder] Moving %s border to (%d, %d) radius %d in %d second(s).",
                world.getName(), newX, newZ, newRadius, seconds));
        border.moveWorldBorder(newX, newZ, newRadius, seconds);

    }

    @CommandPermission("staff.perm")
    @Subcommand("anti-mining")
    @CommandAlias("anti-mining")
    @CommandCompletion("@bool")
    public void antimining(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isAntiMining();

        instance.getGame().setAntiMining(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "Anti Mining has been set to: " + bool, permissionDebug);
    }

    @CommandPermission("admin.perm")
    @Subcommand("combatlog")
    @CommandAlias("combatlog")
    @CommandCompletion("@bool")
    public void combatLog(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !instance.getGame().isCombatLog();

        instance.getGame().setCombatLog(bool);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.YELLOW + "CombatLog has been set to: " + bool, permissionDebug);
    }

    @CommandPermission("staff.perm")
    @Subcommand("official")
    @CommandAlias("official")
    @CommandCompletion("@bool")
    public void officialGame(CommandSender sender, @Optional Boolean bool) {
        if (bool == null)
            bool = !(instance.getGame().getGameInfo() == GameInfo.OFFICIAL);

        if(bool){
            instance.getGame().setGameInfo(GameInfo.OFFICIAL);
        }else{
            instance.getGame().setGameInfo(GameInfo.COMMUNITY);
        }
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        Bukkit.broadcast(senderName + ChatColor.of("#c76905") + "Official UHC Competitive game has been set to: " + bool, permissionDebug);
    }

    @Subcommand("setHost")
    @CommandCompletion("@onlineplayers")
    @CommandPermission("uhc.admin")
    public void changeHost(CommandSender sender, String newHost) {
        instance.getGame().setHostname(newHost);
        Bukkit.broadcastMessage(ChatColor.GREEN + "New host: " + sender.getName());

    }

    @Subcommand("autodestroy|autodestruction|")
    @CommandAlias("autodestroy|destruction")
    @CommandPermission("uhc.destroy.cmd")
    public void autoDestroy(CommandSender sender) {
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        if (instance.getGame().isAutoDestruction()) {
            instance.getGame().setAutoDestruction(false);
            Bukkit.broadcast(senderName + ChatColor.YELLOW + "Auto destruction has been disabled.",
            permissionDebug);
        } else {
            instance.getGame().setAutoDestruction(true);
            Bukkit.broadcast(senderName + ChatColor.YELLOW + "Auto destruction has been enabled.",
            permissionDebug);
        }

    }

    @Subcommand("claim")
    @CommandPermission("staff.perm")
    public void claimHost(CommandSender sender) {
        instance.getGame().setHostname(sender.getName());
        Bukkit.broadcastMessage(ChatColor.GREEN + "New host: " + sender.getName());
    }

    @Data
    public class Rectangle {
        double minX;
        double maxX;
        double minZ;
        double maxZ;

        public Rectangle(WorldBorder border) {
            double size = border.getSize();
            double sizeHalved = size / 2;
            double originX = border.getCenter().getX();
            double originZ = border.getCenter().getZ();

            this.minX = originX - sizeHalved;
            this.maxX = originX + sizeHalved;
            this.minZ = originZ - sizeHalved;
            this.maxZ = originZ + sizeHalved;
        }

        public double distance(Location point) {
            var dX = Math.max(minX - point.getX(), point.getX() - maxX);
            dX = Math.max(dX, 0);
            var dZ = Math.max(minZ - point.getX(), point.getX() - maxZ);
            dZ = Math.max(dZ, 0);
            // Hypothenuse
            var distance = Math.sqrt(dX * dX + dZ * dZ);
            // No hypothenuse, calculate distance to nearest point
            if (distance == 0) {
                distance = Math.min(Math.min(point.getX() - minX, maxX - point.getX()),
                        Math.min(point.getZ() - minZ, maxZ - point.getZ()));
            }
            return distance;
        }

    }

}