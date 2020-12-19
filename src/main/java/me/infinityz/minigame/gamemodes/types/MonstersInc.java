package me.infinityz.minigame.gamemodes.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class MonstersInc extends IGamemode implements Listener {
    private UHC instance;
    private final List<Location> doorLocs;

    public MonstersInc(UHC instance) {
        super("MonstersInc", "All doors are linked as portals.");
        this.instance = instance;
        doorLocs = new ArrayList<>();
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Location loc = e.getBlock().getLocation();

        if(isDoor(block)) {
            doorLocs.add(loc);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockClick(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        Player player = e.getPlayer();
        Location goToLoc;

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (block == null) return;
        

        if(isDoor(block)) {
            Block below = block.getRelative(BlockFace.DOWN, 1);
            if (isDoor(below)) {
                block = below;
            }

            if (doorLocs.size() > 1) {
                do {
                    goToLoc = doorLocs.get((int) (Math.random() * doorLocs.size()));
                    
                    if (!isValidDoorLocation(goToLoc)){
                        doorLocs.remove(goToLoc);
                        goToLoc = null;
                    }
                } while ((goToLoc == null || goToLoc.equals(block.getLocation())) && doorLocs.size() > 1);
                if (goToLoc != null) {
                    player.teleportAsync(goToLoc.clone().add(0.5, 0, 0.5));
                    player.playSound(goToLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
                }
            }
        }
    }

    @EventHandler
    public void onOtherCraft(PrepareItemCraftEvent e) {
        var result = e.getInventory().getResult();
        if (result == null || result.getType() == Material.AIR) {
            return;
        }
        var re = result.getType().toString();
        if (!re.contains("TRAP") && re.contains("DOOR"))
            result.setAmount(1);
    }

    private boolean isValidDoorLocation(Location loc){
        return isDoor(loc.getBlock()) && Bukkit.getWorld(loc.getWorld().getUID()).getWorldBorder().isInside(loc);
    }

    private boolean isDoor(Block b) {
        return !b.getType().toString().contains("TRAP") && b.getType().toString().contains("DOOR");
    }


}