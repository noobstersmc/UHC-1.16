package me.infinityz.minigame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.block.Banner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.enums.Stage;
import me.infinityz.minigame.events.NetherDisabledEvent;
import me.infinityz.minigame.events.TeleportationCompletedEvent;
import me.infinityz.minigame.game.Game;
import me.infinityz.minigame.scoreboard.IngameScoreboard;
import me.infinityz.minigame.tasks.GameLoop;
import net.md_5.bungee.api.ChatColor;

public class GlobalListener implements Listener {

    private UHC instance;

    public GlobalListener(UHC instance) {
        this.instance = instance;
    }

    @EventHandler
    public void joinMessage(PlayerJoinEvent e) {
        e.getPlayer().sendMessage(ChatColor.BLUE + "Discord! discord.noobsters.net\n" + ChatColor.AQUA
                + "Twitter! twitter.com/NoobstersMC\n" + ChatColor.GOLD + "Donations! noobsters.buycraft.net");
        e.setJoinMessage("");
        var footer = GameLoop.HAVELOCK_BLUE + "\nJoin Our UHC Community!\n" + GameLoop.SHAMROCK_GREEN
                + "discord.noobsters.net";
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
        // Mensaje para todos. Algo mas?
        Bukkit.broadcastMessage(ChatColor.of("#2be49c") + "The Nether has been disabled.");

    }

    @EventHandler
    public void onPortal(PlayerPortalEvent e) {
        if (!instance.getGame().isNether() && e.getTo().getWorld().getEnvironment() == Environment.NETHER) {
            e.getPlayer().sendMessage(ChatColor.RED + "Nether is currently disabled!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent e) {
        if (!instance.getGame().isNether() && e.getReason() == CreateReason.FIRE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoinInNether(PlayerJoinEvent e) {
        if (!instance.getGame().isNether()) {// If nether is disabled
            var player = e.getPlayer();
            // If player's world is nether, scatter them in the overworld.
            if (player.getWorld().getEnvironment() == Environment.NETHER) {
                var worldToTeleport = Bukkit.getWorlds().get(0);
                var radius = (int) worldToTeleport.getWorldBorder().getSize() / 2;
                // Teleport Async to save resources.
                player.teleportAsync(
                        ChunksManager.centerLocation(ChunksManager.findScatterLocation(worldToTeleport, radius)));
            }
        }
    }

    /*
     * End portal cancel creation
     */

    @EventHandler
    public void onInteractWithPortal(PlayerInteractEvent e) {
        if (!instance.getGame().isEnd() && e.getAction() == Action.RIGHT_CLICK_BLOCK
                && e.getItem().getType() == Material.ENDER_EYE)
            e.setCancelled(true);

    }

    /**
     * Apple Rate code
     */
    @EventHandler
    public void onLeaf(LeavesDecayEvent e) {
        if (Math.random() <= 0.0080) {
            e.getBlock().setType(Material.AIR);
            e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(),
                    new ItemStack(Material.APPLE));
        }

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
     * Strength nerf
     */
    @EventHandler
    public void strengthFix(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() != EntityType.PLAYER)
            return;
        var damager = (Player) e.getDamager();
        var strength = damager.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
        if (strength != null) {
            var strengthAmplifier = 1 + strength.getAmplifier();
            var differential = strengthAmplifier * 1.5;
            e.setDamage(e.getDamage() - differential);
        }

    }

    /**
     * Nerfed critical damage to + 2 instead of 1.5x
     */
    @EventHandler(priority = EventPriority.LOW)
    public void nerfCriticalDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER || e.getDamager().getType() != EntityType.PLAYER)
            return;
        var damager = (Player) e.getDamager();
        if (isCritical(damager)) {
            final var damage = e.getDamage();
            final var backToBaseDamage = damage / 1.5;
            final var damageDifferential = damage - backToBaseDamage;
            if (damageDifferential > 2)
                e.setDamage(backToBaseDamage + 2);

        }
    }

    private boolean isCritical(Player player) {
        return player.getFallDistance() > 0.0F && !player.isOnGround() && !player.isInsideVehicle()
                && !player.hasPotionEffect(PotionEffectType.BLINDNESS)
                && player.getLocation().getBlock().getType() != Material.LADDER
                && player.getLocation().getBlock().getType() != Material.VINE;
    }

    /**
     * Global Mute
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent e) {
        if (instance.getGame().isGlobalMute() && !e.getPlayer().hasPermission("staff.perm")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Globalmute is Enabled.");
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent  e){
        if(e.getRecipe() != null && e.getRecipe().getResult().getType() == Material.SHIELD){
            var team = instance.getTeamManger().getPlayerTeam(e.getView().getPlayer().getUniqueId());
            if(team != null){
                var pattern = team.getTeamShieldPattern();
                if(pattern != null){
                    var item = e.getRecipe().getResult();
                    var meta = item.getItemMeta();
                    var bmeta = (BlockStateMeta)meta;
                    var banner = (Banner)bmeta.getBlockState();
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
            // Remove all potion effects
            var bar = Game.getBossbar();
            Bukkit.getOnlinePlayers().forEach(players -> {
                // Send the new scoreboard
                var sb = new IngameScoreboard(players, instance);
                sb.update();
                instance.getScoreboardManager().getFastboardMap().put(players.getUniqueId().toString(), sb);

                players.getActivePotionEffects().forEach(all -> players.removePotionEffect(all.getType()));

                players.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 15, 20));
                players.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 15, 20));
                players.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 15, 20));
                players.playSound(players.getLocation(), Sound.ENTITY_RAVAGER_CELEBRATE, 1, 1);

                bar.addPlayer(players);
            });
            Bukkit.broadcastMessage(GameLoop.SHAMROCK_GREEN + "UHC has started!");

            instance.setGameStage(Stage.INGAME);

            instance.getListenerManager().registerListener(instance.getListenerManager().getIngameListeners());
            new GameLoop(instance).runTaskTimerAsynchronously(instance, 0L, 20L);

        }, e.getStartDelayTicks());

    }

}