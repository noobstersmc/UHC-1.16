package me.infinityz.minigame.tasks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import gnu.trove.map.hash.THashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.enums.DQReason;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.events.NetherDisabledEvent;
import me.infinityz.minigame.events.UHCPlayerDequalificationEvent;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.gamemodes.types.GoToHell;
import me.infinityz.minigame.players.UHCPlayer;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class GameLoop extends BukkitRunnable {
    private boolean borderShrink = false;
    private final World mainWorld = Bukkit.getWorlds().get(0);
    private @NonNull UHC instance;
    private THashMap<UUID, Long> borderTeleportMap = new THashMap<>();
    public static final ChatColor HAVELOCK_BLUE = ChatColor.of("#4788d9");
    public static final ChatColor SHAMROCK_GREEN = ChatColor.of("#2be49c");

    // Move from
    @Override
    public void run() {
        int time = (int) (Math.floor((System.currentTimeMillis() - instance.getGame().getStartTime()) / 1000.0));
        instance.getGame().setGameTime(time);
        Bukkit.getPluginManager().callEvent(new GameTickEvent(time, true));

        var worldBorder = mainWorld.getWorldBorder();

        if (!instance.getGamemodeManager().isScenarioEnable(GoToHell.class)) {
            if (!borderShrink && worldBorder.getSize() <= 1000) {
                borderShrink = true;
                Bukkit.getScheduler().runTask(instance, () -> {
                    Bukkit.getWorlds().forEach(worlds -> {
                        worlds.setGameRule(GameRule.DO_INSOMNIA, false);
                        worlds.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                        worlds.setTime(400);
                    });
                    Bukkit.getPluginManager().callEvent(new NetherDisabledEvent());
                    instance.getGame().setNether(false);
                });
            }
        }

        performBorderDamage();
        timedEvent(time);

        var border = ((int) (worldBorder.getSize() / 2));
        handleBossbar(time);

        autoDQ();

        Bukkit.getOnlinePlayers().parallelStream()
                .forEach(player -> borderDistanceActionBar(player, worldBorder, border));

    }

    private void timedEvent(int time) {

        var game = instance.getGame();
        var triggered = false;

        if (time == game.getHealTime() - 300) {
            // AVISO 5MIN LEFT FOR FINAL HEAL
            Bukkit.getOnlinePlayers()
                    .forEach(all -> all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1));
            Bukkit.broadcastMessage(HAVELOCK_BLUE + "5 minutes left for Final Heal.");
            triggered = true;
        }
        if (time == game.getHealTime()) {
            // TODO: FINAL HEAL
            Bukkit.getScheduler().runTask(instance, () -> {
                Bukkit.getOnlinePlayers().forEach(all -> {
                    all.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 20));
                    all.setFoodLevel(20);
                    all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
                });
            });
            Bukkit.broadcastMessage(SHAMROCK_GREEN + "Final heal!");
            triggered = true;

        }
        if (time == game.getPvpTime() - 300) {
            // AVISO 5 MINS LEFT FOR PVP
            Bukkit.getOnlinePlayers()
                    .forEach(all -> all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1));
            Bukkit.broadcastMessage(HAVELOCK_BLUE + "PvP will be enabled in 5 minutes.");
            triggered = true;

        }
        if (time == game.getPvpTime()) {
            // TODO: PVP ON
            Bukkit.getOnlinePlayers()
                    .forEach(all -> all.playSound(all.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 1.9F));
            instance.getGame().setPvp(true);
            Bukkit.broadcastMessage(SHAMROCK_GREEN + "PvP has been enabled.");
            triggered = true;

        }
        if (time == game.getBorderTime()) {
            // BORDER TIME
            Bukkit.broadcastMessage(HAVELOCK_BLUE + "The border has started to move to 100 blocks in the next "
                    + (game.getBorderCenterTime() / 60) + " minutes at a speed of 1 block per second!");

            if (game.isNether()) {
                Bukkit.broadcastMessage(SHAMROCK_GREEN
                        + "Players in nether will be randomly teleported to the overworld once the border reaches 500 blocks.");
            }

            Bukkit.getOnlinePlayers()
                    .forEach(all -> all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1));
            Bukkit.getScheduler().runTask(instance, () -> Bukkit.getWorlds().forEach(worlds -> {
                worlds.getWorldBorder().setSize(game.getBorderCenter(),
                        game.getBorderCenterTime());
            }));
            triggered = true;
        }

        if(time == game.getBorderCenterTime()+game.getBorderTime()+600){
            //PENULTIMO BORDE 100
            instance.getGame().setAntiMining(true);
        }

        if(time == game.getBorderCenterTime()+game.getBorderTime()+300){
            //ULTIMO BORDE 50
            Bukkit.getScheduler().runTask(instance, () -> Bukkit.getWorlds().forEach(worlds -> {
                worlds.getWorldBorder().setSize(50 , 60 );
            }));
        }

        if(time == game.getBorderCenterTime()+game.getBorderTime()+600){
            //DEATHMATCH
            instance.getGame().setDeathMatch(true);
            Bukkit.broadcastMessage(ChatColor.of("#d40c42") + "Death Match has started.");
            Bukkit.getScheduler().runTask(instance, () -> Bukkit.getOnlinePlayers().forEach(players -> {
                players.playSound(players.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 0.5f);
            }));
        }

        if ((time % 1200) == 0 && !triggered) {
            sendPromo();
        }
    }

    private void handleBossbar(final int time) {
        var bossBar = Game.getBossbar();
        var game = instance.getGame();
        double percent = 0;
        if (time < game.getHealTime()) {
            bossBar.setColor(BarColor.GREEN);
            var differential = game.getHealTime() - time;
            bossBar.setTitle("Heal in: " + timeFormat(differential));
            percent = (double) time / game.getHealTime();

        } else if (time < game.getPvpTime()) {
            bossBar.setColor(BarColor.YELLOW);
            var differential = game.getPvpTime() - time;
            bossBar.setTitle("PvP in: " + timeFormat(differential));
            percent = (double) time / game.getPvpTime();

        } else if (time >= game.getBorderTime() - 600 && time <= game.getBorderTime()) {
            if (!bossBar.isVisible()) {
                bossBar.setVisible(true);
            }
            bossBar.setColor(BarColor.BLUE);
            var differential = game.getBorderTime() - time;
            bossBar.setTitle("Border in: " + timeFormat(differential));
            percent = (double) time / game.getBorderTime();

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
                    if (!of.isOnline() && (System.currentTimeMillis()
                            - of.getLastSeen() > (instance.getGame().getMaxDisconnectTime() * 1000L))) {
                        Bukkit.getPluginManager()
                                .callEvent(new UHCPlayerDequalificationEvent(all, DQReason.OFFLINE_DQ));
                        all.setDead(true);
                        all.setAlive(false);
                    }

                });
    }

    private boolean isMoving(int time) {
        var border = instance.getGame().getBorderTime();
        var finalborder = border + instance.getGame().getBorderCenterTime();
        return time >= border && time <= finalborder;
    }

    private void borderDistanceActionBar(final Player player, final WorldBorder worldBorder, final int border) {
        if (isMoving(instance.getGame().getGameTime())) {
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
                + "Twitter! twitter.com/NoobstersMC");
    }

    // Fast algo to calculate distance to nearest border point or absolute
    // maxima/minima
    public static double distanceToNearestPoint(final WorldBorder border, final Location point) {
        double size = border.getSize();
        double sizeHalved = size / 2;
        double originX = border.getCenter().getX();
        double originZ = border.getCenter().getZ();
        // Obtain the edges of worldborder (Cuboid)
        double minX = originX - sizeHalved;
        double maxX = originX + sizeHalved;
        double minZ = originZ - sizeHalved;
        double maxZ = originZ + sizeHalved;
        // Calculate delta on distance
        var dX = Math.max(minX - point.getX(), point.getX() - maxX);
        dX = Math.max(dX, 0);
        var dZ = Math.max(minZ - point.getX(), point.getX() - maxZ);
        dZ = Math.max(dZ, 0);
        // Hypothenuse
        var distance = Math.sqrt(dX * dX + dZ * dZ);
        // Distance = 0 = No hypothenuse, calculate distance to nearest point
        return distance == 0 ? Math.min(Math.min(point.getX() - minX, maxX - point.getX()),
                Math.min(point.getZ() - minZ, maxZ - point.getZ())) : distance;
    }

    private void performBorderDamage() {
        Bukkit.getOnlinePlayers().stream()
                .filter(players -> players.getGameMode() == GameMode.SURVIVAL
                        && !players.getWorld().getWorldBorder().isInside(players.getLocation()))
                .forEach(outsideBorderPlayer -> Bukkit.getScheduler().runTask(instance, () -> {
                    outsideBorderPlayer.damage(1);

                    handleBorderRescue(outsideBorderPlayer);

                    outsideBorderPlayer
                            .setLastDamageCause(new EntityDamageEvent(outsideBorderPlayer, DamageCause.CUSTOM, 1));
                }));
    }

    public void handleBorderRescue(Player player) {
        var time = borderTeleportMap.getOrDefault(player.getUniqueId(), 0L);
        var diff = time > 0L ? System.currentTimeMillis() - time : 60_000L;

        if (diff >= 60_000) {
            var border = player.getWorld().getWorldBorder();
            var loc = player.getLocation();
            var distance = Math.abs(distanceToNearestPoint(border, loc));
            if (distance >= 10) {
                var newLoc = loc.getWorld().getHighestBlockAt(loc).getLocation().add(0.0, 1.5, 0.0);
                player.teleportAsync(newLoc);
                player.sendMessage(ChatColor.of("#1fbd90") + "The gods have decided to give you a second chance.");
                player.playSound(newLoc, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.VOICE, 1.0f, 0.5f);
                borderTeleportMap.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    public static String timeConvert(int t) {
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
