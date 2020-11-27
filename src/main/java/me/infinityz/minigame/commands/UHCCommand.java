package me.infinityz.minigame.commands;

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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BannerMeta;

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
import co.aikar.taskchain.TaskChain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunkLoadTask;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.events.NetherDisabledEvent;
import me.infinityz.minigame.events.PlayerJoinedLateEvent;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.game.border.FortniteBorder;
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

    /*
     * var winnersTitle = Title.builder() .title(new
     * ComponentBuilder("You Win!").bold(true).color(ChatColor.GOLD).create())
     * .subtitle(ChatColor.GREEN + "Congratulations " +
     * winnersName.toString()).stay(6 * 20).fadeIn(10) .fadeOut(3 * 20).build();
     */

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

    @CommandPermission("uhc.debug")
    @Subcommand("debug")
    public void onC(CommandSender sender) {
        var count = 10;
        var chain = UHC.newChain().sync(() -> countDown(20));

        while (count-- > 1) {
            final var current = count;
            chain.delay(20).sync(() -> countDown(current));
        }
        chain.delay(20).sync(TaskChain::abort).execute();
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

    @CommandPermission("staff.perm")
    @Conditions("ingame")
    @CommandAlias("respawn")
    @Subcommand("respawn|revive|reinstantiate")
    @CommandCompletion("@onlineplayers @respawnArgs")
    @Syntax("<uhcPlayer> &e- Player that has to be scattered")
    public void onLateScatter(CommandSender sender, @Conditions("dead") @Flags("other") UHCPlayer uhcPlayer,
            @Optional String[] args) {

        var world = Bukkit.getWorlds().get(0);
        var target = uhcPlayer.getPlayer();
        var reviveArgs = ReviveArgs.from(uhcPlayer, args);

        Bukkit.getPluginManager().callEvent(PlayerJoinedLateEvent.of(target));

        target.teleportAsync(reviveArgs.isWithLocation() ? uhcPlayer.getLastKnownPosition().toLocation()
                : ChunksManager.findLateScatterLocation(world))
                .thenAccept(result -> target.sendMessage("You've been scattered into the world by an admin"));

        uhcPlayer.setAlive(true);

        if (reviveArgs.isWithItems())
            target.getInventory().setContents(uhcPlayer.getLastKnownInventory());

        target.setGameMode(GameMode.SURVIVAL);
        sender.sendMessage(ChatColor.of("#7ab83c") + "The player has been scattered into the world");
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

    @Subcommand("destroy now")
    @CommandPermission("condor.destroy")
    public void teleportTest(CommandSender sender) {
        instance.getCondorManager().getJedis().publish("destroy",
                String.format("{%s: %s}", "\"ip\"", "\"" + instance.getGame().getIp() + " \""));

    }

    @Subcommand("ocs")
    @CommandPermission("UHC.CRACK")
    public void openChunkSystem(Player player) {
        var mainHandItem = player.getInventory().getItemInMainHand();
        if (mainHandItem != null) {
            if (mainHandItem.getItemMeta() instanceof BannerMeta) {
                var bannerMeta = (BannerMeta) mainHandItem.getItemMeta();
                var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                if (team != null) {
                    team.setTeamShieldPattern(bannerMeta.getPatterns());
                    team.sendTeamMessage("Team banner has been changed.");
                }

            }

        }

    }

    @Subcommand("seed")
    @CommandPermission("uhc.admin")
    public void onChangeSeed(CommandSender sender, @Default("") String seed) {
        instance.changeSeed(seed);
        sender.sendMessage("Attempting to change seed to: " + seed);

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

    @CommandPermission("staff.perm")
    @Subcommand("nether")
    @Syntax("<bol> - Set nether to enabled or disabled")
    @CommandCompletion("@bool")
    public void onNetherOff(CommandSender sender, @Optional Boolean bool) {
        if (bool != null) {
            instance.getGame().setNether(bool);
        } else {
            instance.getGame().setNether(!instance.getGame().isNether());
        }
        sender.sendMessage("Nether has been set to " + instance.getGame().isNether());
        if (!instance.getGame().isNether()) {
            // Call Event
            Bukkit.getPluginManager().callEvent(new NetherDisabledEvent());
        }
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
                newTittle = ChatColor.of("#A40A0A") + "" + ChatColor.BOLD + "UHC";
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game title " + newTittle);
                Game.setScoreColors(ChatColor.of("#82abba") + "");
            }
                break;
            case "RUN": {
                newTittle = ChatColor.GOLD + "" + ChatColor.BOLD + "UHC RUN";
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game title " + newTittle);
                Game.setScoreColors(ChatColor.of("#FFFF55") + "");
            }
                break;
            case "CLOROX": {
                newTittle = ChatColor.of("#29f4ff") + "" + ChatColor.BOLD + "UHC CLOROX";
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "game title " + newTittle);
                Game.setScoreColors(ChatColor.of("#0ca2d4") + "");
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

    @CommandPermission("uhc.scoreboard.change")
    @Subcommand("selector")
    public void getSelector(CommandSender sender, String input) {

    }

    @CommandPermission("uhc.tab.change")
    @Subcommand("header")
    @CommandCompletion("@chatcolors @players")
    @Syntax("<header> - New header string")
    public void changeTablistHeader(CommandSender sender, final String newHeader) {
        var coloredHeader = ChatColor.translateAlternateColorCodes('&', newHeader).replace("\\n", "\n");

        sender.sendMessage("Changing tablist header to: " + coloredHeader);
        Game.setTablistHeader(coloredHeader);

        Bukkit.getOnlinePlayers().forEach(all -> all.setPlayerListHeader(coloredHeader));
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
            var task = new ChunkLoadTask(sender.getWorld(), instance.getChunkManager());
            instance.getChunkManager().getPendingChunkLoadTasks().add(task);
        }
        sender.sendActionBar("Queued up " + size + " task(s)...");

    }

    @Subcommand("unload")
    @CommandPermission("uhc.admin")
    public void unloadForceLoaded(CommandSender sender) {
        sender.sendMessage("Unloaded all chunks");
        Bukkit.getWorlds().get(0).getForceLoadedChunks().forEach(all -> all.setForceLoaded(false));

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

    @Subcommand("setHost")
    @CommandCompletion("@onlineplayers")
    @CommandPermission("uhc.admin")
    public void changeHost(CommandSender sender, String newHost) {
        instance.getGame().setHostname(newHost);
        sender.sendMessage("New host = " + newHost);

    }

    @Subcommand("claim")
    @CommandPermission("staff.perm")
    public void claimHost(CommandSender sender) {
        instance.getGame().setHostname(sender.getName());
        sender.sendMessage("New host = " + sender.getName());
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