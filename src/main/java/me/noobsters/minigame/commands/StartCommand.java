package me.noobsters.minigame.commands;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.destroystokyo.paper.Title;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.taskchain.TaskChain;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.chunks.ChunkLoadTask;
import me.noobsters.minigame.enums.Stage;
import me.noobsters.minigame.events.TeleportationCompletedEvent;
import me.noobsters.minigame.gamemodes.types.UHCMeetup;
import me.noobsters.minigame.scoreboard.ScatterScoreboard;
import me.noobsters.minigame.tasks.TeleportTemporalTask;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

@RequiredArgsConstructor
@Conditions("lobby")
@CommandAlias("start")
public class StartCommand extends BaseCommand {
    private @NonNull UHC instance;

    void countDown(final int time) {
        final var title = Title.builder().title("")
                .subtitle(new ComponentBuilder("" + time).bold(true).color(ChatColor.GREEN).create()).stay(10).fadeIn(0)
                .build();
        Bukkit.getOnlinePlayers().forEach(players -> {

            players.sendTitle(title);
            if (time < 4)
                players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
        });
    }

    @CommandPermission("start.cmd")
    @Default
    public void newScatter(CommandSender sender) {

        var chunkyTasks = Bukkit.getScheduler().getActiveWorkers().stream()
                .filter(worker -> worker.getOwner().getName().toLowerCase().startsWith("chunky"))
                .collect(Collectors.toList());

        if (!chunkyTasks.isEmpty()){
            // DONT START

            Bukkit.dispatchCommand(sender, "chunky-hynix status");
            sender.sendMessage(ChatColor.RED + "World is not loaded, You can't start to load locations.");
            return;
        }

        if (instance.getGamemodeManager().isScenarioEnable(UHCMeetup.class)) {
            meetupStart();
            return;
        }

        var locs = instance.getChunkManager().getLocations();
        var tasks = instance.getChunkManager().neededLocations();
        var pending_tasks = instance.getChunkManager().getPendingChunkLoadTasks().size();
        if (locs == null || locs.isEmpty()) {
            sender.sendMessage("No locations have been found yet.");
            for (int i = 0; i < tasks; i++) {
                var task = new ChunkLoadTask(Bukkit.getWorld("world"), instance.getChunkManager());
                instance.getChunkManager().getPendingChunkLoadTasks().add(task);
            }
            sender.sendMessage("Queued up " + tasks + " task(s)...");
            return;
        }
        if (tasks > 0) {
            sender.sendMessage("Not enough locations have been found. (" + locs.size() + "/" + tasks
                    + ")\n Scheduling more locations now...");

            for (int i = 0; i < tasks; i++) {
                var task = new ChunkLoadTask(Bukkit.getWorld("world"), instance.getChunkManager());
                instance.getChunkManager().getPendingChunkLoadTasks().add(task);
            }
            sender.sendMessage("Queued up " + tasks + " task(s)...");
            return;
        }
        if (pending_tasks > 1) {
            sender.sendMessage(ChatColor.RED + "Can't start the game with " + pending_tasks + " pending tasks!");
            return;
        }

        instance.setGameStage(Stage.SCATTER);
        var count = 10;
        var chain = UHC.newChain().sync(() -> countDown(10));

        while (count-- > 1) {
            final var current = count;
            chain.delay(20).sync(() -> countDown(current));
        }

        if (instance.getTeamManger().getTeamSize() > 1) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team random");
        }

        instance.getChunkManager().getAutoChunkScheduler().cancel();
        Bukkit.broadcastMessage(ChatColor.of("#2be49c") + "Starting the teleportation task...");
        // Start Parameters
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard objectives setdisplay list health_name");

