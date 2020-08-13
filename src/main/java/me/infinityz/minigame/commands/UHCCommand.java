package me.infinityz.minigame.commands;

import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

import com.destroystokyo.paper.Title;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
import co.aikar.taskchain.TaskChain;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunkLoadTask;
import me.infinityz.minigame.events.PlayerWinEvent;
import me.infinityz.minigame.events.TeamWinEvent;
import me.infinityz.minigame.players.PositionObject;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.tasks.ScatterTask;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

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
                    teleportLocation = ScatterTask.findScatterLocation(world,
                            (int) world.getWorldBorder().getSize() / 2);
            } else {
                teleportLocation = ScatterTask.findScatterLocation(world, (int) world.getWorldBorder().getSize() / 2);

            }

        } else {
            teleportLocation = ScatterTask.findScatterLocation(world, (int) world.getWorldBorder().getSize() / 2);

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
    @CommandCompletion("@onlineplayers")
    public void onGiveKills(CommandSender sender, @Flags("other") UHCPlayer target) {
        target.setKills(target.getKills() + 1);
        sender.sendMessage("Gave " + 1 + " kills to " + Bukkit.getOfflinePlayer(target.getUUID()).getName());

    }

    @Subcommand("status|check")
    @CommandCompletion("@onlineplayers")
    @CommandPermission("uhc.admin")
    @Syntax("<target> - UHCPlayer to recieve the kills")
    public void checkPlayerObjectStatus(CommandSender sender, @Flags("other") UHCPlayer target) {
        var player = Bukkit.getPlayer(target.getUUID());
        if (player != null && player.isOnline()) {
            target.setLastKnownHealth(player.getHealth() + player.getAbsorptionAmount());
            target.setLastKnownPosition(PositionObject.getPositionFromWorld(player.getLocation()));
            target.setLastKnownInventory(player.getInventory().getContents());
        }
        sender.sendMessage(target.toStringNoInventory());
    }

    @Subcommand("status --i")
    @CommandCompletion("@onlineplayers @onlineplayers")
    @CommandPermission("uhc.admin")
    @Syntax("<target> - UHCPlayer to recieve the kills")
    public void checkPlayerObjectStatusWithInv(CommandSender sender, @Flags("other") UHCPlayer target) {
        var player = Bukkit.getPlayer(target.getUUID());
        if (player != null && player.isOnline() && target.isAlive()) {
            target.setLastKnownHealth(player.getHealth() + player.getAbsorptionAmount());
            target.setLastKnownPosition(PositionObject.getPositionFromWorld(player.getLocation()));
            target.setLastKnownInventory(player.getInventory().getContents());
        }
        sender.sendMessage(target.toString());
    }

    @Subcommand("alpha")
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

    @Subcommand("dq")
    @CommandCompletion("@uhcPlayers")
    @CommandPermission("uhc.admin")
    public void dq(CommandSender sender, @Flags("other") UHCPlayer target) {
        // TODO: Implement the Dequalify command.

    }

    @Conditions("ingame")
    @Subcommand("test solo")
    @CommandPermission("uhc.admin")
    public void testSoloWin(CommandSender sender) {
        Bukkit.getPluginManager().callEvent(new PlayerWinEvent(
                instance.getPlayerManager().getUhcPlayerMap().values().stream().findFirst().get().getUUID()));

    }

    @Conditions("ingame")
    @Subcommand("test team")
    @CommandPermission("uhc.admin")
    public void testTeamWin(CommandSender sender) {
        Bukkit.getPluginManager().callEvent(
                new TeamWinEvent(instance.getTeamManger().getAliveTeams().stream().findFirst().get().getTeamID()));

    }

    @Subcommand("delta")
    @CommandPermission("uhc.admin")
    public void onDelta(Player sender, int size) {
        for (int i = 0; i < size; i++) {
            var task = new ChunkLoadTask(sender.getWorld(), instance.getChunkManager());
            instance.getChunkManager().getPendingChunkLoadTasks().add(task);
        }
        sender.sendActionBar("Queued up " + size + " task(s)...");

    }

    @CommandPermission("uhc.admin")
    @Subcommand("deleteworld")
    @CommandCompletion("@worlds")
    public void deleteWorldOnRestart(CommandSender sender, World world) {
        instance.getDeleteWorldQueue()[0] = world;
        Bukkit.dispatchCommand(sender, "stop");

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

    @Subcommand("beta")
    @CommandPermission("uhc.admin")
    public void onBeta(Player sender) {
        sender.getWorld().getForceLoadedChunks().stream().forEach(chunk -> chunk.setForceLoaded(false));

    }

    @Subcommand("win")
    @CommandPermission("uhc.admin")
    public void onWin(Player sender) {
        Bukkit.getOnlinePlayers().stream().filter(player -> player != sender)
                .forEach(all -> all.sendTitle(Title.builder()
                        .title(new ComponentBuilder("Victory!").bold(true).color(ChatColor.GOLD).create())
                        .subtitle(ChatColor.GREEN + sender.getName() + " has won!").stay(6 * 20).fadeIn(10)
                        .fadeOut(3 * 20).build()));
        sender.playEffect(EntityEffect.TOTEM_RESURRECT);
        sender.sendTitle(
                Title.builder().title(new ComponentBuilder("Victory!").bold(true).color(ChatColor.GOLD).create())
                        .subtitle(ChatColor.GREEN + "Congratulations " + sender.getName()).stay(6 * 20).fadeIn(10)
                        .fadeOut(3 * 20).build());

        var command1 = "summon firework_rocket %d %d %d {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:1,Explosions:[{Type:2,Flicker:0,Trail:1,Colors:[I;3887386,8073150,2651799,4312372],FadeColors:[I;3887386,11250603,4312372,15790320]}]}}}}";
        var command2 = "summon firework_rocket %d %d %d {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:2,Explosions:[{Type:3,Flicker:1,Trail:1,Colors:[I;5320730,2437522,8073150,11250603,6719955],FadeColors:[I;2437522,2651799,11250603,6719955,15790320]}]}}}}";
        var command3 = "summon firework_rocket %d %d %d {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:3,Explosions:[{Type:1,Flicker:1,Trail:1,Colors:[I;11743532,14602026,12801229,15435844],FadeColors:[I;11743532,14188952,15435844]}]}}}}";
        UHC.newChain().delay(1).sync(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command2, sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY() + 1, sender.getLocation().getBlockZ()));
        }).delay(20).sync(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command1, sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY() + 1, sender.getLocation().getBlockZ()));

        }).delay(20).sync(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command3, sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY() + 1, sender.getLocation().getBlockZ()));
        }).delay(20).sync(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command2, sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY() + 1, sender.getLocation().getBlockZ()));
        }).delay(20).sync(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command1, sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY() + 1, sender.getLocation().getBlockZ()));

        }).delay(20).sync(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command3, sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY() + 1, sender.getLocation().getBlockZ()));
        }).delay(20).sync(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command2, sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY() + 1, sender.getLocation().getBlockZ()));
        }).delay(20).sync(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command1, sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY() + 1, sender.getLocation().getBlockZ()));

        }).delay(20).sync(() -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(command3, sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY() + 1, sender.getLocation().getBlockZ()));
        }).sync(TaskChain::abort).execute();
    }

}