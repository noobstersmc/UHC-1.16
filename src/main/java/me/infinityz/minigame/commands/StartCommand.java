package me.infinityz.minigame.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunkLoadTask;
import me.infinityz.minigame.scoreboard.ScatterScoreboard;
import me.infinityz.minigame.tasks.TeleportTemporalTask;
import net.md_5.bungee.api.ChatColor;

@CommandPermission("staff.perm")
@CommandAlias("start")
public class StartCommand extends BaseCommand {
    private UHC instance;

    public StartCommand(UHC instance) {
        this.instance = instance;
    }

    @Default
    public void newScatter(CommandSender sender) {
        var locs = instance.getChunkManager().getLocations();
        if (locs == null || locs.isEmpty()) {
            sender.sendMessage("No locations have been found yet.");

            var tasks = Bukkit.getOnlinePlayers().size();
            for (int i = 0; i < tasks; i++) {
                var task = new ChunkLoadTask(Bukkit.getWorlds().get(0), instance.getChunkManager());
                instance.getChunkManager().getPendingChunkLoadTasks().add(task);
            }
            sender.sendMessage("Queued up " + tasks + " task(s)...");
            return;
        }
        if (locs.size() < Bukkit.getOnlinePlayers().size()) {
            sender.sendMessage("Not enough locations have been found. (" + locs.size() + "/"
                    + Bukkit.getOnlinePlayers().size() + ")\n Scheduling more locations now...");

            var tasks = Bukkit.getOnlinePlayers().size() - locs.size();
            for (int i = 0; i < tasks; i++) {
                var task = new ChunkLoadTask(Bukkit.getWorlds().get(0), instance.getChunkManager());
                instance.getChunkManager().getPendingChunkLoadTasks().add(task);
            }
            sender.sendMessage("Queued up " + tasks + " task(s)...");
            return;
        }
        Bukkit.broadcastMessage(ChatColor.of("#2be49c") + "Starting the teleportation task...");
        // Start Parameters
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard objectives setdisplay list health_name");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add @a");

        Bukkit.getWorlds().forEach(it -> {
            it.getWorldBorder().setSize(4001);
            it.setDifficulty(Difficulty.HARD);
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            it.setTime(0);
        });

        new TeleportTemporalTask(instance, locs, new ArrayList<>(Bukkit.getOnlinePlayers())).runTaskTimer(instance, 20L,
                20L);

        instance.getListenerManager().unregisterListener(instance.getListenerManager().getLobby());

        instance.getScoreboardManager().purgeScoreboards();

        Bukkit.getOnlinePlayers().forEach(players -> {
            // cosas del inicio
            players.getInventory().clear();
            players.setGameMode(GameMode.SURVIVAL);
            ScatterScoreboard sb = new ScatterScoreboard(players);
            sb.update();
            instance.getScoreboardManager().getFastboardMap().put(players.getUniqueId().toString(), sb);
        });

        instance.getListenerManager().registerListener(instance.getListenerManager().getScatter());
    }

}
