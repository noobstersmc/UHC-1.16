package me.infinityz.minigame.tasks;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.enums.DQReason;
import me.infinityz.minigame.events.NetherDisabledEvent;
import me.infinityz.minigame.events.UHCPlayerDequalificationEvent;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.scoreboard.objects.UpdateObject;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class GameLoop extends BukkitRunnable {
    private boolean borderShrink = false;
    private final World mainWorld = Bukkit.getWorlds().get(0);
    private @NonNull UHC instance;
    public static final ChatColor HAVELOCK_BLUE = ChatColor.of("#4788d9");
    public static final ChatColor SHAMROCK_GREEN = ChatColor.of("#2be49c");

    // Move from
    @Override
    public void run() {
        var time = (int) (Math.floor((System.currentTimeMillis() - instance.getGame().getStartTime()) / 1000.0));
        instance.getGame().setGameTime(time);

        var worldBorder = mainWorld.getWorldBorder();

        if (!borderShrink && worldBorder.getSize() <= 1000) {
            borderShrink = true;
            Bukkit.getScheduler().runTask(instance, () -> {
                Bukkit.getPluginManager().callEvent(new NetherDisabledEvent());
                instance.getGame().setNether(false);
            });
        }

        performBorderDamage();
        timedEvent(time);

        var border = ((int) (worldBorder.getSize() / 2));
        updateScoreboards(border);

        handleBossbar(time);

        autoDQ();

        Bukkit.getOnlinePlayers().parallelStream()
                .forEach(player -> borderDistanceActionBar(player, worldBorder, border));

    }

    private void timedEvent(int time) {
        switch (time) {
            case 300:
                // AVISO DE FINAL HEAL 5min left
                Bukkit.getOnlinePlayers()
                        .forEach(all -> all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1));
                Bukkit.broadcastMessage(HAVELOCK_BLUE + "5 minutes left for Final Heal.");
                break;
            case 600:
                // TODO: FINAL HEAL
                Bukkit.getOnlinePlayers().forEach(all -> {
                    all.setHealth(20.0);
                    all.setFoodLevel(20);
                    all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
                });
                Bukkit.broadcastMessage(SHAMROCK_GREEN + "Final heal!");
                break;
            case 900:
                // AVISO DE PVP 5min left
                Bukkit.getOnlinePlayers()
                        .forEach(all -> all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1));
                Bukkit.broadcastMessage(HAVELOCK_BLUE + "PvP will be enabled in 5 minutes.");
                break;
            case 1200:
                // TODO: PVP ON
                Bukkit.getOnlinePlayers()
                        .forEach(all -> all.playSound(all.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 1.9F));
                instance.getGame().setPvp(true);
                Bukkit.broadcastMessage(SHAMROCK_GREEN + "PvP has been enabled.");
                break;
            case 3600:
                Bukkit.broadcastMessage(HAVELOCK_BLUE
                        + "The world will shrink to 100 blocks in the next 25 minutes at a speed of 1 block per second!");
                Bukkit.broadcastMessage(SHAMROCK_GREEN
                        + "Players in the nether will be randomly teleported to the overworld once the border reaches 500 blocks.");
                Bukkit.getOnlinePlayers()
                        .forEach(all -> all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1));
                Bukkit.getScheduler().runTask(instance, () -> Bukkit.getWorlds().forEach(worlds -> {
                    worlds.setGameRule(GameRule.DO_INSOMNIA, false);
                    worlds.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    worlds.setTime(400);
                    worlds.getWorldBorder().setSize(200, 1800);

                }));
                break;
            case 1800:
                sendPromo();
                break;
            case 2400:
                sendPromo();
                break;
            case 3000:
                sendPromo();
                break;
            case 4200:
                sendPromo();
                break;
            case 4800:
                sendPromo();
                break;
            default:
                break;
        }

    }

    private void handleBossbar(final int time) {
        var bossBar = Game.getBossbar();
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

    private void autoDQ() {
        instance.getPlayerManager().getUhcPlayerMap().values().parallelStream().filter(UHCPlayer::isAlive)
                .forEach(all -> {
                    var of = Bukkit.getOfflinePlayer(all.getUUID());
                    if (!of.isOnline() && System.currentTimeMillis() - of.getLastSeen() > 600_000) {
                        Bukkit.getPluginManager()
                                .callEvent(new UHCPlayerDequalificationEvent(all, DQReason.OFFLINE_DQ));
                        all.setDead(true);
                        all.setAlive(false);
                    }

                });
    }

    private void updateScoreboards(final int border) {
        final int alivePlayers = instance.getPlayerManager().getAlivePlayers();
        final String timeFormatted = timeConvert(instance.getGame().getGameTime());
        instance.getScoreboardManager().getFastboardMap().values().stream().forEach(value -> {
            value.addAllUpdates(new UpdateObject(ChatColor.GRAY + "Game Time: " + ChatColor.WHITE + timeFormatted, 0),
                    new UpdateObject(ChatColor.GRAY + "Players: " + ChatColor.WHITE + alivePlayers, 3),
                    new UpdateObject(ChatColor.GRAY + "Border: " + ChatColor.WHITE + border, 5));

        });

    }

    private void borderDistanceActionBar(final Player player, final WorldBorder worldBorder, final int border) {
        if (border != 2000 && border != 100) {
            String distanceText = "";
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

                var distance = (isNegative ? "-" : "") + String.format("%.1f", distanceFromBorder);

                distanceText = distanceText + "You are " + distance + " blocks away from the border";
                player.sendActionBar(distanceText);

            }
        }
    }

    private void sendPromo() {
        Bukkit.broadcastMessage(ChatColor.BLUE + "Discord! discord.noobsters.net\n" + ChatColor.AQUA
                + "Twitter! twitter.com/NoobstersUHC");
    }

    private void performBorderDamage() {
        Bukkit.getOnlinePlayers().stream()
                .filter(players -> !players.getWorld().getWorldBorder().isInside(players.getLocation()))
                .forEach(outsideBorderPlayer -> Bukkit.getScheduler().runTask(instance, () -> {
                    outsideBorderPlayer.damage(1);
                    outsideBorderPlayer
                            .setLastDamageCause(new EntityDamageEvent(outsideBorderPlayer, DamageCause.CUSTOM, 1));
                }));
    }

    private String timeConvert(int t) {
        int hours = t / 3600;

        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }

    private String timeFormat(int t) {
        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return (minutes > 0 ? String.format("%dm %d", minutes, seconds) : String.format("%d", seconds)) + "s";
    }

}
