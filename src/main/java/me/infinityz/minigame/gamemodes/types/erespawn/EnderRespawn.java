package me.infinityz.minigame.gamemodes.types.erespawn;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EnderCrystal;
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

import co.aikar.taskchain.TaskChain;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import me.infinityz.minigame.teams.objects.Team;

/**
 * EnderRespawn
 */
public class EnderRespawn extends IGamemode implements Listener {
    private UHC instance;
    private EnderRespawnRecipe recipe;

    private ArrayList<Long> respawnedList = new ArrayList<>();

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
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && instance.getTeamManger().isTeams()) {
            var block = e.getClickedBlock();
            if (block.getType() != Material.AIR) {
                var itemInHand = e.getItem();
                if (allowEnderRespawn(itemInHand)) {
                    var player = e.getPlayer();
                    var team = instance.getTeamManger().getPlayerTeam(player.getUniqueId());
                    if (canTeamLeaderRespawn(team)) {
                        respawnAnimation(block.getLocation());
                        respawnedList.add(team.getTeamLeader().getMostSignificantBits());
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

    public void respawnAnimation(Location loc) {
        final double x = loc.getBlockX() + 0.0;
        final double y = loc.getBlockY() + 0.0;
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
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            "execute in minecraft:overworld run weather clear");
                }).sync(TaskChain::abort).execute();
    }

}