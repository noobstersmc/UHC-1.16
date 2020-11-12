package me.infinityz.minigame.listeners;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.block.Banner;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.events.GameStartedEvent;
import me.infinityz.minigame.events.GameTickEvent;
import me.infinityz.minigame.events.NetherDisabledEvent;
import me.infinityz.minigame.events.TeleportationCompletedEvent;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.scoreboard.IngameScoreboard;
import me.infinityz.minigame.tasks.AntiFallDamage;
import me.infinityz.minigame.tasks.GameLoop;
import net.md_5.bungee.api.ChatColor;

public class GlobalListener implements Listener {

    private UHC instance;

    public GlobalListener(UHC instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onShieldBreak(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            var victim = (Player) e.getEntity();
            var player = (Player) e.getDamager();
            if (victim.isBlocking() && isAxe(player.getInventory().getItemInMainHand())) {
                player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
            }

        }
    }

    private boolean isAxe(ItemStack e) {
        return e != null && e.getType().toString().contains("_AXE");
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onArrow(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Arrow))
            return;
        if (!(((Arrow) e.getDamager()).getShooter() instanceof Player))
            return;
        if (!(e.getEntity() instanceof Player))
            return;

        Player p = (Player) e.getEntity();

        if (p.getHealth() - e.getFinalDamage() <= 0.0D || p.isBlocking())
            return;

        Player shooter = ((Player) ((Arrow) e.getDamager()).getShooter());

        if (shooter == p)
            return;

        shooter.sendMessage(ChatColor.GOLD + "🏹 " + p.getDisplayName() + ChatColor.GRAY + " is at " + ChatColor.WHITE
                + (((int) (p.getHealth() - e.getFinalDamage())) / 2.0D) + ChatColor.DARK_RED + "❤");

    }

    //DEATHMATCH
    
    @EventHandler
    public void onDeathMatch(GameTickEvent e) {
        if (instance.getGame().isDeathMatch() && e.getSecond() % 5 == 0) {
            Bukkit.getScheduler().runTask(instance, ()->{
                Bukkit.getOnlinePlayers().forEach(players -> {
                    if (players.getGameMode() == GameMode.SURVIVAL)
                            players.setHealth(players.getHealth()-1);
                            players.damage(1);
                });
            });
        }
  
    }

    @EventHandler
    public void onAntiMining(GameTickEvent e) {
        if (instance.getGame().isAntiMining()) {
            Bukkit.getScheduler().runTask(instance, ()->{
                Bukkit.getOnlinePlayers().forEach(players -> {
                    if (players.getGameMode() == GameMode.SURVIVAL 
                        && players.getWorld().getEnvironment() != Environment.NETHER
                        && players.getLocation().getY() < 55)
                            players.sendActionBar(ChatColor.YELLOW + "⚠ You must be on surface at meetup.");
                });
            });
        }
  
    }

    @EventHandler
    public void onAntiMiningMine(BlockBreakEvent e){
        if(instance.getGame().isAntiMining()){
            var player = e.getPlayer().getLocation().getY();
            var block = e.getBlock().getLocation().getY();
            if(e.getPlayer().getWorld().getEnvironment() != Environment.NETHER && 
                player < 50 && player > block){
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "Mining is not allowed at meetup.");
            }
        }
    }

    @EventHandler
    public void joinMessage(PlayerJoinEvent e) {
        e.setJoinMessage("");
        var footer = GameLoop.HAVELOCK_BLUE + "\nJoin Our UHC Community!\n" + GameLoop.SHAMROCK_GREEN
                + "discord.noobsters.net\n" + ChatColor.AQUA + "twitter.com/NoobstersMC\n";
        e.getPlayer().setPlayerListHeaderFooter(Game.getTablistHeader(), footer);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage("");
    }

    /*
     * Nether disabled
     */
    @EventHandler
    public void onNetherDisabled(NetherDisabledEvent e) {
        var worldToTeleport = Bukkit.getWorlds().get(0);
        var radius = (int) worldToTeleport.getWorldBorder().getSize() / 2;
        // Teleport all players currently in the nether to the overworld.
        Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().getEnvironment() == Environment.NETHER)
                .forEach(netherPlayer -> netherPlayer.teleportAsync(
                        ChunksManager.centerLocation(ChunksManager.findScatterLocation(worldToTeleport, radius))));
        // Mensaje para todos. 
        Bukkit.broadcastMessage(ChatColor.of("#2be49c") + "The Nether has been disabled.");

    }

    /**
     * PVP boolean code
     */
    @EventHandler
    public void onPVP(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p2 = null;
            if (e.getDamager() instanceof Player) {
                p2 = (Player) e.getDamager();
            } else if (e.getDamager() instanceof Projectile) {
                Projectile proj = (Projectile) e.getDamager();
                if (proj.getShooter() instanceof Player) {
                    p2 = (Player) proj.getShooter();
                }
            }
            if (p2 != null && !instance.getGame().isPvp())
                e.setCancelled(true);
        }
    }

    /**
     * Shield feature
     */
    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {
        if (e.getRecipe() != null && e.getRecipe().getResult().getType() == Material.SHIELD) {
            var team = instance.getTeamManger().getPlayerTeam(e.getView().getPlayer().getUniqueId());
            if (team != null) {
                var pattern = team.getTeamShieldPattern();
                if (pattern != null) {
                    var item = e.getRecipe().getResult();
                    var meta = item.getItemMeta();
                    var bmeta = (BlockStateMeta) meta;
                    var banner = (Banner) bmeta.getBlockState();
                    banner.setPatterns(pattern);
                    banner.update();
                    bmeta.setBlockState(banner);
                    item.setItemMeta(bmeta);
                    e.getInventory().setResult(item);

                }
            }
        }

    }

    @EventHandler
    public void onTeleportCompleted(TeleportationCompletedEvent e) {
        Bukkit.broadcastMessage(GameLoop.SHAMROCK_GREEN + "Starting soon...");
        instance.getScoreboardManager().getUpdateTask().cancel();

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            instance.getScoreboardManager().purgeScoreboards();
            instance.getGame().setGameTime(0);
            instance.getGame().setStartTime(System.currentTimeMillis());
            instance.getListenerManager().unregisterListener(instance.getListenerManager().getScatter());
            Bukkit.getPluginManager().callEvent(new GameStartedEvent());

            instance.setGameStage(Stage.INGAME);

            instance.getListenerManager().registerListener(instance.getListenerManager().getIngameListeners());
            new GameLoop(instance).runTaskTimerAsynchronously(instance, 0L, 20L);

        }, e.getStartDelayTicks());

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onGameStartEvent(GameStartedEvent e) {
        // Remove all potion effects
        var bar = Game.getBossbar();
        Bukkit.getOnlinePlayers().forEach(players -> {
            // Send the new scoreboard
            var sb = new IngameScoreboard(players, instance);
            sb.update();
            instance.getScoreboardManager().getFastboardMap().put(players.getUniqueId().toString(), sb);

            players.getActivePotionEffects().forEach(all -> players.removePotionEffect(all.getType()));

            players.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20 * 1, 20));
            players.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 15, 20));
            players.setFoodLevel(26);
            players.playSound(players.getLocation(), Sound.ENTITY_RAVAGER_CELEBRATE, 1, 1);

            bar.addPlayer(players);
        });
        Bukkit.broadcastMessage(GameLoop.SHAMROCK_GREEN + "UHC has started!");
        // TODO: Add No fall damage for first hit
        new AntiFallDamage(instance, Bukkit.getOnlinePlayers().stream()
                .map(p -> p.getUniqueId().getMostSignificantBits()).collect(Collectors.toList()));
    }

}