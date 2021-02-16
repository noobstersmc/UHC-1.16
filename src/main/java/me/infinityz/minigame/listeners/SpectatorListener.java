package me.infinityz.minigame.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.mrmicky.fastinv.FastInv;
import gnu.trove.map.hash.THashMap;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.game.UpdatableInventory;
import me.infinityz.minigame.players.UHCPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class SpectatorListener implements Listener {
    private UHC instance;

    private static Class<?> packetClass;
    private static Constructor<?> packetConstructor;
    private static Method sendPacket;
    private THashMap<UUID, Long> pvpSpecInfoCoolDown = new THashMap<>();
    private THashMap<UUID, Long> diamondsSpecInfoCoolDown = new THashMap<>();

    public SpectatorListener(final UHC instance) {
        this.instance = instance;

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            var iterator = pvpSpecInfoCoolDown.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                var differential = entry.getValue() - System.currentTimeMillis();
                if (differential <= 0) {
                    iterator.remove();
                }
            }
        }, 2L, 2L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            var iterator = diamondsSpecInfoCoolDown.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                var differential = entry.getValue() - System.currentTimeMillis();
                if (differential <= 0) {
                    iterator.remove();
                }
            }
        }, 2L, 2L);

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPVP(EntityDamageByEntityEvent e) {
        if(!instance.getGame().isPvp()) return;

        if (e.getEntity() instanceof Player) {
            Player p1 = (Player) e.getEntity();
            Player p2 = null;
            if (e.getDamager() instanceof Player) {
                p2 = (Player) e.getDamager();
            } else if (e.getDamager() instanceof Projectile) {
                Projectile proj = (Projectile) e.getDamager();
                if (proj.getShooter() instanceof Player) {
                    p2 = (Player) proj.getShooter();
                }
            }

            if (p2 != null) {
                var player2 = p2;
                
                var team1 = instance.getTeamManger().getPlayerTeam(p1.getUniqueId());
                
                if (p1 == player2 
                || (team1 != null && team1.isMember(p2.getUniqueId()))
                || ( pvpSpecInfoCoolDown.containsKey(p1.getUniqueId()) && pvpSpecInfoCoolDown.containsKey(player2.getUniqueId()) )) return;
                

                pvpSpecInfoCoolDown.put(p1.getUniqueId(), System.currentTimeMillis() + 15_000);
                pvpSpecInfoCoolDown.put(p2.getUniqueId(), System.currentTimeMillis() + 15_000);

                var component = new ComponentBuilder("")
                            .append("" + p1.getName().toString()).color((ChatColor.of("#d53088")))
                            .event(new ClickEvent(Action.RUN_COMMAND, "/t " + p1.getName()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")))
                            .append(ChatColor.WHITE + "" + ChatColor.BOLD + " VS ")
                            .append("" + player2.getName().toString()).color(ChatColor.of("#d53088"))
                            .event(new ClickEvent(Action.RUN_COMMAND, "/t " + player2.getName()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")))
                            .create();

                Bukkit.getOnlinePlayers().forEach(players ->{
                    UHCPlayer player = instance.getPlayerManager().getPlayer(players.getUniqueId());
                    if(player.isSpecInfo()){
                        players.sendMessage(component);
                    }
                });

            }
        }
    }
    
    /*
     * Spec info
     */
    
    @EventHandler
    public void gamemodeChange(PlayerGameModeChangeEvent e){
        UHCPlayer uhcPlayer = null;
        if(instance.getPlayerManager().getUhcPlayerMap().containsKey(e.getPlayer().getUniqueId().getMostSignificantBits()))
            uhcPlayer = instance.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());   
        
        if(uhcPlayer != null){
            if(e.getNewGameMode() == GameMode.SPECTATOR && e.getPlayer().hasPermission("staff.perm")){
                uhcPlayer.setSpecInfo(true);
            }else if(e.getNewGameMode() == GameMode.SURVIVAL){
                uhcPlayer.setSpecInfo(false);
            }
        }

    }

    @EventHandler
     public void onBreakSpecInfo(BlockBreakEvent e){
         if(e.getPlayer() == null) return;

         var block = e.getBlock();
         var p = e.getPlayer();
         UHCPlayer miner = instance.getPlayerManager().getPlayer(p.getUniqueId());
        
        if(block.getType() == Material.DIAMOND_ORE){
            miner.setMinedDiamonds(miner.getMinedDiamonds() + 1);

            if(diamondsSpecInfoCoolDown.containsKey(p.getUniqueId())) return;
            
            diamondsSpecInfoCoolDown.put(p.getUniqueId(), System.currentTimeMillis() + 15_000);
            
            var component = new ComponentBuilder(
                ChatColor.AQUA + "Diamond " + ChatColor.WHITE + "[" + miner.getMinedDiamonds() + "] ")
                .append(ChatColor.AQUA + "" + e.getPlayer().getName().toString())
                .event(new ClickEvent(Action.RUN_COMMAND, "/t " + e.getPlayer().getName()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")))
                .create();

            Bukkit.getOnlinePlayers().forEach(players ->{
                UHCPlayer player = instance.getPlayerManager().getPlayer(players.getUniqueId());

                if(player.isSpecInfo()){
                    players.sendMessage(component);
                }
                     
                
            });

        }else if(block.getType() == Material.GOLD_ORE){
            miner.setMinedGold(miner.getMinedGold() + 1);
            if(miner.getMinedGold() % 32 != 0) return;

            var component = new ComponentBuilder(
                            ChatColor.GOLD + "Gold " + ChatColor.WHITE + "[" + miner.getMinedGold() + "] ")
                            .append(ChatColor.GOLD + "" + e.getPlayer().getName().toString())
                            .event(new ClickEvent(Action.RUN_COMMAND, "/t " + e.getPlayer().getName()))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")))
                            .create();

            Bukkit.getOnlinePlayers().forEach(players ->{
                UHCPlayer player = instance.getPlayerManager().getPlayer(players.getUniqueId());
                if(player.isSpecInfo()){
                    players.sendMessage(component);
                }
            });

        }else if(block.getType() == Material.ANCIENT_DEBRIS){
            miner.setMinedAncientDebris(miner.getMinedAncientDebris() + 1);

            var component = new ComponentBuilder(
                    "Ancient Debris ").color(ChatColor.of("#95562F"))
                    .append(ChatColor.WHITE + "[" + miner.getMinedAncientDebris() + "] ")
                    .append("" + e.getPlayer().getName().toString()).color(ChatColor.of("#95562F"))
                    .event(new ClickEvent(Action.RUN_COMMAND, "/t " + e.getPlayer().getName()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")))
                    .create();

            Bukkit.getOnlinePlayers().forEach(players ->{
                UHCPlayer player = instance.getPlayerManager().getPlayer(players.getUniqueId());
                if(player.isSpecInfo()){
                    players.sendMessage(component);
                }
            });
        }
     }

    /*
     * Spec hider from no spectators
     */
    @EventHandler
    public void onJoinHide(PlayerJoinEvent e) {
        var player = e.getPlayer();
        // If gamemode is Spectator, then hide him from all other non spectators
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            Bukkit.getOnlinePlayers().stream().filter(all -> all.getGameMode() != GameMode.SPECTATOR)
                    .forEach(all -> all.hidePlayer(instance, player));
        } else {
            // If gamemode isn't Spectator, then hide all spectators for him.
            Bukkit.getOnlinePlayers().stream().filter(it -> it.getGameMode() == GameMode.SPECTATOR)
                    .forEach(all -> player.hidePlayer(instance, all.getPlayer()));
        }
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e) {
        var player = e.getPlayer();
        // If gamemode to change is spectator
        if (e.getNewGameMode() == GameMode.SPECTATOR) {
            // If player has no perms hide f3
            if (!player.hasPermission("uhc.spec.coords"))
                disableF3(player);

            Bukkit.getOnlinePlayers().stream().forEach(all -> {
                // If players are not specs, hide them the player
                if (all.getGameMode() != GameMode.SPECTATOR) {
                    all.hidePlayer(instance, player);
                } else {
                    // If players are specs, then show them to the player
                    player.showPlayer(instance, all);
                }
            });
        } else {
            enableF3(player);
            Bukkit.getOnlinePlayers().stream().forEach(all -> {
                // When switching to other gamemodes, show them if not visible to player
                if (!all.canSee(player)) {
                    all.showPlayer(instance, player);
                }
                // If one of the players is a spec, hide them from the player
                if (all.getGameMode() == GameMode.SPECTATOR) {
                    player.hidePlayer(instance, all);
                }
            });
        }
    }

    /*
     * Spectator disable F3 Codigo
     */
    public static void disableF3(Player player) {
        try {
            if (packetClass == null)
                packetClass = getNMSClass("PacketPlayOutEntityStatus");
            if (packetConstructor == null)
                packetConstructor = packetClass.getConstructor(new Class[] { getNMSClass("Entity"), byte.class });
            if (sendPacket == null)
                sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket",
                        new Class[] { getNMSClass("Packet") });
            Object packet = packetConstructor.newInstance(new Object[] { getHandle(player), Byte.valueOf((byte) 22) });
            sendPacket.invoke(getConnection(player), new Object[] { packet });
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public static void enableF3(Player player) {
        try {
            if (packetClass == null)
                packetClass = getNMSClass("PacketPlayOutEntityStatus");
            if (packetConstructor == null)
                packetConstructor = packetClass.getConstructor(new Class[] { getNMSClass("Entity"), byte.class });
            if (sendPacket == null)
                sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket",
                        new Class[] { getNMSClass("Packet") });
            Object packet = packetConstructor.newInstance(new Object[] { getHandle(player), Byte.valueOf((byte) 23) });
            sendPacket.invoke(getConnection(player), new Object[] { packet });
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    private static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = "net.minecraft.server." + version + nmsClassString;
        return Class.forName(name);
    }

    private static Object getConnection(Player player) throws SecurityException, NoSuchMethodException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Field conField = getHandle(player).getClass().getField("playerConnection");
        return conField.get(getHandle(player));
    }

    private static Object getHandle(Player player) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method getHandle = player.getClass().getMethod("getHandle", new Class[0]);
        return getHandle.invoke(player, new Object[0]);
    }

    /**
     * Spectator cancel any possible damage
     */
    @EventHandler
    public void onDamageSpec(EntityDamageEvent e) {
        if (e.getEntityType() == EntityType.PLAYER && ((Player) (e.getEntity())).getGameMode() == GameMode.SPECTATOR)
            e.setCancelled(true);
    }

    @EventHandler
    public void onStartSpec(PlayerStartSpectatingEntityEvent e) {
        if (e.getNewSpectatorTarget().getType() != EntityType.PLAYER) {
            e.setCancelled(true);
            return;
        }
        var player = e.getPlayer();
        // Change the name to display who they are spectating
        if (player.hasPermission("uhc.spec.name")) {
            player.setPlayerListName(player.getName() + ChatColor.RESET + "" + ChatColor.DARK_GRAY + " ("
                    + e.getNewSpectatorTarget().getName() + ")");
        }
    }

    @EventHandler
    public void onStopSpectating(PlayerStopSpectatingEntityEvent e) {
        var player = e.getPlayer();
        // Change the name back
        if (!player.getPlayerListName().equalsIgnoreCase(player.getName())) {
            player.setPlayerListName(player.getName());
        }
    }

    /*
     * Spec Inventory with right click.
     */
    @EventHandler
    public void invSpecEvent(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() == null || e.getRightClicked().getType() != EntityType.PLAYER) return;

        var player = e.getPlayer();
        var playerManager = instance.getPlayerManager();

        var uhcPlayer = playerManager.getPlayer(player.getUniqueId());

        if (!uhcPlayer.isSpecInfo() || e.getPlayer().getGameMode() != GameMode.SPECTATOR) return;
            
        var clicked = (Player) e.getRightClicked();

        var fastInv = new UpdatableInventory(5 * 9, clicked.getName() + "'s inventory'");
        fastInv.addUpdateTask(new BukkitRunnable() {
            @Override
            public void run() {
                if (isCancelled()) {
                    cancel();
                    return;
                }
                updateInventory(fastInv, clicked);
            }

        }, instance, 0, 20, true);
        fastInv.open(player);
    }

    private void updateInventory(FastInv fastInv, Player target) {
        var count = 0;
        for (var itemStack : target.getInventory()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                fastInv.setItem(count, new ItemStack(Material.AIR));
            } else {
                fastInv.setItem(count, itemStack);
            }
            count++;
        }
        // Obtain a list of all the active potion effects as strings
        var effects = target
                .getActivePotionEffects().stream().map(it -> ChatColor.GRAY + it.getType().getName() + " "
                        + (1 + it.getAmplifier()) + ": " + ChatColor.WHITE + (it.getDuration() / 20) + "s")
                .collect(Collectors.toList());
        // Create a new Item Stack
        var potionEffectsItem = new ItemStack(Material.GLASS_BOTTLE);
        // Obtain the meta
        var potionEffectsItemMeta = potionEffectsItem.getItemMeta();
        // Change the meta
        potionEffectsItemMeta.setDisplayName(ChatColor.GOLD + "Active Potion Effects:");
        potionEffectsItemMeta.setLore(effects);
        potionEffectsItem.setItemMeta(potionEffectsItemMeta);
        // Add the item to the inventory 41 is the one next to the offhand item.
        fastInv.setItem(41, potionEffectsItem);
        // Repeat for Health
        var healthItem = new ItemStack(Material.RED_BANNER);
        var healthItemMeta = healthItem.getItemMeta();
        healthItemMeta.setDisplayName(ChatColor.GOLD + "Health:");
        healthItemMeta.setLore(List.of(ChatColor.WHITE + "Hearts: " + (int) target.getHealth(),
                ChatColor.WHITE + "Absorption: " + (int) target.getAbsorptionAmount()));
        healthItem.setItemMeta(healthItemMeta);
        fastInv.setItem(42, healthItem);
        // Repeat for EXP values
        var experienceItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        var experienceItemMeta = experienceItem.getItemMeta();
        experienceItemMeta.setDisplayName(ChatColor.GOLD + "Experience:");
        experienceItemMeta.setLore(List.of(ChatColor.WHITE + "Levels: " + target.getLevel(),
                ChatColor.WHITE + "Percent to next level: " + String.format("%.2f", target.getExp() * 100)));
        experienceItem.setItemMeta(experienceItemMeta);
        fastInv.setItem(43, experienceItem);

    }

}