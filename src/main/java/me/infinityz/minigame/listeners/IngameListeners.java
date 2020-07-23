package me.infinityz.minigame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.scoreboard.IScoreboard;
import me.infinityz.minigame.scoreboard.IngameScoreboard;
import me.infinityz.minigame.scoreboard.objects.UpdateObject;
import net.md_5.bungee.api.ChatColor;

public class IngameListeners implements Listener {
    UHC instance;

    public IngameListeners(UHC instance) {
        this.instance = instance;
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
        if(uhcp == null){
            instance.getPlayerManager().addCreateUHCPlayer(e.getPlayer().getUniqueId());
            uhcp = instance.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
            uhcp.setAlive(false);
            uhcp.setSpectator(true);
        }
        sb.addUpdates(new UpdateObject(ChatColor.GRAY + "Kills: " +ChatColor.WHITE + uhcp.getKills(), 2));

    }

    @EventHandler
    public void onJoinLater(PlayerJoinEvent e) {
        // TODO: Make this more compact and effcient.
        Player p = e.getPlayer();
        UHCPlayer uhcP = instance.getPlayerManager().getPlayer(p.getUniqueId());
        if (uhcP == null || !uhcP.isAlive()) {
            p.setGameMode(GameMode.SPECTATOR);
            if (uhcP == null)
                instance.getPlayerManager().addCreateUHCPlayer(p.getUniqueId());

        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        IScoreboard sb = instance.getScoreboardManager().getFastboardMap()
                .remove(e.getPlayer().getUniqueId().toString());
        if (sb != null) {
            sb.delete();
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // 3-second timeout to get respawned in spectator mode.
        Player p = e.getEntity();
        //remove player from whitelist.
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist remove " + e.getEntity().getName());
        UHCPlayer uhcPlayer = instance.getPlayerManager().getPlayer(p.getUniqueId());
        if (uhcPlayer != null) {
            if (uhcPlayer.isAlive()) {
                uhcPlayer.setAlive(false);
            } // TODO: Send update to players left.

        }
        if (p.getKiller() != null) {
            Player killer = p.getKiller();
            UHCPlayer uhcKiller = instance.getPlayerManager().getPlayer(killer.getUniqueId());
            uhcKiller.setKills(uhcKiller.getKills() + 1);
            IScoreboard sb = instance.getScoreboardManager().findScoreboard(killer.getUniqueId());
            if(sb != null && sb instanceof IngameScoreboard){
                IngameScoreboard sbi = (IngameScoreboard) sb;
                sbi.addUpdates(new UpdateObject(ChatColor.GRAY + "Kills: " +ChatColor.WHITE + uhcKiller.getKills(), 2));
            }
        }
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            if (p != null && p.isOnline()) {
                if (p.isDead()) {
                    p.spigot().respawn();
                    Bukkit.getScheduler().runTaskLater(instance, () -> {
                        p.setGameMode(GameMode.SPECTATOR);
                    }, 5);
                }
            }
        }, 20 * 3);
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        e.getPlayer().setGameMode(GameMode.SPECTATOR);
    }
}