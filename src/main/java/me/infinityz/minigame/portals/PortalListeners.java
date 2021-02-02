package me.infinityz.minigame.portals;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import gnu.trove.map.hash.THashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("portals")
public class PortalListeners extends BaseCommand implements Listener {

    private UHC instance;
    private THashMap<UUID, Long> portalProtectionMap = new THashMap<>();
    private static String PORTAL_PROT_OBTAINED = ChatColor.RED + "You have acquired 20 seconds of portal protection.";
    private static String PORTAL_PROT_PLAYER_PROTECTED = ChatColor.RED + "You can't damage %s for the next %.1f" + "s";
    private static String PORTAL_PROT_LOST = ChatColor.RED + "You have lost your portal protection.";
    private static String PORTAL_PROT_OVER_ACTIONBAR = ChatColor.YELLOW + " ⚠ ";
    private static String PORTAL_PROT_STATUS_ACTIONBAR = ChatColor.GREEN + "Portal protection: %.1f" + "s";

    public PortalListeners(final UHC instance) {
        this.instance = instance;
        instance.getListenerManager().registerListener(this);
        instance.getCommandManager().registerCommand(this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            var iterator = portalProtectionMap.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                var player = Bukkit.getPlayer(entry.getKey());
                var differential = entry.getValue() - System.currentTimeMillis();
                if (differential <= 0) {
                    iterator.remove();
                    if (player != null && player.isOnline()) {
                        player.sendMessage(PORTAL_PROT_LOST);
                        player.sendActionBar(PORTAL_PROT_OVER_ACTIONBAR);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.6f);
                    }
                } else if (player != null && player.isOnline()) {
                    player.sendActionBar(String.format(PORTAL_PROT_STATUS_ACTIONBAR, differential / 1000.0D));
                }
            }
        }, 2L, 2L);

    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            var damagerFromProj = getProjectileOwner(e);
            var damager = damagerFromProj != null ? damagerFromProj
                    : (e.getDamager() instanceof Player ? (Player) e.getDamager() : null);
            var time = portalProtectionMap.get(e.getEntity().getUniqueId());

            if (damager != null && time != null) {
                damager.sendMessage(String.format(PORTAL_PROT_PLAYER_PROTECTED, e.getEntity().getName(),
                        (time - System.currentTimeMillis()) / 1000.0D));
                e.setCancelled(true);
            } else if (damager != null && portalProtectionMap.contains(damager.getUniqueId())) {
                portalProtectionMap.remove(damager.getUniqueId());
                damager.sendMessage(PORTAL_PROT_LOST);
                damager.sendActionBar(PORTAL_PROT_OVER_ACTIONBAR);
            }

        }
    }

    @EventHandler
    public void onAllDamage(EntityDamageEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            var player = (Player) e.getEntity();
            var time = portalProtectionMap.get(player.getUniqueId());
            if (time != null) {
                e.setCancelled(true);
            }
        }
    }

    private Player getProjectileOwner(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Projectile) {
            var proj = (Projectile) e.getDamager();
            if (proj.getShooter() != null && proj.getShooter() instanceof Player)
                return (Player) proj.getShooter();
        }
        return null;
    }

    @CommandCompletion("@worlds")
    @Default
    public void findPortal(final Player player, @Name("world") final String world) {
        var w = Bukkit.getWorld(world);
        if (w != null) {
            player.sendMessage("Unloading world " + world);
            Bukkit.unloadWorld(w, false);
        } else {
            WorldCreator wc = new WorldCreator(world);
            if (world.contains("_nether"))
                wc.environment(Environment.NETHER);
            wc.createWorld();

            player.sendMessage("Created world " + world);
        }

    }

    @Subcommand("pc")
    @CommandCompletion("@worlds <Integer>")
    public void onPortalCreate(final Player player, World world, int ratio, boolean go) {
        var initial_time = System.currentTimeMillis();
        travelDimensions(player, player.getLocation(), world, ratio, go);
        Bukkit.broadcastMessage("Took " + (System.currentTimeMillis() - initial_time) + "ms to teleport.");
    }

    @Subcommand("shape")
    public void findShape(final Player player, @Default("3") Integer row, @Default("2") Integer columm) {
        var targetBlock = player.getTargetBlock(5);
        // 3 by 2 is nether portal
        var portalShape = PortalShape.of(targetBlock.getLocation());
        var teleportLocation = portalShape.getTeleportLocation(player);
        player.teleport(teleportLocation);
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    public static class Portal {
        Block[][] portal_blocks;
        Block[][][] frame;
        Block[][][] air_blocks;

        public static Portal createPortal(Location loc) {
            final var b = loc.getBlock();
            final var portalBlocks = new Block[4][5];
            final var frameBlocks = new Block[4][5][3];
            final var airBlocks = new Block[4][5][3];

            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 5; y++) {
                    for (int z = 0; z < 3; z++) {
                        if (z == 1) {
                            if ((x == 1 || x == 2) && y >= 1 && y <= 3) {
                                var relative = b.getRelative(x, y, z);
                                relative.setType(Material.AIR);
                                portalBlocks[x][y] = relative;
                                continue;
                            }
                            var relative = b.getRelative(x, y, z);
                            relative.setType(Material.OBSIDIAN);
                            frameBlocks[x][y][z] = relative;
                        } else if (y == 0) {

                            var relative = b.getRelative(x, y, z);
                            relative.setType(Material.OBSIDIAN);
                            frameBlocks[x][y][z] = relative;
                        } else {
                            var relative = b.getRelative(x, y, z);
                            relative.setType(Material.AIR);
                            airBlocks[x][y][z] = relative;
                        }

                    }
                }

            }

            portalBlocks[1][1].setType(Material.FIRE);

            return of(portalBlocks, frameBlocks, airBlocks);
        }

        public Location getTeleportLocation() {
            var left = portal_blocks[1][2];
            var right = portal_blocks[2][2];
            return new Location(left.getWorld(), (left.getX() + right.getX()) / 2, left.getY(),
                    (left.getZ() + right.getZ()) / 2);
        }
    }

    // Travel function
    public boolean travelDimensions(Entity entity, Location from, World target, int ratio, boolean go) {
        var calcRatio = new Location(target, from.getX() / ratio, from.getY(), from.getZ() / ratio);
        if(!go){
            calcRatio = new Location(target, from.getX() * ratio, from.getY(), from.getZ() * ratio);

            var worldBorderSize = (int) Bukkit.getWorld("world").getWorldBorder().getSize() / 2;
            var absX = Math.abs(calcRatio.getX());
            var absZ = Math.abs(calcRatio.getZ());
            if(absX >= worldBorderSize){
                if(calcRatio.getX() >= 0)
                    calcRatio.setX(worldBorderSize-10);
                else
                    calcRatio.setX(~(worldBorderSize - 10));
            }
            if(absZ >= worldBorderSize){
                if(calcRatio.getZ() >= 0)
                    calcRatio.setZ(worldBorderSize-10);
                else
                    calcRatio.setZ(~(worldBorderSize - 10));
            }
        }
        

        final var ratioed_target = calcRatio;

        var min = ratioed_target.clone().add(-64, 0, -64);
        min.setY(30);
        var max = ratioed_target.clone().add(64, 0, 64);
        max.setY(100);

        double last_distance = Integer.MAX_VALUE;
        Location closest_loc = null;

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY()
                    && y <= Math.min(max.getBlockY(), target.getMaxHeight()); y++) {
                for (int z = max.getBlockZ(); z >= min.getBlockZ(); z--) {
                    var block = target.getBlockAt(x, y, z);
                    if (block.getType() == Material.NETHER_PORTAL) {
                        var portalShape = PortalShape.of(block.getLocation());
                        var tpLoc = portalShape.getTeleportLocation(entity);
                        entity.teleport(tpLoc);
                        // portal protection
                        if (entity instanceof Player) {
                            portalProtectionMap.put(entity.getUniqueId(), System.currentTimeMillis() + 20_000);
                            entity.sendMessage(PORTAL_PROT_OBTAINED);
                        }
                        // Clean lava if nearby
                        replaceNearbyLava(portalShape.getBlocks()[0][0].getRelative(BlockFace.DOWN).getLocation(), 10);
                        return true;
                    } else if (block.getType() != Material.AIR
                            && block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                        var loc = block.getLocation();
                        var d = loc.distance(ratioed_target);
                        if (d < last_distance) {
                            last_distance = d;
                            closest_loc = loc;
                        }
                    }
                }
            }
        }

        var portal = Portal.createPortal(closest_loc != null ? closest_loc : ratioed_target);
        var tpLoc = portal.getTeleportLocation();
        entity.teleport(tpLoc);
        // portal protection
        if (entity instanceof Player) {
            portalProtectionMap.put(entity.getUniqueId(), System.currentTimeMillis() + 20_000);
            entity.sendMessage(PORTAL_PROT_OBTAINED);
        }
        // Clean lava if nearby

        var lowest_block = portal.getPortal_blocks()[1][1].getRelative(BlockFace.DOWN);
        replaceNearbyLava(lowest_block.getLocation(), 10);

        return false;
    }

    private void replaceNearbyLava(Location location, int radius) {
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                if (y <= location.getBlockY())
                    continue;
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    var block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.LAVA) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    /**
     * NETHER ALGO ENDS
     */

    HashMap<Player, Integer> p = new HashMap<>();
    HashMap<Player, Location> portalLocation = new HashMap<>();

    @EventHandler
    public void enterPortal(EntityPortalEnterEvent e) {
        if (e.getEntityType() != EntityType.PLAYER)
            return;
        final var player = (Player) e.getEntity();
        final var cd = p.get(player);
        portalLocation.put(player, player.getLocation());
        if (cd != null) {
            return;
        }
        travel(player, 50);

    }

    public void travel(Player player, int cd) {
        if (!p.containsKey(player)) {
            p.put(player, cd);
        }
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            var block = player.getLocation();
            var distance = block.distance(portalLocation.get(player));
            if (distance == 0.0) {
                if (cd > 0) {
                    travel(player, (cd - 1));
                } else {
                    var world = player.getWorld();
                    if (world.getEnvironment() == Environment.NORMAL) {
                        var nether = Bukkit.getWorld(world.getName() + "_nether");
                        if (nether != null || !instance.getGame().isNether()) {
                            travelDimensions(player, player.getLocation(), nether, 4, true);
                        } else {
                            player.sendMessage(ChatColor.RED + "Nether world not available.");
                        }
                        Bukkit.getScheduler().runTaskLater(instance, () -> {
                            p.remove(player);
                        }, 40);
                    } else if (world.getEnvironment() == Environment.NETHER) {
                        var overworld = Bukkit.getWorld(world.getName().replace("_nether", ""));
                        if (overworld != null) {
                            travelDimensions(player, player.getLocation(), overworld, 4, false);
                        } else {
                            player.sendMessage(ChatColor.RED + "Overworld not available.");
                        }
                        Bukkit.getScheduler().runTaskLater(instance, () -> {
                            p.remove(player);
                        }, 40);
                    }
                }

            } else {
                p.remove(player);
            }
        }, 1L);
    }

    /**
     * Cancel vanilla behavior
     */
    @EventHandler
    public void cancelPortal(PlayerPortalEvent e) {
        e.setCancelled(true);
    }

}
