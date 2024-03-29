package me.noobsters.minigame.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.destroystokyo.paper.Title;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World.Environment;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import co.aikar.taskchain.TaskChain;
import fr.mrmicky.fastinv.ItemBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.noobsters.minigame.UHC;
import me.noobsters.minigame.chunks.ChunksManager;
import me.noobsters.minigame.enums.DQReason;
import me.noobsters.minigame.enums.Stage;
import me.noobsters.minigame.events.GameTickEvent;
import me.noobsters.minigame.events.PlayerJoinedLateEvent;
import me.noobsters.minigame.events.PlayerWinEvent;
import me.noobsters.minigame.events.ScoreboardUpdateEvent;
import me.noobsters.minigame.events.TeamWinEvent;
import me.noobsters.minigame.events.UHCPlayerDequalificationEvent;
import me.noobsters.minigame.game.Game;
import me.noobsters.minigame.game.Game.GameInfo;
import me.noobsters.minigame.gamemodes.interfaces.TimeBombData;
import me.noobsters.minigame.gamemodes.types.BareBones;
import me.noobsters.minigame.gamemodes.types.GoldenRetriever;
import me.noobsters.minigame.gamemodes.types.SkyHigh;
import me.noobsters.minigame.gamemodes.types.TiempoBomba;
import me.noobsters.minigame.gamemodes.types.UHCMeetup;
import me.noobsters.minigame.players.PositionObject;
import me.noobsters.minigame.players.UHCPlayer;
import me.noobsters.minigame.scoreboard.IScoreboard;
import me.noobsters.minigame.scoreboard.IngameScoreboard;
import me.noobsters.minigame.scoreboard.objects.UpdateObject;
import me.noobsters.minigame.teams.objects.Team;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.api.trait.trait.Owner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.noobsters.kern.paper.Kern;

@RequiredArgsConstructor
public class IngameListeners implements Listener {
    private @NonNull UHC instance;
    private @Getter List<Material> possibleFence = Arrays.stream(Material.values())
            .filter(material -> material.name().contains("FENCE") && !material.name().contains("FENCE_GATE"))
            .collect(Collectors.toList());

    // COMBATLOG

