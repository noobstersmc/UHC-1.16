package me.infinityz.minigame.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.destroystokyo.paper.Title;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import co.aikar.taskchain.TaskChain;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.events.PlayerWinEvent;
import me.infinityz.minigame.events.TeamWinEvent;
import me.infinityz.minigame.players.PositionObject;
import me.infinityz.minigame.players.UHCPlayer;
import me.infinityz.minigame.scoreboard.IScoreboard;
import me.infinityz.minigame.scoreboard.IngameScoreboard;
import me.infinityz.minigame.scoreboard.objects.UpdateObject;
import me.infinityz.minigame.teams.objects.Team;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

@RequiredArgsConstructor
public class IngameListeners implements Listener {
    private @NonNull UHC instance;
    private @Getter List<Material> possibleFence = Arrays.stream(Material.values())
            .filter(material -> material.name().contains("FENCE") && !material.name().contains("FENCE_GATE"))
            .collect(Collectors.toList());;

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
        } else if (!uhcP.isDead() && !uhcP.isAlive() && GlobalListener.time < 1800) {
            p.sendMessage(ChatColor.of("#2be49c") + "The UHC has already started, to play use /play");
            uhcP.setAlive(false);
            p.setGameMode(GameMode.SPECTATOR);
        } else if (uhcP.isDead() && !uhcP.isAlive()) {
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

        var uhcp = instance.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        if (uhcp.isAlive()) {
            uhcp.setLastKnownPosition(PositionObject.getPositionFromWorld(e.getPlayer().getLocation()));
            uhcp.setLastKnownInventory(e.getPlayer().getInventory().getContents());
            uhcp.setLastKnownHealth(e.getPlayer().getHealth() + e.getPlayer().getAbsorptionAmount());
        }

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
        var inv = p.getInventory().getContents();
        UHCPlayer uhcPlayer = instance.getPlayerManager().getPlayer(p.getUniqueId());
        if (uhcPlayer != null) {
            if (uhcPlayer.isAlive()) {
                uhcPlayer.setAlive(false);
                uhcPlayer.setDead(true);
                uhcPlayer.setLastKnownHealth(0.0);
                uhcPlayer.setLastKnownPositionFromLoc(p.getLocation());
                uhcPlayer.setLastKnownInventory(inv);
            } // TODO: Send update to players left.

            /**
             * Team and Player win event takes places here.
             */
            Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
                if (instance.getTeamManger().getTeamSize() > 1) {
                    // ToX games
                    var teamsAlive = instance.getTeamManger().getAliveTeams();
                    if (teamsAlive.size() <= 1) {
                        var optionalTeam = teamsAlive.stream().findFirst();
                        if (optionalTeam.isPresent()) {
                            Bukkit.getPluginManager().callEvent(new TeamWinEvent(optionalTeam.get().getTeamID(), true));
                        } else {
                            Bukkit.broadcastMessage("Hm... this is awkward... there is no winner.");
                        }

                    }

                } else {
                    // FFA Games
                    if (instance.getPlayerManager().getAlivePlayers() <= 1) {
                        var lastAlivePlayer = instance.getPlayerManager().getUhcPlayerMap().values().stream()
                                .filter(UHCPlayer::isAlive).findFirst();
                        if (lastAlivePlayer.isPresent()) {
                            Bukkit.getPluginManager()
                                    .callEvent(new PlayerWinEvent(lastAlivePlayer.get().getUUID(), true));
                        } else {
                            Bukkit.broadcastMessage("Hm... this is awkward... there is no winner.");
                        }
                    }
                }
            });

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

    @EventHandler
    public void onPlayerWin(PlayerWinEvent e) {
        var player = e.getPlayer();
        playWinEffect(player);

    }

    @EventHandler
    public void onTeamWinEvent(TeamWinEvent e) {
        var team = instance.getTeamManger().getTeamMap().get(e.getTeamUUID());
        playTeamWinEffect(team);
    }

    /*
     * Recursive methods:
     */
    private Material getRandomFence() {
        return possibleFence.get(new Random().nextInt(possibleFence.size()));
    }

    private void playTeamWinEffect(Team team) {
        var winnersName = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        var winnersTitle = Title.builder()
                .title(new ComponentBuilder("Victory!").bold(true).color(ChatColor.GOLD).create())
                .subtitle(ChatColor.GREEN + "Congratulations " + winnersName.toString()).stay(6 * 20).fadeIn(10)
                .fadeOut(3 * 20).build();
        var titleToOthers = Title.builder()
                .title(new ComponentBuilder("Victory!").bold(true).color(ChatColor.GOLD).create())
                .subtitle(ChatColor.GREEN + winnersName.toString() + " have won!").stay(6 * 20).fadeIn(10)
                .fadeOut(3 * 20).build();

        team.getPlayerStream().forEach(member -> {
            member.playEffect(EntityEffect.TOTEM_RESURRECT);
            member.sendTitle(winnersTitle);
            fireWorksEffect(member);
        });

        Bukkit.getOnlinePlayers().stream().filter(all -> !winnersName.contains(all.getName()))
                .forEach(all -> all.sendTitle(titleToOthers));
    }

    private void playWinEffect(Player player) {
        player.sendTitle(
                Title.builder().title(new ComponentBuilder("Victory!").bold(true).color(ChatColor.GOLD).create())
                        .subtitle(ChatColor.GREEN + "Congratulations " + player.getName()).stay(6 * 20).fadeIn(10)
                        .fadeOut(3 * 20).build());
        fireWorksEffect(player);
        player.playEffect(EntityEffect.TOTEM_RESURRECT);

        var titleToOthers = Title.builder()
                .title(new ComponentBuilder("Victory!").bold(true).color(ChatColor.GOLD).create())
                .subtitle(ChatColor.GREEN + player.getName() + " has won!").stay(6 * 20).fadeIn(10).fadeOut(3 * 20)
                .build();
        Bukkit.getOnlinePlayers().stream().filter(all -> all != player).forEach(all -> all.sendTitle(titleToOthers));

    }

    private void fireWorksEffect(Player player) {
        var command1 = "summon firework_rocket %d %d %d {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:1,Explosions:[{Type:2,Flicker:0,Trail:1,Colors:[I;3887386,8073150,2651799,4312372],FadeColors:[I;3887386,11250603,4312372,15790320]}]}}}}";
        var command2 = "summon firework_rocket %d %d %d {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:2,Explosions:[{Type:3,Flicker:1,Trail:1,Colors:[I;5320730,2437522,8073150,11250603,6719955],FadeColors:[I;2437522,2651799,11250603,6719955,15790320]}]}}}}";
        var command3 = "summon firework_rocket %d %d %d {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:3,Explosions:[{Type:1,Flicker:1,Trail:1,Colors:[I;11743532,14602026,12801229,15435844],FadeColors:[I;11743532,14188952,15435844]}]}}}}";
        UHC.newChain().delay(1).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command2, player.getLocation().getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command1, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));

        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command3, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command2, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command1, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));

        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command3, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command2, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command1, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));

        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command3, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).sync(TaskChain::abort).execute();
    }

}