package me.infinityz.minigame.gamemodes.types;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import co.aikar.taskchain.TaskChain;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.crafting.recipes.EnderRespawnRecipe;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.teams.objects.Team;
import net.md_5.bungee.api.ChatColor;

/**
 * EnderRespawn
 */
public class EnderRespawnLeader extends IGamemode implements Listener {
    private UHC instance;
    private EnderRespawnRecipe recipe;

    private ArrayList<Long> respawnedList = new ArrayList<>();

    public EnderRespawnLeader(UHC instance) {
        super("Ender RespawnLeader", "Respawn your team leader with a Respawn Crystal.", Material.END_CRYSTAL);
        this.instance = instance;
        this.recipe = new EnderRespawnRecipe(new NamespacedKey(instance, "respawn_crystal"), null, "Respawn Crystal");
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled() || !instance.getTeamManger().isTeams())
            return false;
    
        instance.getListenerManager().registerListener(this);
        Bukkit.addRecipe(recipe.getRecipe());
        Bukkit.getOnlinePlayers().forEach(all -> all.discoverRecipe(this.recipe.getNamespacedKey()));
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        Bukkit.removeRecipe(recipe.getNamespacedKey());
        respawnedList.clear();
        Bukkit.getOnlinePlayers().forEach(all -> all.undiscoverRecipe(this.recipe.getNamespacedKey()));
        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().discoverRecipe(this.recipe.getNamespacedKey());

    }

    boolean allowEnderRespawn(ItemStack item) {
        return item != null && item.getType() == Material.END_CRYSTAL && item.hasItemMeta()
                && item.getItemMeta().getDisplayName().contains("Respawn Crystal");
    }

    boolean canTeamLeaderRespawn(Team team) {
        if (team != null) {
            var leader = team.getTeamLeader();
            var leaderPlayer = Bukkit.getOfflinePlayer(leader);
            if (leaderPlayer.isOnline() && !respawnedList.contains(leader.getMostSignificantBits()))
                return true;
        }
        return false;
    }

    @EventHandler
    public void onEnderRespawnPlaceAttempt(PlayerInteractEvent e) {
        var player = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && instance.getTeamManger().isTeams()
            && player.getWorld().getEnvironment() == Environment.NORMAL) {
            var block = e.getClickedBlock();
            if (block.getType() != Material.AIR) {
                var itemInHand = e.getItem();
                if (allowEnderRespawn(itemInHand)) {
                    var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                    if(team == null){
                        player.sendMessage(ChatColor.RED + "You don't have a team.");
                        e.setCancelled(true);
                        e.setUseInteractedBlock(Result.DENY);
                        e.setUseItemInHand(Result.DENY);
                        return;                        
                    }
                    if (canTeamLeaderRespawn(team)) {
                        var uhcPlayer = instance.getPlayerManager().getPlayer(team.getTeamLeader());
                        if(uhcPlayer.isAlive() || uhcPlayer.getPlayer().getWorld().getEnvironment() != Environment.NORMAL){
                            player.sendMessage(ChatColor.RED + "Can't respawn is still alive or is not in overworld.");
                            e.setCancelled(true);
                            e.setUseInteractedBlock(Result.DENY);
                            e.setUseItemInHand(Result.DENY);
                        }else{
                            respawnAnimation(block.getLocation(), Bukkit.getPlayer(team.getTeamLeader()));
                            respawnedList.add(team.getTeamLeader().getMostSignificantBits());
                            player.getInventory().removeItem(itemInHand);

                        }
                    } else {
                        e.setCancelled(true);
                        e.setUseInteractedBlock(Result.DENY);
                        e.setUseItemInHand(Result.DENY);
                    }

                }
            }

        }
    }

    @EventHandler
    public void onAnvilRename(InventoryClickEvent e) {
        var clickedInventory = e.getClickedInventory();
        if (clickedInventory != null && clickedInventory.getType() == InventoryType.ANVIL) {
            var itemSlot0 = e.getInventory().getItem(0);
            if ((e.getSlotType() == SlotType.RESULT && allowEnderRespawn(e.getCurrentItem()))
                    || allowEnderRespawn(itemSlot0)) {
                e.setCancelled(true);
            }
        }
    }

    public void respawnAnimation(Location loc, Player toRespawn) {
        final double x = loc.getBlockX() + 0.0;
        final double y = loc.getBlockY() + 0.0;
        final double z = loc.getBlockZ() + 0.0;
        final var tower = "execute in minecraft:world run fill %.0f %.0f %.0f %.0f %.0f %.0f minecraft:obsidian destroy";
        UHC.newChain().delay(1).sync(() -> {
            // TOWER 1

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:world run weather thunder");

            loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.VOICE, 10, 0.1f);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(tower, x - 17, y - 5, z - 1, x - 15, y + 15, z + 1));

        }).delay(20).sync(() -> {
            // TOWER 2

            loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.VOICE, 10, 0.1f);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(tower, x + 17, y - 5, z - 1, x + 15, y + 15, z + 1));

        }).delay(20).sync(() -> {
            // TOWER 3

            loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.VOICE, 10, 0.1f);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(tower, x - 1, y - 5, z - 17, x + 1, y + 15, z - 15));

        }).delay(20).sync(() -> {
            // TOWER 4

            loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.VOICE, 10, 0.1f);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    String.format(tower, x - 1, y - 5, z + 17, x + 1, y + 15, z + 15));

        }).delay(60).sync(() -> {
            // CRYSTAL 1
            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE, 10, 1.5f);
            loc.getWorld().spawn(loc.clone().add(-16, 16.5, 0), EnderCrystal.class).setBeamTarget(loc);

        }).delay(20).sync(() -> {
            // CRYSTAL 2
            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE, 10, 1.5f);
            loc.getWorld().spawn(loc.clone().add(16, 16.5, 0), EnderCrystal.class).setBeamTarget(loc);

        }).delay(20).sync(() -> {
            // CRYSTAL 3
            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE, 10, 1.5f);
            loc.getWorld().spawn(loc.clone().add(0, 16.5, -16), EnderCrystal.class).setBeamTarget(loc);

        }).delay(20).sync(() -> {
            // CRYSTAL 4
            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE, 10, 1.5f);
            loc.getWorld().spawn(loc.clone().add(0, 16.5, 16), EnderCrystal.class).setBeamTarget(loc);

        }).delay(60).sync(() ->
        // LIGHTNING 1
        loc.getWorld().strikeLightning(loc.clone().set(x - 16, y + 16.5, z))

        ).delay(20).sync(() ->
        // LIGHTNING 2
        loc.getWorld().strikeLightning(loc.clone().set(x + 16, y + 16.5, z))

        ).delay(20).sync(() ->
        // LIGHTNING 3
        loc.getWorld().strikeLightning(loc.clone().set(x, y + 16.5, z - 16))

        ).delay(20).sync(() ->
        // LIGHTNING 4
        loc.getWorld().strikeLightning(loc.clone().set(x, y + 16.5, z + 16))

        ).delay(60).sync(() ->
        // CENTRAL LIGHTNING & RESPAWN
        loc.getWorld().strikeLightning(loc.clone().set(x, y, z))).delay(10)
                .sync(() -> loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z))).delay(10)
                .sync(() -> loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z))).delay(10)
                .sync(() -> loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z))).delay(10).sync(() -> {
                    loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z));
                    //Respawn player and give resistance
                    var uhcPlayer = instance.getPlayerManager().getPlayer(toRespawn.getUniqueId());
                    if(uhcPlayer != null){
                        uhcPlayer.setAlive(true);
                        uhcPlayer.setLastKnownPositionFromLoc(loc);
                    }
                    toRespawn.teleport(loc);
                    toRespawn.setGameMode(GameMode.SURVIVAL);
                    toRespawn.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 15, 20));
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "" + toRespawn.getName() + " has been reborn from the darkness!");

                }).delay(10).sync(() -> {
                    loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            "execute in minecraft:world run weather clear");
                }).sync(TaskChain::abort).execute();
    }

}