    @EventHandler
    public void leaveCombatLog(PlayerQuitEvent e) {
        if (!instance.getGame().isCombatLog())
            return;

        var player = e.getPlayer();
        var uuid = player.getUniqueId();
        var name = player.getName().toString();

        if (instance.getGameStage() == Stage.INGAME && player.getGameMode() == GameMode.SURVIVAL) {

            var combatLoggers = instance.getGame().getCombatLoggers();

            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.SKELETON, name + "'s CombatLog");

            var head = new ItemBuilder(Material.PLAYER_HEAD)
                    .meta(SkullMeta.class, meta -> meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId())))
                    .build();

            npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.HELMET, head);

            npc.data().setPersistent(NPC.DEFAULT_PROTECTED_METADATA, false);

            npc.data().setPersistent(NPC.SILENT_METADATA, true);

            npc.getOrAddTrait(Owner.class).setOwner(name, uuid);

            npc.spawn(player.getLocation());

            combatLoggers.put(uuid.toString(), npc.getUniqueId());

        }
    }

    @EventHandler
    public void preCombatLog(PlayerJoinEvent e) {
        var player = e.getPlayer();
        var uuid = player.getUniqueId().toString();
        var combatLoggers = instance.getGame().getCombatLoggers();

        if (combatLoggers.containsKey(uuid)) {
            var npcID = combatLoggers.get(uuid);
            var combatlog = CitizensAPI.getNPCRegistry().getByUniqueId(npcID);
            if (combatlog.isSpawned()) {
                player.teleport(combatlog.getEntity().getLocation());
                combatlog.despawn();
                combatLoggers.remove(uuid);
                combatlog.destroy();
            }
        }
    }

    @EventHandler
    public void onNPCDamage(NPCDamageEvent e) {
        if (e.getCause() == DamageCause.SUFFOCATION) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void combatLogDeath(NPCDeathEvent e) {
        var npc = e.getNPC();
        var ownerID = npc.getOrAddTrait(Owner.class).getOwnerId();
        var combatLoggers = instance.getGame().getCombatLoggers();
        var killer = e.getEvent().getEntity().getKiller();
        var playerManager = instance.getPlayerManager();

        if (combatLoggers.containsKey(ownerID.toString())
                && playerManager.getUhcPlayerMap().containsKey(ownerID.getMostSignificantBits())) {

            var uhcPlayer = playerManager.getPlayer(ownerID);
            var items = uhcPlayer.getLastKnownInventory();

            var head = new ItemBuilder(Material.PLAYER_HEAD)
                    .meta(SkullMeta.class, meta -> meta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerID))).build();
            var goldenHead = new ItemBuilder(Material.GOLDEN_APPLE).name(ChatColor.GOLD + "Golden Head").build();

            var gamemodeManager = instance.getGamemodeManager();

            var loc = e.getEvent().getEntity().getLocation();

            if (gamemodeManager.isScenarioEnable(TiempoBomba.class)) {

                var timebomb = gamemodeManager.getScenario(TiempoBomba.class);

                loc.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
                Bukkit.getScheduler().runTaskLater(instance, () -> {
                    var dc = timebomb.createDoubleChestAt(loc);

                    for (var item : items) {
                        if (item != null) {
                            dc.getInventory().addItem(item);
                        }
                    }
                    if (!instance.getGamemodeManager().isScenarioEnable(GoldenRetriever.class)) {

                        dc.getInventory().addItem(head);
                    } else {
                        dc.getInventory().addItem(goldenHead);
                    }

                    var holoLoc = new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ());
                    var holo = timebomb.createHoloAt(holoLoc, ChatColor.of(timebomb.getColors().get(30 - 1)) + "30s");

                    timebomb.getHologramChestMap().put(
                            new TimeBombData(holo, loc.getBlock(), loc.clone().add(0, 0, -1).getBlock()),
                            System.currentTimeMillis());

                }, 1l);

            } else {
                if (instance.getGamemodeManager().isScenarioEnable(GoldenRetriever.class)) {

                    e.getEvent().getDrops().add(goldenHead);
                } else {
                    loc.getBlock().setType(getRandomFence());

                    Block headBlock = loc.getBlock().getRelative(BlockFace.UP);
                    headBlock.setType(Material.PLAYER_HEAD);

                    if (headBlock.getState() instanceof Skull) {
                        Skull skull = (Skull) headBlock.getState();
                        skull.setOwningPlayer(Bukkit.getOfflinePlayer(ownerID));
                        skull.update();
                    }
                }

                for (var item : items) {
                    if (item != null) {
                        e.getEvent().getDrops().add(item);
                    }
                }
            }

            var entity = e.getNPC().getEntity();

            entity.getWorld().strikeLightningEffect(entity.getLocation());

            uhcPlayer.setDead(true);
            uhcPlayer.setAlive(false);

            if (killer != null) {
                Bukkit.broadcastMessage(
                        ChatColor.WHITE + (npc.getName() + " was hunted by " + killer.getName().toString()));
                if (playerManager.getUhcPlayerMap().containsKey(killer.getUniqueId().getMostSignificantBits())) {
                    var uhcKiller = playerManager.getPlayer(killer.getUniqueId());
                    uhcKiller.setKills(uhcKiller.getKills() + 1);
                }

                var team = instance.getTeamManger().getPlayerTeam(killer.getUniqueId());
                if (team != null)
                    team.addKills(1);

            } else {
                Bukkit.broadcastMessage(ChatColor.WHITE + npc.getName() + " was destroyed.");
            }

            Bukkit.getScheduler().runTaskAsynchronously(instance, task -> {
                Bukkit.getPluginManager()
                        .callEvent(new UHCPlayerDequalificationEvent(uhcPlayer, DQReason.COMBATLOG_DQ));
            });

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void lateJoin(PlayerJoinedLateEvent e) {
        final var location = e.getPlayer().getLocation();
        var spawn = Game.getLobbySpawn();
        var player = e.getPlayer();
        var name = player.getName().toString();
        var uuid = player.getUniqueId().toString();
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 20));
        player.teleport(spawn);
        player.teleportAsync(location);

        instance.getGame().getWhitelist().put(name, uuid);
    }
    // DEATHMATCH

    @EventHandler
    public void onDeathMatch(GameTickEvent e) {
        var game = instance.getGame();
        if (game.isDeathMatch() && !game.isHasSomeoneWon() && instance.getGame().isDeathMatchDamage()
                && e.getSecond() % 5 == 0) {
            Bukkit.getScheduler().runTask(instance, () -> {
                Bukkit.getOnlinePlayers().forEach(players -> {
                    if (players.getGameMode() == GameMode.SURVIVAL) {
                        if (players.getHealth() > 2)
                            players.setHealth(players.getHealth() - 2);
                        players.damage(2);
                    }
                });
            });
        }

    }

    // ANTIMINING

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAntiMining(GameTickEvent e) {
        if (instance.getGame().isAntiMining()) {
            Bukkit.getScheduler().runTask(instance, () -> {
                Bukkit.getOnlinePlayers().forEach(players -> {
                    if (players.getGameMode() == GameMode.SURVIVAL
                            && players.getWorld().getEnvironment() != Environment.NETHER
                            && players.getLocation().getY() < 55)
                        players.sendActionBar(ChatColor.YELLOW + "⚠ You must be on surface at meetup.");
                });
            });
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAntiMiningMine(BlockBreakEvent e) {
        var block = e.getBlock();
        if (instance.getGame().isAntiMining()) {
            var player = e.getPlayer().getLocation().getY();
            if (e.getPlayer().getWorld().getEnvironment() != Environment.NETHER && player < 55
                    && player > block.getLocation().getY()) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "Mining is not allowed at meetup.");
            }

        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        if (player.getWorld() == Bukkit.getWorld("lobby")) {
            var world = Bukkit.getWorld("world");
            var worldBorderSizeHaved = (int) world.getWorldBorder().getSize() / 2;
            var loc = ChunksManager.findScatterLocation(world, worldBorderSizeHaved).add(0, 5, 0);
            player.teleport(loc);
        }

    }

    @EventHandler
    public void onDeathFromBorder(PlayerDeathEvent e) {
        if (e.getEntity().getLastDamageCause().getCause() == DamageCause.CUSTOM) {
            e.setDeathMessage("");
            var player = e.getEntity().getPlayer();
            if (instance.getGamemodeManager().isScenarioEnable(SkyHigh.class)
                    && player.getWorld().getWorldBorder().isInside(player.getLocation())) {
                Bukkit.broadcastMessage(player.getName() + " was devoured by the earth");
                return;
            }
            if (player.getWorld().getWorldBorder().isInside(player.getLocation())
                    && player.getWorld().getEnvironment() != Environment.NETHER) {
                Bukkit.broadcastMessage(player.getName() + " was devoured by his arrogance");
                return;
            }
            var deathMessageEnglish = e.getEntity().getName() + " was devoured by the border";
            var deathMessageSpanish = e.getEntity().getName() + " fue devorado por el borde";
            var deathMessageFrench = e.getEntity().getName() + " a été dévoré par la frontière";
            Bukkit.getOnlinePlayers().stream().forEach(all -> {
                if (all.getLocale().startsWith("es_")) {
                    all.sendMessage(deathMessageSpanish);
                } else if (all.getLocale().startsWith("fr_")) {
                    all.sendMessage(deathMessageFrench);
                } else {
                    all.sendMessage(deathMessageEnglish);
                }
            });
            instance.getLogger().info(deathMessageEnglish);
        }
    }
    /*
     * Game tick events start
     */

    @EventHandler
    public void onGameTick(GameTickEvent e) {
        instance.getScoreboardManager().getScoreboardsOfType(IngameScoreboard.class).parallelStream()
                .forEach(all -> Bukkit.getPluginManager().callEvent(new ScoreboardUpdateEvent(all, true, "")));
    }

    private String timeConvert(int t) {
        int hours = t / 3600;

        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onScoreboardUpdate(ScoreboardUpdateEvent e) {
        var uhcPlayer = instance.getPlayerManager().getPlayer(e.getScoreboard().getPlayer().getUniqueId());
        var isTeams = instance.getTeamManger().getTeamSize() > 1;

        if (isTeams) {
            var team = instance.getTeamManger().getPlayerTeam(e.getScoreboard().getPlayer().getUniqueId());
            e.setLines(new String[] {
                    Game.getScoreColors() + "Time: " + ChatColor.WHITE + timeConvert(instance.getGame().getGameTime()),
                    "",
                    Game.getScoreColors() + "Your Kills: " + ChatColor.WHITE
                            + (uhcPlayer != null ? uhcPlayer.getKills() : 0),
                    Game.getScoreColors() + "Team Kills: " + ChatColor.WHITE + (team != null ? team.getTeamKills() : 0),
                    "",
                    Game.getScoreColors() + "Players Left: " + ChatColor.WHITE
                            + instance.getPlayerManager().getAlivePlayers(),
                    Game.getScoreColors() + "Border: " + ChatColor.WHITE
                            + ((int) e.getScoreboard().getPlayer().getWorld().getWorldBorder().getSize() / 2),
                    "", ChatColor.WHITE + "noobsters.net" });

        } else {
            e.setLines(new String[] {
                    Game.getScoreColors() + "Time: " + ChatColor.WHITE + timeConvert(instance.getGame().getGameTime()),
                    "",
                    Game.getScoreColors() + "Your Kills: " + ChatColor.WHITE
                            + (uhcPlayer != null ? uhcPlayer.getKills() : 0),
                    "",
                    Game.getScoreColors() + "Players Left: " + ChatColor.WHITE
                            + instance.getPlayerManager().getAlivePlayers(),
                    Game.getScoreColors() + "Border: " + ChatColor.WHITE
                            + ((int) e.getScoreboard().getPlayer().getWorld().getWorldBorder().getSize() / 2),
                    "", ChatColor.WHITE + "noobsters.net" });
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void updateGameboards(ScoreboardUpdateEvent e) {
        if (!e.isCancelled()) {
            e.getScoreboard().updateLines(e.getLines());
        }
    }

    @EventHandler
    public void onJoinLater(PlayerJoinEvent e) {
        var p = e.getPlayer();
        var uhcP = instance.getPlayerManager().getPlayer(p.getUniqueId());
        var time = instance.getGame().getGameTime();
        /*
         * Bossbar code
         */
        var bossbar = Game.getBossbar();
        if (bossbar != null) {
            bossbar.addPlayer(p);
        }

        if (uhcP == null) {
            p.setGameMode(GameMode.SPECTATOR);
            uhcP = instance.getPlayerManager().addCreateUHCPlayer(p.getUniqueId(), false);
            uhcP.setAlive(false);
            if (!instance.getGamemodeManager().isScenarioEnable(UHCMeetup.class)
                    && time < instance.getGame().getPvpTime())
                p.sendMessage(ChatColor.of("#2be49c") + "The UHC has already started, to play use /play");

        } else if (!uhcP.isDead() && !uhcP.isAlive()) {
            if (!instance.getGamemodeManager().isScenarioEnable(UHCMeetup.class)
                    && time < instance.getGame().getPvpTime())
                p.sendMessage(ChatColor.of("#2be49c") + "The UHC has already started, to play use /play");
            uhcP.setAlive(false);
            p.setGameMode(GameMode.SPECTATOR);
        } else if (uhcP.isDead() && !uhcP.isAlive()) {
            p.teleport(Bukkit.getWorld("world").getSpawnLocation().add(0.0, 10, 0.0));
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
        final IngameScoreboard sb = new IngameScoreboard(e.getPlayer(), instance);
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
        if (uhcp != null && uhcp.isAlive()) {
            uhcp.setLastKnownPosition(PositionObject.getPositionFromWorld(e.getPlayer().getLocation()));
            uhcp.setLastKnownInventory(e.getPlayer().getInventory().getContents());
            uhcp.setLastKnownHealth(e.getPlayer().getHealth());
        }

    }

    @EventHandler
    public void onDeathHead(PlayerDeathEvent e) {
        final Player p = e.getEntity();
        if (instance.getGamemodeManager().isScenarioEnable(GoldenRetriever.class)
                || instance.getGamemodeManager().isScenarioEnable(UHCMeetup.class)
                || instance.getGamemodeManager().isScenarioEnable(BareBones.class))
            return;

        if (instance.getGamemodeManager().isScenarioEnable(TiempoBomba.class)
                && !instance.getGamemodeManager().isScenarioEnable(GoldenRetriever.class)) {
            var head = new ItemBuilder(Material.PLAYER_HEAD)
                    .meta(SkullMeta.class, meta -> meta.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId())))
                    .build();
            e.getDrops().add(head);
            return;
        }

        p.getLocation().getBlock().setType(getRandomFence());

        Block head = p.getLocation().getBlock().getRelative(BlockFace.UP);
        head.setType(Material.PLAYER_HEAD);

        if (head.getState() instanceof Skull) {
            Skull skull = (Skull) head.getState();
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
            skull.update();
        }
    }

    @EventHandler
    public void onPlayerDQ(UHCPlayerDequalificationEvent e) {

        Bukkit.getScheduler().runTaskAsynchronously(instance, this::calculateWin);

        if (e.getReason() == DQReason.OFFLINE_DQ) {
            Bukkit.broadcastMessage(e.getOfflinePlayer().getName() + " has abandoned the game");
        }
    }

    private void calculateWin() {
        if (instance.getGame().isHasSomeoneWon())
            return;
        var solos = instance.getPlayerManager().getAliveSoloPlayersListNonLambda();

        // Team Games
        if (instance.getTeamManger().getTeamSize() > 1) {
            var teamsAlive = instance.getTeamManger().getAliveTeams();
            if ((teamsAlive.size() + solos.size()) == 1) {
                var optionalTeam = teamsAlive.stream().findFirst();
                if (optionalTeam.isPresent()) {
                    Bukkit.getPluginManager().callEvent(new TeamWinEvent(optionalTeam.get().getTeamID(), true));
                    instance.getGame().setHasSomeoneWon(true);
                    Bukkit.getScheduler().runTask(instance, () -> {
                        Kern.getInstance().getChatManager().setSpecChat(false);
                    });
                } else if (solos.size() == 1) {
                    var optionalPlayer = solos.get(0);
                    if (optionalPlayer != null) {
                        Bukkit.getPluginManager().callEvent(new PlayerWinEvent(optionalPlayer.getUUID(), true));
                        instance.getGame().setHasSomeoneWon(true);
                        Bukkit.getScheduler().runTask(instance, () -> {
                            Kern.getInstance().getChatManager().setSpecChat(false);
                        });
                    }
                }
            }

        } else if (instance.getPlayerManager().getAlivePlayers() == 1) {
            // FFA Games
            if (!solos.isEmpty()) {
                var lastAlivePlayer = solos.get(0);
                if (lastAlivePlayer != null) {
                    Bukkit.getPluginManager().callEvent(new PlayerWinEvent(lastAlivePlayer.getUUID(), true));
                    instance.getGame().setHasSomeoneWon(true);
                    Bukkit.getScheduler().runTask(instance, () -> {
                        Kern.getInstance().getChatManager().setSpecChat(false);
                    });
                }
            }

        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // 3-second timeout to get respawned in spectator mode.
        Player p = e.getEntity();
        var game = instance.getGame();

        p.getActivePotionEffects().forEach(all -> p.removePotionEffect(all.getType()));
        p.setGameMode(GameMode.SPECTATOR);
        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

        p.getWorld().strikeLightningEffect(p.getLocation());
        p.sendTitle(Title.builder().title("")
                .subtitle(new ComponentBuilder("YOU ARE DEAD").bold(true).color(ChatColor.DARK_RED).create()).build());
        p.playSound(p.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.VOICE, 1.0f, 0.1f);

        var inv = p.getInventory().getContents();
        UHCPlayer uhcPlayer = instance.getPlayerManager().getPlayer(p.getUniqueId());
        if (uhcPlayer != null) {
            if (uhcPlayer.isAlive()) {
                uhcPlayer.setAlive(false);
                uhcPlayer.setDead(true);
                uhcPlayer.setLastKnownHealth(0.0);
                uhcPlayer.setLastKnownPositionFromLoc(p.getLocation());
                uhcPlayer.setLastKnownInventory(inv);
                var name = p.getName().toString();
                if (!game.isPrivateGame() && game.getWhitelist().containsKey(name)) {
                    game.getWhitelist().remove(name);
                }
                if (!game.isPrivateGame() && instance.getGame().getGameInfo() == GameInfo.OFFICIAL
                        && game.getGameTime() > game.getPvpTime() && game.getGameTime() < game.getBorderTime()) {
                    Bukkit.getScheduler().runTaskLater(instance, () -> {
                        if (!game.getWhitelist().containsKey(name) && !p.hasPermission("uhc.spec.ingame"))
                            p.kickPlayer(ChatColor.WHITE + "Your spectator period is over.\n" + Game.getUpToMVP());
                    }, 20 * 60);
                }

                var combatLoggers = instance.getGame().getCombatLoggers();
                var uuid = uhcPlayer.getUUID().toString();

                if (combatLoggers.containsKey(uuid)) {
                    var npcID = combatLoggers.get(uuid);
                    var combatlog = CitizensAPI.getNPCRegistry().getByUniqueId(npcID);
                    if (combatlog.isSpawned()) {
                        combatlog.despawn();
                        combatLoggers.remove(uuid);
                        combatlog.destroy();
                    }
                }

                Bukkit.getPluginManager()
                        .callEvent(new UHCPlayerDequalificationEvent(uhcPlayer, DQReason.DEATH, false));

            }
        }
        if (p.getKiller() != null) {
            Player killer = p.getKiller();
            Bukkit.getScheduler().runTaskLater(instance, () -> {
                if (p.getGameMode() == GameMode.SPECTATOR && p.isOnline() && killer.getGameMode() == GameMode.SURVIVAL
                        && killer.isOnline()) {
                    p.setSpectatorTarget(killer);
                }
            }, 160L);
            UHCPlayer uhcKiller = instance.getPlayerManager().getPlayer(killer.getUniqueId());
            var team = instance.getTeamManger().getPlayerTeam(killer.getUniqueId());
            if (team != null)
                team.addKills(1);

            uhcKiller.setKills(uhcKiller.getKills() + 1);
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerWin(PlayerWinEvent e) {
        var player = e.getPlayer();
        var uuid = player.getUniqueId();
        var playerManager = instance.getPlayerManager();

        playWinEffect(player);

        List<UHCPlayer> list = new ArrayList<>();
        list.add(playerManager.getPlayer(uuid));

        if (instance.getGame().getGameInfo() == GameInfo.OFFICIAL) {
            playerManager.updateStats(list);
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onTeamWinEvent(TeamWinEvent e) {
        var playerManager = instance.getPlayerManager();
        var team = instance.getTeamManger().getTeamMap().get(e.getTeamUUID());

        playTeamWinEffect(team);

        var list = team.getMembersUUIDStream().map(id -> instance.getPlayerManager().getPlayer(id))
                .collect(Collectors.toList());

        if (instance.getGame().getGameInfo() == GameInfo.OFFICIAL) {
            playerManager.updateStats(list);
        }
    }

    /*
     * Recursive methods:
     */
    private Material getRandomFence() {
        return possibleFence.get(new Random().nextInt(possibleFence.size()));
    }

    public static void playWinForTeam(Team team, String title) {
        var winnersName = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        var winnersTitle = Title.builder()
                .title(new ComponentBuilder("You Win!").bold(true).color(ChatColor.GOLD).create())
                .subtitle(ChatColor.GREEN + "Congratulations " + winnersName.toString()).stay(6 * 20).fadeIn(10)
                .fadeOut(3 * 20).build();
        var titleToOthers = Title.builder().title(new ComponentBuilder(title).bold(true).color(ChatColor.GOLD).create())
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

    public void playTeamWinEffect(Team team) {
        instance.getGame().setDeathMatchDamage(false);
        var winnersName = team.getOfflinePlayersStream().map(OfflinePlayer::getName).collect(Collectors.toList());
        var winnersTitle = Title.builder()
                .title(new ComponentBuilder("Victory!").bold(true).color(ChatColor.GOLD).create())
                .subtitle(ChatColor.GREEN + "Congratulations "
                        + (team.isCustomName() ? team.getTeamDisplayName() : winnersName.toString()))
                .stay(6 * 20).fadeIn(10).fadeOut(3 * 20).build();
        var titleToOthers = Title.builder()
                .title(new ComponentBuilder("Victory!").bold(true).color(ChatColor.GOLD).create())
                .subtitle(ChatColor.GREEN + (team.isCustomName() ? team.getTeamDisplayName() : winnersName.toString())
                        + " have won!")
                .stay(6 * 20).fadeIn(10).fadeOut(3 * 20).build();

        team.getPlayerStream().forEach(member -> {
            member.playEffect(EntityEffect.TOTEM_RESURRECT);
            member.sendTitle(winnersTitle);
            fireWorksEffect(member);
        });

        Bukkit.getOnlinePlayers().stream().filter(all -> !winnersName.contains(all.getName()))
                .forEach(all -> all.sendTitle(titleToOthers));
    }

    private void playWinEffect(Player player) {
        instance.getGame().setDeathMatchDamage(false);
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

    private static void fireWorksEffect(Player player) {
        var command1 = "execute in minecraft:world run summon firework_rocket %d %d %d {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:1,Explosions:[{Type:2,Flicker:0,Trail:1,Colors:[I;3887386,8073150,2651799,4312372],FadeColors:[I;3887386,11250603,4312372,15790320]}]}}}}";
        var command2 = "execute in minecraft:world run summon firework_rocket %d %d %d {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:2,Explosions:[{Type:3,Flicker:1,Trail:1,Colors:[I;5320730,2437522,8073150,11250603,6719955],FadeColors:[I;2437522,2651799,11250603,6719955,15790320]}]}}}}";
        var command3 = "execute in minecraft:world run summon firework_rocket %d %d %d {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:3,Explosions:[{Type:1,Flicker:1,Trail:1,Colors:[I;11743532,14602026,12801229,15435844],FadeColors:[I;11743532,14188952,15435844]}]}}}}";

        UHC.newChain().delay(1).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command1, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command2, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command3, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command1, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command2, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command3, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command1, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command2, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).delay(20).sync(() -> {
            var loc = player.getLocation();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(command3, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()));
        }).sync(TaskChain::abort).execute();
    }

}
