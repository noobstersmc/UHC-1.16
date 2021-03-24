package me.infinityz.minigame.gamemodes.types;

import java.awt.Color;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Chest.Type;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import gnu.trove.map.hash.THashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;
import net.md_5.bungee.api.ChatColor;

/**
 * TiempoBomba
 */
public class TiempoBomba extends IGamemode implements Listener {
    private UHC instance;
    private THashMap<TimebombData, Long> hologramChestMap = new THashMap<>();
    private ArrayList<java.awt.Color> colors = new ArrayList<>();
    private BukkitTask task;

    public TiempoBomba(UHC instance) {
        super("TimeBomb", "Player stuff explode in a chest after 30 seconds.", Material.TNT);
        this.instance = instance;
        for (int i = 0; i < 30; i++) {
            colors.add(Color.getHSBColor((i*2.5F)/100.0F, 1.0F, 1.0F));            
        }
    }

    @Data
    @AllArgsConstructor
    public class TimebombData {    
        ArmorStand armorStand;
        Block left;
        Block right;        
    }

    private void handleHologramSet() {
        var iterator = hologramChestMap.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            var armorStand = entry.getKey().getArmorStand();
            var timeOfCreation = entry.getValue();
            var differential = (System.currentTimeMillis() - timeOfCreation) / 1000.0;
            if (differential >= 30) {
                armorStand.remove();
                // Maybe explode here?
                Bukkit.getScheduler().runTask(instance, ()->{
                    entry.getKey().left.setType(Material.AIR);
                    entry.getKey().right.setType(Material.AIR);
                    armorStand.getLocation().getWorld().createExplosion(armorStand.getLocation(), 6F);
                });
                iterator.remove();
            } else {
                int second = (30 - (int) differential);
                if(second < 4) Bukkit.getOnlinePlayers().forEach(player -> player.playSound(armorStand.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 0.5f, 0.6f));
                armorStand.setCustomName(ChatColor.of(colors.get(second-1)) + "" + second + "s");
            }
        }
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled()) {
            return false;
        }
        if(task == null){
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> handleHologramSet(), 20L, 20L);
        }
        instance.getListenerManager().registerListener(this);
        setEnabled(true);

        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled()) {
            return false;
        }
        
        if(task != null){
            task.cancel();
            hologramChestMap.clear();
        }
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        var player = e.getEntity();
        var loc = player.getLocation();
        var clonedDrops = new ArrayList<>(e.getDrops());
        // Clear the drops from ground.
        e.getDrops().clear();
        
        loc.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
        Bukkit.getScheduler().runTaskLater(instance, ()->{
            var dc = createDoubleChestAt(loc);
    
            for (var stack : clonedDrops) {
                if (stack != null && stack.getType() != Material.AIR) {
                    dc.getInventory().addItem(stack);
                }
            }
            var holoLoc = new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ());
            var holo = createHoloAt(holoLoc, ChatColor.of(colors.get(30-1)) + "30s");
    
            hologramChestMap.put(new TimebombData(holo, loc.getBlock(), loc.clone().add(0, 0, -1).getBlock()), System.currentTimeMillis());

        }, 1l);
    }

    @EventHandler
    public void onRightClickHologram(PlayerInteractAtEntityEvent e) {
        var entity = e.getRightClicked();
        if (entity instanceof ArmorStand) {
            var block = entity.getLocation().getBlock();
            if (block.getType() == Material.CHEST) {
                var chest = (Chest) block.getState();
                chest.open();
                e.getPlayer().openInventory(chest.getInventory());
            }

        }
    }

    private Chest createDoubleChestAt(Location loc) {
        Block leftSide = loc.getBlock();
        Block rightSide = loc.clone().add(0, 0, -1).getBlock();
        rightSide.getRelative(BlockFace.UP).setType(Material.AIR);
        leftSide.getRelative(BlockFace.UP).setType(Material.AIR);

        leftSide.setType(Material.CHEST);
        rightSide.setType(Material.CHEST);

        BlockData leftData = leftSide.getBlockData();
        ((Directional) leftData).setFacing(BlockFace.EAST);
        leftSide.setBlockData(leftData);

        org.bukkit.block.data.type.Chest chestDataLeft = (org.bukkit.block.data.type.Chest) leftData;
        chestDataLeft.setType(Type.RIGHT);
        leftSide.setBlockData(chestDataLeft);

        BlockData rightData = rightSide.getBlockData();
        ((Directional) rightData).setFacing(BlockFace.EAST);
        rightSide.setBlockData(rightData);

        org.bukkit.block.data.type.Chest chestDataRight = (org.bukkit.block.data.type.Chest) rightData;
        chestDataRight.setType(Type.LEFT);
        rightSide.setBlockData(chestDataRight);
        return (Chest) leftSide.getState();
    }

    private ArmorStand createHoloAt(Location loc, String name) {
        var holo = loc.getWorld().spawn(loc, ArmorStand.class);
        holo.setCollidable(false);
        holo.setAI(false);
        holo.setGravity(false);
        holo.setArms(false);
        holo.setCanMove(false);
        holo.setInvulnerable(true);
        holo.setSmall(true);
        holo.setVisible(false);
        holo.setCustomNameVisible(true);
        holo.setCustomName(name);
        return holo;
    }

}