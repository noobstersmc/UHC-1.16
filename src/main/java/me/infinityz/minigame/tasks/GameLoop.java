package me.infinityz.minigame.tasks;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.listeners.GlobalListener;
import me.infinityz.minigame.scoreboard.objects.UpdateObject;
import net.md_5.bungee.api.ChatColor;

public class GameLoop extends BukkitRunnable {
    private boolean borderShrink = false;
    private World mainWorld = Bukkit.getWorlds().get(0);
    private UHC instance;

    public GameLoop(UHC instance) {
        this.instance = instance;
    }

    // Move from
    @Override
    public void run() {
        GlobalListener.time++;
        if (!borderShrink && mainWorld.getWorldBorder().getSize() <= 1000) {
            borderShrink = true;
            Bukkit.getScheduler().runTask(instance, () -> {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.getWorld().getEnvironment() == Environment.NETHER) {
                        player.teleport(ChunksManager.findScatterLocation(mainWorld, 499));
                    }
                });
            });
        }

        switch (GlobalListener.time) {
            case 300: {
                // AVISO DE FINAL HEAL 5min left
                Bukkit.getOnlinePlayers().forEach(all -> {
                    all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                });
                Bukkit.broadcastMessage(ChatColor.of("#4788d9") + "5 minutes left for Final Heal.");
                break;
            }
            case 600: {
                // TODO: FINAL HEAL
                Bukkit.getOnlinePlayers().forEach(all -> {
                    all.setHealth(20.0);
                    all.setFoodLevel(20);
                    all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
                });
                Bukkit.broadcastMessage(ChatColor.of("#2be49c") + "Final heal!");

                break;
            }
            case 900: {
                // AVISO DE PVP 5min left
                Bukkit.getOnlinePlayers().forEach(all -> {
                    all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                });
                Bukkit.broadcastMessage(ChatColor.of("#4788d9") + "PvP will be enabled in 5 minutes.");
                break;
            }
            case 1200: {
                // TODO: PVP ON
                Bukkit.getOnlinePlayers().forEach(all -> {
                    all.playSound(all.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 1.9F);
                });
                instance.getGame().setPvp(true);
                Bukkit.broadcastMessage(ChatColor.of("#2be49c") + "PvP has been enabled.");
                break;
            }
            case 1800: {
                Bukkit.broadcastMessage(ChatColor.BLUE + "Discord! discord.noobsters.net\n" + ChatColor.AQUA
                        + "Twitter! twitter.com/NoobstersUHC");

                break;
            }
            case 3000: {
                Bukkit.broadcastMessage(ChatColor.BLUE + "Discord! discord.noobsters.net\n" + ChatColor.AQUA
                        + "Twitter! twitter.com/NoobstersUHC");
                break;
            }
            case 3600: {
                Bukkit.broadcastMessage(ChatColor.of("#4788d9")
                        + "The world will shrink to 100 blocks in the next 25 minutes at a speed of 1 block per second!");
                Bukkit.broadcastMessage(ChatColor.of("#2be49c")
                        + "Players in the nether will be randomly teleported to the overworld once the border reaches 500 blocks.");
                Bukkit.getOnlinePlayers().forEach(all -> {
                    all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
                });
                Bukkit.getScheduler().runTask(instance, () -> {
                    var world = Bukkit.getWorlds().get(0);
                    world.setGameRule(GameRule.DO_INSOMNIA, false);
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    world.setTime(400);
                    world.getWorldBorder().setSize(200, 1500);
                });
                break;
            }
            case 4200: {
                Bukkit.broadcastMessage(ChatColor.BLUE + "Discord! discord.noobsters.net\n" + ChatColor.AQUA
                        + "Twitter! twitter.com/NoobstersUHC");
                break;
            }
            case 4800: {
                Bukkit.broadcastMessage(ChatColor.BLUE + "Discord! discord.noobsters.net\n" + ChatColor.AQUA
                        + "Twitter! twitter.com/NoobstersUHC");
                break;
            }

        }
        instance.getScoreboardManager().getFastboardMap().entrySet().forEach(entry -> {
            entry.getValue().addUpdates(new UpdateObject(
                    ChatColor.GRAY + "Game Time: " + ChatColor.WHITE + timeConvert(GlobalListener.time), 0));
            entry.getValue().addUpdates(new UpdateObject(
                    ChatColor.GRAY + "Players: " + ChatColor.WHITE + instance.getPlayerManager().getAlivePlayers(), 3));
            entry.getValue().addUpdates(new UpdateObject(ChatColor.GRAY + "Border: " + ChatColor.WHITE
                    + ((int) (Bukkit.getWorlds().get(0).getWorldBorder().getSize() / 2)), 5));
            // TODO: Improve this method, it shouldn't be necessary to have to update this
            // line every second.

        });

        instance.getPlayerManager().getUhcPlayerMap().entrySet().parallelStream().forEach(entry -> {
            if (entry.getValue().isAlive()) {
                var of = Bukkit.getOfflinePlayer(entry.getValue().getUUID());
                if (!of.isOnline()) {
                    if (System.currentTimeMillis() - of.getLastSeen() > 600000) {
                        Bukkit.broadcastMessage(ChatColor.of("#DABC12") + of.getName()
                                + " has been disqualified for abandoning the game.");
                        entry.getValue().setDead(true);
                        entry.getValue().setAlive(false);
                    }
                }
            }

        });

    }

    String timeConvert(int t) {
        int hours = t / 3600;

        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }

}
