package me.infinityz.minigame.listeners;

import java.util.stream.Collectors;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.entity.Egg;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import co.aikar.taskchain.TaskChain;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.events.GameStartedEvent;
import me.infinityz.minigame.events.NetherDisabledEvent;
import me.infinityz.minigame.events.TeleportationCompletedEvent;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.gamemodes.types.UHCMeetup;
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
    public void onDiscoverBook(PlayerRecipeBookClickEvent e){
        e.setCancelled(true);
        instance.getGuiManager().getMainGui().open(e.getPlayer());
    }

    @EventHandler
    public void DisableAdvancements(PlayerAdvancementCriterionGrantEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL)
            e.setCancelled(true);
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
        var damager = e.getDamager();
        if (damager instanceof Trident || damager instanceof Egg || damager instanceof FishHook
                || damager instanceof Snowball || !(damager instanceof Projectile))
            return;

        if (!(((Projectile) e.getDamager()).getShooter() instanceof Player))
            return;
        if (!(e.getEntity() instanceof Player))
            return;

        Player shooter = ((Player) ((Projectile) e.getDamager()).getShooter());

        Player p = (Player) e.getEntity();

        if (p.getHealth() - e.getFinalDamage() <= 0.0D || p.isBlocking())
            return;

        if (shooter == p)
            return;

        shooter.sendMessage(ChatColor.GOLD + "🏹 " + p.getDisplayName() + ChatColor.GRAY + " is at " + ChatColor.WHITE
                + (((int) (p.getHealth() - e.getFinalDamage())) / 2.0D) + ChatColor.DARK_RED + "❤");

    }

    @EventHandler
    public void login(PlayerLoginEvent e) {
        var player = e.getPlayer();
        if (player.hasPermission("staff.perm") || player.hasPermission("group.host"))
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + player.getName().toString());
    }

    @EventHandler
    public void joinMessage(PlayerJoinEvent e) {
        var player = e.getPlayer();

        e.setJoinMessage("");

        player.setPlayerListHeader(Game.getTablistHeader());
        if (player.getUniqueId().compareTo(instance.getGame().getHostUUID()) == 0) {
            player.addAttachment(instance).setPermission("group.host", true);
            player.updateCommands();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + player.getName().toString());
        }

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
        if (instance.getGameStage() == Stage.INGAME) {
            var worldToTeleport = Bukkit.getWorld("world");
            var radius = (int) worldToTeleport.getWorldBorder().getSize() / 2;
            // Teleport all players currently in the nether to the overworld.
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getWorld().getEnvironment() == Environment.NETHER)
                    .forEach(netherPlayer -> netherPlayer.teleportAsync(
                            ChunksManager.centerLocation(ChunksManager.findScatterLocation(worldToTeleport, radius))));
            // Mensaje para todos.
            Bukkit.broadcastMessage(ChatColor.of("#2be49c") + "The Nether has been disabled.");
        }

    }

    @EventHandler
    public void onPortal(PlayerPortalEvent e) {
        if (e.getPlayer().getWorld().getName().toString() == "lobby")
            e.setCancelled(true);
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
            players.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 20));
            players.setFoodLevel(26);
            players.playSound(players.getLocation(), Sound.ENTITY_RAVAGER_CELEBRATE, 1, 1);

            if (!instance.getGamemodeManager().isScenarioEnable(UHCMeetup.class))
                Bukkit.dispatchCommand(players, "config");

            bar.addPlayer(players);
        });
        Bukkit.broadcastMessage(GameLoop.SHAMROCK_GREEN + "UHC has started!");
        showRules();

        new AntiFallDamage(instance, Bukkit.getOnlinePlayers().stream()
                .map(p -> p.getUniqueId().getMostSignificantBits()).collect(Collectors.toList()));

    }

    public void showRules() {
        var rules = instance.getGame().getRules();
        var count = 0;
        var chain = UHC.newChain().sync(() -> {
            Bukkit.broadcastMessage("");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "globalmute true");
        });

        while (count <= rules.length) {
            if (count == rules.length) {
                chain.delay(60).sync(() -> {
                    Bukkit.broadcastMessage("");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "globalmute false");
                    Bukkit.broadcastMessage("");
                });
                break;
            }
            final var current = count;
            chain.delay(60).sync(() -> {
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(rules[current]);
            });
            count++;
        }
        chain.sync(TaskChain::abort).execute();

    }

}