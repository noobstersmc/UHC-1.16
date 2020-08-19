package me.infinityz.minigame.tasks;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.scheduler.BukkitRunnable;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.NetherDisabledEvent;
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
        var time = (int) (Math.floor((System.currentTimeMillis() - instance.getGame().getStartTime()) / 1000));
        instance.getGame().setGameTime(time);

        var worldBorder = mainWorld.getWorldBorder();

        if (!borderShrink && worldBorder.getSize() <= 1000) {
            borderShrink = true;
            Bukkit.getScheduler().runTask(instance, () -> {
                Bukkit.getPluginManager().callEvent(new NetherDisabledEvent());
                instance.getGame().setNether(false);
            });
        }

        switch (time) {
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
                    Bukkit.getWorlds().forEach(worlds -> {
                        worlds.setGameRule(GameRule.DO_INSOMNIA, false);
                        worlds.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                        worlds.setTime(400);
                        worlds.getWorldBorder().setSize(200, 1500);

                    });
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

        var border = ((int) (worldBorder.getSize() / 2));
        instance.getScoreboardManager().getFastboardMap().entrySet().forEach(entry -> {
            entry.getValue().addUpdates(new UpdateObject(
                    ChatColor.GRAY + "Game Time: " + ChatColor.WHITE + timeConvert(instance.getGame().getGameTime()),
                    0));
            entry.getValue().addUpdates(new UpdateObject(
                    ChatColor.GRAY + "Players: " + ChatColor.WHITE + instance.getPlayerManager().getAlivePlayers(), 3));

            entry.getValue().addUpdates(new UpdateObject(ChatColor.GRAY + "Border: " + ChatColor.WHITE + border, 5));
            // TODO: Improve this method, it shouldn't be necessary to have to update this

            if (border != 2000 && border != 100) {
                String distanceText = "";
                var player = entry.getValue().getPlayer();
                var playerLOC = player.getLocation().clone();
                double borderSize = worldBorder.getSize() / 2;

                double absoluteX = Math.abs(playerLOC.getX());
                double absoluteZ = Math.abs(playerLOC.getZ());
                double distanceX = borderSize - absoluteX;
                double distanceZ = borderSize - absoluteZ;

                double distanceFromBorder = Math.min(Math.abs(distanceX), Math.abs(distanceZ));
                if (distanceFromBorder <= 20.9) {

                    var isNegative = distanceX < 0 || distanceZ < 0;
                    if (isNegative) {
                        distanceText = "" + ChatColor.RED;
                    } else if (distanceFromBorder < 10) {
                        distanceText = "" + ChatColor.RED;
                    } else {
                        distanceText = "" + ChatColor.YELLOW;
                    }

                    var distanceColoredAmount = String.format(distanceText + (isNegative ? "-" : "") + "%.1f",
                            distanceFromBorder);

                    distanceText = distanceText + "You are " + distanceColoredAmount + " blocks away from the border";
                    player.sendActionBar(distanceText);

                } else {
                    player.sendActionBar("");
                }
            }
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

        var bossBar = instance.getGame().getBossbar();
        double percent = 0;
        if (time < 600) {
            bossBar.setColor(BarColor.GREEN);
            var differential = 600 - time;
            bossBar.setTitle("Final heal in: " + timeFormat(differential));
            percent = time / 600.0;
        } else if (time < 1200) {
            bossBar.setColor(BarColor.YELLOW);
            var differential = 1200 - time;
            bossBar.setTitle("PvP in: " + timeFormat(differential));
            percent = time / 1200.0;
        } else if (time >= 3000 && time <= 3600) {
            if (!bossBar.isVisible()) {
                bossBar.setVisible(true);
            }
            bossBar.setColor(BarColor.BLUE);
            var differential = 3600 - time;
            bossBar.setTitle("Border Shrink in: " + timeFormat(differential));
            percent = time / 3600.0;

        } else {
            bossBar.setVisible(false);
        }
        if (bossBar.isVisible() && percent >= 0.0 && percent <= 1.0) {
            bossBar.setProgress(percent);
        }

    }

    String timeConvert(int t) {
        int hours = t / 3600;

        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }

    String timeFormat(int t) {
        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return (minutes > 0 ? String.format("%dm %d", minutes, seconds) : String.format("%d", seconds)) + "s";
    }

}
