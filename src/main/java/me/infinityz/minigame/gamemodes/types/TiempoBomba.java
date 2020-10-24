package me.infinityz.minigame.gamemodes.types;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Chest.Type;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import gnu.trove.map.hash.THashMap;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

/**
 * TiempoBomba
 */
public class TiempoBomba extends IGamemode implements Listener {
    private UHC instance;
    private THashMap<ArmorStand, Long> hologramChestMap = new THashMap<>();

    public TiempoBomba(UHC instance) {
        super("Tiempo Bomba", "Las cosas explosionan. Cuidadao");
        this.instance = instance;
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,
                () -> hologramChestMap.entrySet().forEach(this::handleEntrySet), 20L, 20L);
    }

    private void handleEntrySet(Map.Entry<ArmorStand, Long> entry) {
        var armorStand = entry.getKey();
        var timeOfCreation = entry.getValue();
        var differential = (System.currentTimeMillis() - timeOfCreation) / 1000.0;
        if (differential >= 30) {
            armorStand.remove();
            hologramChestMap.remove(armorStand);
            //Maybe explode here?
        } else {
            armorStand.setCustomName((int) differential + "s");
        }
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled()) {
            return false;
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
        var dc = createDoubleChestAt(loc);
        for (var stack : clonedDrops) {
            if (stack != null && stack.getType() != Material.AIR) {
                dc.getInventory().addItem(stack);
            }
        }
        var holo = createHoloAt(loc, "30s");
        hologramChestMap.put(holo, System.currentTimeMillis());
        // Clean ram up.
        clonedDrops = null;
        player = null;
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
            // TODO: find block underneath the hologram

        }
    }

    private DoubleChest createDoubleChestAt(Location loc) {
        Block leftSide = loc.getBlock();
        Block rightSide = loc.clone().add(0, 0, -1).getBlock();

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
        return (DoubleChest) leftSide.getState();
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