        chain.delay(20).sync(() -> {

            Bukkit.getWorlds().forEach(it -> {
                it.getWorldBorder().setSize(instance.getGame().getBorderSize());
                it.setDifficulty(Difficulty.HARD);
                it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                it.setTime(0);
            });

            new TeleportTemporalTask(instance, locs, new ArrayList<>(Bukkit.getOnlinePlayers())).runTaskTimer(instance,
                    0, 10L);

            instance.getListenerManager().unregisterListener(instance.getListenerManager().getLobby());

            instance.getScoreboardManager().purgeScoreboards();

            Bukkit.getOnlinePlayers().forEach(players -> {
                // cosas del inicio
                players.setStatistic(Statistic.TIME_SINCE_REST, 0);
                players.getInventory().clear();
                players.setExp(0.0f);
                players.setFoodLevel(26);
                players.setTotalExperience(0);
                players.setLevel(0);
                players.setGameMode(GameMode.SURVIVAL);
                players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
                ScatterScoreboard sb = new ScatterScoreboard(players);
                sb.update();
                instance.getScoreboardManager().getFastboardMap().put(players.getUniqueId().toString(), sb);
            });

            instance.getListenerManager().registerListener(instance.getListenerManager().getScatter());
        }).sync(TaskChain::abort).execute();

    }

    @CommandPermission("uhc.admin")
    @Subcommand("no-scatter")
    @CommandAlias("start-no-scatter")
    @Syntax("<ticks> - Ticks to delay the start once completed teleport.")
    public void onStartNoScatter(CommandSender sender, @Optional Integer delayTicks) {

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard objectives setdisplay list health_name");

        instance.getChunkManager().getAutoChunkScheduler().cancel();

        Bukkit.getWorlds().forEach(it -> {
            it.getWorldBorder().setSize(instance.getGame().getBorderSize());
            it.setDifficulty(Difficulty.HARD);
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            it.setTime(0);
        });

        instance.getScoreboardManager().purgeScoreboards();

        instance.getListenerManager().unregisterListener(instance.getListenerManager().getLobby());

        Bukkit.getOnlinePlayers().stream().filter(it -> it.getGameMode() != GameMode.SPECTATOR)
                .forEach(players -> instance.getPlayerManager().addCreateUHCPlayer(players.getUniqueId(), true));

        var teleportEvent = new TeleportationCompletedEvent();
        if (delayTicks != null && delayTicks > 0) {
            teleportEvent.setStartDelayTicks(delayTicks);
        }
        Bukkit.getPluginManager().callEvent(teleportEvent);

    }

    public void meetupStart() {

        instance.getGamemodeManager().getScenario(UHCMeetup.class).cancelWaitingForPlayers();
        var count = 10;
        var chain = UHC.newChain().sync(() -> countDown(10));

        while (count-- > 1) {
            final var current = count;
            chain.delay(20).sync(() -> countDown(current));
        }

        chain.sync(() -> {

            if (instance.getTeamManger().isTeamManagement()) {
                instance.getTeamManger().setTeamManagement(false);
            }

            if (instance.getTeamManger().getTeamSize() > 1) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team random");
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard objectives setdisplay list health_name");

            instance.getChunkManager().getAutoChunkScheduler().cancel();

            Bukkit.getWorlds().forEach(it -> {
                it.getWorldBorder().setSize(instance.getGame().getBorderSize());
                it.setDifficulty(Difficulty.HARD);
                it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                it.setTime(0);
            });

            instance.getScoreboardManager().purgeScoreboards();

            instance.getListenerManager().unregisterListener(instance.getListenerManager().getLobby());

            Bukkit.getOnlinePlayers().forEach(players -> {

                players.setStatistic(Statistic.TIME_SINCE_REST, 0);
                players.getInventory().clear();
                players.setExp(0.0f);
                players.setFoodLevel(26);
                players.setTotalExperience(0);
                players.setLevel(0);
                players.setGameMode(GameMode.SURVIVAL);
                players.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            });

            Bukkit.getOnlinePlayers().stream().filter(it -> it.getGameMode() != GameMode.SPECTATOR)
                    .forEach(players -> instance.getPlayerManager().addCreateUHCPlayer(players.getUniqueId(), true));

            var teleportEvent = new TeleportationCompletedEvent();
            Bukkit.getPluginManager().callEvent(teleportEvent);

        }).sync(TaskChain::abort).execute();

    }

}
