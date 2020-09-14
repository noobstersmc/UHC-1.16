package me.infinityz.minigame.gamemodes.types;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import co.aikar.taskchain.TaskChain;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

/**
 * EnderRespawn
 */
public class EnderRespawn extends IGamemode implements Listener {
    private UHC instance;
    private EnderRespawnRecipe recipe;

    private ArrayList<String> alreadyRespawn = new ArrayList<>();

    public EnderRespawn(UHC instance) {
        super("Ender Respawn", "Respawn team leader with EnderCrystal.");
        this.instance = instance;
        this.recipe = new EnderRespawnRecipe(new NamespacedKey(instance, "respawn_crystal"), null);
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
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
        Bukkit.getOnlinePlayers().forEach(all -> all.undiscoverRecipe(this.recipe.getNamespacedKey()));
        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().discoverRecipe(this.recipe.getNamespacedKey());

    }

    @EventHandler
    public void onEntityHanging(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            var block = e.getClickedBlock();
            if (block.getType() != Material.AIR) {
                var itemInHand = e.getItem();
                if (itemInHand != null && itemInHand.getType() == Material.END_CRYSTAL) {
                    if (itemInHand.hasItemMeta()
                            && itemInHand.getItemMeta().getDisplayName().contains("Respawn Crystal")
                            && (itemInHand.getItemMeta().getDisplayName().contains(ChatColor.LIGHT_PURPLE + ""))) {
                                var player = e.getPlayer();

                                respawnAnimation(e.getClickedBlock().getLocation());
                                /*
                                var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                                if(team != null){
                                    var uhcLeader = instance.getPlayerManager().getPlayer(team.getTeamLeader());
                                    if(uhcLeader != null){
                                        if(!alreadyRespawn.contains(uhcLeader.getUUID().toString())){
                                            alreadyRespawn.add(uhcLeader.getUUID().toString());
                                            respawnAnimation(e.getClickedBlock().getLocation());

                                        }else{
                                            player.sendMessage(ChatColor.RED + "You can only respawn your leader once.");
                                            
                                        e.setUseItemInHand(Result.DENY);
                                        }

                                    }else{
                                        e.setUseItemInHand(Result.DENY);
                                    }
                                }else{
                                    e.setUseItemInHand(Result.DENY);
                                }

                                */


                    }
                }
            }

        }
    }

    public void respawnAnimation(Location loc) {
        final double x = loc.getBlockX()+ 0.0;
        final double y = loc.getBlockY()+ 0.0;
        final double z = loc.getBlockZ() + 0.0;
        final var tower = "fill %.0f %.0f %.0f %.0f %.0f %.0f minecraft:obsidian destroy";
        UHC.newChain().delay(1).sync(() -> {
            // TOWER 1
            
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:overworld run weather thunder");

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
                .sync(() -> loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z))).delay(10)
                .sync(() -> loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z))).delay(10).sync(() -> {
                    loc.getWorld().strikeLightningEffect(loc.clone().set(x, y, z));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:overworld run weather clear");
                }).sync(TaskChain::abort).execute();
    }

}