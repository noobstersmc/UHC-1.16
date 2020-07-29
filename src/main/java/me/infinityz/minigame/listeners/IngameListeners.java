package me.infinityz.minigame.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.scoreboard.IScoreboard;
import me.infinityz.minigame.scoreboard.IngameScoreboard;
import me.infinityz.minigame.scoreboard.objects.UpdateObject;
import net.md_5.bungee.api.ChatColor;

public class IngameListeners implements Listener {
    UHC instance;
    List<Material> possibleFence;

    public IngameListeners(UHC instance) {
        this.instance = instance;

        possibleFence = new ArrayList<>();
        for (Material m : Material.values()) {
            if (m.name().contains("FENCE") && !m.name().contains("FENCE_GATE")) {
                possibleFence.add(m);
            }
        }
    }

    @EventHandler
    public void onJoinLater(PlayerJoinEvent e) {
        // TODO: Make this more compact and effcient.
        var p = e.getPlayer();
        var uhcP = instance.getPlayerManager().getPlayer(p.getUniqueId());

        if (uhcP == null) {
            p.setGameMode(GameMode.SPECTATOR);
            uhcP = instance.getPlayerManager().addCreateUHCPlayer(p.getUniqueId(), false);
            uhcP.setAlive(false);
            if (GlobalListener.time < 1800) {
                p.sendMessage(ChatColor.of("#2be49c") + "The UHC has already started, to play use /play");
            }
        } else if (!uhcP.hasDied && GlobalListener.time < 1800) {
            p.sendMessage(ChatColor.of("#2be49c") + "The UHC has already started, to play use /play");
            uhcP.setAlive(false);
            p.setGameMode(GameMode.SPECTATOR);
        } else if (uhcP.hasDied) {
            p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation().add(0.0, 10, 0.0));
            p.setGameMode(GameMode.SPECTATOR);
            uhcP.setAlive(false);
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
        }
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {
        if (instance.getScoreboardManager().findScoreboard(e.getPlayer().getUniqueId()) != null) {
            return;
        }
        final IngameScoreboard sb = new IngameScoreboard(e.getPlayer());
        sb.update();
        instance.getScoreboardManager().getFastboardMap().put(e.getPlayer().getUniqueId().toString(), sb);
        UHCPlayer uhcp = instance.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        sb.addUpdates(new UpdateObject(
                ChatColor.GRAY + "Kills: " + ChatColor.WHITE + (uhcp == null ? 0 : uhcp.getKills()), 2));

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        IScoreboard sb = instance.getScoreboardManager().getFastboardMap()
                .remove(e.getPlayer().getUniqueId().toString());
        if (sb != null) {
            sb.delete();
        }
    }

    Material getRandomFence() {
        return possibleFence.get(new Random().nextInt(possibleFence.size()));
    }

    @EventHandler
    public void onDeathHead(PlayerDeathEvent e) {
        final Player p = e.getEntity();

        p.getLocation().getBlock().setType(getRandomFence());

        Block head = p.getLocation().getBlock().getRelative(BlockFace.UP);
        head.setType(Material.PLAYER_HEAD);

        Skull skull = (Skull) head.getState();
        skull.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
        skull.update();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // 3-second timeout to get respawned in spectator mode.
        Player p = e.getEntity();
        // remove player from whitelist.
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist remove " + e.getEntity().getName());
        UHCPlayer uhcPlayer = instance.getPlayerManager().getPlayer(p.getUniqueId());
        if (uhcPlayer != null) {
            if (uhcPlayer.isAlive()) {
                uhcPlayer.setAlive(false);
                uhcPlayer.hasDied = true;
            } // TODO: Send update to players left.

        }
        if (p.getKiller() != null) {
            Player killer = p.getKiller();
            UHCPlayer uhcKiller = instance.getPlayerManager().getPlayer(killer.getUniqueId());
            uhcKiller.setKills(uhcKiller.getKills() + 1);
            IScoreboard sb = instance.getScoreboardManager().findScoreboard(killer.getUniqueId());
            if (sb != null && sb instanceof IngameScoreboard) {
                IngameScoreboard sbi = (IngameScoreboard) sb;
                sbi.addUpdates(
                        new UpdateObject(ChatColor.GRAY + "Kills: " + ChatColor.WHITE + uhcKiller.getKills(), 2));
            }
        }
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            p.setGameMode(GameMode.SPECTATOR);
            if (p != null && p.isOnline()) {
                if (p.isDead()) {
                    p.spigot().respawn();
                    Bukkit.getScheduler().runTaskLater(instance, () -> {
                    }, 5);
                }
            }
        }, 20 * 3);
    }

}